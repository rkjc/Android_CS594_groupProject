package com.teamrocket.superlocalcardgamesystem;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rkjcx on 6/10/2015.
 */
public class Playingcard {
    public static final String TAG = "Playingcard";
    public String value;
    // ex. 3c = three of clubs; kd = king of diamonds

    public boolean faceup;

    public Playingcard() {
        value = "joker";
        faceup = false;
    }

    public Playingcard(String val, boolean up) {
        value = val;
        faceup = up;
    }

    public Playingcard(String val) {
        value = val;
        faceup = false;
    }

    public JSONObject getJSONObject() {
        Log.i(TAG, "getJSONObject");
        JSONObject obj = new JSONObject();
        try {
            obj.put("value", value);
            obj.put("faceup", faceup);
        } catch (JSONException e) {
            //trace("DefaultListItem.toString JSONException: "+e.getMessage());
            Log.i(TAG, "JSONException " + e.getMessage());
        }
        return obj;
    }

    public static Playingcard cardFromJSON(JSONObject jcard){
        Log.i(TAG, "cardFromJSON");
        Playingcard returnCard = null;
        try{
            String cvalue = (String)jcard.get("value");
            boolean cfaceup = (Boolean)jcard.get("faceup");

           returnCard = new Playingcard(cvalue, cfaceup);

        }catch(JSONException e){

        }
        return returnCard;
    }

    public static String[] cardvalues = {"1H","2H","3H","4H","5H","6H","7H","8H","9H","10H","JH","QH","KH",
            "1D","2D","3D","4D","5D","6D","7D","8D","9D","10D","JD","QD","KD",
            "1C","2C","3C","4C","5C","6C","7C","8C","9C","10C","JC","QC","KC",
            "1S","2S","3S","4S","5S","6S","7S","8S","9S","10S","JS","QS","KS"};
}
