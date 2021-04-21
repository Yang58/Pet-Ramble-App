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

import com.example.project2.FirebaseDB.User;
import com.example.project2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PeopleFragment extends Fragment {

    private static String TAG = "PeopleFragment";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<User> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_people,container,false);

        recyclerView = v.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); // User 객체 담을 리스드 (어뎁터 쪽으로 보냄)


        database = FirebaseDatabase.getInstance();
        FirebaseUser  user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = database.getReference(user.getUid());
//        databaseReference = database.getReference(user.getUid()).child("profile");
//        databaseReference = database.getReference("Login_user").child(user.getUid()).child("Info").child("profile");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                // 데이터 베이스에서 데이터 받아오는 부분
                arrayList.clear(); // 수정
                for(DataSnapshot snapshot : datasnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    arrayList.add(user);

                    Log.i("FriendList","log_test 아아아아"+ user.getPhotoUrl()+"아아아아"+user.getName()+"아아아아아"+user.getpetAge()+"아아아아아"+user.getpetKind());
//                    User user = snapshot.getValue(User.class);
//                    arrayList.add(user);
//                    Log.i("FriendList","log_test아아아아"+ user.getPhotoUrl()+"아아아아"+user.getName()+"아아아아아"+user.getpetAge()+"아아아아아"+user.getpetName());
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PeopleFragment", String.valueOf(error.toException()));
            }
        });

//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        DocumentReference docRef = db.collection("Login_user").document(user.getUid()).collection("Info").document("profile");
//        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//
//                UserProfileDB user = new UserProfileDB(value.getString("profile_Uri"),value.getString("profile_petName"),value.getString("profile_petKind"),value.getString("profile_petAge"));
//                profileDB.add(user);
//            }
//        });


        adapter = new CustomAdapter(arrayList, getContext().getApplicationContext()); // 수정
        recyclerView.setAdapter(adapter);

        return v;
    }
}
