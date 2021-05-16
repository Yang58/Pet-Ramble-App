package com.example.project2.Main;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import com.example.project2.Community.DB.user;
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

public class DateEditFragment extends Fragment {
    public TimePicker timepicker;
    public TextView title;
    private String datename;
    private String timename;
    private Button save_btn;
    private EditText edit_schedule;
    private RadioGroup Personal_check;
    private RadioButton personal_t,personal_f;
    private Boolean Personal;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dateedit, container, false);
        Log.d("Debug","Running DateEditFragment.");
        personal_t = v.findViewById(R.id.personal_t);
        personal_f = v.findViewById(R.id.personal_f);
        timepicker= v.findViewById(R.id.timepick);
        title = v.findViewById(R.id.edit_title);
        save_btn = v.findViewById(R.id.schedule_sav);
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

                CalendarDB calendarDB = new CalendarDB(datename,timename,edit_schedule.getText().toString(),Personal);
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

                CalendarFragment calendarFragment = new CalendarFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_schedule,calendarFragment).commit();

                Toast.makeText(getActivity(),"수정 완료",Toast.LENGTH_LONG);

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

                            if (Personal.equals(true)) {
                                Log.d("Debug", "true");
                                personal_t.setChecked(true);
                            } else {
                                Log.d("Debug", "false");
                                personal_f.setChecked(true);
                            }
                            //timename = String.format("%d:%d",time[0],time[1]);
                        }
                    });
        } catch (Exception e){
            e.printStackTrace();
            Log.d("Debug", "null!");
        }

    }
}
