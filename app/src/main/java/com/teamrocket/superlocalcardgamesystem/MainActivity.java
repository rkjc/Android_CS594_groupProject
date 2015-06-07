package com.teamrocket.superlocalcardgamesystem;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;

@SuppressWarnings("deprecation")
public class MainActivity extends ThreadHandlingActivity {
	private final String TAG = "MainActivity";
	MyApplication myApp;
	private int threadType;
	TextView info, infoIp, roomNameText;
	String receivedMessage = "";
	RegisterNetworkService registerNetworkService;
	DiscoverNetworkService discoverNetworkService;
	ServerSocket serverSocket;
	ConnectedThread connectedThread;
//	HashMap<Integer, ConnectedThread> threadMap;
	EditText editTextMessage, joinRoomName;
	Button buttonSend, buttonStartGame, buttonHost, buttonClient;
	ArrayAdapter convoArrayAdapter;
	ListView convoView;
	ImageView iconView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		info = (TextView) findViewById(R.id.info);
		infoIp = (TextView) findViewById(R.id.infoip);
        roomNameText= (TextView) findViewById(R.id.roomNameText);
		convoArrayAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.message);
		convoView = (ListView) findViewById(R.id.convo);
		convoView.setAdapter(convoArrayAdapter);
		editTextMessage = (EditText) findViewById(R.id.message);
		joinRoomName = (EditText) findViewById(R.id.join_room_name);
		buttonSend = (Button) findViewById(R.id.send);
		buttonStartGame = (Button) findViewById(R.id.start_game);
		buttonHost = (Button) findViewById(R.id.host);
		buttonClient = (Button) findViewById(R.id.client);
		iconView = (ImageView) findViewById(R.id.splash_icon);
//		threadMap = new HashMap<Integer, ConnectedThread>();
		setMultiWriteListener();

		infoIp.setText(getIpAddress());
		Log.i(TAG, getIpAddress());

		buttonHost.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "setting up host thread");
                String roomName = joinRoomName.getText().toString();
				registerNetworkService = new RegisterNetworkService(getApplicationContext(), roomName);
                roomNameText.setText(roomName);
				threadType = Constants.HOST_THREAD;
				Thread socketServerThread = new Thread(new SocketServerThread());
				socketServerThread.start();
				setupHostLobby();
			}
		});
		buttonClient.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "setting up client thread");
				Toast.makeText(getApplicationContext(),
						"searching for game to join, please wait", Toast.LENGTH_LONG)
						.show();
                String roomName = joinRoomName.getText().toString();
				ServiceResolvedHandler serviceResolvedHandler = new ServiceResolvedHandlerImpl();
				discoverNetworkService = new DiscoverNetworkService(getApplicationContext(),
						serviceResolvedHandler, roomName );
                roomNameText.setText(roomName);
			}
		});
		// testing global object MyApplication
//		myApp = (MyApplication) getApplication();
//		Log.i(TAG, myApp.getNum() + "");
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
		buttonSend.setVisibility(View.VISIBLE);
		buttonStartGame.setVisibility(View.VISIBLE);
		buttonClient.setVisibility(View.GONE);
		buttonHost.setVisibility(View.GONE);
		info.setVisibility(View.VISIBLE);
        roomNameText.setVisibility(View.VISIBLE);
		editTextMessage.setVisibility(View.VISIBLE);
		joinRoomName.setVisibility(View.GONE);
		iconView.setVisibility(View.GONE);
	}

	public void setupClientLobby() {
		buttonSend.setVisibility(View.VISIBLE);
		buttonClient.setVisibility(View.GONE);
		buttonHost.setVisibility(View.GONE);
		info.setVisibility(View.VISIBLE);
        roomNameText.setVisibility(View.VISIBLE);
		editTextMessage.setVisibility(View.VISIBLE);
		joinRoomName.setVisibility(View.GONE);
		iconView.setVisibility(View.GONE);
	}

	public void setMultiWriteListener() {
		buttonSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Integer[] keys = MyApplication.threadMap.keySet().toArray(new Integer[0]);
				for (Integer key : keys) {
                    MyApplication.threadMap.get(key).write(editTextMessage.getText().toString());
				}
				editTextMessage.setText("");
			}
		});

        editTextMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_GO){
                    Log.i(TAG, "pressed enter");
                    Integer[] keys = MyApplication.threadMap.keySet().toArray(new Integer[0]);
                    for (Integer key : keys) {
                        MyApplication.threadMap.get(key).write(editTextMessage.getText().toString());
                    }
                    editTextMessage.setText("");
                    return true;
                }
                return false;
            }
        });
	}

    public void handleReceivedMessage(String message){
        convoArrayAdapter.add(message);
    }

	public void makeClientConnection(InetAddress ipAddress, int port) {
		threadType = Constants.CLIENT_THREAD;
		Thread thread = new Thread(new SocketClientThread(ipAddress.getHostAddress(), port));
		thread.start();
		setupClientLobby();
		Toast.makeText(getApplicationContext(), "entered the lobby", Toast.LENGTH_SHORT).show();
	}

	private class SocketServerThread extends Thread {
		@Override
		public void run() {
			try {
				serverSocket = new ServerSocket(Constants.PORT);
				MainActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						info.setText("Game Port: " + serverSocket.getLocalPort());
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


	private String getIpAddress() {
		String ip = "";
		try {
			Enumeration<NetworkInterface> enumNetworkInterfaces =
					NetworkInterface.getNetworkInterfaces();
			while (enumNetworkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
				Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
				while (enumInetAddress.hasMoreElements()) {
					InetAddress inetAddress = enumInetAddress.nextElement();
					if (inetAddress.isSiteLocalAddress()) {
						ip += "Address: " + inetAddress.getHostAddress() + "\n";
					}
				}
			}
		} catch (SocketException e) {
			Log.e(TAG, "getIpAddress()", e);
		}
		return ip;
	}


	class ServiceResolvedHandlerImpl implements ServiceResolvedHandler {
		public void onServiceResolved(final InetAddress address, final int port) {
			MainActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					makeClientConnection(address, port);
				}
			});
		}
	}
}
