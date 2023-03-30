package com.naveedshahzad.automation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class TaskBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Task Broadcast Receiver", Toast.LENGTH_LONG).show();
        MainActivity activity = ((MainActivity) context);
        activity.setCounting();
        activity.startWork();
    }
}
