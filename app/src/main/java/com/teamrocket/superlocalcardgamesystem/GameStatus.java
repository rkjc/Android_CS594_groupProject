package com.teamrocket.superlocalcardgamesystem;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by rkjcx on 6/9/2015.
 */
public class GameStatus {
    public static final String TAG = "GameStatus";

    public Playingcard cardDeck[];
    public Playingcard cardSetDeck[];
    public Playingcard discardDeck[];

    public Playingcard player0Hand[];
    public Playingcard player1Hand[];
    public Playingcard player2Hand[];
    public Playingcard player3Hand[];

    public Playingcard player0Table[];
    public Playingcard player1Table[];
    public Playingcard player2Table[];
    public Playingcard player3Table[];

    public int activePlayerID;

    GameStatus(){
        Log.i(TAG, "constructor called");
        cardDeck = new Playingcard[52];
        cardSetDeck = new Playingcard[52];
        discardDeck = new Playingcard[52];

        player0Hand = new Playingcard[5];
        player1Hand = new Playingcard[5];
        player2Hand = new Playingcard[5];
        player3Hand = new Playingcard[5];

        player0Table = new Playingcard[5];
        player1Table = new Playingcard[5];
        player2Table = new Playingcard[5];
        player3Table = new Playingcard[5];

        buildSetDeck();
        //might remove auto shuffleing from constructor
        shuffleDeck();
    }

    public void buildSetDeck(){
        Log.i(TAG, "buildSetDeck called");
        for(int i = 0; i < 52; i++){
            cardSetDeck[i] = new Playingcard(Playingcard.cardvalues[i], false);
            cardDeck[i] = cardSetDeck[i];
        }
    }

    public void shuffleDeck(){
        Log.i(TAG, "shuffleDeck called");
//        ArrayList<Integer> remainingIndex = new ArrayList<Integer>();
//        for(int i = 0; i < 52; i++){
//            //remainingIndex.add(i);
//            remainingIndex.add(27);
//        }
//
//        Random rand = new Random();
//        //run until there are no more index numbers
//        while(remainingIndex.size() > 0){
//            int randomNum = rand.nextInt(remainingIndex.size() - 1);
//            //gets a random index number then removes that index from the list
//            int tempIndex = remainingIndex.get(randomNum);
//            remainingIndex.remove(randomNum);
//            //fills deck top down with random cards
//            cardDeck[remainingIndex.size() - 1] = cardSetDeck[tempIndex];
//        }
    }

    public void updateStatus(JSONObject stats){
        Log.i(TAG, "updateStatus called");
        //load attributes
    }

    public JSONObject getStatus(){
        Log.i(TAG, "getStatus called");
        //load attributes
            JSONObject status = new JSONObject();
        //load up the values
            return status;
    }
}
