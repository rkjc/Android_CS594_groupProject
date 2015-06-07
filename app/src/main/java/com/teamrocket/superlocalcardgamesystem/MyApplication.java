package com.teamrocket.superlocalcardgamesystem;

import android.app.Application;

import java.util.HashMap;

/**
 * Created by tj on 5/21/15.
 */
public class MyApplication extends Application {
	public static int autoId = 0;
    public static HashMap<Integer, ConnectedThread> threadMap = new HashMap<Integer, ConnectedThread>();
    public static int playerId;


	@Override
	public void onCreate() {
		super.onCreate();
	}

    public static int getPlayerId() {
        return playerId;
    }

    public static void setPlayerId(int playerId) {
        MyApplication.playerId = playerId;
    }
}
