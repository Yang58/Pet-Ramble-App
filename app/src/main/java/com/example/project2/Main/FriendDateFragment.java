package com.example.project2.Main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.example.project2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FriendDateFragment extends Fragment {
    private String F_UIDArray[];
    private String F_IDArray[];
    private String datename;
    private TextView friendschedule;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_frienddate, container, false);

        Log.d("Debug", "Running FriendDateFragment");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        getParentFragmentManager().setFragmentResultListener("key",this,new FragmentResultListener(){
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle){
                        datename = bundle.getString("asdf");
                        friendschedule = v.findViewById(R.id.friend_schedule);
                        friendschedule.setText(datename + " 친구들의 일정.");

                        db.collection("community").document(user.getUid()).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        DocumentSnapshot document = task.getResult();

                                        List<String> friendgroup = (List<String>) document.get("friend");
                                        List<String> friendgroup_mail = (List<String>) document.get("friend_mail");

                                        if (friendgroup != null) {
                                            F_UIDArray = friendgroup.toArray(new String[friendgroup.size()]);
                                            Log.d("Debug", String.valueOf(friendgroup.size()));
                                            Log.d("Debug", "친구uid 배열: " + String.valueOf(friendgroup));
                                        }

                                        if (friendgroup_mail != null) {
                                            F_IDArray = friendgroup_mail.toArray(new String[friendgroup_mail.size()]);
                                            Log.d("Debug", String.valueOf(friendgroup_mail.size()));
                                            Log.d("Debug", "친구id 배열: " + String.valueOf(friendgroup_mail));
                                        }

                                        FriendDateAdapter adapter;
                                        adapter = new FriendDateAdapter() ;

                                        // 리스트뷰 참조 및 Adapter달기
                                        ListView listview = (ListView) v.findViewById(R.id.listview_frienddate);

                                        for (int i = 0; i < F_UIDArray.length; i++) {
                                            int finalI = i;
                                            db.collection("community").document(F_UIDArray[i]).collection("calendar").document(datename).get()
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            DocumentSnapshot document = task.getResult();
                                                            String calendarcontent = (String) document.get("content");
                                                            //String calendardate = (String) document.get("date");
                                                            String calendartime = (String) document.get("time");
                                                            Boolean calendarpublic =(Boolean) document.get("public");
                                                            if (calendarcontent != null && calendarpublic == true) {
                                                                Log.d("Debug", calendarcontent);
                                                                listview.setAdapter(adapter);
                                                                adapter.addItem(F_IDArray[finalI],calendartime,calendarcontent);
                                                            } else {
                                                                Log.d("Debug", "null");
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                    }
        });
        //bundle참고로 바꿔야
        /*
        Intent intent = getIntent();
        datename =  intent.getExtras().getString("datename");
         */

        return v;


    }



}




