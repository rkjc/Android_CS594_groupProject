package com.teamrocket.superlocalcardgamesystem;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectedThread extends Thread {
    public static final String TAG = "ConnectedThread";
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
            String msgReply = "Player: " + socketId + " has entered";

            // need to inform player what their id is
            writeToGroup(msgReply);
            write(prepareIdJSON(socketId));
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
                                String groupMessage = receivedMessage;
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
        out.println( currentActivity.handleWrittenMessage(msgReply) );
    }

    public void writeToGroup(String msgReply){
        Integer[] keys = MyApplication.threadMap.keySet().toArray( new Integer[0] );
        for(Integer key: keys ){
            MyApplication.threadMap.get(key).write(msgReply);
        }
    }

    public String prepareIdJSON(int id){
        try{
            JSONObject json = new JSONObject();
            json.put("playerId", id);
            Log.i(TAG, "sent player id as json: "+ json.toString());
            return json.toString();
        }
        catch (JSONException e){
            Log.e(TAG, "prepareIdJSON", e);
        }
        return "";
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
