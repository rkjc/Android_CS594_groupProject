package com.teamrocket.superlocalcardgamesystem;

/**
 * Created by rkjcx on 6/10/2015.
 */
public class Playingcard {
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

    public static String[] cardvalues = {"1H","2H","3H","4H","5H","6H","7H","8H","9H","10H","JH","QH","KH","AH",
            "1D","2D","3D","4D","5D","6D","7D","8D","9D","10D","JD","QD","KD","AD",
            "1C","2C","3C","4C","5C","6C","7C","8C","9C","10C","JC","QC","KC","AC",
            "1S","2S","3S","4S","5S","6S","7S","8S","9S","10S","JS","QS","KS","AS",};
}
