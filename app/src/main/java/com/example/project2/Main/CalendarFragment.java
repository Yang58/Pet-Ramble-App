package com.example.project2.Main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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

/*  1.위치정보추가
   2.커뮤니티 공유
   3.시간 알림

 */
public class CalendarFragment extends Fragment {
    public String datename =null;
    public String str=null;
    public CalendarView calendarView;
    public Button cha_Btn,save_Btn,friend_date;
    public TextView diaryTextView,textView2,textView3;
    public EditText contextEditText;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d("Debug", "Running CalendarFragment");
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);
        calendarView=v.findViewById(R.id.calendarView);
        diaryTextView=v.findViewById(R.id.diaryTextView);
        save_Btn=v.findViewById(R.id.save_Btn);
        cha_Btn=v.findViewById(R.id.cha_Btn);
        friend_date=v.findViewById(R.id.friend_date);
        textView2=v.findViewById(R.id.cm_detail_view_txt_like);
        textView3=v.findViewById(R.id.textView3);
        contextEditText=v.findViewById(R.id.contextEditText);

        textView3.setText("산책 일정");

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                diaryTextView.setVisibility(View.VISIBLE);
                save_Btn.setVisibility(View.VISIBLE);
                contextEditText.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.INVISIBLE);
                cha_Btn.setVisibility(View.INVISIBLE);

                contextEditText.setText("");
                diaryTextView.setText(String.format("%d-%d-%d",year,month+1,dayOfMonth));
                checkDay(year,month,dayOfMonth); // checkDay(year,month,dayOfMonth,userID);
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                diaryTextView.setVisibility(View.VISIBLE);
                save_Btn.setVisibility(View.VISIBLE);
                contextEditText.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.INVISIBLE);
                cha_Btn.setVisibility(View.INVISIBLE);


                diaryTextView.setText(String.format("%d-%d-%d",year,month+1,dayOfMonth));
                contextEditText.setText("");
                checkDay(year,month,dayOfMonth); // checkDay(year,month,dayOfMonth,userID);
            }
        });
        return v;
    }

    public void  checkDay(int cYear,int cMonth,int cDay){ // public void  checkDay(int cYear,int cMonth,int cDay,String userID){
        datename = diaryTextView.getText().toString();
        /*
        datename =""+cYear+"-"+(cMonth+1)+""+"-"+cDay+".txt";//저장할 파일 이름설정 // fname=""+userID+cYear+"-"+(cMonth+1)+""+"-"+cDay+".txt";//저장할 파일 이름설정
         */
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        /*
        FileInputStream fis=null;//FileStream fis 변수
         */
        try{
            db.collection("community").document(user.getUid()).collection("calendar").document(datename).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();
                            String calendarcontent = (String) document.get("content");
                            String calendardate = (String) document.get("date");
                            String calendartime = (String) document.get("time");

                            textView2.setText(calendarcontent);
                        }
                    });

            contextEditText.setVisibility(View.INVISIBLE);
            textView2.setVisibility(View.VISIBLE);
            textView2.setText(str);

            save_Btn.setVisibility(View.INVISIBLE);
            friend_date.setVisibility(View.VISIBLE);
            cha_Btn.setVisibility(View.VISIBLE);

            /*
            fis=openFileInput(datename);
            byte[] fileData=new byte[fis.available()];
            fis.read(fileData);
            fis.close();
            str=new String(fileData);
             */

            cha_Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*
                    contextEditText.setVisibility(View.VISIBLE);
                    textView2.setVisibility(View.INVISIBLE);
                    contextEditText.setText(str);

                    save_Btn.setVisibility(View.VISIBLE);
                    cha_Btn.setVisibility(View.INVISIBLE);
                    del_Btn.setVisibility(View.INVISIBLE);
                    textView2.setText(contextEditText.getText());

                     */
                    Bundle bundle = new Bundle();
                    bundle.putString("asdf", datename);
                    getParentFragmentManager().setFragmentResult("key",bundle);

                    DateEditFragment dateEditFragment = new DateEditFragment();
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_schedule,dateEditFragment).commit();

                }
            });


            friend_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("asdf", datename);
                    getParentFragmentManager().setFragmentResult("key",bundle);

                    FriendDateFragment friendDateFragment = new FriendDateFragment();
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_schedule,friendDateFragment).commit();
                }
            });

            if(textView2.getText()==null){
                textView2.setVisibility(View.INVISIBLE);
                diaryTextView.setVisibility(View.VISIBLE);
                save_Btn.setVisibility(View.VISIBLE);
                cha_Btn.setVisibility(View.INVISIBLE);
                contextEditText.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressLint("WrongConstant")
    public void removeDiary(String readDay){
        /*
        FileOutputStream fos=null;
        try{
            fos=openFileOutput(readDay,MODE_NO_LOCALIZED_COLLATORS);
            String content="";
            fos.write((content).getBytes());
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
         */
    }
    @SuppressLint("WrongConstant")
    public void saveDiary(String readDay, CalendarDB calendarDB){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("community").document(user.getUid()).collection("calendar").document(readDay).set(calendarDB)
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
        /*
        FileOutputStream fos=null;

        try{
            fos=openFileOutput(readDay,MODE_NO_LOCALIZED_COLLATORS);
            String content=contextEditText.getText().toString();
            fos.write((content).getBytes());
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        */
    }

}

