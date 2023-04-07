package com.naveedshahzad.automation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class TaskBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        MainActivity activity = ((MainActivity) context);
        activity.clearBrowsingData();
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            Log.e("Exception: ", e.getMessage());
        }
        activity.showToast("AirPlane mode is on");
        //activity.setAirplaneMode(true);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Log.e("Exception: ", e.getMessage());
        }
        activity.showToast("AirPlane mode is off");
        //activity.setAirplaneMode(false);
        activity.setCounting();
        activity.startWork();
    }
}
