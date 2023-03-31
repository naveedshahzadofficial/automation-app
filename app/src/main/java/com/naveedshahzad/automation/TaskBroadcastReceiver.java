package com.naveedshahzad.automation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class TaskBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        MainActivity activity = ((MainActivity) context);
        activity.clearBrowsingData();
        activity.setAirplaneMode(true);
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        activity.setAirplaneMode(false);
        activity.setCounting();
        activity.startWork();
    }
}
