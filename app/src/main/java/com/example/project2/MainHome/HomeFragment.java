package com.example.project2.MainHome;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.project2.Community.ui.main.CommunityMain;
import com.example.project2.Friend.FriendFragment;
import com.example.project2.GoogleMap.MapsFragment;
import com.example.project2.Main.MainActivity;
import com.example.project2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class HomeFragment extends Fragment {

    static final String TAG = "HomeFragment";

    ImageView petImage;
    TextView petname ;
    TextView petage ;
    TextView petkind ;

    TextView home_text;
    TextView petweight;
    Spinner Option_Spinner;
    EditText Edit_Feed_kcal;

    TextView home_Day_kcal;
    TextView home_Day_feed;
    TextView home_Walking_time;

    Button home_btn_Check;


    TextView c;
    TextView d;
    TextView h;
    TextView m;

    Button btnCamera;

    FragmentManager FM;

    private CommunityMain fragment_Community;
    private MapsFragment fragmentMap;
    private FriendFragment friendFragment;

    MainActivity activity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //이 메소드가 호출될떄는 프래그먼트가 엑티비티위에 올라와있는거니깐 getActivity메소드로 엑티비티참조가능
        activity = (MainActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //이제 더이상 엑티비티 참초가안됨
        activity = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //프래그먼트 메인을 인플레이트해주고 컨테이너에 붙여달라는 뜻임
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.home_fragment , container, false);

        petname = v.findViewById(R.id.MPN);
        petage = v.findViewById(R.id.MPA);
        petkind = v.findViewById(R.id.MPK);
        petImage = v.findViewById(R.id.home_img);

        home_text = v.findViewById(R.id.home_text);
        petweight = v.findViewById(R.id.home_text_weight);
        Edit_Feed_kcal = v.findViewById(R.id.home_Edit_feed_kcal);
        home_Day_kcal = v.findViewById(R.id.home_text_kcal);
        home_Day_feed = v.findViewById(R.id.home_text_feed);
        home_Walking_time = v.findViewById(R.id.home_text_walking_time);

        c = v.findViewById(R.id.tv_c);
        d = v.findViewById(R.id.tv_d);
        h = v.findViewById(R.id.tv_h);
        m = v.findViewById(R.id.tv_m);

        Edit_Feed_kcal.setInputType(EditorInfo.TYPE_NULL);
        Edit_Feed_kcal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((EditText)view).setInputType(EditorInfo.TYPE_CLASS_TEXT);
            }
        });
        Edit_Feed_kcal.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String inText = textView.getText().toString();
                // Do Something...
                textView.setInputType(EditorInfo.TYPE_NULL);
                return true;
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore FBdb = FirebaseFirestore.getInstance();

        Option_Spinner = v.findViewById(R.id.home_spinner);
        Option_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                double Option = 0;
                if(position == 0){
                }else if(position == 1){
                    Option = 3;//         이유식 ~ 4개월령
                }else if(position == 2){
                    Option = 2;//         4개월령 ~ 성견
                }else if(position == 3){
                    Option = 1.8;//       중성화하지 않은 경우
                }else if(position == 4){
                    Option = 1.6;//       중성화한 경우
                }else if(position == 5){
                    Option = 1.4;//       과체중
                }else if(position == 6){
                    Option = 1;//         비만
                }else if(position == 7){
                    Option = 1.8;//       임신 전분 42일간
                }else if(position == 8){
                    Option = 3.1;//         임신 후반 21일간
                }else if(position == 9){
                    Option = 1.4;//       수유기간
                }else if(position == 10){
                    Option = 1.4;//       노령견
                }
                home_btn_Check = v.findViewById(R.id.home_button_check);
                double finalOption = Option;
                home_btn_Check.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (position == 0){
                            Toast.makeText(getContext().getApplicationContext(), "옵션을 선택해주세요", Toast.LENGTH_SHORT).show();
                            home_Day_kcal.setText(" 00 kcal ");
                            home_Day_feed.setText(" 00 g ");
                        }else{
                            if (Edit_Feed_kcal.length() == 0){
                                // 옵션만 선택 했을 때 사료 칼로리  입력 X
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                DocumentReference documentReference = db.collection("Login_user").document(user.getUid()).collection("Info").document("PetInfo");
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document != null) {
                                            if (document.exists()) {
                                                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        String v = documentSnapshot.getString("petVaccination");
                                                        if(v.equals("예")) {
                                                            if(finalOption == 3){
                                                                home_Walking_time.setText("산책 금지");
                                                            }else{
                                                                home_Walking_time.setText("30~60");
                                                            }
                                                        }else{
                                                            home_Walking_time.setText("산책 금지");
                                                        }
                                                        String weight = documentSnapshot.getString("petWeight");
                                                        double standard_kcal = 70 * Math.pow( Double.valueOf(weight) , 0.75) * finalOption;
                                                        home_Day_kcal.setText(String.format("%.2f",standard_kcal)+ " kcal ");
                                                        Toast.makeText(getContext().getApplicationContext(),"사료 칼로리를 입력하지 않으셨습니다.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }else{
                                                Toast.makeText(getContext().getApplicationContext(),"회원 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                            }else{
                                // 옵션과 사료 칼로리 모두 입력 했을 때
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                DocumentReference documentReference = db.collection("Login_user").document(user.getUid()).collection("Info").document("PetInfo");
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document != null) {
                                            if (document.exists()) {
                                                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        String v = documentSnapshot.getString("petVaccination");
                                                        if(v.equals("예")) {
                                                            if(finalOption == 3){
                                                                home_Walking_time.setText("산책 금지");
                                                            }else{
                                                                home_Walking_time.setText("30~60");
                                                            }
                                                        }else{
                                                            home_Walking_time.setText("산책 금지");
                                                        }
                                                        String weight = documentSnapshot.getString("petWeight");
                                                        double day_kcal = 70 * Math.pow( Double.valueOf(weight) , 0.75) * finalOption;
                                                        double day_feed = 1000 * day_kcal / Double.valueOf(Edit_Feed_kcal.getText().toString());

                                                        home_Day_kcal.setText(String.format("%.2f",day_kcal)+ " kcal ");
                                                        home_Day_feed.setText(String.format("%.2f",day_feed)+ " g ");
                                                    }
                                                });

                                            }else{
                                                Toast.makeText(getContext().getApplicationContext(),"회원 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                            }
                        }
                        // EditText 포커스 ( 커서 ) 제거
                        Edit_Feed_kcal.setInputType(EditorInfo.TYPE_NULL);
                        // 키패드 내리기
                        InputMethodManager mInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        mInputMethodManager.hideSoftInputFromWindow(Edit_Feed_kcal.getWindowToken(), 0);

                    }
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (user == null){
            Log.e("HomeFragment","회원 정보 없음");
        }else{
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Login_user").document(user.getUid()).collection("Info").document("PetInfo")
                    .addSnapshotListener(MetadataChanges.INCLUDE,new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w(TAG, "Listen failed.", e);
                                return;
                            }
                            if (value != null && value.exists()) {
                                Log.d(TAG, value + " data: success");
                                petname.setText(value.getString("petName"));
                                petage.setText(value.getString("petAge") + " 살 ");
                                petkind.setText(value.getString("petKind"));

                                home_text.setText(value.getString("petName")+" 건강 관리 ");
                                petweight.setText(value.getString("petWeight")+" KG ");
                            } else {
                                Log.d(TAG, value + " data: null");
                            }
                        }
                    });
            DocumentReference doc = FBdb.collection("Login_user").document(user.getUid()).collection("Info").document("Walk");
            doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    if(document != null) {
                        if (document.exists()) {
                            doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                    double distance = Double.valueOf(value.getString("walking_Distance")) / 1000;
                                    h.setText(value.getString("walking_Time_h"));
                                    m.setText(value.getString("walking_Time_m"));
                                    c.setText(value.getString("walking_Count") + "회");
                                    d.setText(String.format("%.2f",distance) + " km ");

                                }
                            });
                        }else{
                            h.setText("0");
                            m.setText("00");
                            c.setText("0" + "회");
                            d.setText("0" +"km");
                        }
                    }
                }
            });

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference();
            storageReference.child("users/" + user.getUid() + "/profileImage.jpg").getDownloadUrl().addOnSuccessListener(
                    new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(getContext()).load(uri).apply(new RequestOptions().circleCrop()).into(petImage);
//                            Glide.with(getContext()).load(uri).into(petImage);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }

        btnCamera = (Button)v.findViewById(R.id.action_camera);


        return v;



    }

}
