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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


/**
 * Created by tj on 6/6/15.
 */
public class LobbyFragment extends Fragment {

    ArrayAdapter<String> convoArrayAdapter;
    ListView convoView;
    MainActivity mainActivity;

    Button buttonSend, buttonStartGame;
    EditText editTextMessage;
    TextView portInfo, ipInfo, roomName;


    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        Log.i(MainActivity.TAG, "inflating connection fragement");
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.i(MainActivity.TAG, "Fragment onCreatecalled");
        mainActivity = (MainActivity)getActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(MainActivity.TAG, "Fragment onActivityCreated called");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_lobby, container, false);

        convoArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.message);
        convoView = (ListView) view.findViewById(R.id.lobby_convo);
        convoView.setAdapter(convoArrayAdapter);

        roomName = (TextView) view.findViewById(R.id.roomName);

        editTextMessage = (EditText) view.findViewById(R.id.lobby_message);
        buttonSend = (Button) view.findViewById(R.id.lobby_send);

        buttonStartGame = (Button) view.findViewById(R.id.start_game);
        if(mainActivity.threadType == Constants.HOST_THREAD){
            buttonStartGame.setVisibility(View.VISIBLE);
        }
        buttonStartGame.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(), CardplayPlaceholderActivity.class);
                intent.putExtra("threadType", mainActivity.threadType);
                startActivity(intent);
            }
        });
        setMultiWriteListener();
        return view;
    }



    public void setMultiWriteListener() {
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.writeToThreads(editTextMessage.getText().toString());
                editTextMessage.setText("");
            }
        });

        editTextMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_GO){
                    mainActivity.writeToThreads(editTextMessage.getText().toString());
                    editTextMessage.setText("");
                    return true;
                }
                return false;
            }
        });
    }


    @Override
    public void onStart() {
        Log.i(MainActivity.TAG, "Fragment onStart called");
        super.onStart();
        roomName.setText(mainActivity.localName);
    }

    @Override
    public void onResume() {
        Log.i(MainActivity.TAG, "Fragment onResume called");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i(MainActivity.TAG, "Fragment onPause called");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i(MainActivity.TAG, "Fragment onStop called");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.i(MainActivity.TAG, "Fragment onDestroyView called");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i(MainActivity.TAG, "Fragment onDestroy called");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.i(MainActivity.TAG, "Fragment onDetach called");
        super.onDetach();
    }
}
