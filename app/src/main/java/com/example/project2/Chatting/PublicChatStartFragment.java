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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.project2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class PublicChatStartFragment extends Fragment {

    private EditText user_chat;
    private String user_name;
    private Button user_next;
    private ListView chat_list;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    FirebaseFirestore firestoreDatabase = FirebaseFirestore.getInstance();

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_publicchatstart, container, false);
        Log.d("Debug", "Running StartActivity");

        user_chat = (EditText) v.findViewById(R.id.user_chat);
        user_next = (Button) v.findViewById(R.id.user_next);
        chat_list = (ListView) v.findViewById(R.id.chat_list);

        DocumentReference UserInfo = firestoreDatabase.collection("Login_user").document(user.getUid()).collection("Info").document("UserInfo");
        UserInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                user_name = value.getString("user_name");
                Log.d("Debug", user_name);
                TextView welcome_message = (TextView) v.findViewById(R.id.welcome);
                welcome_message.setText("환영합니다 " + user_name + "님.");
            }
        });

        user_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Debug", "Chatname:" + user_chat.getText().toString());
                Log.d("Debug", "Username:" + user_name);

                if (user_chat.getText().toString().equals("") )
                    return;

                Bundle bundle1 = new Bundle();
                bundle1.putString("chatName", user_chat.getText().toString());
                bundle1.putString("userName", user_name);
                getParentFragmentManager().setFragmentResult("key",bundle1);


                PublicChattingFragment publicChattingFragment = new PublicChattingFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.friend_frameLayout, publicChattingFragment).commit();

                /*
                Intent intent = new Intent(getActivity(), PublicChattingFragment.class);
                intent.putExtra("chatName", user_chat.getText().toString());
                intent.putExtra("userName", user_name);
                startActivity(intent);
                 */
            }
        });
        chat_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String value=((TextView)view).getText().toString();
                Log.d("Debug:",value);

                Bundle bundle1 = new Bundle();
                bundle1.putString("chatName", value);
                bundle1.putString("userName", user_name);
                getParentFragmentManager().setFragmentResult("key",bundle1);

                PublicChattingFragment publicChattingFragment = new PublicChattingFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.friend_frameLayout, publicChattingFragment).commit();
            }
        });
        showChatList();
    return v;
}


    private void showChatList() {
        // 리스트 어댑터 생성 및 세팅
        final ArrayAdapter<String> adapter

                = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1);
        chat_list.setAdapter(adapter);

        // 데이터 받아오기 및 어댑터 데이터 추가 및 삭제 등..리스너 관리
        databaseReference.child("publicchat").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("LOG", "dataSnapshot.getKey() : " + dataSnapshot.getKey());
                adapter.add(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

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
