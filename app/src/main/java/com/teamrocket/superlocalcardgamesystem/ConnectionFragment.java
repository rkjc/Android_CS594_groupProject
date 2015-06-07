package com.teamrocket.superlocalcardgamesystem;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


/**
 * Created by tj on 6/6/15.
 */
public class ConnectionFragment extends Fragment {
    ArrayAdapter<String> adapter;
    ListView gameRooms;
    MainActivity mainActivity;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        Log.i(MainActivity.TAG, "inflating connection fragement");
        mainActivity = (MainActivity)getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.i(MainActivity.TAG, "Fragment onCreatecalled");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(MainActivity.TAG, "Fragment onActivityCreated called");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_connection, container, false);

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        gameRooms = (ListView) view.findViewById(R.id.game_rooms);
        gameRooms.setAdapter(adapter);
        gameRooms.setClickable(true);
        gameRooms.setOnItemClickListener(new AdapterView.OnItemClickListener()  {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(MainActivity.TAG, "room has ipaddress:port " +
                        mainActivity.addresses.get(position) + ":" + mainActivity.ports.get(position));
                mainActivity.makeClientConnection(mainActivity.addresses.get(position),
                        mainActivity.ports.get(position));
            }
        });

        return view;
    }


    @Override
    public void onStart() {
        Log.i(MainActivity.TAG, "Fragment onStart called");
        super.onStart();
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
