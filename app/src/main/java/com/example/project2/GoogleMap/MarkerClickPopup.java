package com.example.project2.GoogleMap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.project2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

public class MarkerClickPopup extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "MarkerClickPopup";

    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    private Button btnexit;
    private TextView like;
    private TextView likecount;

    private TextView NickName;
    private TextView PetName;
    private TextView PetAge;
    private TextView PetKind;


    public static final String TAG_EVENT_DIALOG = "dialog_event";

    public MarkerClickPopup() {}

    public static MarkerClickPopup getInstance(){

        MarkerClickPopup m = new MarkerClickPopup();
        return m;

    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){

        final View v = inflater.inflate(R.layout.activity_marker_click_popup,container);

        NickName = v.findViewById(R.id.TV_Nickname);
        PetName = v.findViewById(R.id.TV_PetName);
        PetAge = v.findViewById(R.id.TV_PetAge);
        PetKind = v.findViewById(R.id.TV_PetKind);
               //addListenerForSingleValueEvent
//        mRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                DataSnapshot name =snapshot.child("name");
//                Toast.makeText(getContext().getApplicationContext(),"dfjalskdjfljasd"+ name,Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

//        Ref.child().addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
//                for(DataSnapshot snapshot : datasnapshot.getChildren()){
//                    Log.d("DDD", String.valueOf(docRef.getClass()));
//                    Userinfo user = snapshot.getValue(Userinfo.class);
//
//                    Log.d("DDD", String.valueOf(docRef.getClass()));
//
//                    Log.d("DDD", user.getName());
//
//                }
//
//            }

//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        btnexit = (Button)v.findViewById(R.id.btn_exit);
        btnexit.setOnClickListener(this);

        like = (TextView)v.findViewById(R.id.Like_count);

        likecount = (TextView)v.findViewById(R.id.TV_like);
        likecount.setOnClickListener(new View.OnClickListener(){
            int count = 0;
            @Override
            public void onClick(View v){
                ++count;
                like.setText(" "+ count);

            }
        });

        setCancelable(true); // 화면 밖에 터치시 화면이 꺼지지 않게 하기 위함

        return v;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
    public void getUser (String uID){

        mRef.child("users").child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
