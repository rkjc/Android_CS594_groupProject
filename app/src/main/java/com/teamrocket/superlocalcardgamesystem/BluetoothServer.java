package com.teamrocket.superlocalcardgamesystem;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by tj on 5/12/15.
 */
public class BluetoothServer extends ConnectionActivity {

    private static final String TAG = "BluetoothServer";
    private  UUID mUuid = UUID.fromString("41209FF4-F934-11E4-A11D-9DE8802E39D2");
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private AcceptThread mAcceptThread;
    private ConnectedThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;

	public static final int STATE_NONE = 0;
	public static final int STATE_LISTEN = 1;
	public static final int STATE_CONNECTING = 2;
	public static final int STATE_CONNECTED = 3;

    public BluetoothServer(Context context, Handler handler){
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
    }

    private synchronized void setState(int state){
        Log.i(TAG, " setState() " + mState + " => " + state);
        mState = state;

        mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    public synchronized int getState() {
        return mState;
    }

    public synchronized void start(){
        Log.i(TAG, "starting");

        if(mConnectedThread != null){
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null){
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(STATE_LISTEN);

        if(mAcceptThread == null){
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();;
        }
    }

    public synchronized void connect(BluetoothDevice device){
        ; // TODO
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device,
        final String socketType){
        ; // TODO
    }

    public synchronized void stop(){
        ; // TODO
    }

    public void write(byte[] out){
        ; // TODO
    }

    public void connectionFailed(){
        ; // TODO
    }

    public void connectionLost(){
        ; // TODO
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;
        private String mSocketType;

        public AcceptThread() {
            BluetoothServerSocket temp = null;
            try{
                temp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("SLGCS", mUuid);
            }
            catch(IOException ex){

            }
            mmServerSocket = temp;
        }

        public void run(){
            BluetoothSocket socket = null;
            while(true){
                try{
                    socket = mmServerSocket.accept();
                }
                catch(IOException ex){

                }
                if(socket != null){
                    //manage the connect socket(socket)
                    synchronized (BluetoothServer.this){

                    }

                }
            }
        }

        public void cancel(){
            try{
                mmServerSocket.close();
            }
            catch (IOException ex){

            }
        }
    }

    private class ConnectThread extends Thread {
        ; // TODO
    }


    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        public ConnectedThread(BluetoothSocket socket, String socketType){
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try{
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }
            catch(IOException ex){

            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run(){
            ; // TODO
        }

        public void write(byte[] buffer){
            ; // TODO
        }

        public void cancel() {
            ; // TODO
        }
    }
}
