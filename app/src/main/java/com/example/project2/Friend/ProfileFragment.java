package com.example.project2.Friend;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.project2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/*To-Do
1.커뮤니티에서 친구추가 가능하게.
2.친구화면에서 프로필 사진 출력가능.
3.이메일 클릭으로 이메일 외 정보 참조. (일단 프로필 까진 나옴.)
4.정보 공개/비공개 여부 설정?(필요 할려나?)
5.친구 신청 수락/거절?
6.공개채팅에서 채팅장 이름 터치로 채팅창 입장가능.
7.공개채팅 프래그먼트로.
 */

public class ProfileFragment extends Fragment {
    private String result;
    private String target_uid;

    private static String TAG = "ProfileFragment";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        Button btn_back = (Button) v.findViewById(R.id.btn_Back);
        Button btn_chat = (Button) v.findViewById(R.id.btn_Chat);

        getParentFragmentManager().setFragmentResultListener("key",this,new FragmentResultListener(){
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                result = bundle.getString("ID_Clicked");
                TextView profile_welcome = (TextView) v.findViewById(R.id.profile_welcome);
                profile_welcome.setText(result + "님의 정보");
                Log.d("Debug","result=" + result);

                db.collection("Login_user").whereEqualTo("user_ID", result).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        target_uid = (String) document.get("user_UID");
                                        Log.d("Debug","targetuid=" + target_uid);

                                        Bundle chatfriend = new Bundle();
                                        chatfriend.putString("Chatfriend", target_uid);
                                        getParentFragmentManager().setFragmentResult("FriendUID",chatfriend);

                                        db.collection("Login_user").document(target_uid).collection("Info").document("UserInfo").get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        DocumentSnapshot document = task.getResult();
                                                        TextView profile_name = (TextView) v.findViewById(R.id.profile_name);
                                                        profile_name.setText((String) document.get("user_name"));
                                                        Log.d("Debug","1="+(String) document.get("user_name"));
                                                        TextView profile_phoneNum = (TextView) v.findViewById(R.id.profile_phoneNum);
                                                        profile_phoneNum.setText((String) document.get("user_phoneNumber"));
                                                        Log.d("Debug","2="+(String) document.get("user_phoneNumber"));
                                                        TextView profile_Nickname =(TextView) v.findViewById(R.id.profile_Nickname);
                                                        profile_Nickname.setText((String) document.get("user_nickname"));
                                                        Log.d("Debug", "3="+(String) document.get("user_nickname"));


                                                        /*
                                                        asdf = (String) document.get("user_name");
                                                        asdf = (String) document.get("user_profile");
                                                         */
                                                    }
                                                });
                                        db.collection("Login_user").document(target_uid).collection("Info").document("PetInfo").get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        DocumentSnapshot document = task.getResult();
                                                        TextView profile_petName = (TextView) v.findViewById(R.id.profile_petName);
                                                        profile_petName.setText((String) document.get("petName"));
                                                        TextView profile_petAge = (TextView) v.findViewById(R.id.profile_petAge);
                                                        profile_petAge.setText((String) document.get("petAge"));
                                                        TextView profile_petKind = (TextView) v.findViewById(R.id.profile_petKind);
                                                        profile_petKind.setText((String) document.get("petKind"));
                                                        TextView profile_petWeight = (TextView) v.findViewById(R.id.profile_petWeight);
                                                        profile_petWeight.setText((String) document.get("petWeight"));
                                                        TextView profile_petBirthday = (TextView) v.findViewById(R.id.profile_petBirthday);
                                                        profile_petBirthday.setText((String) document.get("petBrithday"));

                                                        /*
                                                        asdf = (String) document.get("petGender");
                                                        asdf = (String) document.get("petNeutralization");
                                                        asdf = (String) document.get("petVaccaination");
                                                         */
                                                    }
                                                });
                                        FirebaseStorage storage = FirebaseStorage.getInstance();
                                        StorageReference storageRef = storage.getReference();
                                        storageRef.child("users/"+ target_uid +"/profileImage.jpg").getDownloadUrl().addOnSuccessListener(
                                                new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        ImageView profile_imageView = (ImageView) v.findViewById(R.id.profile_imageView);
                                                        Glide.with(getActivity())
                                                                .load(uri)
                                                                .into(profile_imageView);

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


        btn_back.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                PeopleFragment peopleFragment = new PeopleFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.friend_frameLayout, peopleFragment).commit();
            }
        });

        btn_chat.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ID = result;
                FriendChatFragment friendChatFragment = new FriendChatFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.friend_frameLayout, friendChatFragment).commit();

                Log.d("Debug","sending " + result);



            }
        });



        /*
        DocumentReference UserInfo = db.collection("Login_user").document("").collection("Info").document("UserInfo");
        UserInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

            }
        });
        */

        //profileImage추가.




        return v;
    }
}