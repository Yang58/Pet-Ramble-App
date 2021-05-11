package com.example.project2.Main.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.project2.Community.ui.main.CommunityMain;
import com.example.project2.Friend.FriendMainActivity;
import com.example.project2.GoogleMap.MapsFragment;
import com.example.project2.Login_Membership.InfoEditActivity;
import com.example.project2.Main.CustomAdapter;
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

import me.relex.circleindicator.CircleIndicator;


public class HomeFragment extends Fragment {

    ImageView petImage;
    TextView petname ;
    TextView petage ;
    TextView petkind ;
    TextView infoedit;

    TextView c;
    TextView d;
    TextView h;
    TextView m;

    Button btnCamera;

    FragmentManager FM;

    private CommunityMain fragment_Community;
    private MapsFragment fragmentMap;

    ViewPager pager;
    CircleIndicator indicator;

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        pager = (ViewPager)v.findViewById(R.id.pager);

        petname = v.findViewById(R.id.MPN);
        petage = v.findViewById(R.id.MPA);
        petkind = v.findViewById(R.id.MPK);
        petImage = v.findViewById(R.id.home_img);

        c = v.findViewById(R.id.tv_c);
        d = v.findViewById(R.id.tv_d);
        h = v.findViewById(R.id.tv_h);
        m = v.findViewById(R.id.tv_m);

        infoedit = v.findViewById(R.id.info_edit);
        //ViewPager에 설정할 Adapter 객체 생성
        //ListView에서 사용하는 Adapter와 같은 역할.
        //다만. ViewPager로 스크롤 될 수 있도록 되어 있다는 것이 다름
        //PagerAdapter를 상속받은 CustomAdapter 객체 생성
        //CustomAdapter에게 LayoutInflater 객체 전달
        CustomAdapter adapter= new CustomAdapter(getLayoutInflater());

        //ViewPager에 Adapter 설정
        pager.setAdapter(adapter);
        indicator = v.findViewById(R.id.indicator);
        indicator.setViewPager(pager);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore FBdb = FirebaseFirestore.getInstance();

        if (user == null){
            Log.e("HomeFragment","회원 정보 없음");
        }else{
            DocumentReference docRef = FBdb.collection("users").document(user.getUid());
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    petname.setText(value.getString("petName"));
                    petage.setText(value.getString("petAge"));
                    petkind.setText(value.getString("petKind"));
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
                                    h.setText(value.getString("walking_Time_h"));
                                    m.setText(value.getString("walking_Time_m"));
                                    c.setText(value.getString("walking_Count"));
                                    d.setText(value.getString("walking_Distance"));

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

        infoedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), InfoEditActivity.class);
                startActivity(intent);
            }
        });

        return v;
    }


}