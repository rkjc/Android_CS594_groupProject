package com.teamrocket.superlocalcardgamesystem;

import android.os.Bundle;
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
 * Created by tj on 6/6/15.
 */
public class CardplayPlaceholderActivity extends ThreadHandlingActivity {
    EditText editTextMessage;
    Button buttonSend;
    ArrayAdapter convoArrayAdapter;
    ListView convoView;
    int threadType;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardplay_placeholder);

        threadType = getIntent().getExtras().getInt("threadType");

        convoArrayAdapter = new ArrayAdapter<String>(CardplayPlaceholderActivity.this, R.layout.message);
        convoView = (ListView) findViewById(R.id.convo);
        convoView.setAdapter(convoArrayAdapter);

        editTextMessage = (EditText) findViewById(R.id.message);
        buttonSend = (Button) findViewById(R.id.send);
        setMultiWriteListener();
        updateThreadsActivity();
    }

    protected void onDestroy(){

    }

    public void handleReceivedMessage(String message){
        convoArrayAdapter.add(message);
    }

    public String handleWrittenMessage(final String message){
        if(threadType == Constants.HOST_THREAD) {
            try {
                JSONObject update = new JSONObject(message);
            }
            catch (JSONException e) {
                CardplayPlaceholderActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        convoArrayAdapter.add(message);
                    }
                });
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
                if(actionId == EditorInfo.IME_ACTION_GO){
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
            MyApplication.threadMap.get(key).currentActivity = CardplayPlaceholderActivity.this;
        }
    }


}
