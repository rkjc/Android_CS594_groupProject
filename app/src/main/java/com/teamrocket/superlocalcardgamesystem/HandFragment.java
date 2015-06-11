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
    TextView handText, playerID;
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
        CardplayActivity activity = (CardplayActivity)getActivity();

        counter = 0;

        handText = (TextView) view.findViewById(R.id.handText);
        playerID = (TextView) view.findViewById(R.id.player_id);

        button1 = (Button) view.findViewById(R.id.button1);
        buttonHold = (Button) view.findViewById(R.id.hold);
        buttonReveal = (Button) view.findViewById(R.id.reveal);
        buttonFold = (Button) view.findViewById(R.id.fold);
        buttonDeal = (Button) view.findViewById(R.id.deal);
        buttonShuffle = (Button) view.findViewById(R.id.shuffle);

        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                CardplayActivity currentActivity = (CardplayActivity) getActivity();
//                currentActivity.showTableFragment();
                cardplayActivity.showTableFragment();
            }
        });

        buttonHold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        buttonReveal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        buttonFold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardplayActivity.printBack();
            }
        });

        buttonDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //for testing debug
                cardplayActivity.writeToThreads("Deal button test " + counter++);
            }
        });

        buttonShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //gamestat.shuffleDeck();
            }
        });
        return view;
    }


    public void refreshView(){
        Log.i(TAG, "refreshView called");
        playerID.setText("refresh view " + counter++);
    }

    @Override
    public void onStart() {
        Log.i(TAG, "Fragment onStart called");
        super.onStart();
        handText.setText("eventually playerID here"); //(mainActivity.playerID);
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

