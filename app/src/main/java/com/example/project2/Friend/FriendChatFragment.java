package com.example.project2.Friend;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.bumptech.glide.Glide;
import com.example.project2.FirebaseDB.ChatDTO;
import com.example.project2.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Arrays;


public class FriendChatFragment extends Fragment {
    private String FRIEND_ID;
    private String FRIEND_UID;
    private String USER_UID;
    private String ROOM_ID;
    private String CHAT_NAME;
    private String USER_NAME;

    private ListView chat_view;
    private EditText chat_edit;
    private TextView friend_name;
    private Button chat_send;
    private ImageView friend_image;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    FirebaseFirestore firestoreDatabase = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friendchat, container, false);

        chat_view = (ListView) v.findViewById(R.id.listview_chat);
        chat_edit = (EditText) v.findViewById(R.id.chat_edit);
        chat_send = (Button) v.findViewById(R.id.chat_sent);

        friend_image = (ImageView) v.findViewById(R.id.friendchat_imageview);
        friend_name = (TextView) v.findViewById(R.id.friendchat_friendname);

        Log.d("Debug","Running FriendChatFragment");

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        getParentFragmentManager().setFragmentResultListener("Chatfriend",this,new FragmentResultListener(){
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                FRIEND_UID = bundle.getString("friend_uid");
                FRIEND_ID = bundle.getString("friend_id");
                Log.d("Debug","FRIEND_UID= " + FRIEND_UID);
                USER_UID = user.getUid();

                friend_name.setText(FRIEND_ID);

                //RID 산출과정.
                String[] Room_users = new String[]{USER_UID, FRIEND_UID};
                Arrays.sort(Room_users);
                ROOM_ID = Room_users[0] + Room_users[1];

                Log.d("Debug","ROOMID=" + ROOM_ID);

                Log.d("Debug", "users/" + FRIEND_UID + "/profileImage.jpg");

                storageRef.child("users/" + FRIEND_UID + "/profileImage.jpg").getDownloadUrl().addOnSuccessListener(
                        new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(getActivity()).load(uri).into(friend_image);
                            }
                        });

                CHAT_NAME = ROOM_ID;
                USER_NAME = user.getEmail();

//                openChat(CHAT_NAME);


                // 메시지 전송 버튼에 대한 클릭 리스너 지정
                chat_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (chat_edit.getText().toString().equals(""))
                            return;
                        ChatDTO chat = new ChatDTO(USER_NAME, chat_edit.getText().toString(),null ,Timestamp.now().getSeconds()); //ChatDTO를 이용하여 데이터를 묶는다.
                        databaseReference.child("friendchat").child(CHAT_NAME).push().setValue(chat); // 데이터 푸쉬
                        chat_edit.setText(""); //입력창 초기화
                    }
                });
            }
        });

        return v;
    }

    private void addMessage(DataSnapshot dataSnapshot, ArrayAdapter<String> adapter) {
        ChatDTO chatDTO = dataSnapshot.getValue(ChatDTO.class);
        long time = chatDTO.getTimestamp()*1000;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        if(chatDTO.getPhotourl() == null) {
            adapter.add(sdf.format(time) + " " + chatDTO.getUserName() + " : " + chatDTO.getMessage());
        } else{

        }
    }

    private void removeMessage(DataSnapshot dataSnapshot, ArrayAdapter<String> adapter) {
        ChatDTO chatDTO = dataSnapshot.getValue(ChatDTO.class);
        long time = chatDTO.getTimestamp()*1000;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        adapter.remove(sdf.format(time)+" " + chatDTO.getUserName() + " : " + chatDTO.getMessage());
    }

//    private void openChat(String chatName) {
//        // 리스트 어댑터 생성 및 세팅
//        final ArrayAdapter<String> adapter
//                = new ArrayAdapter<String>(getActivity(), R.id.listview_chat, R.id.chat_text);
//        chat_view.setAdapter(adapter);
//
//        // 데이터 받아오기 및 어댑터 데이터 추가 및 삭제 등..리스너 관리
//        databaseReference.child("friendchat").child(chatName).addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                addMessage(dataSnapshot, adapter);
//                Log.e("LOG", "s:"+s);
//            }
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//            }
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                removeMessage(dataSnapshot, adapter);
//            }
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

}


