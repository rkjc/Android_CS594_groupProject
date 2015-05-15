package com.teamrocket.superlocalcardgamesystem;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by tj on 5/14/15.
 */
public class BluetoothFragment extends Fragment {
    private static final String TAG = "BluetoothFragment";

    private static final int REQUEST_CONNECT = 1;
    private static final int REQUEST_ENABLE_BLUETOOTH = 2;

    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;

    private String mConnectedDeviceName = null;

    private ArrayAdapter<String> mConversationArrayAdapter;

    private StringBuffer mOutStringBuffer;

    private BluetoothAdapter mBluetoothAdapter = null;

    private ConnectionActivity mConnectionService = null;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter == null){
            Toast.makeText(getActivity(), "bluetooth is not availabl", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        if(!mBluetoothAdapter.isEnabled()){
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        } else if (mConnectionService == null){
            setupChat();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mConnectionService != null){
            mConnectionService.stop();
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        if(mConnectionService.getState() == ConnectionActivity.STATE_NONE){
            mConnectionService.start();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_bluetooth, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        mConversationView = (ListView) view.findViewById(R.id.in);
        mOutEditText = (EditText) view.findViewById(R.id.edit_text_out);
        mSendButton = (Button) view.findViewById(R.id.button_send);
    }

    private void setupChat(){
        Log.i(TAG, "setting up chat");

        mConversationArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.message);
        mConversationView.setAdapter(mConversationArrayAdapter);
        mSendButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                View view = getView();
                if(null != view){
                    TextView textView = (TextView) view.findViewById(R.id.edit_text_out);
                    String message = textView.getText().toString();
                    sendMessage(message);
                }
            }
        });

        mConnectionService = new BluetoothServer(getActivity(), mHandler);
        mOutStringBuffer = new StringBuffer("");
    }

    public void ensureDiscoverable(){
        if(mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    public void sendMessage(String message){
        if(mConnectionService.getState() != BluetoothServer.STATE_CONNECTED){
            Toast.makeText(getActivity(), "not connected", Toast.LENGTH_LONG).show();
            return;
        }

        if(message.length() > 0){
            byte[] send = message.getBytes();
            mConnectionService.write(send);

            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
        }
    }

    private TextView.OnEditorActionListener mWriteListener = new TextView.OnEditorActionListener(){
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event){
            if(actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP){
                String message = view.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };

    private void setStatus(int resId){
        Activity activity = getActivity();
        if(null == activity){
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if(null == actionBar){
            return;
        }
        actionBar.setSubtitle(resId);
    }

    private void setStatus(CharSequence subTitle) {
        Activity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    private final Handler mHandler = new Handler() {
      @Override
      public void handleMessage(Message msg){
            Activity activity = getActivity();
          switch (msg.what){
              case Constants.MESSAGE_STATE_CHANGE:
                  switch (msg.arg1) {
                      case BluetoothServer.STATE_CONNECTED:
                          setStatus(getString("connected to", mConnectedDeviceName));
                          mConversationArrayAdapter.clear();
                          break;
                      case BluetoothServer.STATE_CONNECTING:
                          setStatus("connecting");
                          break;
                      case BluetoothServer.STATE_LISTEN:
                      case BluetoothServer.STATE_NONE:
                          setStatus("not connected");
                          break;
                  }
                  break;
              case Constants.MESSAGE_WRITE:
                  byte[] writeBuf = (byte[]) msg.obj;
                  // construct a string from the buffer
                  String writeMessage = new String(writeBuf);
                  mConversationArrayAdapter.add("Me:  " + writeMessage);
                  break;
              case Constants.MESSAGE_READ:
                  byte[] readBuf = (byte[]) msg.obj;
                  // construct a string from the valid bytes in the buffer
                  String readMessage = new String(readBuf, 0, msg.arg1);
                  mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                  break;
              case Constants.MESSAGE_DEVICE_NAME:
                  // save the connected device's name
                  mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                  if (null != activity) {
                      Toast.makeText(activity, "Connected to "
                              + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                  }
                  break;
              case Constants.MESSAGE_TOAST:
                  if (null != activity) {
                      Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                              Toast.LENGTH_SHORT).show();
                  }
                  break;


          }
      }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BLUETOOTH:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(getActivity(),"blue tooth not enabled",
                            Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
        }
    }

    private void connectDevice(Intent data){
        String address = data.getExtras()
                .getString(ConnectionActivity.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        mConnectionService.connect(device);
    }

}
