package com.example.project2.Friend;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.project2.FirebaseDB.User;
import com.example.project2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class PeopleFragment extends Fragment {

    private static String TAG = "PeopleFragment";
    /*
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    */
    private ArrayList<User> arrayList = new ArrayList<>(); // User 객체 담을 리스드 (어뎁터 쪽으로 보냄);
    private List<String> friendgroup;
    private List<String> friendgroup_mail;
    private ListView friend_list;
    private EditText friend_mail;
    private Button add_friend;
    private ImageView friend_image;
    private TextView friend_text;
    private String F_UIDArray[];
    private String F_IDArray[];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_people, container, false);
        friend_mail = (EditText) v.findViewById(R.id.friend_mail);
        add_friend = (Button) v.findViewById(R.id.add_friend);
        friend_list = (ListView) v.findViewById(R.id.listview_people);
        /*
        friend_image = (ImageView) v.findViewById(R.id.friend_image);

         */
        friend_text = (TextView) v.findViewById(R.id.friend_text);


        Log.d("Debug", "Running PeopleFragment");
        /*
        recyclerView = v.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        */
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

//        databaseReference = database.getReference(user.getUid()).child("profile");
//        databaseReference = database.getReference("Login_user").child(user.getUid()).child("Info").child("profile");
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference emailRef = db.collection("Login_user");
        Query query = emailRef.whereEqualTo("user_ID", true);


        db.collection("community").document(user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        friendgroup = (List<String>) document.get("friend");
                        friendgroup_mail = (List<String>) document.get("friend_mail");

                        if(friendgroup !=null) {
                            F_UIDArray = friendgroup.toArray(new String[friendgroup.size()]);
                            Log.d("Debug",String.valueOf(friendgroup.size()));
                            Log.d("Debug", "친구uid 배열: " + String.valueOf(friendgroup));
                        }

                        if(friendgroup_mail !=null) {
                            F_IDArray = friendgroup_mail.toArray(new String[friendgroup_mail.size()]);
                            Log.d("Debug",String.valueOf(friendgroup_mail.size()));
                            Log.d("Debug", "친구id 배열: " + String.valueOf(friendgroup_mail));
                        }

                        if(F_IDArray!=null){
                            ListView listview ;
                            FriendListAdapter adapter;

                            // Adapter 생성
                            adapter = new FriendListAdapter() ;

                            // 리스트뷰 참조 및 Adapter달기
                            listview = (ListView) v.findViewById(R.id.listview_people);
                            listview.setAdapter(adapter);

                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference();

                            for(int i=0; i<F_IDArray.length;i++) {
                                Log.d("Debug", "users/" + F_UIDArray[i] + "/profileImage.jpg");
                                storageRef.child("users/" + F_UIDArray[i] + "/profileImage.jpg").getDownloadUrl().addOnSuccessListener(
                                        new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                /*
                                                ImageView friend_image = (ImageView) v.findViewById(R.id.friend_image);
                                                Glide.with(getActivity()).load(uri).into(friend_image);

                                                 */
                                            }
                                        });
                                adapter.addItem(friend_image, F_IDArray[i]);
                            }

                            /*
                            for(int i=0; i<F_IDArray.length;i++) {
                                int finalI = i;

                                if(F_UIDArray[i]!=null) {
                                    Log.d("Debug", "users/" + F_UIDArray[i] + "/profileImage.jpg");
                                    storageRef.child("users/" + F_UIDArray[i] + "/profileImage.jpg").getDownloadUrl().addOnSuccessListener(
                                            new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    Glide.with(getActivity())
                                                            .load(uri)
                                                            .into(friend_image);
                                                }
                                            });
                                }
                                adapter.addItem(friend_image, F_IDArray[finalI]);
                            }

                             */

                            /*
                            ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.listview_people,F_IDArray);
                            friend_list.setAdapter(adapter);

                            Log.d("Debug", "Summon array");
                             */
                        }
                    }
                });

        friend_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String ID = F_IDArray[i];
                Log.d("Debug","Clicked_id=" + ID);

                Bundle bundle = new Bundle();
                bundle.putString("ID_Clicked", ID);
                getParentFragmentManager().setFragmentResult("key",bundle);

                ProfileFragment profileFragment = new ProfileFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.friend_frameLayout, profileFragment).commit();
            }
        });

        //DocumentReference emailReference =  db.collection("Login_user").document("user_ID");
        add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Debug", friend_mail.getText().toString());
                db.collection("Login_user").whereEqualTo("user_ID", friend_mail.getText().toString()).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("Debug", document.getId() + " => " + document.getData());
                                        Toast.makeText(getActivity(), "회원 정보 저장중... ", Toast.LENGTH_SHORT).show();
                                        String newfriend =
                                                document.get("user_UID").toString();
                                        String newfriend_mail =
                                                document.get("user_ID").toString();
                                        db.collection("community").document(user.getUid())
                                                .update("friend", FieldValue.arrayUnion(newfriend))
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "성공");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error", e);
                                                    }
                                                });
                                        db.collection("community").document(user.getUid())
                                                .update("friend_mail", FieldValue.arrayUnion(newfriend_mail))
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "성공");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error", e);
                                                    }
                                                });
                                    }
                                } else {
                                    Log.d("Debug", "Error Getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });

        /*
                DocumentReference UserInfo = firestoreDatabase.collection("Login_user").document("2jXnAr0uCiag40q1jv1TjCmHK9o2");
        UserInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                friend_id = value.getString("user_ID");
                Log.d("Debug", friend_id);
                friend_pw = value.getString("user_PW");
                Log.d("Debug", friend_pw);
                friend_uid = value.getString("user_UID");
                Log.d("Debug", friend_uid);
            }
        });
         */
        /*
        adapter = new CustomAdapter(arrayList, getContext().getApplicationContext()); // 수정
        recyclerView.setAdapter(adapter);
        */
         /*
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                            Snapshot document = task.getResult();
                                            if (task.isSuccessful()) {
                                                Log.d("Debug", document.getId() + " => " + document.getData());

                                                F_IDArray[finalI] = (String) document.get("user_ID");
                                                Log.d("Debug","F_ID=" + F_IDArray[finalI]);
                                                Log.d("Debug","F_UID=" + F_UIDArray[finalI]);
                                            } else {
                                                Log.d("Debug", "Error Getting documents: ", task.getException());
                                            }
                                        }
                                        */
        return v;
    }
}

