package com.example.project2.Login_Membership;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.project2.FirebaseDB.MyPetDB;
import com.example.project2.FirebaseDB.UserInfoDB;
import com.example.project2.Main.MainActivity;
import com.example.project2.R;
import com.example.project2.tensorflowTest;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.WatchEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private String edit_petKind;
    private EditText edit_petWeight;
    private Spinner Kind_spinner;
    private String temp_uri;
    private ImageButton get_petKind;
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
        Kind_spinner = (Spinner) findViewById(R.id.Kind_spinner);
        edit_petWeight = (EditText) findViewById(R.id.edit_petWeight);
        edit_petBirthday = (EditText) findViewById(R.id.edit_birthday);
        Edit_profile = (ImageView) findViewById(R.id.edit_Profile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        edit_name.setInputType(EditorInfo.TYPE_NULL);
        edit_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((EditText)view).setInputType(EditorInfo.TYPE_CLASS_TEXT);
            }
        });

        edit_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String inText = textView.getText().toString();
                // Do Something...
                textView.setInputType(EditorInfo.TYPE_NULL);
                return true;
            }
        });

        Kind_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){ // 종류 선택 안함
                    edit_petKind = null;
                }else{
                    InputMethodManager mInputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    mInputMethodManager.hideSoftInputFromWindow(edit_petBirthday.getWindowToken(), 0);
                    if (position == 1) {
                        Intent intent = new Intent(getApplicationContext(), tensorflowTest.class);
                        startActivityForResult(intent, 7465);
                    }
                    edit_petKind = (String) parent.getItemAtPosition(position);
                    Log.e(TAG,"1. test log : "+ edit_petKind );
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(InfoEditActivity.this,"반려견 종류를 선택해주세요.",Toast.LENGTH_SHORT).show();
            }
        });

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
                    String Edit_petKind = edit_petKind;
                    //String Edit_petKind = edit_petKind.getText().toString();
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

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference photo = database.getReference("friend").child(user.getUid()).child("photo");
                    DatabaseReference name = database.getReference("friend").child(user.getUid()).child("name");
                    DatabaseReference pet = database.getReference("friend").child(user.getUid()).child("pet");
                    DatabaseReference age = database.getReference("friend").child(user.getUid()).child("age");

                    StorageReference mountainImagesRef = storageRef.child("users/" + user.getUid() + "/profileImage.jpg");
                    Log.d("Debug",Edit_name);
                    if (uri == null) {
                        UserInfoDB userInfoDB = new UserInfoDB(Edit_name, Edit_Nickname, null, Edit_phoneNum);
                        MyPetDB myPetDB = new MyPetDB(Edit_petName,Edit_petAge,Edit_petBirthday,Edit_petKind,Edit_petWeight, GenderCheck.getText().toString(), NeutralizationCheck.getText().toString(), VaccinationCheck.getText().toString());

                        name.setValue(Edit_Nickname);
                        pet.setValue(Edit_petKind);
                        age.setValue(Edit_petAge);
                        photo.setValue(temp_uri);

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

                                        photo.setValue(downloadUri.toString());
                                        name.setValue(Edit_Nickname);
                                        pet.setValue(Edit_petName);
                                        age.setValue(Edit_petAge);

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

    edit_petBirthday.setFocusable(false);
   edit_petBirthday.setOnClickListener(new View.OnClickListener() {
         @RequiresApi(api = Build.VERSION_CODES.N)
         @Override
        public void onClick(View v) {
             DatePickerDialog datePickerDialog = new DatePickerDialog(InfoEditActivity.this);
             datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                 @Override
                 public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                     String tmpDate="";
                     if(month+1>9) {
                         tmpDate = Integer.toString(year) + Integer.toString(month+1) + Integer.toString(dayOfMonth);
                     }else{
                         tmpDate = Integer.toString(year) +"0"+ Integer.toString(month+1) + Integer.toString(dayOfMonth);
                     }
                     Log.wtf("asdf",tmpDate);
                     SimpleDateFormat sDate = new SimpleDateFormat("yyyyMMdd");
                     try {
                         Date date = sDate.parse(tmpDate);
                         String d = sDate.format(date);
                         edit_petBirthday.setText(d);
                     } catch (ParseException e) {
                         e.printStackTrace();
                     }
                 }
             });
             datePickerDialog.show();
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

                                                temp_uri = (String) document.get("user_profile");
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

                                                Spinner Kind_Spinner = (Spinner) findViewById(R.id.Kind_spinner);

                                                String myString = (String) document.get("petKind"); //the value you want the position for

                                                ArrayAdapter myAdap = (ArrayAdapter) Kind_Spinner.getAdapter(); //cast to an ArrayAdapter

                                                int spinnerPosition = myAdap.getPosition(myString);

                                                Kind_Spinner.setSelection(spinnerPosition);

                                                EditText edit_petWeight = (EditText) findViewById(R.id.edit_petWeight);
                                                edit_petWeight.setText((String) document.get("petWeight"));

                                                EditText edit_petBirthday = (EditText) findViewById(R.id.edit_birthday);
                                                Timestamp i = (Timestamp) document.get("petBrithday");

                                                SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMdd" , Locale.KOREA );
                                                String str = sdf.format( new Date( i.toDate().getTime()));
                                                Log.d("Debug", str);


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
                Log.d("Debug", uri.toString());
                imageUrl = getRealPathFromUri(data.getData());
                RequestOptions cropOptions = new RequestOptions();
                Glide.with(getApplicationContext())
                        .load(imageUrl)
                        .apply(cropOptions.optionalCircleCrop())
                        .into(Edit_profile);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(requestCode == 7465 && resultCode == RESULT_OK){
            edit_petKind = data.getExtras().getString("my_data");
            EditText photo_value = findViewById(R.id.kind_photoValue2);
            Kind_spinner.setVisibility(View.GONE);
            photo_value.setVisibility(View.VISIBLE);
            photo_value.setText(edit_petKind);
            photo_value.setEnabled(false);
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
