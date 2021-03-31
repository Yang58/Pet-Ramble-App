package com.example.project2.Login_Membership;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.project2.Data.DBHelper;
import com.example.project2.Data.Userinfo;
import com.example.project2.Main.MainActivity;
import com.example.project2.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class UserinfoActivity extends AppCompatActivity {

    private static final String TAG = "UserinfoActivity";
    private static final int PICK_IMAGE = 0;

    private Button Check;

    ImageView user_profile;

    private String profilePath;
    private FirebaseUser user;

    private EditText petAge;
    private EditText petKind;
    private EditText petName;
    private EditText Edit_name;
    private EditText Edit_age;

    private int GALLEY_CODE = 10;
    private String imageUrl="";

    int Imagepick = 0;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        Edit_name = (EditText)findViewById(R.id.edit_Name);
        Edit_age = (EditText)findViewById(R.id.edit_Age);

        petAge = (EditText)findViewById(R.id.edit_petAge);
        petName = (EditText)findViewById(R.id.edit_petName);
        petKind = (EditText) findViewById(R.id.edit_petKind);

        user_profile = findViewById(R.id.profile_imageView);
        user_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,GALLEY_CODE);
                Imagepick = 1;
                Toast.makeText(getApplicationContext()," "+Imagepick,Toast.LENGTH_LONG).show();
            }
        });


        // 이미지뷰 원 형태로 변경
//        user_profile.setBackground(new ShapeDrawable(new OvalShape()));
//        if(Build.VERSION.SDK_INT >= 21){
//            user_profile.setClipToOutline(true);
//        }


        Check = (Button)findViewById(R.id.btn_Check);
        Check.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(Edit_name.length() > 0 && Edit_age.length() > 0){ // 회원 정보 입력
                    if(petAge.length() > 0 && petName.length() > 0 && petKind != null ){
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        profileUpdate();
                        finish();
                    }else{
                        Toast.makeText(UserinfoActivity.this,"애완동물 정보를 입력해주세요.",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(UserinfoActivity.this,"회원 정보를 입력해주세요.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //갤러리에서 넘어온 사진의 절대경로를 구한다.
    private String getRealPathFromUri(Uri uri)
    {
        String[] proj =  {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this,uri,proj,null,null,null);
        Cursor cursor = cursorLoader.loadInBackground();

        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String url = cursor.getString(columnIndex);
        cursor.close();
        return  url;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GALLEY_CODE)
        {
            try {
                imageUrl = getRealPathFromUri(data.getData());
                RequestOptions cropOptions = new RequestOptions();
                Glide.with(getApplicationContext())
                        .load(imageUrl)
                        .apply(cropOptions.optionalCircleCrop())
                        .into(user_profile);
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void profileUpdate() {

        String person_name = ((EditText) findViewById(R.id.edit_Name)).getText().toString();
        String person_age = ((EditText) findViewById(R.id.edit_Age)).getText().toString();

        String petName = ((EditText) findViewById(R.id.edit_petName)).getText().toString();
        String petAge = ((EditText) findViewById(R.id.edit_petAge)).getText().toString();
        String petKind = ((EditText) findViewById(R.id.edit_petKind)).getText().toString();

        if (person_name.length() > 0 && person_age.length() > 0) {

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            user = FirebaseAuth.getInstance().getCurrentUser();

            StorageReference mountainImagesRef = storageRef.child("users/" + user.getUid() + "/profileImage.jpg");


            if (Imagepick == 0) {
                Userinfo userinfo = new Userinfo(person_name, person_age, petName, petAge, petKind);
                storeUploader(userinfo);
                Log.d("log_test","1");
            } else if(Imagepick == 1){
                try {
                    InputStream stream = new FileInputStream(new File(imageUrl));
                    UploadTask uploadTask = mountainImagesRef.putStream(stream);
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return mountainImagesRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                Userinfo userinfo = new Userinfo(person_name, person_age, petName, petAge, petKind, downloadUri.toString());
                                storeUploader(userinfo);
                                Log.d("log_test","2");
                            } else {
                                Toast.makeText(UserinfoActivity.this, "회원 정보를 저장하지 못했습니다. ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (FileNotFoundException e) {
                    Log.e("로그", "에러: " + e.toString());
                }
            }
        } else {
            Toast.makeText(this, "회원 정보를 입력해주세요 ", Toast.LENGTH_SHORT).show();
        }
    }

    private  void storeUploader(Userinfo userinfo){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).set(userinfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UserinfoActivity.this, "회원 정보 등록완료 ", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserinfoActivity.this, "회원 정보 등록실패 (다시 시도해주세요!!) ", Toast.LENGTH_LONG).show();
                        Log.w(TAG, "Error",e);
                    }
                });
    }

    private void SQLupdate(){

        SQLiteDatabase db;
        String sql;

        String person_name = ((EditText) findViewById(R.id.edit_Name)).getText().toString();
        String person_age = ((EditText) findViewById(R.id.edit_Age)).getText().toString();

        String petName = ((EditText) findViewById(R.id.edit_petName)).getText().toString();
        String petAge = ((EditText) findViewById(R.id.edit_petAge)).getText().toString();
        String petKind = (findViewById(R.id.edit_petKind)).toString();

        db = dbHelper.getWritableDatabase();
        sql = String.format("INSERT INTO info VALUES('"+person_name+"','"+person_age+"','"+petName+"','"+petAge+"','"+petKind+"',0);");

        db.execSQL(sql);
        Log.d(TAG,sql);

    }
}
