package com.teamrocket.superlocalcardgamesystem;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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

import java.util.HashMap;

/**
 * Created by rkjcx on 6/8/2015.
 */
public class CardplayActivity extends ThreadHandlingActivity {
    public static final String TAG = "CardplayActivity";
//    TextView cardplayText_1, cardplayText_2, cardplayText_3;
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


//        cardplayText_1 = (TextView) findViewById(R.id.cardplay_textview_1);
//        cardplayText_2 = (TextView) findViewById(R.id.cardplay_textview_2);
//        cardplayText_3 = (TextView) findViewById(R.id.cardplay_textview_3);


        int numss = 27;
        String testss = Integer.toString(numss);
//        cardplayText_1.setText("threadType= " + threadType + "  playerID= " + MyApplication.playerId);
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


    @Override
    public void onBackPressed(){

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
//            cardplayText_2.setText(incommingMessage);
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
//                cardplayText_3.setText(otherMessage);
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
//        cardplayText_2.setText("printback " + testCount++);
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

    public HashMap<String, Drawable> makeCardMap(){
        HashMap<String, Drawable> map = new HashMap<String, Drawable>();
        String[] vals = {"1H","2H","3H","4H","5H","6H","7H","8H","9H","10H","JH","QH","KH","AH",
                "1D","2D","3D","4D","5D","6D","7D","8D","9D","10D","JD","QD","KD","AD",
                "1C","2C","3C","4C","5C","6C","7C","8C","9C","10C","JC","QC","KC","AC",
                "1S","2S","3S","4S","5S","6S","7S","8S","9S","10S","JS","QS","KS","AS","B","X"};
        // clubs

//        RotateDrawable rd = new RotateDrawable();
//        rd.setDrawable( getResources().getDrawable(R.drawable.card_clubs_1 ));
//        rd.setFromDegrees(0);
//        rd.setToDegrees(90);
//        rd.getDrawable()

        map.put("1C", getResources().getDrawable(R.drawable.card_clubs_1));
        map.put("2C", getResources().getDrawable(R.drawable.card_clubs_2));
        map.put("3C", getResources().getDrawable(R.drawable.card_clubs_3));
        map.put("4C", getResources().getDrawable(R.drawable.card_clubs_4));
        map.put("5C", getResources().getDrawable(R.drawable.card_clubs_5));
        map.put("6C", getResources().getDrawable(R.drawable.card_clubs_6));
        map.put("7C", getResources().getDrawable(R.drawable.card_clubs_7));
        map.put("8C", getResources().getDrawable(R.drawable.card_clubs_8));
        map.put("9C", getResources().getDrawable(R.drawable.card_clubs_9));
        map.put("10C", getResources().getDrawable(R.drawable.card_clubs_10));
        map.put("JC", getResources().getDrawable(R.drawable.card_clubs_11));
        map.put("QC", getResources().getDrawable(R.drawable.card_clubs_12));
        map.put("KC", getResources().getDrawable(R.drawable.card_clubs_13));

        // diamonds
        map.put("1D", getResources().getDrawable(R.drawable.card_diamonds_1));
        map.put("2D", getResources().getDrawable(R.drawable.card_diamonds_2));
        map.put("3D", getResources().getDrawable(R.drawable.card_diamonds_3));
        map.put("4D", getResources().getDrawable(R.drawable.card_diamonds_4));
        map.put("5D", getResources().getDrawable(R.drawable.card_diamonds_5));
        map.put("6D", getResources().getDrawable(R.drawable.card_diamonds_6));
        map.put("7D", getResources().getDrawable(R.drawable.card_diamonds_7));
        map.put("8D", getResources().getDrawable(R.drawable.card_diamonds_8));
        map.put("9D", getResources().getDrawable(R.drawable.card_diamonds_9));
        map.put("10D", getResources().getDrawable(R.drawable.card_diamonds_10));
        map.put("JD", getResources().getDrawable(R.drawable.card_diamonds_11));
        map.put("QD", getResources().getDrawable(R.drawable.card_diamonds_12));
        map.put("KD", getResources().getDrawable(R.drawable.card_diamonds_13));

        // hearts
        map.put("1H", getResources().getDrawable(R.drawable.card_hearts_1));
        map.put("2H", getResources().getDrawable(R.drawable.card_hearts_2));
        map.put("3H", getResources().getDrawable(R.drawable.card_hearts_3));
        map.put("4H", getResources().getDrawable(R.drawable.card_hearts_4));
        map.put("5H", getResources().getDrawable(R.drawable.card_hearts_5));
        map.put("6H", getResources().getDrawable(R.drawable.card_hearts_6));
        map.put("7H", getResources().getDrawable(R.drawable.card_hearts_7));
        map.put("8H", getResources().getDrawable(R.drawable.card_hearts_8));
        map.put("9H", getResources().getDrawable(R.drawable.card_hearts_9));
        map.put("10H", getResources().getDrawable(R.drawable.card_hearts_10));
        map.put("JH", getResources().getDrawable(R.drawable.card_hearts_11));
        map.put("QH", getResources().getDrawable(R.drawable.card_hearts_12));
        map.put("KH", getResources().getDrawable(R.drawable.card_hearts_13));

        // spades
        map.put("1S", getResources().getDrawable(R.drawable.card_spades_1));
        map.put("2S", getResources().getDrawable(R.drawable.card_spades_2));
        map.put("3S", getResources().getDrawable(R.drawable.card_spades_3));
        map.put("4S", getResources().getDrawable(R.drawable.card_spades_4));
        map.put("5S", getResources().getDrawable(R.drawable.card_spades_5));
        map.put("6S", getResources().getDrawable(R.drawable.card_spades_6));
        map.put("7S", getResources().getDrawable(R.drawable.card_spades_7));
        map.put("8S", getResources().getDrawable(R.drawable.card_spades_8));
        map.put("9S", getResources().getDrawable(R.drawable.card_spades_9));
        map.put("10S", getResources().getDrawable(R.drawable.card_spades_10));
        map.put("JS", getResources().getDrawable(R.drawable.card_spades_11));
        map.put("QS", getResources().getDrawable(R.drawable.card_spades_12));
        map.put("KS", getResources().getDrawable(R.drawable.card_spades_13));

        map.put("B", getResources().getDrawable(R.drawable.card_back_side));
        map.put("X", getResources().getDrawable(R.drawable.place_holder));
        return map;
    }
}
