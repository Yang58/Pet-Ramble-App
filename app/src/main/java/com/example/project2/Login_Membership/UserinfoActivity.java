package com.example.project2.Login_Membership;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.project2.Data.DBHelper;
import com.example.project2.Data.Userinfo;
import com.example.project2.Main.MainActivity;
import com.example.project2.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UserinfoActivity extends AppCompatActivity {

    private static final String TAG = "UserinfoActivity";

    private Button Check;

    ImageView user_profile;

    private EditText petAge;
    private EditText petKind;
    private EditText petName;
    private EditText Edit_name;
    private EditText Edit_age;

    int GET_GALLERY_IMAGE = 0;

    DBHelper dbHelper;

    String[] items = {"말티즈", "빠삐용","요크셔테리어","시베리아 허스키", "골든 두들","골드 리트리버"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        Edit_name = (EditText)findViewById(R.id.edit_Name);
        Edit_age = (EditText)findViewById(R.id.edit_Age);

        petAge = (EditText)findViewById(R.id.edit_petAge);
        petName = (EditText)findViewById(R.id.edit_petName);
        petKind = (EditText) findViewById(R.id.edit_petKind);

        user_profile = (ImageView) findViewById(R.id.profile_imageView);
                user_profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                        startActivityForResult(intent, 1);
                    }
                });

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try{
            // 사진을 선택하고 왔을 때만 처리한다.
            if(resultCode == RESULT_OK){
                // 선택한 이미지를 지칭하는 Uri 객체를 얻어온다.
                Uri uri = data.getData();
                // Uri 객체를 통해서 컨텐츠 프로바이더를 통해 이미지의 정보를 가져온다.
                ContentResolver resolver = getContentResolver();
                Cursor cursor = resolver.query(uri, null, null, null, null);
                cursor.moveToNext();

                // 사용자가 선택한 이미지의 경로 데이터를 가져온다.
                int index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                String source = cursor.getString(index);

                // 경로 데이터를 통해서 이미지 객체를 생성한다
                Bitmap bitmap = BitmapFactory.decodeFile(source);

                // 이미지의 크기를 조정한다.
                Bitmap bitmap2 = resizeBitmap(1024, bitmap);

                // 회전 각도 값을 가져온다.
                float degree = getDegree(source);
                Bitmap bitmap3 = rotateBitmap(bitmap2, degree);

                user_profile.setImageBitmap(bitmap3);

            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public Bitmap resizeBitmap(int targetWith, Bitmap source){
        double ratio = (double)targetWith / (double)source.getWidth();

        int targetHeight = (int)(source.getHeight() * ratio);

        Bitmap result = Bitmap.createScaledBitmap(source, targetWith, targetHeight, false);

        if(result != source){
            source.recycle();
        }
        return result;
    }

    public float getDegree(String source){
        try{
            ExifInterface exif = new ExifInterface(source);

            int degree = 0;

            int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            switch (ori){
                case ExifInterface.ORIENTATION_ROTATE_90 :
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180 :
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270 :
                    degree = 270;
                    break;
            }
            return (float)degree;
        }catch(Exception e){
            e.printStackTrace();
        }
        return 0.0f;
    }

    public Bitmap rotateBitmap(Bitmap bitmap, float degree){
        try{

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            Matrix matrix = new Matrix();
            matrix.postRotate(degree);

            Bitmap bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
            bitmap.recycle();

            return bitmap2;

        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }


    private void profileUpdate() {

        String person_name = ((EditText) findViewById(R.id.edit_Name)).getText().toString();
        String person_age = ((EditText) findViewById(R.id.edit_Age)).getText().toString();

        String petName = ((EditText) findViewById(R.id.edit_petName)).getText().toString();
        String petAge = ((EditText) findViewById(R.id.edit_petAge)).getText().toString();
        String petKind = ((EditText)findViewById(R.id.edit_petKind)).getText().toString();

        if (person_name.length() > 0 && person_age.length() > 0) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Userinfo userinfo = new Userinfo(person_name, person_age, petName, petAge, petKind);

            if(user != null){
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

        } else {
            Toast.makeText(this, "회원 정보를 입력해주세요 ", Toast.LENGTH_SHORT).show();
        }
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
