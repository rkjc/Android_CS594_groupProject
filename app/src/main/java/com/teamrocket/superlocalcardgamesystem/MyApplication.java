package com.teamrocket.superlocalcardgamesystem;

import android.app.Application;

/**
 * Created by tj on 5/21/15.
 */
public class MyApplication extends Application {
	public int num = 1;
	int autoId = -1;

	public int getNum() {
		return num;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}
}
