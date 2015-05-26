package com.teamrocket.superlocalcardgamesystem;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.List;

@SuppressWarnings("deprecation")
public class MainActivity extends ActionBarActivity {

    private final String TAG = "MainActivity";
    private int threadType;
    TextView info, infoIp;
    String message = "";
    String receivedMessage = "";
    ServerSocket serverSocket;
    ConnectedThread connectedThread;
    List<ConnectedThread> listThreads;
    EditText editTextMessage, joinPort, joinAddress;
    Button buttonSend, buttonHost, buttonClient;
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
        joinPort = (EditText)findViewById(R.id.join_port);
        buttonSend = (Button)findViewById(R.id.send);
        buttonHost = (Button)findViewById(R.id.host);
        buttonClient = (Button)findViewById(R.id.client);
        iconView = (ImageView)findViewById(R.id.splash_icon);
        listThreads = new ArrayList<ConnectedThread>();
        setMultiWriteListener();

        infoIp.setText(getIpAddress());
        Log.i(TAG, getIpAddress());

        buttonHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                threadType = Constants.HOST_THREAD;
                Thread socketServerThread = new Thread(new SocketServerThread());
                socketServerThread.start();
                setupLobby();
            }
        });
        buttonClient.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                threadType = Constants.CLIENT_THREAD;
                String ipAddress = joinAddress.getText().toString();
                int port = Integer.parseInt(joinPort.getText().toString());
                Thread thread = new Thread(new SocketClientThread(ipAddress, port));
                thread.start();
                setupLobby();
            }
        });
        // testing global object MyApplication
        MyApplication myApplication = (MyApplication)getApplication();
        Log.i(TAG, myApplication.getNum() + "" );
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

            }
        }
    }

    public void setupLobby(){
        buttonSend.setVisibility(View.VISIBLE);
        info.setVisibility(View.VISIBLE);
        editTextMessage.setVisibility(View.VISIBLE);
        joinPort.setVisibility(View.GONE);
        joinAddress.setVisibility(View.GONE);
        iconView.setVisibility(View.GONE);
    }

    public void setMultiWriteListener(){
        buttonSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                for(ConnectedThread thread: listThreads){
                    thread.write(editTextMessage.getText().toString());
                }
                editTextMessage.setText("");
            }
        });
    }

    private class SocketServerThread extends Thread{
        static final int port = 8080;
        int count = 0;

        @Override
        public void run(){
            try{
                serverSocket = new ServerSocket(port);
                MainActivity.this.runOnUiThread( new Runnable() {
                    @Override
                    public void run(){
                        info.setText("Game Port: " + serverSocket.getLocalPort());
                    }
                });
                while(true){
                    Socket socket = serverSocket.accept();
                    count++;
                    message = "#" + count + " from " + socket.getInetAddress() + ":"
                            + socket.getPort() + "\n";
                    connectedThread = new ConnectedThread(socket, count);
                    listThreads.add(connectedThread);
                    connectedThread.start();
                }
            }
            catch (IOException e){

            }
        }
    }

    private class SocketClientThread extends Thread{
        String ipAddress;
        int port;
        public SocketClientThread(String ipAddress, int port){
            this.ipAddress = ipAddress;
            this.port = port;
        }
        @Override
        public void run(){
            Socket s;
            try{
                s = new Socket(ipAddress, port);
                connectedThread =  new ConnectedThread(s,0);
                listThreads.add(connectedThread);
                connectedThread.start();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    private class ConnectedThread extends Thread {
        private Socket hostThreadSocket;
        int cnt;
        OutputStream outputStream;
        PrintWriter out;
        InputStream inputStream;
        BufferedReader in;

        ConnectedThread(Socket socket, int c) {
            hostThreadSocket = socket;
            cnt = c;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = hostThreadSocket.getInputStream();
                tmpOut = hostThreadSocket.getOutputStream();
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
                String msgReply = "Hello from Android, you are player#" + cnt;
                write(msgReply);
                while (true) {
                    try {
                        receivedMessage = in.readLine();
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for(ConnectedThread thread: listThreads){
                                    thread.write(receivedMessage);
                                }
                                convoArrayAdapter.add(receivedMessage);
                            }
                        });
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
}
