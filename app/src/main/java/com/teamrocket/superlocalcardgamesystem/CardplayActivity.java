package com.teamrocket.superlocalcardgamesystem;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
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
    TextView cardplayText_1, cardplayText_2, cardplayText_3;
    Button buttonSend;
    ArrayAdapter convoArrayAdapter;
    ListView convoView;
    String otherMessage, outgoingMessage, incommingMessage;
    GameStatus gamestat;
    JSONObject jsonStatusObject;
    int testCount;

    int threadType;

    protected void onCreate(Bundle savedInstanceState){
        Log.i(TAG, "Fragment onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardplay);

        threadType = getIntent().getExtras().getInt("threadType");
        gamestat = new GameStatus();
        testCount = 0;
        jsonStatusObject = new JSONObject();

        otherMessage = "onCreate init";
        outgoingMessage = "";
        incommingMessage = "";


        cardplayText_1 = (TextView) findViewById(R.id.cardplay_textview_1);
        cardplayText_2 = (TextView) findViewById(R.id.cardplay_textview_2);
        cardplayText_3 = (TextView) findViewById(R.id.cardplay_textview_3);
        editTextMessage = (EditText) findViewById(R.id.edit_message);

        buttonSend = (Button) findViewById(R.id.send);


        int numss = 27;
        String testss = Integer.toString(numss);
        cardplayText_1.setText("threadType= " + threadType + "  playerID= " + MyApplication.playerId);
        setMultiWriteListener();
        updateThreadsActivity();

        showHandFragment();

        if(threadType == Constants.HOST_THREAD){
            Log.i(TAG, "inside threadType test");
            //buttonDeal.setVisibility(View.VISIBLE);
            //buttonDeal.setBackgroundColor(Color.RED);
            //buttonShuffle.setVisibility(View.VISIBLE);
        }else{

        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    protected void onDestroy(){
        Log.i(TAG, "Fragment onDestroy called");
    }

    //does this on local Activity; Host has additional handling for rebroadcast
    public void handleReceivedMessage(String message){
        Log.i(TAG, "Fragment handleReceivedMessage called");
        //convoArrayAdapter.add(message);
        //theLastMessage = message;
        //displays received message
        incommingMessage = message;

        try {
            JSONObject update = new JSONObject(incommingMessage);
            gamestat.updateStatus(update);
            currentFragment().refreshView();
        }
        catch(JSONException e){
            cardplayText_2.setText(incommingMessage);
        }
    }

    //host rebroadcasts received messages
    //this handles what happens locally to message when sending or when host is rebroadcasting
    public String handleWrittenMessage(final String message){
        Log.i(TAG, "Fragment handleWrittenMessage called");
        //if(threadType == Constants.HOST_THREAD) {
            try {
                JSONObject update = new JSONObject(message);
                //run method to update the display fragment
            }
            catch (JSONException e) {
                //sending message string
                otherMessage = message;
                //displays sent string on sending system (not good to do with json)
                cardplayText_3.setText(otherMessage);
//                CardplayActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        convoArrayAdapter.add(message);
//                    }
//                });
            }
        //}
        return message;
    }

    public void setMultiWriteListener() {
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outgoingMessage = editTextMessage.getText().toString();
                editTextMessage.setText("");
                writeToThreads();
            }
        });

        //this is used so that the message is sent using the keyboard go button
        editTextMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    outgoingMessage = editTextMessage.getText().toString();
                    editTextMessage.setText("");
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
            MyApplication.threadMap.get(key).write(outgoingMessage);
            //MyApplication.threadMap.get(key).write(editTextMessage.getText().toString());
        }
        //editTextMessage.setText("");
    }

    public void writeToThreads(String mssg){
        Integer[] keys = MyApplication.threadMap.keySet().toArray(new Integer[0]);
        for (Integer key : keys) {
            MyApplication.threadMap.get(key).write(mssg);
        }
        //editTextMessage.setText("");
    }


    //used when starting CardplayActivity to reasign from MainActivity to this one
    public void updateThreadsActivity(){
        Integer[] keys = MyApplication.threadMap.keySet().toArray(new Integer[0]);
        for (Integer key : keys) {
            MyApplication.threadMap.get(key).currentActivity = CardplayActivity.this;
        }
    }



    void buttonHold(){
        jsonStatusObject = gamestat.getStatus();
    }

    void buttonReveal(){
        gamestat.updateStatus(jsonStatusObject.toString());
    }
    void buttonFold(){}

    void buttonDeal(){
        Log.i(TAG, "buttonDeal");
        gamestat.dealCards();
        currentFragment().refreshView();
        jsonStatusObject = gamestat.getStatus();
        writeToThreads(jsonStatusObject.toString());
    }

    void buttonShuffle(){
        Log.i(TAG, "buttonShuffle");
        gamestat.shuffleDeck();
    }


    public void showHandFragment(){
        Log.i(TAG, "showHandFragment");
        HandFragment handFrag = new HandFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.layout_container, handFrag, "current_fragment_tag");
        ft.addToBackStack("fragment hand");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    public void showTableFragment() {
        Log.i(TAG, "showTableFragment");
        TableFragment tableFrag = new TableFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.layout_container, tableFrag, "current_fragment_tag");
        ft.addToBackStack("fragment table");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    public void printBack(){
        Log.i(TAG, "printBack called");
        cardplayText_2.setText("printback " + testCount++);
//        FragmentManager fm = getFragmentManager();
//        CardplayFragmentInterface fragment = (CardplayFragmentInterface)fm.findFragmentByTag("current_fragment_tag");
//        fragment.refreshView();
        currentFragment().refreshView();
    }

    public CardplayFragmentInterface currentFragment(){
        FragmentManager fm = getFragmentManager();
        CardplayFragmentInterface fragment = (CardplayFragmentInterface)fm.findFragmentByTag("current_fragment_tag");
        return fragment;
    }
}
