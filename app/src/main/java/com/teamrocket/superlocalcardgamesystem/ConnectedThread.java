package com.teamrocket.superlocalcardgamesystem;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectedThread extends Thread {
    private final String TAG = "MainActivity";
    private Socket connectedSocket;
    int socketId;
    int connectionStatus;
    int threadType;
    String receivedMessage;
    OutputStream outputStream;
    PrintWriter out;
    InputStream inputStream;
    BufferedReader in;
    ThreadHandlingActivity currentActivity;

    ConnectedThread(Socket socket, int id, int threadType, ThreadHandlingActivity activity) {
        this.threadType = threadType;
        this.currentActivity = activity;
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
        in = new BufferedReader( new InputStreamReader(inputStream));
        if(threadType == Constants.HOST_THREAD){
            String msgReply = "Entered the game lobby as player: " + socketId;
            write(msgReply);
            while (connectionStatus == Constants.CONNECTED) {
                try {
                    receivedMessage = in.readLine();
                    if(receivedMessage == null && connectionStatus == Constants.CONNECTED){ // the socket was lost
                        connectionStatus = Constants.DISCONNECTED;
                        connectionLost(socketId, this);
                    }
                    else if(receivedMessage != null){
                        currentActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String groupMessage = socketId + ": " + receivedMessage;
                                writeToGroup(groupMessage);
                                currentActivity.handleReceivedMessage(groupMessage);
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
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            currentActivity.handleReceivedMessage(receivedMessage);
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
    public void writeToGroup(String msgReply){
        Integer[] keys = MyApplication.threadMap.keySet().toArray( new Integer[0] );
        for(Integer key: keys ){
            MyApplication.threadMap.get(key).write(msgReply);
        }
    }

    private void connectionLost(int socketId, ConnectedThread connectedHostThread) {
        // Send a failure message
        Integer id = new Integer(socketId);
        try {
            ConnectedThread lostThread = MyApplication.threadMap.get(id);
            MyApplication.threadMap.remove(id);
            lostThread.connectedSocket.close();
            connectedHostThread.connectedSocket.close();
            Log.i(TAG, "closed socket for player: " + id.toString());
        } catch (IOException e) {
            Log.e(TAG, "connectionLost() on socketId: " + socketId, e);
        }
    }
}
