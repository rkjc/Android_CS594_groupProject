package com.teamrocket.superlocalcardgamesystem;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by rkjcx on 6/9/2015.
 */
public class GameStatus {
    public static final String TAG = "GameStatus";
    public Playingcard cardSetDeck[];
   // public ArrayList<Playingcard>[] playersHands;

    public ArrayList<Playingcard> cardDeck;
    public ArrayList<Playingcard> discardDeck;

    public ArrayList<Playingcard> player0Hand;
    public ArrayList<Playingcard> player1Hand;
    public ArrayList<Playingcard> player2Hand;
    public ArrayList<Playingcard> player3Hand;

    public List<ArrayList<Playingcard>> playerHands;

    public ArrayList<Playingcard> player0Table;
    public ArrayList<Playingcard> player1Table;
    public ArrayList<Playingcard> player2Table;
    public ArrayList<Playingcard> player3Table;

    public int activePlayerID;

    GameStatus(){
        Log.i(TAG, "constructor called");
        cardSetDeck = new Playingcard[52];
        //playersHands = new ArrayList[4];

        cardDeck = new ArrayList<Playingcard>();
        discardDeck = new ArrayList<Playingcard>();

        player0Hand = new ArrayList<Playingcard>();
        player1Hand = new ArrayList<Playingcard>();
        player2Hand = new ArrayList<Playingcard>();
        player3Hand = new ArrayList<Playingcard>();

        playerHands = new ArrayList<ArrayList<Playingcard>>();
        playerHands.add(player0Hand);
        playerHands.add(player1Hand);
        playerHands.add(player2Hand);
        playerHands.add(player3Hand);

        player0Table = new ArrayList<Playingcard>();
        player1Table = new ArrayList<Playingcard>();
        player2Table = new ArrayList<Playingcard>();
        player3Table = new ArrayList<Playingcard>();

        buildSetDeck();
        //might remove auto shuffleing from constructor
        //shuffleDeck();
    }

    public void buildSetDeck(){
        Log.i(TAG, "buildSetDeck called");
        for(int i = 0; i < 52; i++){
            cardSetDeck[i] = new Playingcard(Playingcard.cardvalues[i], false);
            cardDeck.add(cardSetDeck[i]);
        }
    }

    public void shuffleDeck(){ //fills deck with full set of randomized cards
        Log.i(TAG, "shuffleDeck called");
        cardDeck.clear();

        ArrayList<Integer> remainingIndex = new ArrayList<Integer>();
        for(int i = 0; i < 52; i++){
            //remainingIndex.add(i);
            remainingIndex.add(i);
        }

        Random rand = new Random(System.currentTimeMillis());
        int randomNum;

        //run until there are no more index numbers
        while(remainingIndex.size() > 0){
            randomNum = rand.nextInt(remainingIndex.size());

            //gets a random index number then removes that index from the list
            int tempIndex = remainingIndex.get(randomNum);

            //fills deck top down with random cards
            cardDeck.add(cardSetDeck[tempIndex]);
            remainingIndex.remove(randomNum);
        }

        //display the deck values for debug
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < cardDeck.size(); i++){
            sb.append(cardDeck.get(i).value);
            sb.append(" ");
        }
        Log.i(TAG, "cardDeck " + sb.toString());
    }

    public void dealCards(){
        Log.i(TAG, "dealCards called");
        player0Hand.clear();
        player1Hand.clear();
        player2Hand.clear();
        player3Hand.clear();

        for(int i = 0; i < 5; i++){
            if(!cardDeck.isEmpty())
                player0Hand.add(cardDeck.remove(0));
            if(!cardDeck.isEmpty())
                player1Hand.add(cardDeck.remove(0));
            if(!cardDeck.isEmpty())
                player2Hand.add(cardDeck.remove(0));
            if(!cardDeck.isEmpty())
                player3Hand.add(cardDeck.remove(0));
        }
    }

    //update function that accepts JSON object in String form
    public void updateStatus(String jsonStr){
        try {
            updateStatus(new JSONObject(jsonStr));
        }catch(JSONException e){
            Log.i(TAG, "getStatus JSON exploded");
        }
    }

    //loads in JSON file and updates all attribute values to match
    public void updateStatus(JSONObject stats){
        Log.i(TAG, "updateStatus called");
        //load attributes from JSON into local variables and arrays
        cardDeck.clear();
        discardDeck.clear();
        player0Hand.clear();
        player1Hand.clear();
        player2Hand.clear();
        player3Hand.clear();
        player0Table.clear();
        player1Table.clear();
        player2Table.clear();
        player3Table.clear();

        JSONObject jobject = stats;
        JSONArray jsonA;
        try {
            jsonA = jobject.getJSONArray("cardDeck");
            for(int i = 0; i < jsonA.length(); i++){
                JSONObject jcard = jsonA.getJSONObject(i);
                Playingcard acard = Playingcard.cardFromJSON(jcard);
                cardDeck.add(acard);
            }

            jsonA = jobject.getJSONArray("discardDeck");
            for(int i = 0; i < jsonA.length(); i++){
                JSONObject jcard = jsonA.getJSONObject(i);
                Playingcard acard = Playingcard.cardFromJSON(jcard);
                discardDeck.add(acard);
            }

            jsonA = jobject.getJSONArray("player0Hand");
            for(int i = 0; i < jsonA.length(); i++){
                JSONObject jcard = jsonA.getJSONObject(i);
                Playingcard acard = Playingcard.cardFromJSON(jcard);
                player0Hand.add(acard);
            }

            jsonA = jobject.getJSONArray("player1Hand");
            for(int i = 0; i < jsonA.length(); i++){
                JSONObject jcard = jsonA.getJSONObject(i);
                Playingcard acard = Playingcard.cardFromJSON(jcard);
                player1Hand.add(acard);
            }

            jsonA = jobject.getJSONArray("player2Hand");
            for(int i = 0; i < jsonA.length(); i++){
                JSONObject jcard = jsonA.getJSONObject(i);
                Playingcard acard = Playingcard.cardFromJSON(jcard);
                player2Hand.add(acard);
            }

            jsonA = jobject.getJSONArray("player3Hand");
            for(int i = 0; i < jsonA.length(); i++){
                JSONObject jcard = jsonA.getJSONObject(i);
                Playingcard acard = Playingcard.cardFromJSON(jcard);
                player3Hand.add(acard);
            }

            //import new values for player table card display
            jsonA = jobject.getJSONArray("player0Table");
            for(int i = 0; i < jsonA.length(); i++){
                JSONObject jcard = jsonA.getJSONObject(i);
                Playingcard acard = Playingcard.cardFromJSON(jcard);
                player0Table.add(acard);
            }

            jsonA = jobject.getJSONArray("player1Table");
            for(int i = 0; i < jsonA.length(); i++){
                JSONObject jcard = jsonA.getJSONObject(i);
                Playingcard acard = Playingcard.cardFromJSON(jcard);
                player1Table.add(acard);
            }

            jsonA = jobject.getJSONArray("player2Table");
            for(int i = 0; i < jsonA.length(); i++){
                JSONObject jcard = jsonA.getJSONObject(i);
                Playingcard acard = Playingcard.cardFromJSON(jcard);
                player2Table.add(acard);
            }

            jsonA = jobject.getJSONArray("player3Table");
            for(int i = 0; i < jsonA.length(); i++){
                JSONObject jcard = jsonA.getJSONObject(i);
                Playingcard acard = Playingcard.cardFromJSON(jcard);
                player3Table.add(acard);
            }


        }catch(JSONException e){
            Log.i(TAG, "getStatus JSON exploded");
        }

        //display the contents of player0Hand
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < player0Hand.size(); i++){
            sb.append(player0Hand.get(i).value); sb.append(" ");
        }
        Log.i(TAG, "updateStatus player0Hand " + sb.toString());

    }

    //load arrays and variables into a JSON object to send
    public JSONObject getStatus(){
        Log.i(TAG, "getStatus called");
        JSONObject status = new JSONObject();
        //JSONObject playHand = new JSONObject();


        //display the contents of player0Hand for debug
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < player0Hand.size(); i++){
            sb.append(player0Hand.get(i).value); sb.append(" ");
        }
        Log.i(TAG, "getStatus player0Hand " + sb.toString());


        //load up the attribute values
        Log.i(TAG, "player0Hand.size()" + player0Hand.size());
        JSONArray tempHandArray;
        try {
            tempHandArray = new JSONArray();
            for(int j = 0; j < cardDeck.size(); j++) {
                tempHandArray.put(cardDeck.get(j).getJSONObject());
            }
            status.put("cardDeck", tempHandArray);

            tempHandArray = new JSONArray();
            for(int j = 0; j < discardDeck.size(); j++) {
                tempHandArray.put(discardDeck.get(j).getJSONObject());
            }
            status.put("discardDeck", tempHandArray);

            tempHandArray = new JSONArray();
            for(int j = 0; j < player0Hand.size(); j++) {
                tempHandArray.put(player0Hand.get(j).getJSONObject());
            }
            status.put("player0Hand", tempHandArray);

            tempHandArray = new JSONArray();
            for(int j = 0; j < player1Hand.size(); j++) {
                tempHandArray.put(player1Hand.get(j).getJSONObject());
            }
            status.put("player1Hand", tempHandArray);

            tempHandArray = new JSONArray();
            for(int j = 0; j < player2Hand.size(); j++) {
                tempHandArray.put(player2Hand.get(j).getJSONObject());
            }
            status.put("player2Hand", tempHandArray);

            tempHandArray = new JSONArray();
            for(int j = 0; j < player3Hand.size(); j++) {
                tempHandArray.put(player3Hand.get(j).getJSONObject());
            }
            status.put("player3Hand", tempHandArray);

            //******

            tempHandArray = new JSONArray();
            for(int j = 0; j < player0Table.size(); j++) {
                tempHandArray.put(player0Table.get(j).getJSONObject());
            }
            status.put("player0Table", tempHandArray);

            tempHandArray = new JSONArray();
            for(int j = 0; j < player1Table.size(); j++) {
                tempHandArray.put(player1Table.get(j).getJSONObject());
            }
            status.put("player1Table", tempHandArray);

            tempHandArray = new JSONArray();
            for(int j = 0; j < player2Table.size(); j++) {
                tempHandArray.put(player2Table.get(j).getJSONObject());
            }
            status.put("player2Table", tempHandArray);

            tempHandArray = new JSONArray();
            for(int j = 0; j < player3Table.size(); j++) {
                tempHandArray.put(player3Table.get(j).getJSONObject());
            }
            status.put("player3Table", tempHandArray);

            //****
            //add individual attribute values here

        }catch(JSONException e){
            Log.i(TAG, "getStatus JSON exploded");
        }

        Log.i(TAG, "leaving getStatus");
        return status;
    }
}

//StringBuilder sb = new StringBuilder();
//for(int i = 0; i < player0Hand.size(); i++){
//        sb.append(player0Hand.get(i).value);
//        sb.append(" ");
//        }
//        Log.i(TAG, "player0Hand " + sb.toString());

//Log.i(TAG, "jcard value " + (String)jcard.get("value"));
//Log.i(TAG, "acard value " + acard.value);