package com.teamrocket.superlocalcardgamesystem;

import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class MainActivity extends ThreadHandlingActivity {
	public static final String TAG = "MainActivity";

    public int threadType;
    public boolean runningNetworkService;
    public String localName;
	RegisterNetworkService registerNetworkService;
	DiscoverNetworkService discoverNetworkService;
	ServerSocket serverSocket;
	ConnectedThread connectedThread;

    List<InetAddress> addresses;
    List<Integer> ports;
    List<String> hostNames;

    // UI elements
	EditText joinRoomName;
	Button buttonHost, buttonClient;

    ConnectionFragment connectionFragment;
    LobbyFragment lobbyFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		joinRoomName = (EditText) findViewById(R.id.join_room_name);
		buttonHost = (Button) findViewById(R.id.host);
		buttonClient = (Button) findViewById(R.id.client);

        addresses = new ArrayList<InetAddress>();
        ports = new ArrayList<Integer>();
        hostNames = new ArrayList<String>();

        runningNetworkService = false;

		buttonHost.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "setting up host thread");
                String roomName = joinRoomName.getText().toString();
                localName = roomName;
				registerNetworkService = new RegisterNetworkService(getApplicationContext(), roomName);
				threadType = Constants.HOST_THREAD;
                runningNetworkService = true;
				Thread socketServerThread = new Thread(new SocketServerThread());
				socketServerThread.start();
				setupHostLobby();
			}
		});
		buttonClient.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                connectionFragment = new ConnectionFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container, connectionFragment);
                ft.commit();

				Log.d(TAG, "setting up client thread");
                threadType = Constants.CLIENT_THREAD;
                runningNetworkService = true;
				Toast.makeText(getApplicationContext(),
						"searching for game to join, please wait", Toast.LENGTH_LONG)
						.show();
				ServiceResolvedHandler serviceResolvedHandler = new ServiceResolvedHandlerImpl();
				discoverNetworkService = new DiscoverNetworkService(getApplicationContext(),
						serviceResolvedHandler );
                //roomNameText.setText(roomName);
			}
		});

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "error on onDestroy() to close the socket", e);
			}
		}
		if (registerNetworkService != null)
			registerNetworkService.tearDown();
		if (discoverNetworkService != null)
			discoverNetworkService.tearDown();
	}

	public void setupHostLobby() {
		buttonClient.setVisibility(View.GONE);
		buttonHost.setVisibility(View.GONE);
		joinRoomName.setVisibility(View.GONE);

        lobbyFragment = new LobbyFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container, lobbyFragment);
        ft.commit();
	}

    public void setupClientLobby(){
        buttonClient.setVisibility(View.GONE);
		buttonHost.setVisibility(View.GONE);
        joinRoomName.setVisibility(View.GONE);
    }

    public void writeToThreads(String message){
        Integer[] keys = MyApplication.threadMap.keySet().toArray(new Integer[0]);
        for (Integer key : keys) {
            MyApplication.threadMap.get(key).write(message);
        }
    }

    public void handleReceivedMessage(String message){
        // check if it is a JSON command
        //
        try {
            JSONObject update = new JSONObject(message);
            if(update.has("playerId")){
                MyApplication.setPlayerId(update.getInt("playerId"));
                Log.i(TAG, "updated playerId to " + MyApplication.getPlayerId() );
            }
        }
        catch(JSONException e){
            if(threadType == Constants.CLIENT_THREAD) {
                lobbyFragment.convoArrayAdapter.add(message);
            }
        }

    }

    public String handleWrittenMessage(final String message){
        if(threadType == Constants.HOST_THREAD) {
            try {
                JSONObject update = new JSONObject(message);
            }
            catch (JSONException e) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lobbyFragment.convoArrayAdapter.add(message);
                    }
                });
            }
        }
        return message;
    }

	public void makeClientConnection(InetAddress ipAddress, int port) {
        discoverNetworkService.tearDown();
        runningNetworkService = false;
		Thread thread = new Thread(new SocketClientThread(ipAddress.getHostAddress(), port));
		thread.start();

        lobbyFragment = new LobbyFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container, lobbyFragment);
        ft.commit();

		setupClientLobby();
		Toast.makeText(getApplicationContext(), "entered the lobby", Toast.LENGTH_SHORT).show();
	}

	private class SocketServerThread extends Thread {
		@Override
		public void run() {
			try {
				serverSocket = new ServerSocket(Constants.PORT);
                MyApplication.setPlayerId(0);
				MainActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
//						info.setText("Game Port: " + serverSocket.getLocalPort());
					}
				});
				while (true) {
					Socket socket = serverSocket.accept();
                    if(hasMaxPlayers()){
                        socket.close();
                    }
                    else {
                        MyApplication.autoId++;
                        connectedThread = new ConnectedThread(socket, MyApplication.autoId, threadType, MainActivity.this);
                        MyApplication.threadMap.put(new Integer(MyApplication.autoId), connectedThread);
                        connectedThread.start();
                    }
				}
			} catch (IOException e) {
				Log.e(TAG, "exception in SocketServerThread", e);
			}
		}
        public boolean hasMaxPlayers(){
            return MyApplication.threadMap.keySet().size() > Constants.MAX_CLIENTS - 1;
        }
	}

	private class SocketClientThread extends Thread {
		String ipAddress;
		int port;

		public SocketClientThread(String ipAddress, int port) {
			this.ipAddress = ipAddress;
			this.port = port;
		}

		@Override
		public void run() {
			Socket s;
			try {
				s = new Socket(ipAddress, port);
				connectedThread = new ConnectedThread(s, 0, threadType, MainActivity.this);
                MyApplication.threadMap.put(new Integer(MyApplication.autoId), connectedThread);
				connectedThread.start();
			} catch (IOException e) {
				Log.e(TAG, "exception in SocketClientThread", e);
				e.printStackTrace();
			}
		}
	}


	class ServiceResolvedHandlerImpl implements ServiceResolvedHandler {
		public void onServiceResolved(final InetAddress address, final int port, final String name) {
			MainActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
                    // add to the list view the possible games, that aren't already in the list
                    if(!hostNames.contains(name)){
                        connectionFragment.adapter.add(name);
                        hostNames.add(name);
                        addresses.add(address);
                        ports.add( new Integer(port) );
                    }
				}
			});
		}
	}

    @Override
    protected void onPause(){
        if (registerNetworkService != null)
            registerNetworkService.tearDown();
        if (discoverNetworkService != null)
            discoverNetworkService.tearDown();
        super.onPause();

    }

    @Override
    protected void onResume(){
        super.onResume();
        String roomName = joinRoomName.getText().toString();
        if(runningNetworkService && threadType == Constants.HOST_THREAD){
            registerNetworkService = new RegisterNetworkService(getApplicationContext(),roomName );
        }
        else if(runningNetworkService && threadType == Constants.CLIENT_THREAD){
            ServiceResolvedHandler serviceResolvedHandler = new ServiceResolvedHandlerImpl();
            discoverNetworkService = new DiscoverNetworkService(getApplicationContext(),
                    serviceResolvedHandler );
        }

    }

}
