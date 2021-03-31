package com.example.project2.Main;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.project2.CalenderActivity;
import com.example.project2.Camera.CameraActivity;
import com.example.project2.Data.Userinfo;
import com.example.project2.GoogleMap.MapsFragment;
import com.example.project2.Login_Membership.LoginActivity;
import com.example.project2.Login_Membership.UserinfoActivity;
import com.example.project2.R;
import com.example.project2.Community.ui.main.CommunityMain;
import com.example.project2.Setting.MyInfomationFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends AppCompatActivity{

    //프래그먼트는  xml레이아웃 파일 하나랑 자바소스 파일 하나로 정의할 수 있다.
    //이게 하나의 뷰처럼 쓸 수 있는데 뷰하고 약간 다른특성들이 있다.
    //엑티비티를 본떠 만들었기 떄문에 프래그먼트 매니저가 소스코드에서 담당한다.

    private static final String TAG = "MainActivity";

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

    final int GET_GALLERY_IMAGE = 200;

    // 마지막으로 뒤로 가기 버튼을 눌렀던 시간 저장
    private long backKeyPressedTime = 0;
    // 첫 번째 뒤로 가기 버튼을 누를 때 표시
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        petname = findViewById(R.id.MPN);
        petage = findViewById(R.id.MPA);
        petkind = findViewById(R.id.MPK);

//        DBHelper dbHelper = new DBHelper(this);
//        SQLiteDatabase sqldb;
//        String sql;
//
//        sqldb = dbHelper.getReadableDatabase();
//        sql = "SELECT * FROM info;";
//        Cursor cursor = sqldb.rawQuery(sql, null);
//        if(cursor.getCount() > 0){
//            while (cursor.moveToNext()){
//                petname.setText(cursor.getString(2));
//                petage.setText(cursor.getString(3));
//                petkind.setText(cursor.getString(4));
//
//            }
//        }

        // 액션바
        getSupportActionBar().setTitle("🦮");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff000000));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 홈버튼 활성화
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_black_24dp); // 홈버튼 이미지

        final Button btn_test = findViewById(R.id.test);

        pager = (ViewPager)findViewById(R.id.pager);

        //ViewPager에 설정할 Adapter 객체 생성
        //ListView에서 사용하는 Adapter와 같은 역할.
        //다만. ViewPager로 스크롤 될 수 있도록 되어 있다는 것이 다름
        //PagerAdapter를 상속받은 CustomAdapter 객체 생성
        //CustomAdapter에게 LayoutInflater 객체 전달
        CustomAdapter adapter= new CustomAdapter(getLayoutInflater());

        //ViewPager에 Adapter 설정
        pager.setAdapter(adapter);
        indicator = findViewById(R.id.indicator);
        indicator.setViewPager(pager);

        // Firebase 회원정보
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){ // 회원 정보가 없을시 로그인화면이동
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
        }else{ // 회원가입 또는 로그인 성공시 바로 메인화면이동
            FirebaseFirestore FBdb = FirebaseFirestore.getInstance();
            DocumentReference docRef = FBdb.collection("users").document(user.getUid());
            docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    petname.setText(value.getString("petName"));
                    petage.setText(value.getString("petAge"));
                    petkind.setText(value.getString("petKind"));
                }
            });

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if(document != null){
                            if (document.exists()) {
                                Log.d(TAG, "" +document.getId()+
                                        " data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");
                                Intent intent = new Intent(getApplicationContext(), UserinfoActivity.class);
                                startActivity(intent);
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

        }

        btnCamera = (Button)findViewById(R.id.action_camera);

        //프래그먼트는 뷰와 다르게 context를 매개변수로 넣어줄 필요가 없다.
        FM = getSupportFragmentManager();

        //지도
        Button buttonMap = findViewById(R.id.btn_Map);
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
        Button community = findViewById(R.id.btn_community);
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
        Button btn_set = findViewById(R.id.btn_Setting);
        btn_set.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(fragmentInfo == null){
                    fragmentInfo = new MyInfomationFragment();
                    FM.beginTransaction().add(R.id.container,fragmentInfo).commit();
                }
                if(fragmentMap != null) FM.beginTransaction().hide(fragmentMap).commit();
                if(fragment_Community != null) FM.beginTransaction().hide(fragment_Community).commit();
                if(fragmentInfo != null) FM.beginTransaction().show(fragmentInfo).commit();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Toast.makeText(this, "메인으로", Toast.LENGTH_SHORT).show();
            if(fragmentMap != null) FM.beginTransaction().hide(fragmentMap).commit();
            if(fragment_Community != null) FM.beginTransaction().hide(fragment_Community).commit();
            if(fragmentInfo != null) FM.beginTransaction().hide(fragmentInfo).commit();
            return true;
        }

        if (id == R.id.action_search) {
            Toast.makeText(this, "갤러리", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent,GET_GALLERY_IMAGE);
        }
        if( id == R.id.action_camera){
            Toast.makeText(this, "카메라 실행",Toast.LENGTH_SHORT).show();
            Intent cameraIntent = new Intent(getApplicationContext(), CameraActivity.class);
            startActivity(cameraIntent);
        }
        if( id == R.id.action_calender){
            Toast.makeText(this, "달력 실행",Toast.LENGTH_SHORT).show();
            Intent CalenderIntent = new Intent(getApplicationContext(), CalenderActivity.class);
            startActivity(CalenderIntent);
        }
        if (id == R.id.action_LoginOut) {
            Toast.makeText(this, "로그아웃", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        if(id == R.id.action_inquiry){
            Toast.makeText(this, "문의하기", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), InquiryActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //메뉴를 한번 이상 이동했을 경우 뒤로가기 눌렀을 때 이전 프래그먼트로 이동해야함
        //따라서 뒤로가기 두번 시행시 어플 종료는 backstack이 0일때만 작동해야함
        if(FM.getBackStackEntryCount()>0) {
            FM.popBackStack();
        }else{
            // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 2.5초를 더해 현재 시간과 비교 후
            // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 2.5초가 지났으면 Toast 출력
            // 2500 milliseconds = 2.5 seconds
            if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
                backKeyPressedTime = System.currentTimeMillis();
                toast = Toast.makeText(this, "한 번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG);
                toast.show();
                return;
            }
            // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 2.5초를 더해 현재 시간과 비교 후
            // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 2.5초가 지나지 않았으면 종료
            if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
                moveTaskToBack(true);
                System.exit(1);
            }
        }
    }

}