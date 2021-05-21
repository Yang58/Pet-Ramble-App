package com.example.project2.Main.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.project2.Community.ui.main.CommunityMain;
import com.example.project2.Friend.FriendMainActivity;
import com.example.project2.GoogleMap.MapsFragment;
import com.example.project2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class HomeFragment extends Fragment {

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

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        petname = v.findViewById(R.id.MPN);
        petage = v.findViewById(R.id.MPA);
        petkind = v.findViewById(R.id.MPK);
        petImage = v.findViewById(R.id.home_img);

        home_text = v.findViewById(R.id.home_text);
        petweight = v.findViewById(R.id.home_text_weight);
        Edit_Feed_kcal = v.findViewById(R.id.home_Edit_feed_kcal);
        home_Day_kcal = v.findViewById(R.id.home_text_kcal);
        home_Day_feed = v.findViewById(R.id.home_text_feed);

        c = v.findViewById(R.id.tv_c);
        d = v.findViewById(R.id.tv_d);
        h = v.findViewById(R.id.tv_h);
        m = v.findViewById(R.id.tv_m);

        //EditText 클릭 이벤트
        Edit_Feed_kcal.setInputType(EditorInfo.TYPE_NULL);
        Edit_Feed_kcal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((EditText)view).setInputType(EditorInfo.TYPE_CLASS_TEXT);
            }
        });
        //EditText 종료 후 이벤트
        Edit_Feed_kcal.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String inText = textView.getText().toString();
                // Do Something...
                textView.setInputType(EditorInfo.TYPE_NULL);
                return true;
            }
        });

        //ViewPager에 설정할 Adapter 객체 생성
        //ListView에서 사용하는 Adapter와 같은 역할.
        //다만. ViewPager로 스크롤 될 수 있도록 되어 있다는 것이 다름
        //PagerAdapter를 상속받은 CustomAdapter 객체 생성
        //CustomAdapter에게 LayoutInflater 객체 전달

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore FBdb = FirebaseFirestore.getInstance();

        Option_Spinner = v.findViewById(R.id.home_spinner);
        Option_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                double Option = 0;
                if(position == 0){
                }else if(position == 1){
                    Option = 2.5;
                }else if(position == 2){
                    Option = 1.8;
                }else if(position == 3){
                    Option = 1.6;
                }else if(position == 4){
                    Option = 1.4;
                }else if(position == 5){
                    Option = 1.2;
                }else if(position == 6){
                    Option = 0.9;
                }else if(position == 7){
                    Option = 2.0;
                }else if(position == 8){
                    Option = 1.4;
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
                                Toast.makeText(getContext().getApplicationContext()," 옵선 O 사료 X", Toast.LENGTH_SHORT).show();
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
                                                        String weight = documentSnapshot.getString("petWeight");
                                                        double day_kcal = 70 * Double.valueOf(weight) * 0.75 * finalOption;
                                                        home_Day_kcal.setText(String.format("%.2f",day_kcal)+ " kcal ");
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
                                Toast.makeText(getContext().getApplicationContext()," 옵선 O 사료 O", Toast.LENGTH_SHORT).show();
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
                                                        String weight = documentSnapshot.getString("petWeight");
                                                        double day_kcal = 70 * Double.valueOf(weight) * 0.75 * finalOption;
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
            db.collection("Login_user").document(user.getUid()).collection("Info").document("PetInfo").get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();
                            petname.setText(document.getString("petName"));
                            petage.setText(document.getString("petAge"));
                            petkind.setText(document.getString("petKind"));

                            home_text.setText(document.getString("petName")+" 건강 관리 ");
                            petweight.setText(document.getString("petWeight")+" KG ");

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
                            m.setText("0");
                            c.setText("0");
                            d.setText("0");
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
        //프래그먼트는 뷰와 다르게 context를 매개변수로 넣어줄 필요가 없다.
        FM = getChildFragmentManager();

        BottomNavigationView bottomNavigationView = v.findViewById(R.id.home_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case (R.id.action_maps):
                        if (fragmentMap == null) {
                            fragmentMap = new MapsFragment();
                            FM.beginTransaction().add(R.id.container, fragmentMap).commit();
                        }
                        if (fragmentMap != null) FM.beginTransaction().show(fragmentMap).commit();
                        if (fragment_Community != null) FM.beginTransaction().hide(fragment_Community).commit();
                        break;

                    case R.id.action_comm:
                        if (fragment_Community == null) {
                            fragment_Community = new CommunityMain();
                            FM.beginTransaction().add(R.id.container, fragment_Community).commit();
                        }
                        if (fragmentMap != null) FM.beginTransaction().hide(fragmentMap).commit();
                        if (fragment_Community != null) FM.beginTransaction().show(fragment_Community).commit();
                        break;

                    case R.id.action_friend:
                        Intent intent = new Intent(getContext(), FriendMainActivity.class);
                        startActivity(intent);
                        break;
                }

                return false;
            }
        });

        return v;
    }


}