package com.example.project2.Main;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import com.example.project2.FirebaseDB.CalendarDB;
import com.example.project2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class DateEditFragment extends Fragment {
    public TimePicker timepicker;
    public TextView title;
    private String datename;
    private String timename;
    private Button save_btn, savealarm_btn,locate_btn;
    private EditText edit_schedule;
    private RadioGroup Personal_check;
    private RadioButton personal_t,personal_f;
    private Boolean Personal = true;
    private Location savepoint;



    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dateedit, container, false);
        Log.d("Debug","Running DateEditFragment.");
        final Intent my_intent = new Intent(getContext(), AlarmReceiver.class);
        personal_t = v.findViewById(R.id.personal_t);
        personal_f = v.findViewById(R.id.personal_f);
        timepicker= v.findViewById(R.id.timepick);
        title = v.findViewById(R.id.edit_title);
        save_btn = v.findViewById(R.id.schedule_sav);
        savealarm_btn = v.findViewById(R.id.schedule_alarm);
        locate_btn = v.findViewById(R.id.schedule_locate);
        edit_schedule = v.findViewById(R.id.schedule_edit);
        Personal_check = v.findViewById(R.id.personal);
        timename = String.format(timepicker.getHour()+":"+ timepicker.getMinute());

        getParentFragmentManager().setFragmentResultListener("key",this,new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                        datename = bundle.getString("asdf");
                        title.setText(datename + " 일정 수정");
                        setcontent(datename);
                    }
        });




        timepicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(@NonNull TimePicker timePicker, int hour, int min) {
                timename = String.format("%d:%d",hour,min);
                Log.d("Debug", timename);
            }
        });

        Personal_check.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.personal_t){
                    Personal = true;

                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("daily alarm", MODE_PRIVATE);    // test 이름의 기본모드 설정, 만약 test key값이 있다면 해당 값을 불러옴.
                    Long inputTime = sharedPreferences.getLong("NextNotifyTime", 0);
                    String inputText = sharedPreferences.getString("NextNotifyContent","");

                    Date date1 = new Date(inputTime);

                    String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 a hh시 mm분 ", Locale.getDefault()).format(date1);
                    Log.d("Debug",inputText+ " " + date_text);

                }
                else{
                    Personal = false;
                }
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                CalendarDB calendarDB = new CalendarDB(datename,timename,edit_schedule.getText().toString(),Personal,savepoint);
                db.collection("community").document(user.getUid()).collection("calendar").document(datename).set(calendarDB)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Debug", "성공");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Debug", "Error", e);
                            }
                        });


                Toast.makeText(getActivity(),"수정 완료",Toast.LENGTH_LONG).show();

                CalendarFragment calendarFragment = new CalendarFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_schedule,calendarFragment).commit();

            }
        });

        locate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), LocateActivity.class);
                intent.putExtra("datename",datename);
                intent.putExtra("timename",timename);
                intent.putExtra("Content",edit_schedule.getText().toString());
                intent.putExtra("Personal",Personal);
                startActivity(intent);
            }
        });

        savealarm_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                CalendarDB calendarDB = new CalendarDB(datename,timename,edit_schedule.getText().toString(),Personal,savepoint);
                db.collection("community").document(user.getUid()).collection("calendar").document(datename).set(calendarDB)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Debug", "성공");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Debug", "Error", e);
                            }
                        });


                AlarmSet();

                Toast.makeText(getActivity(),"수정 및 알람등록 완료",Toast.LENGTH_LONG).show();

                CalendarFragment calendarFragment = new CalendarFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_schedule,calendarFragment).commit();
            }
        });

    return v;
}


    void setcontent(String date){
        try {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            db.collection("community").document(user.getUid()).collection("calendar").document(date).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();
                            String calendarcontent = (String) document.get("content");
                            String timecontent = (String) document.get("time");
                            edit_schedule.setText(calendarcontent);
                            if( timecontent!=null) {
                                String time[] = timecontent.split(":");
                                timepicker.setHour(Integer.parseInt(time[0]));
                                timepicker.setMinute(Integer.parseInt(time[1]));
                            }
                            Personal = (Boolean) document.get("public");
                            try {
                                if (Personal.equals(true)) {
                                    Log.d("Debug", "true");
                                    personal_t.setChecked(true);
                                } else {
                                    Log.d("Debug", "false");
                                    personal_f.setChecked(true);
                                }
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                            savepoint = (Location) document.get("savepoint");
                            //timename = String.format("%d:%d",time[0],time[1]);
                        }
                    });
        } catch (Exception e){
            e.printStackTrace();
            Log.d("Debug", "null!");
        }

    }

    @SuppressLint("NewApi")
    void AlarmSet(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        String Date[] = datename.split("-");
        calendar.set(Calendar.YEAR, Integer.parseInt(Date[0]));
        calendar.set(Calendar.MONTH,Integer.parseInt(Date[1])-1);
        calendar.set(Calendar.DATE,Integer.parseInt(Date[2]));
        calendar.set(Calendar.HOUR_OF_DAY,timepicker.getHour());
        calendar.set(Calendar.MINUTE,timepicker.getMinute());

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("daily alarm", MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();

        editor.putLong("NextNotifyTime", (long)calendar.getTimeInMillis());
        editor.putString("NextNotifyContent",edit_schedule.getText().toString());

        editor.apply();

        diaryNotification(calendar);
    }

    void diaryNotification(Calendar calendar)
    {
//        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//        Boolean dailyNotify = sharedPref.getBoolean(SettingsActivity.KEY_PREF_DAILY_NOTIFICATION, true);
        Boolean dailyNotify = true; // 무조건 알람을 사용

        PackageManager pm = getActivity().getPackageManager();
        ComponentName receiver = new ComponentName(getContext(), DeviceBootReceiver.class);
        Intent alarmIntent = new Intent(getContext(), AlarmReceiver.class);

        Date currentDateTime = calendar.getTime();
        String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 a hh시 mm분 ", Locale.getDefault()).format(currentDateTime);
        Toast.makeText(getActivity(),date_text + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();

        alarmIntent.putExtra("sendDate",datename + " " + timename);
        alarmIntent.putExtra("sendContent", edit_schedule.getText().toString());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

        // 사용자가 매일 알람을 허용했다면
        if (dailyNotify) {
            if (alarmManager != null) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                         pendingIntent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }

            // 부팅 후 실행되는 리시버 사용가능하게 설정
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }
//        else { //Disable Daily Notifications
//            if (PendingIntent.getBroadcast(this, 0, alarmIntent, 0) != null && alarmManager != null) {
//                alarmManager.cancel(pendingIntent);
//                //Toast.makeText(this,"Notifications were disabled",Toast.LENGTH_SHORT).show();
//            }
//            pm.setComponentEnabledSetting(receiver,
//                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                    PackageManager.DONT_KILL_APP);
//        }
    }


}


