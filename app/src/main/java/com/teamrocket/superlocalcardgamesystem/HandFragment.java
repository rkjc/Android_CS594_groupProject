package com.teamrocket.superlocalcardgamesystem;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RotateDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;

/**
 * Created by rkjcx on 6/8/2015.
 */
public class HandFragment  extends Fragment implements CardplayFragmentInterface{
    public static final String TAG = "HandFragment";

    CardplayActivity cardplayActivity;

    Button button1, buttonHold, buttonReveal, buttonFold, buttonDeal, buttonShuffle;
    TextView handText_1, handText_2;
    FrameLayout cardImage1, cardImage2, cardImage3, cardImage4, cardImage5;


    HashMap<String, Drawable> cardMap;

    int counter;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        Log.i(TAG, "inflating connection fragement");
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Fragment onCreatecalled");
        cardplayActivity = (CardplayActivity)getActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "Fragment onActivityCreated called");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Log.i(TAG, "Fragment onCreateView called");
        View view = inflater.inflate(R.layout.fragment_hand, container, false);
        //CardplayActivity activity = (CardplayActivity)this.getActivity();
        //CardplayActivity activity = (CardplayActivity)getActivity();

        counter = 0;

        // the card images that will be displayed
        cardMap = cardplayActivity.makeCardMap();
        cardImage1 =(FrameLayout) view.findViewById(R.id.card1);
        cardImage2 =(FrameLayout) view.findViewById(R.id.card2);
        cardImage3 =(FrameLayout) view.findViewById(R.id.card3);
        cardImage4 =(FrameLayout) view.findViewById(R.id.card4);
        cardImage5 =(FrameLayout) view.findViewById(R.id.card5);

        handText_1 = (TextView) view.findViewById(R.id.hand_text_1);
        handText_2 = (TextView) view.findViewById(R.id.hand_text_2);

        button1 = (Button) view.findViewById(R.id.button1);
        buttonHold = (Button) view.findViewById(R.id.hold);
        buttonReveal = (Button) view.findViewById(R.id.reveal);
        buttonFold = (Button) view.findViewById(R.id.fold);

        //
        buttonDeal = (Button) view.findViewById(R.id.deal);
        buttonShuffle = (Button) view.findViewById(R.id.shuffle);
        // hide the buttons when the player is a client
        if(cardplayActivity.threadType == Constants.CLIENT_THREAD){
            buttonDeal.setVisibility(View.INVISIBLE);
            buttonShuffle.setVisibility(View.INVISIBLE);
        }


        handText_2.setText("playerID= " + MyApplication.playerId);

        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cardplayActivity.showTableFragment();
            }
        });

        buttonHold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardplayActivity.buttonHold();
            }
        });

        buttonReveal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardplayActivity.buttonReveal();
            }
        });

        buttonFold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardplayActivity.printBack();
                //cardplayActivity.buttonFold();
            }
        });

        buttonDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "buttonDeal setOnClickListener");
                cardplayActivity.buttonDeal();
            }
        });

        buttonShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardplayActivity.buttonShuffle();
            }
        });
        refreshView();
        return view;
    }

    public void refreshView(){
        Log.i(TAG, "refreshView called");
        StringBuilder sb = new StringBuilder();
        int id = MyApplication.playerId;
        try {
            String[] cardValues = new String[5];
            cardValues[0] = cardplayActivity.gamestat.playerHands.get(id).get(0).value;
            cardValues[1] = cardplayActivity.gamestat.playerHands.get(id).get(1).value;
            cardValues[2] = cardplayActivity.gamestat.playerHands.get(id).get(2).value;
            cardValues[3] = cardplayActivity.gamestat.playerHands.get(id).get(3).value;
            cardValues[4] = cardplayActivity.gamestat.playerHands.get(id).get(4).value;

//            cardText_1.setText(cardValues[0]);
//            cardText_2.setText(cardValues[1]);
//            cardText_3.setText(cardValues[2]);
//            cardText_4.setText(cardValues[3]);
//            cardText_5.setText(cardValues[4]);

            cardImage1.setBackground(cardMap.get(cardValues[0]));
            cardImage2.setBackground(cardMap.get(cardValues[1]));
            cardImage3.setBackground(cardMap.get(cardValues[2]));
            cardImage4.setBackground(cardMap.get(cardValues[3]));
            cardImage5.setBackground(cardMap.get(cardValues[4]));

            for (int i = 0; i < cardplayActivity.gamestat.playerHands.get(id).size(); i++) {
                sb.append(cardplayActivity.gamestat.playerHands.get(id).get(i).value);
                sb.append(" ");
            }
            handText_1.setText(sb.toString());
            sb.setLength(0);
        }
        catch(Exception e){
            Log.i(TAG, "refreshView caught exception " + e);
        }

    }



    @Override
    public void onStart() {
        Log.i(TAG, "Fragment onStart called");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.i(TAG, "Fragment onResume called");
        super.onResume();
        refreshView();
    }

    @Override
    public void onPause() {
        Log.i(TAG, "Fragment onPause called");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "Fragment onStop called");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "Fragment onDestroyView called");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Fragment onDestroy called");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.i(TAG, "Fragment onDetach called");
        super.onDetach();
    }
}

