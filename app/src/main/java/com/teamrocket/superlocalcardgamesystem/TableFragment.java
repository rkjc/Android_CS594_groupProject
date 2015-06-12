package com.teamrocket.superlocalcardgamesystem;

import android.app.Activity;
import android.app.Fragment;
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
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by rkjcx on 6/8/2015.
 */
public class TableFragment  extends Fragment implements CardplayFragmentInterface{
    public static final String TAG = "TableFragment";

    CardplayActivity cardplayActivity;

    Button button1, buttonHold;
    TextView tableText_1, tableText_2;
    FrameLayout player0_card1, player0_card2, player0_card3, player0_card4, player0_card5;
    FrameLayout player1_card1, player1_card2, player1_card3, player0_card4, player0_card5;
    FrameLayout player2_card1, player2_card2, player2_card3, player0_card4, player0_card5;
    FrameLayout player3_card1, player3_card2, player3_card3, player0_card4, player0_card5;


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
        View view = inflater.inflate(R.layout.fragment_table, container, false);
        counter =0;

        button1 = (Button) view.findViewById(R.id.button1);
        buttonHold = (Button) view.findViewById(R.id.hold);

        tableText_1 = (TextView) view.findViewById(R.id.table_text_1);
        tableText_2 = (TextView) view.findViewById(R.id.table_text_2);
        CardplayActivity activity = (CardplayActivity)this.getActivity();

        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CardplayActivity currentActivity = (CardplayActivity) getActivity();
                currentActivity.showHandFragment();
            }
        });

        buttonHold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardplayActivity.printBack();
            }
        });

        return view;
    }

    public void refreshView(){
        Log.i(TAG, "refreshView called");
        tableText_2.setText("refresh view " + counter++);
    }

    public void setMultiWriteListener() {
//        buttonSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mainActivity.writeToThreads(editTextMessage.getText().toString());
//                editTextMessage.setText("");
//            }
//        });
//
//        editTextMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if(actionId == EditorInfo.IME_ACTION_GO){
//                    mainActivity.writeToThreads(editTextMessage.getText().toString());
//                    editTextMessage.setText("");
//                    return true;
//                }
//                return false;
//            }
//        });
    }


    @Override
    public void onStart() {
        Log.i(TAG, "Fragment onStart called");
        super.onStart();
        tableText_1.setText("eventually playerID here"); //(mainActivity.playerID);
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

