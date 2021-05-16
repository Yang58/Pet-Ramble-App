package com.example.project2.GoogleMap;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.project2.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

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

    private ImageView profile;

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

        profile = v.findViewById(R.id.Map_Userprofile);

        Bundle bundle = getArguments();
        NickName.setText(bundle.getString("name"));
        PetName.setText(bundle.getString("petName"));
        PetAge.setText(bundle.getString("petAge")+"살");
        PetKind.setText(bundle.getString("petKind"));
        Glide.with(getContext()).load(bundle.getString("photoUrl")).apply(new RequestOptions().circleCrop()).into(profile);

        File file = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/profile_img");
        if (!file.isDirectory()) {
            //디렉토리가 없으면, 디렉토리를 만든다.
            file.mkdir();
        }

        // 이미지뷰 원 형태로 변경
//        profile.setBackground(new ShapeDrawable(new OvalShape()));
//        if(Build.VERSION.SDK_INT >= 21) {
//            profile.setClipToOutline(true);
//        }

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

}
