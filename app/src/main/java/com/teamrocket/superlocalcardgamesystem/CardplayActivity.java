package com.teamrocket.superlocalcardgamesystem;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rkjcx on 6/8/2015.
 */
public class CardplayActivity extends ThreadHandlingActivity {
    public static final String TAG = "CardplayActivity";
    EditText editTextMessage;
    Button buttonSend;
    ArrayAdapter convoArrayAdapter;
    ListView convoView;
    String theLastMessage;
    TextView lastMessage;
    int threadType;

    protected void onCreate(Bundle savedInstanceState){
        Log.i(TAG, "Fragment onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardplay);

        threadType = getIntent().getExtras().getInt("threadType");

        theLastMessage = "onCreate init";
//        convoArrayAdapter = new ArrayAdapter<String>(CardplayActivity.this, R.layout.message);
//        convoView = (ListView) findViewById(R.id.convo);
//        convoView.setAdapter(convoArrayAdapter);

        lastMessage = (TextView) findViewById(R.id.last_message);
//
        editTextMessage = (EditText) findViewById(R.id.message);
        buttonSend = (Button) findViewById(R.id.send);
        setMultiWriteListener();
        updateThreadsActivity();

        showHandFragment();
    }

    protected void onDestroy(){
        Log.i(TAG, "Fragment onDestroy called");
    }

    public void handleReceivedMessage(String message){
        Log.i(TAG, "Fragment handleReceivedMessage called");
        //convoArrayAdapter.add(message);
        //theLastMessage = message;
        //displays received message
        lastMessage.setText(message);
    }

    public String handleWrittenMessage(final String message){
        Log.i(TAG, "Fragment handleWrittenMessage called");
        if(threadType == Constants.HOST_THREAD) {
            try {
                JSONObject update = new JSONObject(message);
            }
            catch (JSONException e) {
                //sending message string
                theLastMessage = message;
                //displays sent string on sending system (not to do with json)
                lastMessage.setText(theLastMessage);
//                CardplayActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        convoArrayAdapter.add(message);
//                    }
//                });
            }
        }
        return message;
    }

    public void setMultiWriteListener() {
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeToThreads();
            }
        });

        editTextMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    writeToThreads();
                    return true;
                }
                return false;
            }
        });
    }

    public void writeToThreads(){
        Integer[] keys = MyApplication.threadMap.keySet().toArray(new Integer[0]);
        for (Integer key : keys) {
            MyApplication.threadMap.get(key).write(editTextMessage.getText().toString());
        }
        editTextMessage.setText("");
    }



    public void updateThreadsActivity(){
        Integer[] keys = MyApplication.threadMap.keySet().toArray(new Integer[0]);
        for (Integer key : keys) {
            MyApplication.threadMap.get(key).currentActivity = CardplayActivity.this;
        }
    }

    public void showHandFragment(){
        Log.i(TAG, "showHandFragment");
        HandFragment handFrag = new HandFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.layout_container, handFrag);
        ft.addToBackStack("fragment hand");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    public void showTableFragment() {
        Log.i(TAG, "showTableFragment");
        TableFragment tableFrag = new TableFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.layout_container, tableFrag);
        ft.addToBackStack("fragment table");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

}
