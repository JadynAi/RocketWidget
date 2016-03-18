package com.jadyn.rocketdemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RocketService extends Service {

    private Rockets rockets;

    public RocketService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        rockets = new Rockets(this);
        rockets.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        
        rockets.hide();
    }
}
