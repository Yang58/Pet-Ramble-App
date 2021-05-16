package com.example.project2.GoogleMap;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.project2.Main.MainActivity;
import com.example.project2.R;

public class LocationBackground extends Service {
    private static Thread triggerService;
    private static LocationManager lm;
    private static Context context;
    private static Thread mThread;
    private static int mCount = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.wtf("서비스", "서비스 시작");
        context = getBaseContext();

        if (intent.getAction().equals("startForeground")) {
            startFgService();
        }
        return START_STICKY;
    }

    public void startFgService() {
        // PendingIntent를 이용하면 포그라운드 서비스 상태에서 알림을 누르면 앱의 MainActivity를 다시 열게 된다.
        Intent testIntent = new Intent(getApplicationContext(), MainActivity.class);
        testIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        testIntent.setAction(Intent.ACTION_MAIN);
        testIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent
                = PendingIntent.getActivity(getApplicationContext(), 0, testIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // 오래오 윗버젼일 때는 아래와 같이 채널을 만들어 Notification과 연결해야 한다.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel", "play!!",
                    NotificationManager.IMPORTANCE_DEFAULT);

            // Notification과 채널 연걸
            NotificationManager mNotificationManager = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
            mNotificationManager.createNotificationChannel(channel);

            // Notification 세팅
            NotificationCompat.Builder notification
                    = new NotificationCompat.Builder(getApplicationContext(), "channel")
                    .setSmallIcon(R.drawable.loading3)
                    .setContentTitle("다른 유저들에게 현재 위치가 표시됩니다")
                    .setContentIntent(pendingIntent)
                    .setContentText("");

            // id 값은 0보다 큰 양수가 들어가야 한다.
            mNotificationManager.notify(1, notification.build());
            startForeground(1, notification.build());
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {

        super.onStart(intent, startId);
    }

    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDestroy() {
        Log.wtf("서비스", "정지");
        stopForeground(true);
        NotificationManager notificationManager = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
        notificationManager.deleteNotificationChannel("channel");
    }
}