package com.example.project2.Login_Membership;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.project2.FirebaseDB.Friend;
import com.example.project2.FirebaseDB.MyPetDB;
import com.example.project2.FirebaseDB.User;
import com.example.project2.FirebaseDB.UserInfoDB;
import com.example.project2.Main.MainActivity;
import com.example.project2.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

/* TO-DO list
    1.petBirthday date형식으로 바꾸기.
    2.적절한 주석 추가.
    3.더 필요한 데이터 추가해야 할지도 모름.
    4.데이터 많아지면 유저&애완동물 페이지를 분리해야할 가능성도 있음.
 */

public class UserinfoActivity extends AppCompatActivity {
    private static final String TAG = "UserinfoActivity";

    private Button Check;
    ImageView user_profile;

    private FirebaseUser user;

    private EditText Edit_name;

    private EditText petBirthday;
    private EditText petWeight;
    private EditText petKind;
    private EditText petName;

    private int GALLEY_CODE = 10;
    Uri uri;
    private String imageUrl;
    private List<String> friend;
    private List<String> friend_mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        user_profile = findViewById(R.id.profile_imageView);
        Edit_name = (EditText)findViewById(R.id.edit_name);

        petBirthday = (EditText)findViewById(R.id.edit_birthday);
        petWeight = (EditText) findViewById(R.id.edit_petWeight);
        petName = (EditText)findViewById(R.id.edit_petName);
        petKind = (EditText) findViewById(R.id.edit_petKind);

        Edit_name.setInputType(EditorInfo.TYPE_NULL);
        Edit_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((EditText)view).setInputType(EditorInfo.TYPE_CLASS_TEXT);
            }
        });
        Edit_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String inText = textView.getText().toString();
                // Do Something...
                textView.setInputType(EditorInfo.TYPE_NULL);
                return true;
            }
        });


        user_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,GALLEY_CODE);
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
                if(Edit_name.length() > 0 ){ // 회원 정보 입력
                    if(petBirthday.length() > 0 && petName.length() > 0 && petKind != null && petWeight.length() > 0){
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        profileUpdate();
                        finish();
                    }else{
                        Toast.makeText(UserinfoActivity.this,"애완동물 정보를 입력해주세요.",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(UserinfoActivity.this,"별명을 입력해주세요.",Toast.LENGTH_SHORT).show();
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
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLEY_CODE && resultCode == RESULT_OK)
        {
            try {
                uri = data.getData();
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
    }

    private void profileUpdate() {
        String userName =  ((EditText) findViewById(R.id.edit_name)).getText().toString();
        String userNickname = ((EditText) findViewById(R.id.edit_Nickname)).getText().toString();
        String userPhoneNumber =((EditText)findViewById(R.id.edit_phoneNum)).getText().toString();

        String petName = ((EditText) findViewById(R.id.edit_petName)).getText().toString();
        String petBrithday = ((EditText) findViewById(R.id.edit_birthday)).getText().toString();
        String petAge = ((EditText) findViewById(R.id.edit_petAge)).getText().toString();
        String petKind = ((EditText) findViewById(R.id.edit_petKind)).getText().toString();
        String petWeight = ((EditText)findViewById(R.id.edit_petWeight)).getText().toString();



        if (userNickname.length() > 0 && petBrithday.length() > 0) {

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            user  = FirebaseAuth.getInstance().getCurrentUser();

            StorageReference mountainImagesRef = storageRef.child("users/" + user.getUid() + "/profileImage.jpg");

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference photo = database.getReference("friend").child(user.getUid()).child("photo");
            DatabaseReference name = database.getReference("friend").child(user.getUid()).child("name");
            DatabaseReference pet = database.getReference("friend").child(user.getUid()).child("pet");
            DatabaseReference age = database.getReference("friend").child(user.getUid()).child("age");

            RadioGroup genderGroup = findViewById(R.id.genderGroup);
            RadioGroup NeutralizationGroup = findViewById(R.id.NeutralizationGroup);
            RadioGroup VaccinationGroup = findViewById(R.id.VaccinationGroup);

            int Gender = genderGroup.getCheckedRadioButtonId();
            int Neutralization = NeutralizationGroup.getCheckedRadioButtonId();
            int Vaccination = VaccinationGroup.getCheckedRadioButtonId();

            RadioButton GenderCheck = findViewById(Gender);
            RadioButton NeutralizationCheck = findViewById(Neutralization);
            RadioButton VaccinationCheck = findViewById(Vaccination);

            MyPetDB myPetDB = new MyPetDB(petName,petBrithday,petAge,petKind,petWeight,GenderCheck.getText().toString(),NeutralizationCheck.getText().toString(), VaccinationCheck.getText().toString());

            if (uri == null) {
                User userinfo = new User(userNickname, petBrithday, petName, petAge, petKind,null);
                UserInfoDB userInfoDB = new UserInfoDB(userName,userNickname,null,userPhoneNumber);
                Friend f_list = new Friend(friend,friend_mail);

                photo.setValue(null);
                name.setValue(userNickname);
                pet.setValue(petKind);
                age.setValue(petAge);

                Petinfouploader(myPetDB);
                UserinfoUploader(userInfoDB);
                UserProfileUploader(userinfo);
                EmptyFriendlistUploader(f_list);

            } else {
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

                                photo.setValue(downloadUri.toString());
                                name.setValue(userNickname);
                                pet.setValue(petKind);
                                age.setValue(petAge);

                                UserInfoDB userInfoDB = new UserInfoDB(userName,userNickname,downloadUri.toString(),userPhoneNumber);
                                User userinfo = new User(userNickname, petBrithday, petName, petAge, petKind, downloadUri.toString());
                                Friend f_list = new Friend(friend,friend_mail);
                                UserinfoUploader(userInfoDB);
                                Petinfouploader(myPetDB);
                                UserProfileUploader(userinfo);
                                EmptyFriendlistUploader(f_list);
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

    private void UserinfoUploader(UserInfoDB userDB){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Login_user").document(user.getUid()).collection("Info").document("UserInfo").set(userDB)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "User Info Success");
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

    private void Petinfouploader(MyPetDB myPetDB){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Login_user").document(user.getUid()).collection("Info").document("PetInfo").set(myPetDB)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Pet info Success");
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
    private  void UserProfileUploader(User userprofile){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).set(userprofile)
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

    private void EmptyFriendlistUploader(Friend FList){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("community").document(user.getUid()).set(FList)
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




}

