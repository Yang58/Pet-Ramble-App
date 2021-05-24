package com.example.project2.Login_Membership;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    // 사용자 정보
    private EditText Edit_name;
    private EditText Edit_Phone;
    private EditText Edit_Nickname;
    // 애견 정보
    private EditText petName;
    private EditText petAge;
    private EditText petBirthday;
    private EditText petWeight;
    private Spinner Kind_spinner;

    private int GALLEY_CODE = 10;

    private Uri imageUri;
    private String pathUri;

    private List<String> friend;
    private List<String> friend_mail;
    private String pet_kind ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        user_profile = findViewById(R.id.profile_imageView);
        // 사용자 정보
        Edit_name = (EditText)findViewById(R.id.edit_name);
        Edit_Phone = (EditText)findViewById(R.id.edit_phoneNum);
        Edit_Nickname = (EditText)findViewById(R.id.edit_Nickname);

        petName = (EditText)findViewById(R.id.edit_petName);
        petAge = (EditText)findViewById(R.id.edit_petAge);
        petWeight = (EditText) findViewById(R.id.edit_petWeight);
        petBirthday = (EditText)findViewById(R.id.edit_birthday);
        Kind_spinner = findViewById(R.id.Kind_spinner);

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

        // 핸드폰 갤러리 접근
        user_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent , GALLEY_CODE);
            }
        });

        RadioButton gender1 = findViewById(R.id.gender1);
        RadioButton Neutralization1= findViewById(R.id.Neutralization1);
        RadioButton Vaccination1 = findViewById(R.id.Vaccination1);

        // 라디오 버튼 기본 값 설정
        gender1.setChecked(true);
        Neutralization1.setChecked(true);
        Vaccination1.setChecked(true);

        Kind_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){ // 종류 선택 안함
                    pet_kind = null;
                }else{
                    InputMethodManager mInputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    mInputMethodManager.hideSoftInputFromWindow(petBirthday.getWindowToken(), 0);
                    pet_kind = (String) parent.getItemAtPosition(position);
                    Log.e(TAG,"1. test log : "+ pet_kind );
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(UserinfoActivity.this,"반려견 종류를 선택해주세요.",Toast.LENGTH_SHORT).show();
            }
        });

        Check = (Button)findViewById(R.id.btn_Check);
        Check.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(Edit_name.length() > 0 && Edit_Phone.length() > 0 && Edit_Nickname.length() > 0) { // 회원 정보 입력 완료
                    if (petName.length() > 0 && petAge.length() > 0 && petBirthday.length() > 0 && petWeight.length() > 0) { // 애견 정보 입력 완료

                        if(pet_kind == null){
                            Log.e(TAG,"2. test log " + pet_kind );
                            Toast.makeText(UserinfoActivity.this,"반려견 종류를 선택해주세요.",Toast.LENGTH_SHORT).show();
                        }else if (pet_kind != null){
                            Log.e(TAG,"2. test log Success" );
                            profileUpdate(pet_kind);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("check",1);
                            startActivity(intent);
                            finish();

                        }
                    }else{
                        Toast.makeText(UserinfoActivity.this,"반려견 정보를 입력해주세요.",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(UserinfoActivity.this,"사용자 정보를 입력해주세요.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        petBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserinfoActivity.this,"8자리숫자를 입력하세요(예:20020525)",Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLEY_CODE && resultCode == RESULT_OK)
        {
            try {
                imageUri = data.getData();
                pathUri = getPath(data.getData());
                RequestOptions cropOptions = new RequestOptions();
                Log.d(TAG, "PICK_FROM_ALBUM photoUri : " + imageUri);
                Glide.with(getApplicationContext())
                        .load(imageUri)
                        .apply(cropOptions.optionalCircleCrop())
                        .into(user_profile);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    // uri 절대경로 가져오기
    public String getPath(Uri uri){

        String [] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this,uri,proj,null,null,null);

        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(index);

    }

    private void profileUpdate(String petKind) {
        String userName =  ((EditText) findViewById(R.id.edit_name)).getText().toString();
        String userNickname = ((EditText) findViewById(R.id.edit_Nickname)).getText().toString();
        String userPhoneNumber =((EditText)findViewById(R.id.edit_phoneNum)).getText().toString();

        String petName = ((EditText) findViewById(R.id.edit_petName)).getText().toString();
        String petAge = ((EditText) findViewById(R.id.edit_petAge)).getText().toString();
        String petWeight = ((EditText)findViewById(R.id.edit_petWeight)).getText().toString();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMdd" , Locale.KOREA );
            Date petBrithday = sdf.parse(petBirthday.getText().toString());

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            user  = FirebaseAuth.getInstance().getCurrentUser();

            StorageReference mountainImagesRef = storageRef.child("users/" + user.getUid() + "/profileImage.jpg");

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference photo = database.getReference("friend").child(user.getUid()).child("photo");
            DatabaseReference name = database.getReference("friend").child(user.getUid()).child("name");
            DatabaseReference pet = database.getReference("friend").child(user.getUid()).child("pet");
            DatabaseReference age = database.getReference("friend").child(user.getUid()).child("age");

            RadioGroup genderGroup = findViewById(R.id.edit_genderGroup);
            RadioGroup NeutralizationGroup = findViewById(R.id.edit_Neutral);
            RadioGroup VaccinationGroup = findViewById(R.id.edit_Vaccination);

            int Gender = genderGroup.getCheckedRadioButtonId();
            int Neutralization = NeutralizationGroup.getCheckedRadioButtonId();
            int Vaccination = VaccinationGroup.getCheckedRadioButtonId();

            RadioButton GenderCheck = findViewById(Gender);
            RadioButton NeutralizationCheck = findViewById(Neutralization);
            RadioButton VaccinationCheck = findViewById(Vaccination);

            MyPetDB myPetDB = new MyPetDB(petName,petAge,petBrithday,petKind,petWeight,GenderCheck.getText().toString(),NeutralizationCheck.getText().toString(), VaccinationCheck.getText().toString());

            if (imageUri == null) {
                Log.d(TAG,"2. 사진 uri 없음 ");
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

                }
            else {
                Log.d(TAG,"3. 사진 uri 있음 ");
                try {
                    Log.d(TAG,"4. 사진 uri 있음 ");
                    final Uri file = Uri.fromFile(new File(pathUri));

                    user  = FirebaseAuth.getInstance().getCurrentUser();
                    FirebaseStorage mStorage =FirebaseStorage.getInstance();
                    StorageReference storageRef1 = mStorage.getReference();
                    StorageReference storageReference = storageRef1.child("users/" + user.getUid() + "/profileImage.jpg");
                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            final Task<Uri> imageUrl = task.getResult().getStorage().getDownloadUrl();
                            while(!imageUrl.isComplete()) ;

                            photo.setValue(imageUrl.getResult().toString());
                            name.setValue(userNickname);
                            pet.setValue(petKind);
                            age.setValue(petAge);

                            UserInfoDB userInfoDB = new UserInfoDB(userName,userNickname,imageUrl.getResult().toString(),userPhoneNumber);
                            User userinfo = new User(userNickname, petBrithday, petName, petAge, petKind, imageUrl.getResult().toString());
                            Friend f_list = new Friend(friend,friend_mail);

                            UserinfoUploader(userInfoDB);
                            Petinfouploader(myPetDB);
                            UserProfileUploader(userinfo);
                            EmptyFriendlistUploader(f_list);

                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(UserinfoActivity.this, " 업로드 실패 ", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "에러: " + e.toString());
                }
            }
        } catch (ParseException e) {
            Log.d(TAG,"null error!");
            e.printStackTrace();
        }
    }

    private void UserinfoUploader(UserInfoDB userDB){
        Log.d(TAG,"7. UserinfoUploader ");
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
        Log.d(TAG,"8. Petinfouploader ");
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
        Log.d(TAG,"9. UserProfileUploader ");
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

