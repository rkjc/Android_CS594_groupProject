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
    TextView handText_1, handText_2, cardText_1, cardText_2, cardText_3, cardText_4, cardText_5;
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
        cardMap = makeCardMap();
        cardImage1 =(FrameLayout) view.findViewById(R.id.card1);
        cardImage2 =(FrameLayout) view.findViewById(R.id.card2);
        cardImage3 =(FrameLayout) view.findViewById(R.id.card3);
        cardImage4 =(FrameLayout) view.findViewById(R.id.card4);
        cardImage5 =(FrameLayout) view.findViewById(R.id.card5);

        handText_1 = (TextView) view.findViewById(R.id.hand_text_1);
        handText_2 = (TextView) view.findViewById(R.id.hand_text_2);
        cardText_1 = (TextView) view.findViewById(R.id.card_text_1);
        cardText_2 = (TextView) view.findViewById(R.id.card_text_2);
        cardText_3 = (TextView) view.findViewById(R.id.card_text_3);
        cardText_4 = (TextView) view.findViewById(R.id.card_text_4);
        cardText_5 = (TextView) view.findViewById(R.id.card_text_5);

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

            cardText_1.setText(cardValues[0]);
            cardText_2.setText(cardValues[1]);
            cardText_3.setText(cardValues[2]);
            cardText_4.setText(cardValues[3]);
            cardText_5.setText(cardValues[4]);

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


//    public void refreshView(){
//        Log.i(TAG, "refreshView called");
//        //handText_1.setText("refresh view " + counter++);
//        StringBuilder sb = new StringBuilder();
//        try {
//            if (MyApplication.playerId == 0) {
//                cardText_1.setText(cardplayActivity.gamestat.player0Hand.get(0).value);
//                cardText_2.setText(cardplayActivity.gamestat.player0Hand.get(1).value);
//                cardText_3.setText(cardplayActivity.gamestat.player0Hand.get(2).value);
//                cardText_4.setText(cardplayActivity.gamestat.player0Hand.get(3).value);
//                cardText_5.setText(cardplayActivity.gamestat.player0Hand.get(4).value);
//
//                for (int i = 0; i < cardplayActivity.gamestat.player0Hand.size(); i++) {
//                    sb.append(cardplayActivity.gamestat.player0Hand.get(i).value);
//                    sb.append(" ");
//                }
//                handText_1.setText(sb.toString());
//                sb.setLength(0);
//            } else if (MyApplication.playerId == 1) {
//                cardText_1.setText(cardplayActivity.gamestat.player1Hand.get(0).value);
//                cardText_2.setText(cardplayActivity.gamestat.player1Hand.get(1).value);
//                cardText_3.setText(cardplayActivity.gamestat.player1Hand.get(2).value);
//                cardText_4.setText(cardplayActivity.gamestat.player1Hand.get(3).value);
//                cardText_5.setText(cardplayActivity.gamestat.player1Hand.get(4).value);
//
//                for (int i = 0; i < cardplayActivity.gamestat.player1Hand.size(); i++) {
//                    sb.append(cardplayActivity.gamestat.player1Hand.get(i).value);
//                    sb.append(" ");
//                }
//                handText_1.setText(sb.toString());
//                sb.setLength(0);
//            } else if (MyApplication.playerId == 2) {
//                cardText_1.setText(cardplayActivity.gamestat.player2Hand.get(0).value);
//                cardText_2.setText(cardplayActivity.gamestat.player2Hand.get(1).value);
//                cardText_3.setText(cardplayActivity.gamestat.player2Hand.get(2).value);
//                cardText_4.setText(cardplayActivity.gamestat.player2Hand.get(3).value);
//                cardText_5.setText(cardplayActivity.gamestat.player2Hand.get(4).value);
//
//                for (int i = 0; i < cardplayActivity.gamestat.player2Hand.size(); i++) {
//                    sb.append(cardplayActivity.gamestat.player2Hand.get(i).value);
//                    sb.append(" ");
//                }
//                handText_1.setText(sb.toString());
//                sb.setLength(0);
//            } else {
//                cardText_1.setText(cardplayActivity.gamestat.player3Hand.get(0).value);
//                cardText_2.setText(cardplayActivity.gamestat.player3Hand.get(1).value);
//                cardText_3.setText(cardplayActivity.gamestat.player3Hand.get(2).value);
//                cardText_4.setText(cardplayActivity.gamestat.player3Hand.get(3).value);
//                cardText_5.setText(cardplayActivity.gamestat.player3Hand.get(4).value);
//
//                for (int i = 0; i < cardplayActivity.gamestat.player3Hand.size(); i++) {
//                    sb.append(cardplayActivity.gamestat.player3Hand.get(i).value);
//                    sb.append(" ");
//                }
//                handText_1.setText(sb.toString());
//                sb.setLength(0);
//            }
//        }catch(Exception e){
//            Log.i(TAG, "refreshView caught exception " + e);
//        }
//
//    }

    public HashMap<String, Drawable> makeCardMap(){
        HashMap<String, Drawable> map = new HashMap<String, Drawable>();
        String[] vals = {"1H","2H","3H","4H","5H","6H","7H","8H","9H","10H","JH","QH","KH","AH",
                "1D","2D","3D","4D","5D","6D","7D","8D","9D","10D","JD","QD","KD","AD",
                "1C","2C","3C","4C","5C","6C","7C","8C","9C","10C","JC","QC","KC","AC",
                "1S","2S","3S","4S","5S","6S","7S","8S","9S","10S","JS","QS","KS","AS"};
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


        return map;
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

