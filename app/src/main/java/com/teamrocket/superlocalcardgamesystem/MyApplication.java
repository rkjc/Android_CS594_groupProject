package com.teamrocket.superlocalcardgamesystem;

import android.app.Application;

import java.util.HashMap;

/**
 * Created by tj on 5/21/15.
 */
public class MyApplication extends Application {
	public static int num = 1;
	public static int autoId = -1;
    public static HashMap<Integer, ConnectedThread> threadMap = new HashMap<Integer, ConnectedThread>();

	public int getNum() {
		return num;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}
}
