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

/**
 * Created by tj on 6/6/15.
 */
public class CardplayPlaceholderActivity extends ThreadHandlingActivity {
    EditText editTextMessage;
    Button buttonSend;
    ArrayAdapter convoArrayAdapter;
    ListView convoView;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardplay_placeholder);

        convoArrayAdapter = new ArrayAdapter<String>(CardplayPlaceholderActivity.this, R.layout.message);
        convoView = (ListView) findViewById(R.id.convo);
        convoView.setAdapter(convoArrayAdapter);

        editTextMessage = (EditText) findViewById(R.id.message);
        buttonSend = (Button) findViewById(R.id.send);
        setMultiWriteListener();
    }

    protected void onDestroy(){

    }

    public void handleReceivedMessage(String message){
        convoArrayAdapter.add(message);
    }

    public void handleWrittenMessage(String message){

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


}
