package com.example.project2.Chatting;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import com.example.project2.FirebaseDB.ChatDTO;
import com.example.project2.Friend.ProfileFragment;
import com.example.project2.R;
import com.google.firebase.Timestamp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;


public class PublicChattingFragment extends Fragment{

    private String CHAT_NAME;
    private String USER_NAME;

    private ListView chat_view;
    private EditText chat_edit;
    private Button chat_send;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_publicchatting, container, false);
        Log.d("Debug", "Running StartActivity");

        chat_view = (ListView) v.findViewById(R.id.chat_view);
        chat_edit = (EditText) v.findViewById(R.id.chat_edit);
        chat_send = (Button) v.findViewById(R.id.chat_sent);
        getParentFragmentManager().setFragmentResultListener("key",this,new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                CHAT_NAME = bundle.getString("chatName");
                USER_NAME = bundle.getString("userName");
                Log.d("Debug", CHAT_NAME);
                Log.d("Debug", USER_NAME);
                //USER_NAME ="asdf";
                // 채팅 방 입장
                openChat(CHAT_NAME);

                chat_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (chat_edit.getText().toString().equals(""))
                            return;
                        /*
                        ChatDTO chat = new ChatDTO(USER_NAME, chat_edit.getText().toString(), Timestamp.now().getSeconds()); //ChatDTO를 이용하여 데이터를 묶는다.
                        databaseReference.child("publicchat").child(CHAT_NAME).push().setValue(chat); // 데이터 푸쉬
                        chat_edit.setText(""); //입력창 초기화

                         */
                    }
                });
            }
        });

        /*
        // 로그인 화면에서 받아온 채팅방 이름, 유저 이름 저장
        Intent intent = getIntent();
        CHAT_NAME = intent.getStringExtra("chatName");
        USER_NAME = intent.getStringExtra("userName");
         */
        return v;
    }

    private void addMessage(DataSnapshot dataSnapshot, ArrayAdapter<String> adapter) {
        ChatDTO chatDTO = dataSnapshot.getValue(ChatDTO.class);
        long time = chatDTO.getTimestamp()*1000;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        adapter.add(sdf.format(time) + " " + chatDTO.getUserName() + " : " + chatDTO.getMessage());
    }

    private void removeMessage(DataSnapshot dataSnapshot, ArrayAdapter<String> adapter) {
        ChatDTO chatDTO = dataSnapshot.getValue(ChatDTO.class);
        long time = chatDTO.getTimestamp()*1000;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        adapter.remove(sdf.format(time)+" " + chatDTO.getUserName() + " : " + chatDTO.getMessage());
    }

    private void openChat(String chatName) {
        // 리스트 어댑터 생성 및 세팅
        final ArrayAdapter<String> adapter

                = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1);
        chat_view.setAdapter(adapter);

        // 데이터 받아오기 및 어댑터 데이터 추가 및 삭제 등..리스너 관리
        databaseReference.child("publicchat").child(chatName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                addMessage(dataSnapshot, adapter);
                Log.e("LOG", "s:"+s);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                removeMessage(dataSnapshot, adapter);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}