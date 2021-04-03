package com.example.project2.Main.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.project2.Community.ui.main.CommunityMain;
import com.example.project2.GoogleMap.MapsFragment;
import com.example.project2.Login_Membership.UserinfoActivity;
import com.example.project2.Main.CustomAdapter;
import com.example.project2.R;
import com.example.project2.Setting.MyInfomationFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import me.relex.circleindicator.CircleIndicator;


public class HomeFragment extends Fragment {

    TextView petname ;
    TextView petage ;
    TextView petkind ;

    Button btnCamera;

    FragmentManager FM;
    Fragment lastFragment;

    CommunityMain fragment_Community;
    MapsFragment fragmentMap;
    MyInfomationFragment fragmentInfo;

    ViewPager pager;
    CircleIndicator indicator;

    FragmentTransaction fragmentTransaction;


    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        final Button btn_test = v.findViewById(R.id.test);

        pager = (ViewPager)v.findViewById(R.id.pager);

        petname = v.findViewById(R.id.MPN);
        petage = v.findViewById(R.id.MPA);
        petkind = v.findViewById(R.id.MPK);

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
        DocumentReference docRef = FBdb.collection("users").document(user.getUid());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                petname.setText(value.getString("petName"));
                petage.setText(value.getString("petAge"));
                petkind.setText(value.getString("petKind"));
            }
        });

        btnCamera = (Button)v.findViewById(R.id.action_camera);
        //프래그먼트는 뷰와 다르게 context를 매개변수로 넣어줄 필요가 없다.
        FM = getChildFragmentManager();
        fragmentTransaction = getChildFragmentManager().beginTransaction();
        //지도
        Button buttonMap = v.findViewById(R.id.btn_Map);
        buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fragmentMap == null){
                    fragmentMap = new MapsFragment();
                    FM.beginTransaction().add(R.id.container,fragmentMap).commit();
                }
                if(fragmentMap != null) FM.beginTransaction().show(fragmentMap).commit();
                if(fragment_Community != null) FM.beginTransaction().hide(fragment_Community).commit();
                if(fragmentInfo != null) FM.beginTransaction().hide(fragmentInfo).commit();
            }
        });

        //커뮤니티
        Button community = v.findViewById(R.id.btn_community);
        community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fragment_Community == null){
                    fragment_Community = new CommunityMain();
                    FM.beginTransaction().add(R.id.container,fragment_Community).commit();
                }
                if(fragmentMap != null) FM.beginTransaction().hide(fragmentMap).commit();
                if(fragment_Community != null) FM.beginTransaction().show(fragment_Community).commit();
                if(fragmentInfo != null) FM.beginTransaction().hide(fragmentInfo).commit();
            }
        });

        //내정보
        Button btn_set = v.findViewById(R.id.btn_Setting);
        btn_set.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                /*
                if(fragmentInfo == null){
                    fragmentInfo = new MyInfomationFragment();
                    FM.beginTransaction().add(R.id.container,fragmentInfo).commit();
                }
                if(fragmentMap != null) FM.beginTransaction().hide(fragmentMap).commit();
                if(fragment_Community != null) FM.beginTransaction().hide(fragment_Community).commit();
                if(fragmentInfo != null) FM.beginTransaction().show(fragmentInfo).commit();
                 */
                Intent intent = new Intent(getContext().getApplicationContext(), UserinfoActivity.class);
                startActivity(intent);
            }
        });


        return v;
    }

    private void UserCheck(){
        // Firebase 회원정보


    }


}