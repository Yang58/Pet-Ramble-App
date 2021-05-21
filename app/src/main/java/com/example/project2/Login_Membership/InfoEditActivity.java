package com.example.project2.Login_Membership;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class InfoEditActivity extends AppCompatActivity {
    private static final String TAG = "InfoEditActivity";
    private int GALLEY_CODE = 10;

    private Button btn_Check;
    private Button btn_Back;
    private String pet_gender;
    private String pet_neutral;
    private String pet_vaccine;
    private String imageUrl;
    private EditText edit_name;
    private EditText edit_phoneNum;
    private EditText edit_Nickname;
    private EditText edit_petName;
    private EditText edit_petAge;
    private EditText edit_petBirthday;
    private EditText edit_petKind;
    private EditText edit_petWeight;
    Uri uri;
    ImageView Edit_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_edit);
        edit_name = (EditText) findViewById(R.id.edit_Name);
        edit_phoneNum = (EditText) findViewById(R.id.edit_phoneNum);
        edit_Nickname = (EditText) findViewById(R.id.edit_Nickname);
        edit_petName = (EditText) findViewById(R.id.edit_petName);
        edit_petAge = (EditText) findViewById(R.id.edit_petAge);
        edit_petKind = (EditText) findViewById(R.id.edit_petKind);
        edit_petWeight = (EditText) findViewById(R.id.edit_petWeight);
        edit_petBirthday = (EditText) findViewById(R.id.edit_birthday);
        Edit_profile = (ImageView) findViewById(R.id.edit_Profile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("Debug", "Running InfoEditActivitiy");
        Log.d("Debug", user.getUid());
        DataCollect();

        Edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLEY_CODE);
            }
        });

        btn_Back = (Button) findViewById(R.id.btn_Back);
        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoEditActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btn_Check = (Button) findViewById(R.id.btn_Check);
        btn_Check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String Edit_name = edit_name.getText().toString();
                    String Edit_phoneNum = edit_phoneNum.getText().toString();
                    String Edit_Nickname = edit_Nickname.getText().toString();
                    String Edit_petName = edit_petName.getText().toString();
                    String Edit_petAge = edit_petAge.getText().toString();
                    String Edit_petKind = edit_petKind.getText().toString();
                    String Edit_petWeight = edit_petWeight.getText().toString();
                    SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMdd" , Locale.KOREA );
                    Date Edit_petBirthday = sdf.parse(edit_petBirthday.getText().toString());

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();

                    RadioGroup genderGroup = findViewById(R.id.edit_genderGroup);
                    RadioGroup Neutralization = findViewById(R.id.edit_Neutral);
                    RadioGroup Vaccination = findViewById(R.id.edit_Vaccination);

                    int Gender = genderGroup.getCheckedRadioButtonId();
                    int Neutral = Neutralization.getCheckedRadioButtonId();
                    int Vaccine = Vaccination.getCheckedRadioButtonId();

                    RadioButton GenderCheck = findViewById(Gender);
                    RadioButton NeutralizationCheck = findViewById(Neutral);
                    RadioButton VaccinationCheck = findViewById(Vaccine);

                    StorageReference mountainImagesRef = storageRef.child("users/" + user.getUid() + "/profileImage.jpg");
                    Log.d("Debug",Edit_name);
                    if (uri == null) {
                        UserInfoDB userInfoDB = new UserInfoDB(Edit_name, Edit_Nickname, null, Edit_phoneNum);
                        MyPetDB myPetDB = new MyPetDB(Edit_petName,Edit_petAge,Edit_petBirthday,Edit_petKind,Edit_petWeight, GenderCheck.getText().toString(), NeutralizationCheck.getText().toString(), VaccinationCheck.getText().toString());

                        Log.d("Debug","Uri was null.");
                        UserinfoUploader(userInfoDB);
                        Petinfouploader(myPetDB);

                        Toast.makeText(InfoEditActivity.this, "업로드.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(InfoEditActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
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

                                        UserInfoDB userInfoDB = new UserInfoDB(Edit_name, Edit_Nickname, downloadUri.toString(), Edit_phoneNum);
                                        MyPetDB myPetDB = new MyPetDB(Edit_petName, Edit_petAge, Edit_petBirthday, Edit_petKind, Edit_petWeight, GenderCheck.getText().toString(), NeutralizationCheck.getText().toString(), VaccinationCheck.getText().toString());

                                        UserinfoUploader(userInfoDB);
                                        Petinfouploader(myPetDB);
                                        Toast.makeText(InfoEditActivity.this, "업로드.", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(InfoEditActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(InfoEditActivity.this, "회원 정보를 저장하지 못했습니다. ", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } catch (FileNotFoundException e) {
                            Log.e("로그", "에러: " + e.toString());
                        }
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
    });
   edit_petBirthday.setOnClickListener(new View.OnClickListener() {
         @Override
        public void onClick(View v) {
            Toast.makeText(InfoEditActivity.this,"8자리숫자를 입력하세요(예:20020525)",Toast.LENGTH_SHORT).show();
        }
    });
}

    private void UserinfoUploader(UserInfoDB userDB) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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
                        Toast.makeText(InfoEditActivity.this, "회원 정보 등록실패 (다시 시도해주세요!!) ", Toast.LENGTH_LONG).show();
                        Log.w(TAG, "Error", e);
                    }
                });
    }

    private void Petinfouploader(MyPetDB myPetDB) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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
                        Toast.makeText(InfoEditActivity.this, "회원 정보 등록실패 (다시 시도해주세요!!) ", Toast.LENGTH_LONG).show();
                        Log.w(TAG, "Error", e);
                    }
                });
    }

    public void DataCollect() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("Login_user").whereEqualTo("user_UID", user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("Login_user").document(user.getUid()).collection("Info").document("UserInfo").get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                DocumentSnapshot document = task.getResult();
                                                EditText profile_name = (EditText) findViewById(R.id.edit_Name);
                                                profile_name.setText((String) document.get("user_name"));

                                                EditText profile_phoneNum = (EditText) findViewById(R.id.edit_phoneNum);
                                                profile_phoneNum.setText((String) document.get("user_phoneNumber"));

                                                EditText profile_Nickname = (EditText) findViewById(R.id.edit_Nickname);
                                                profile_Nickname.setText((String) document.get("user_nickname"));
                                            }
                                        });

                                db.collection("Login_user").document(user.getUid()).collection("Info").document("PetInfo").get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                DocumentSnapshot document = task.getResult();
                                                EditText edit_petName = (EditText) findViewById(R.id.edit_petName);
                                                edit_petName.setText((String) document.get("petName"));

                                                EditText edit_petAge = (EditText) findViewById(R.id.edit_petAge);
                                                edit_petAge.setText((String) document.get("petAge"));

                                                EditText edit_petKind = (EditText) findViewById(R.id.edit_petKind);
                                                edit_petKind.setText((String) document.get("petKind"));

                                                EditText edit_petWeight = (EditText) findViewById(R.id.edit_petWeight);
                                                edit_petWeight.setText((String) document.get("petWeight"));

                                                EditText edit_petBirthday = (EditText) findViewById(R.id.edit_birthday);
                                                Timestamp i = (Timestamp) document.get("petBrithday");

                                                SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMdd" , Locale.KOREA );
                                                String str = sdf.format( new Date( i.toDate().getTime()));
                                                Log.d("Debug", str);

                                                try {
                                                    Date endDate = sdf.parse(edit_petBirthday.getText().toString());

                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }

                                                edit_petBirthday.setText(str);

                                                RadioButton gender1 = (RadioButton) findViewById(R.id.gender1);
                                                RadioButton gender2 = (RadioButton) findViewById(R.id.gender2);
                                                pet_gender = (String) document.get("petGender");

                                                if (pet_gender.equals("남아")) {
                                                    Log.d("Debug", "true");
                                                    gender1.setChecked(true);
                                                } else {
                                                    Log.d("Debug", "false");
                                                    gender2.setChecked(true);
                                                }

                                                RadioButton neutral1 = (RadioButton) findViewById(R.id.Neutralization1);
                                                RadioButton neutral2 = (RadioButton) findViewById(R.id.Neutralization2);

                                                pet_neutral = (String) document.get("petNeutralization");

                                                if (pet_neutral.equals("예")) {
                                                    Log.d("Debug", "true");
                                                    neutral1.setChecked(true);
                                                } else {
                                                    Log.d("Debug", "false");
                                                    neutral2.setChecked(true);
                                                }

                                                RadioButton vaccine1 = (RadioButton) findViewById(R.id.Vaccination1);
                                                RadioButton vaccine2 = (RadioButton) findViewById(R.id.Vaccination2);

                                                pet_vaccine = (String) document.get("petNeutralization");

                                                if (pet_vaccine.equals("예")) {
                                                    Log.d("Debug", "true");
                                                    vaccine1.setChecked(true);
                                                } else {
                                                    Log.d("Debug", "false");
                                                    vaccine2.setChecked(true);
                                                }
                                            }
                                        });
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageRef = storage.getReference();
                                storageRef.child("users/" + user.getUid() + "/profileImage.jpg").getDownloadUrl().addOnSuccessListener(
                                        new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                ImageView edit_imageView = (ImageView) findViewById(R.id.edit_Profile);
                                                Glide.with(InfoEditActivity.this)
                                                        .load(uri)
                                                        .into(edit_imageView);
                                            }
                                        });
                            }
                        } else {
                            Log.d("Debug", "Error Getting documents: ", task.getException());
                        }
                    }
                });

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
                        .into(Edit_profile);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
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
        return url;
    }

}
