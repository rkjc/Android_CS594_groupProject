package com.teamrocket.superlocalcardgamesystem;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by tj on 5/12/15.
 */
public class BluetoothServer {






    private class AcceptThread extends Thread {
        private  UUID mUuid = UUID.fromString("41209FF4-F934-11E4-A11D-9DE8802E39D2");
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        private final BluetoothServerSocket mmServerSocket;

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
}
