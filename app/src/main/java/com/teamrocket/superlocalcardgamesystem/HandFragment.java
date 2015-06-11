package com.teamrocket.superlocalcardgamesystem;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by rkjcx on 6/8/2015.
 */
public class HandFragment  extends Fragment implements CardplayFragmentInterface{
    public static final String TAG = "HandFragment";

    CardplayActivity cardplayActivity;

    Button button1, buttonHold, buttonReveal, buttonFold, buttonDeal, buttonShuffle;
    TextView handText_1, handText_2, cardText_1, cardText_2, cardText_3, cardText_4, cardText_5;
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
        buttonDeal = (Button) view.findViewById(R.id.deal);
        buttonShuffle = (Button) view.findViewById(R.id.shuffle);

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
        //handText_1.setText("refresh view " + counter++);
        StringBuilder sb = new StringBuilder();

        if(MyApplication.playerId == 0) {
            cardText_1.setText(cardplayActivity.gamestat.player0Hand.get(0).value);
            cardText_2.setText(cardplayActivity.gamestat.player0Hand.get(1).value);
            cardText_3.setText(cardplayActivity.gamestat.player0Hand.get(2).value);
            cardText_4.setText(cardplayActivity.gamestat.player0Hand.get(3).value);
            cardText_5.setText(cardplayActivity.gamestat.player0Hand.get(4).value);

            for (int i = 0; i < cardplayActivity.gamestat.player0Hand.size(); i++) {
                sb.append(cardplayActivity.gamestat.player0Hand.get(i).value);
                sb.append(" ");
            }
            handText_1.setText(sb.toString());
            sb.setLength(0);
        }
        else if(MyApplication.playerId == 1){
            cardText_1.setText(cardplayActivity.gamestat.player1Hand.get(0).value);
            cardText_2.setText(cardplayActivity.gamestat.player1Hand.get(1).value);
            cardText_3.setText(cardplayActivity.gamestat.player1Hand.get(2).value);
            cardText_4.setText(cardplayActivity.gamestat.player1Hand.get(3).value);
            cardText_5.setText(cardplayActivity.gamestat.player1Hand.get(4).value);

            for (int i = 0; i < cardplayActivity.gamestat.player1Hand.size(); i++) {
                sb.append(cardplayActivity.gamestat.player1Hand.get(i).value);
                sb.append(" ");
            }
            handText_1.setText(sb.toString());
            sb.setLength(0);
        }
        else if(MyApplication.playerId == 2){
            cardText_1.setText(cardplayActivity.gamestat.player2Hand.get(0).value);
            cardText_2.setText(cardplayActivity.gamestat.player2Hand.get(1).value);
            cardText_3.setText(cardplayActivity.gamestat.player2Hand.get(2).value);
            cardText_4.setText(cardplayActivity.gamestat.player2Hand.get(3).value);
            cardText_5.setText(cardplayActivity.gamestat.player2Hand.get(4).value);

            for (int i = 0; i < cardplayActivity.gamestat.player2Hand.size(); i++) {
                sb.append(cardplayActivity.gamestat.player2Hand.get(i).value);
                sb.append(" ");
            }
            handText_1.setText(sb.toString());
            sb.setLength(0);
        }
        else {
            cardText_1.setText(cardplayActivity.gamestat.player3Hand.get(0).value);
            cardText_2.setText(cardplayActivity.gamestat.player3Hand.get(1).value);
            cardText_3.setText(cardplayActivity.gamestat.player3Hand.get(2).value);
            cardText_4.setText(cardplayActivity.gamestat.player3Hand.get(3).value);
            cardText_5.setText(cardplayActivity.gamestat.player3Hand.get(4).value);

            for (int i = 0; i < cardplayActivity.gamestat.player3Hand.size(); i++) {
                sb.append(cardplayActivity.gamestat.player3Hand.get(i).value);
                sb.append(" ");
            }
            handText_1.setText(sb.toString());
            sb.setLength(0);
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

