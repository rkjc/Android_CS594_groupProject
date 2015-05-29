package com.teamrocket.superlocalcardgamesystem;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("deprecation")
public class MainActivity extends ActionBarActivity {

    private final String TAG = "MainActivity";
    MyApplication myApp;
    private int threadType;
    TextView info, infoIp;
    String message = "";
    String receivedMessage = "";
    RegisterNetworkService registerNetworkService;
    DiscoverNetworkService discoverNetworkService;
    ServerSocket serverSocket;
    ConnectedThread connectedThread;
    List<ConnectedThread> listThreads;
    HashMap<Integer, ConnectedThread> threadMap;
    EditText editTextMessage, joinAddress;
    Button buttonSend, buttonStartGame, buttonHost, buttonClient;
    ArrayAdapter convoArrayAdapter;
    ListView convoView;
    ImageView iconView;
    //PrintWriter out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        info = (TextView)findViewById(R.id.info);
        infoIp = (TextView)findViewById(R.id.infoip);
        convoArrayAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.message);
        convoView = (ListView)findViewById(R.id.convo);
        convoView.setAdapter(convoArrayAdapter);
        editTextMessage = (EditText)findViewById(R.id.message);
        joinAddress = (EditText)findViewById(R.id.join_address);
        buttonSend = (Button)findViewById(R.id.send);
        buttonStartGame = (Button)findViewById(R.id.start_game);
        buttonHost = (Button)findViewById(R.id.host);
        buttonClient = (Button)findViewById(R.id.client);
        iconView = (ImageView)findViewById(R.id.splash_icon);
        listThreads = new ArrayList<ConnectedThread>();
        threadMap = new HashMap<Integer, ConnectedThread>();
        setMultiWriteListener();

        infoIp.setText(getIpAddress());
        Log.i(TAG, getIpAddress());

        buttonHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "setting up host thread");
                registerNetworkService = new RegisterNetworkService(getApplicationContext());
                threadType = Constants.HOST_THREAD;
                Thread socketServerThread = new Thread(new SocketServerThread());
                socketServerThread.start();
                setupLobby();
            }
        });
        buttonClient.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d(TAG, "setting up client thread");
                Toast.makeText(getApplicationContext(), "searching for game to autojoin, please wait", Toast.LENGTH_LONG).show();
                ServiceResolvedHandler serviceResolvedHandler = new ServiceResolvedHandlerImpl();
                discoverNetworkService = new DiscoverNetworkService(getApplicationContext(), serviceResolvedHandler);

//                threadType = Constants.CLIENT_THREAD;
//                String ipAddress = joinAddress.getText().toString();
//                Thread thread = new Thread(new SocketClientThread(ipAddress));
//                thread.start();
//                setupLobby();
            }
        });
        // testing global object MyApplication
        myApp = (MyApplication)getApplication();
        Log.i(TAG, myApp.getNum() + "" );
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(serverSocket != null){
            try{
                serverSocket.close();
            }
            catch(IOException e){
                Log.e(TAG, "error on onDestroy() to close the socket", e);
            }
        }
        if(registerNetworkService != null)
            registerNetworkService.tearDown();
        if(discoverNetworkService != null)
            discoverNetworkService.tearDown();

    }

    public void setupLobby(){
        buttonSend.setVisibility(View.VISIBLE);
        buttonStartGame.setVisibility(View.VISIBLE);
        buttonClient.setVisibility(View.GONE);
        buttonHost.setVisibility(View.GONE);
        info.setVisibility(View.VISIBLE);
        editTextMessage.setVisibility(View.VISIBLE);
        joinAddress.setVisibility(View.GONE);
        iconView.setVisibility(View.GONE);
    }

    public void setMultiWriteListener(){
        buttonSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Integer[] keys = threadMap.keySet().toArray( new Integer[0] );
                for(Integer key: keys ){
                    threadMap.get(key).write(editTextMessage.getText().toString());
                }
                editTextMessage.setText("");
            }
        });
    }

    public void makeClientConnection(InetAddress ipAddress, int port){
        threadType = Constants.CLIENT_THREAD;
        Thread thread = new Thread(new SocketClientThread(ipAddress.getHostAddress(),port));
        thread.start();
        setupLobby();
        Toast.makeText(getApplicationContext(), "entered the lobby", Toast.LENGTH_LONG).show();
    }


    private class SocketServerThread extends Thread{

        @Override
        public void run(){
            try{
                serverSocket = new ServerSocket(Constants.PORT);
                MainActivity.this.runOnUiThread( new Runnable() {
                    @Override
                    public void run(){
                        info.setText("Game Port: " + serverSocket.getLocalPort());
                    }
                });
                while(true){
                    Socket socket = serverSocket.accept();
                    myApp.autoId++;
                    connectedThread = new ConnectedThread(socket, myApp.autoId);
                    threadMap.put(new Integer(myApp.autoId), connectedThread);
                    connectedThread.start();
                }
            }
            catch (IOException e){
                Log.e(TAG, "exception in SocketServerThread", e);
            }
        }
    }

    private class SocketClientThread extends Thread{
        String ipAddress;
        int port;
        public SocketClientThread(String ipAddress, int port){
            this.ipAddress = ipAddress;
//            this.port = Constants.PORT;
            this.port = port;
        }
        @Override
        public void run(){
            Socket s;
            try{
                s = new Socket(ipAddress, port);
                connectedThread =  new ConnectedThread(s,0);
                threadMap.put(new Integer(myApp.autoId), connectedThread);
                connectedThread.start();
            }
            catch(IOException e){
                Log.e(TAG, "exception in SocketClientThread", e);
                e.printStackTrace();
            }
        }
    }

    private class ConnectedThread extends Thread {
        private Socket connectedSocket;
        int socketId;
        int connectionStatus;
        OutputStream outputStream;
        PrintWriter out;
        InputStream inputStream;
        BufferedReader in;

        ConnectedThread(Socket socket, int id) {
            connectedSocket = socket;
            socketId = id;
            connectionStatus = Constants.CONNECTED;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = connectedSocket.getInputStream();
                tmpOut = connectedSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }
            inputStream = tmpIn;
            outputStream = tmpOut;
        }

        @Override
        public void run() {
            out = new PrintWriter(outputStream,true);
            in = new BufferedReader( new InputStreamReader(	inputStream));
            if(threadType == Constants.HOST_THREAD){
                String msgReply = "Entered the game lobby as player: " + socketId;
                write(msgReply);
                while (true && connectionStatus == Constants.CONNECTED) {
                    try {
                        receivedMessage = in.readLine();
                        if(receivedMessage == null && connectionStatus == Constants.CONNECTED){ // the socket was lost
                            connectionStatus = Constants.DISCONNECTED;
                            connectionLost(socketId, this);
                        }
                        else if(receivedMessage != null){
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Integer[] keys = threadMap.keySet().toArray( new Integer[0] );
                                    for(Integer key: keys ){
                                        threadMap.get(key).write(receivedMessage);
                                    }
                                    convoArrayAdapter.add(receivedMessage);
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
            else if(threadType == Constants.CLIENT_THREAD){
                while (true) {
                    try {
                        receivedMessage = in.readLine();
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                convoArrayAdapter.add(receivedMessage);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }
        public void write(String msgReply) {
            out.println(msgReply);
        }
    }

    private String getIpAddress(){
        String ip = "";
        try{
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while(enumNetworkInterfaces.hasMoreElements()){
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while(enumInetAddress.hasMoreElements()){
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if(inetAddress.isSiteLocalAddress()){
                        ip += "Address: " + inetAddress.getHostAddress() + "\n";
                    }
                }
            }
        }
        catch(SocketException e){
            Log.e(TAG, "getIpAddress()", e);
        }
        return ip;
    }

    private void connectionLost(int socketId, ConnectedThread connectedHostThread) {
        // Send a failure message
        Integer id = new Integer(socketId);
        try {
            ConnectedThread lostThread = threadMap.get(id);
            threadMap.remove(id);
            lostThread.connectedSocket.close();
            connectedHostThread.connectedSocket.close();
            Log.i(TAG, "closed socket for player: " + id.toString());
        }
        catch(IOException e){
            Log.e(TAG, "connectionLost() on socketId: " + socketId, e);
        }
    }

    class ServiceResolvedHandlerImpl implements ServiceResolvedHandler {
        public void onServiceResolved(final InetAddress address, final int port){
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    makeClientConnection(address, port);
                }
            });

        }
    }
}

interface ServiceResolvedHandler{
    void onServiceResolved(InetAddress address, int port);
}


//class someClass{
//    ServiceResolvedHandler handler;
//    public void setHandlerListener(ServiceResolvedHandler listener){
//        handler = listener;
//    }
//    public void eventFired(InetAddress address, int port){
//        if(handler != null){
//            handler.onServiceResolved(address, port);
//        }
//    }
//}
