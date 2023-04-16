package com.naveedshahzad.automation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        MainActivity activity = ((MainActivity) context);
        Handler handler = new Handler(Looper.getMainLooper());

        SystemClock.sleep(3000);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        activity.clearBrowsingData();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // Do Background work here
                activity.setAirplaneMode(true);
                SystemClock.sleep(1000);
                activity.setAirplaneMode(false);
                SystemClock.sleep(5000);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Do Ui Thread work here

                        activity.setCounting();
                        activity.startWork();
                    }
                });
            }
        });

    }

}
