package com.example.project2.Friend;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2.FirebaseDB.UserInfoDB;
import com.example.project2.FirebaseDB.WalkingDB;
import com.example.project2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class RankingFragment extends Fragment {

    private static String TAG = "RankingFragment";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<WalkingDB> arrayList;
    private ArrayList<UserInfoDB> arrayList1;
    private ArrayList<Integer> rank_list;
    private FirebaseAuth user;
    private FirebaseFirestore db;
    private DatabaseReference databaseReference;
    private View view;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.ranking_fragment, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); // 리사이클러뷰 기존성능 강화
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        arrayList = new ArrayList<>(); // User 객체를 담을 어레이 리스트 (어댑터쪽으로)
        rank_list = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        db.collection("Login_user").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) { // 모든 Login_user 문서 가져오기
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        arrayList.clear();
                        DocumentReference doc = db.collection("Login_user").document(document.getId()).collection("Info").document("Walk");
                        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) { // WalkingDB 가져오기
                                DocumentSnapshot document = task.getResult();
                                if(document != null) {
                                    if (document.exists()) {

                                        Log.d(TAG, "////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
                                        Log.d(TAG, document.getId() + " => " + document.getData());

                                        String name = document.getString("user_nick");
                                        String count = document.getString("walking_Count");
                                        double distance = Double.valueOf(document.getString("walking_Distance")) / 1000;
                                        String time_h = document.getString("walking_Time_h");
                                        String time_m = document.getString("walking_Time_m");

                                        rank_list.add(Integer.valueOf(count));
                                        Collections.sort(rank_list);

                                        arrayList.add(new WalkingDB(name,time_h,time_m,count,String.format("%.1f",distance)));
                                    }else{
                                        // 산책 데이터 없음
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
        adapter = new RankingAdapter(arrayList,getContext());
        recyclerView.setAdapter(adapter);

        return v;
    }
}

