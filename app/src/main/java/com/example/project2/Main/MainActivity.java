package com.example.project2.Main;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.project2.Camera.CameraActivity;
import com.example.project2.Community.ui.main.CommunityMain;
import com.example.project2.Friend.FriendFragment;
import com.example.project2.GoogleMap.MapsFragment;
import com.example.project2.Login_Membership.InfoEditActivity;
import com.example.project2.Login_Membership.LoginActivity;
import com.example.project2.Login_Membership.UserinfoActivity;
import com.example.project2.MainHome.HomeFragment;
import com.example.project2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;

    private static final String TAG = "MainActivity";

    private HomeFragment homeFragment;
    private CommunityMain fragment_Community;
    private MapsFragment fragmentMap;
    private FriendFragment friendFragment;
    private FragmentManager FM;
    private FragmentTransaction transaction;

    private long backKeyPressedTime = 0;
    final int GET_GALLERY_IMAGE = 200;

    private long BackKeyPressedTime = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        // Firebase 회원정보
        if (user == null) { // 회원 정보가 없을시 로그인 화면이동
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        } else {
            FirebaseFirestore fbdb = FirebaseFirestore.getInstance();
            DocumentReference docRef = fbdb.collection("Login_user").document(user.getUid()).collection("Info").document("PetInfo");

            //            DocumentReference documentReference = fbdb.collection("Login_user").document(user.getUid());
//            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                @Override
//                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                    textViewEmail.setText(value.getString("user_ID"));
//                    Log.d("MainActivity","User_ID : " + textViewEmail);
//                }
//            });
//
//            DocumentReference UserInfo = fbdb.collection("Login_user").document(user.getUid()).collection("Info").document("UserInfo");
//            UserInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                @Override
//                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                    textViewName.setText(value.getString("user_name")+"님 안녕하세요");
//                    Log.d("MainActivity","User_ID : " + textViewEmail);
//                }
//            });

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() { // 유저 uid에 애견정보가 있다면 정보 출력
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                Log.d(TAG, "" + document.getId() +
                                        " data: " + document.getData());
                            } else {
                                Intent getintent = getIntent();
                                int Resultcode = getintent.getIntExtra("check",0);
                                if(Resultcode == 1){ // User info Ac 에서 코드값 넘어옴 ( 회원 정보 정상적으로 저장됨)
                                    Log.d(TAG, "Resultcode = 1");
                                    MainActivity.super.onStart(); //  데이터 출력을 위한 새로고침
                                }
                                else if(Resultcode == 0){
                                    // 유저 uid에 애견정보가 없다면 정보 입력창 이동
                                    Intent intent = new Intent(getApplicationContext(), UserinfoActivity.class);
                                    startActivity(intent);
                                    Log.d(TAG, "Resultcode = 0");
                                    Log.d(TAG, "No such document");
                                }
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

            FM = getSupportFragmentManager();
            homeFragment = new HomeFragment();

            transaction = FM.beginTransaction();
            transaction.replace(R.id.container,homeFragment).commit();
            ActionBar actionBar = getSupportActionBar();
            BottomNavigationView bottomNavigationView = findViewById(R.id.main_nav);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case (R.id.action_home):
                            if (homeFragment == null) {
                                homeFragment = new HomeFragment();
                                FM.beginTransaction().add(R.id.container, homeFragment).commit();
                            }
                            if (homeFragment != null) FM.beginTransaction().show(homeFragment).commit();
                            if (fragmentMap != null) FM.beginTransaction().hide(fragmentMap).commit();
                            if (fragment_Community != null) FM.beginTransaction().hide(fragment_Community).commit();
                            if (friendFragment != null) FM.beginTransaction().hide(friendFragment).commit();
                            actionBar.show();
                            break;
                        case (R.id.action_maps):
                            if (fragmentMap == null) {
                                fragmentMap = new MapsFragment();
                                FM.beginTransaction().add(R.id.container, fragmentMap).commit();
                            }
                            if (homeFragment != null) FM.beginTransaction().hide(homeFragment).commit();
                            if (fragmentMap != null) FM.beginTransaction().show(fragmentMap).commit();
                            if (fragment_Community != null) FM.beginTransaction().hide(fragment_Community).commit();
                            if (friendFragment != null) FM.beginTransaction().hide(friendFragment).commit();
                            actionBar.hide();
                            break;

                        case R.id.action_comm:
                            if (fragment_Community == null) {
                                fragment_Community = new CommunityMain();
                                FM.beginTransaction().add(R.id.container, fragment_Community).commit();
                            }
                            if (homeFragment != null) FM.beginTransaction().hide(homeFragment).commit();
                            if (fragmentMap != null) FM.beginTransaction().hide(fragmentMap).commit();
                            if (fragment_Community != null) FM.beginTransaction().show(fragment_Community).commit();
                            if (friendFragment != null) FM.beginTransaction().hide(friendFragment).commit();
                            actionBar.hide();
                            break;

                        case R.id.action_friend:
                            if (friendFragment == null) {
                                friendFragment = new FriendFragment();
                                FM.beginTransaction().add(R.id.container, friendFragment).commit();
                            }
                            if (homeFragment != null) FM.beginTransaction().hide(homeFragment).commit();
                            if (fragmentMap != null) FM.beginTransaction().hide(fragmentMap).commit();
                            if (fragment_Community != null) FM.beginTransaction().hide(fragment_Community).commit();
                            if (friendFragment != null) FM.beginTransaction().show(friendFragment).commit();
                            actionBar.hide();
                            break;
                    }

                    return false;
                }
            });
        }

    }

        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            int id = item.getItemId();
            if (id == R.id.action_search) {
                Toast.makeText(this, "갤러리", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
            if (id == R.id.action_camera) {
                Toast.makeText(this, "카메라 실행", Toast.LENGTH_SHORT).show();
                Intent cameraIntent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(cameraIntent);
            }
            if (id == R.id.action_calender) {
                Toast.makeText(this, "달력 실행", Toast.LENGTH_SHORT).show();
                Intent CalenderIntent = new Intent(getApplicationContext(), CalenderActivity.class);
                startActivity(CalenderIntent);
            }
            if (id == R.id.action_InfoEdit) {
                Toast.makeText(this, "회원정보 수정", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), InfoEditActivity.class);
                startActivity(intent);
            }
            if (id == R.id.action_LoginOut) {
                Toast.makeText(this, "로그아웃", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
            if (id == R.id.action_inquiry) {
                Toast.makeText(this, "문의하기", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), UserinfoActivity.class);
                startActivity(intent);
            }
            return super.onOptionsItemSelected(item);
        }

    }


