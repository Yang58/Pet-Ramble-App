package com.example.project2.Main;

import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.project2.Camera.CameraActivity;
import com.example.project2.Login_Membership.LoginActivity;
import com.example.project2.Login_Membership.UserinfoActivity;
import com.example.project2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;

    private static final String TAG = "MainActivity";

    ImageView imageView;
    TextView textViewEmail;
    TextView textViewName;

    private long backKeyPressedTime = 0;
    final int GET_GALLERY_IMAGE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

//        getSupportActionBar().setTitle("🦮");
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff000000));
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // Firebase 회원정보
        if (user == null) { // 회원 정보가 없을시 로그인 화면이동
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        } else {
            FirebaseFirestore fbdb = FirebaseFirestore.getInstance();

            DocumentReference docRef = fbdb.collection("Login_user").document(user.getUid()).collection("Info").document("PetInfo");
            // 변경 DB변경 완료 후 펫 정보로 변경



            View headerView = navigationView.getHeaderView(0);
            imageView = headerView.findViewById(R.id.Drawer_image);
            textViewName = headerView.findViewById(R.id.Drawer_name);
            textViewEmail = headerView.findViewById(R.id.Drawer_Email);

            DocumentReference documentReference = fbdb.collection("Login_user").document(user.getUid());
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    textViewEmail.setText(value.getString("user_ID"));
                    Log.d("MainActivity","User_ID : " + textViewEmail);
                }
            });

            DocumentReference UserInfo = fbdb.collection("Login_user").document(user.getUid()).collection("Info").document("UserInfo");
            UserInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    textViewName.setText(value.getString("user_name")+"님 안녕하세요");
                    Log.d("MainActivity","User_ID : " + textViewEmail);
                }
            });

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() { // 유저 uid에 애견정보가 있다면 정보 출력
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                storageReference.child("users/" + user.getUid() + "/profileImage.jpg").getDownloadUrl().addOnSuccessListener(
                                        new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Glide.with(getApplicationContext()).load(uri).apply(new RequestOptions().circleCrop()).into(imageView);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });

                                imageView.setBackground(new ShapeDrawable(new OvalShape()));
                                if(Build.VERSION.SDK_INT >= 21) {
                                    imageView.setClipToOutline(true);
                                }

                                Log.d(TAG, "" + document.getId() +
                                        " data: " + document.getData());
                            } else {
//                                 유저 uid에 애견정보가 없다면 정보 입력창 이동
                                Intent intent = new Intent(getApplicationContext(), UserinfoActivity.class);
                                startActivity(intent);
                                Log.d(TAG, "No such document");
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

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
        @Override
        public boolean onSupportNavigateUp() {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                    || super.onSupportNavigateUp();
        }
    }

