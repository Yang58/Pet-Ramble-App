package com.example.project2.GoogleMap;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

public class LocationBackground extends Service {
    ServiceThread thread;

    public LocationBackground() {
    }

    class LocationBackgroud extends Binder {
        LocationBackground getService() {
            return LocationBackground.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myServiceHandler handler = new myServiceHandler();
        thread = new ServiceThread(handler);
        thread.stopForever();
        Log.wtf("백그라운드 서비스", "파괴됨");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myServiceHandler handler = new myServiceHandler();
        thread = new ServiceThread(handler);
        thread.stopForever();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myServiceHandler handler = new myServiceHandler();
        thread = new ServiceThread(handler);
        thread.start();
    }

    public class myServiceHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.wtf("bgservice","running");
        }
    }
}