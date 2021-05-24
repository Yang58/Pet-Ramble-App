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
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.project2.FirebaseDB.User;
import com.example.project2.Main.MainActivity;
import com.example.project2.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.snapshot.Index;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class LocationBackground extends Service {
    private static Thread triggerService;
    private static LocationManager lm;
    private static Context context;
    private static Thread mThread;
    private static int mCount = 0;
    private static ArrayList<LatLng> interestPoint;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.wtf("서비스", "서비스 시작");
        context = getBaseContext();

        try {
            if (intent.getAction().equals("startForeground")) {
                startFgService();
            } else if (intent.getAction().equals("startUploadPaths")) {
                try {
                    interestPoint = intent.getParcelableArrayListExtra("interest");
                    startUpPaths();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }catch (NullPointerException e){}
        return START_STICKY;
    }

    private void startUpPaths() {
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
                    .setContentTitle("산책하신 길을 저장하는 중이에요")
                    .setContentText("0%")
                    .setContentIntent(pendingIntent);

            //로딩 표시
            final int[] PROGRESS_MAX = {interestPoint.size()};
            final int[] PROGRESS_CURRENT = {0};
            notification.setProgress(PROGRESS_MAX[0], PROGRESS_CURRENT[0], false);
            mNotificationManager.notify(1, notification.build());
            startForeground(1, notification.build());

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(PROGRESS_CURRENT[0]<PROGRESS_MAX[0]){
                        FirebaseDatabase dbInstance = FirebaseDatabase.getInstance();
                        DatabaseReference db = dbInstance.getReference().child("mapData").child("hotSpot");
                        String hash = String.valueOf(Timestamp.now().getNanoseconds());
                        int percentage = Math.round((PROGRESS_CURRENT[0]/PROGRESS_MAX[0]-1)*100);

                        db.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                try {
                                    if(PROGRESS_CURRENT[0]>=PROGRESS_MAX[0]) return;
                                    if (snapshot.getChildrenCount() > 0) {
                                        int cnt = 0;
                                        for (DataSnapshot d : snapshot.getChildren()) {
                                            HashMap<String, Double> value = (HashMap<String, Double>) d.getValue();
                                            float[] distance = new float[1];
                                            Location.distanceBetween(value.get("latitude"), value.get("longitude"),
                                                    interestPoint.get(PROGRESS_CURRENT[0]).latitude, interestPoint.get(PROGRESS_CURRENT[0]).longitude, distance);
                                            if (distance[0] < 75) {
                                                //장소 업데이트
                                                LatLng latLng = new LatLng((value.get("latitude") + interestPoint.get(PROGRESS_CURRENT[0]).latitude) / 2,
                                                        (value.get("longitude") + interestPoint.get(PROGRESS_CURRENT[0]).longitude) / 2);
                                                db.child(d.getKey()).setValue(latLng);
                                                PROGRESS_CURRENT[0]++;
                                                notification.setProgress(PROGRESS_MAX[0], PROGRESS_CURRENT[0], false).setContentText(String.valueOf(percentage));
                                                mNotificationManager.notify(1, notification.build());
                                                Log.wtf("장소업뎃",+distance[0]+", "+d.getKey());
                                                break;
                                            } else {
                                                if(cnt>=snapshot.getChildrenCount()-1) {
                                                    //새 장소 추가
                                                    db.child(String.valueOf(hash.hashCode())).setValue(interestPoint.get(PROGRESS_CURRENT[0]));
                                                    PROGRESS_CURRENT[0]++;
                                                    notification.setProgress(PROGRESS_MAX[0], PROGRESS_CURRENT[0], false).setContentText(String.valueOf(percentage));
                                                    mNotificationManager.notify(1, notification.build());
                                                    Log.wtf("새장소추가", +distance[0] + ", " + String.valueOf(hash.hashCode()));
                                                    break;
                                                }
                                            }
                                            cnt++;
                                        }
                                    } else {
                                        //아무것도 없을때
                                        db.child(String.valueOf(hash.hashCode())).setValue(interestPoint.get(PROGRESS_CURRENT[0]));
                                        PROGRESS_CURRENT[0]++;
                                        notification.setProgress(PROGRESS_MAX[0], PROGRESS_CURRENT[0], false).setContentText(String.valueOf(percentage));
                                        mNotificationManager.notify(1, notification.build());
                                        return;
                                    }
                                }catch(Exception e){
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        if(PROGRESS_CURRENT[0]>=PROGRESS_MAX[0]) break;
                    }

                    notification.setContentTitle("저장을 완료했어요!").setProgress(0, 0, false).setContentText("");
                    mNotificationManager.notify(1, notification.build());
                    stopForeground(Service.STOP_FOREGROUND_DETACH);
                }
            });
            thread.start();
        }
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