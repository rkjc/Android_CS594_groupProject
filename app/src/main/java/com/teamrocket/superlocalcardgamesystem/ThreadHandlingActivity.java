package com.teamrocket.superlocalcardgamesystem;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;

import java.net.InetAddress;

public abstract class ThreadHandlingActivity extends ActionBarActivity {

    public abstract void handleReceivedMessage(String message);

    public abstract String handleWrittenMessage(String message);
}
