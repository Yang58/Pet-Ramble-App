package com.example.project2.Login_Membership;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project2.Data.DBHelper;
import com.example.project2.Data.Userinfo;
import com.example.project2.Main.MainActivity;
import com.example.project2.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserinfoActivity extends AppCompatActivity {



    private static final String TAG = "UserinfoActivity";

    private Button Check;

    private EditText petAge;
    private EditText petKind;
    private EditText petName;
    private EditText Edit_name;
    private EditText Edit_age;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        Edit_name = (EditText)findViewById(R.id.edit_Name);
        Edit_age = (EditText)findViewById(R.id.edit_Age);


        petAge = (EditText)findViewById(R.id.edit_petAge);
        petName = (EditText)findViewById(R.id.edit_petName);
        petKind = (EditText)findViewById(R.id.edit_petKind);

        dbHelper = new DBHelper(this);


        Check = (Button)findViewById(R.id.btn_Check);
        Check.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(Edit_name.length() > 0 && Edit_age.length() > 0){ // 회원 정보 입력
                    if(petAge.length() > 0 && petName.length() > 0 && petKind.length() > 0 ){
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        SQLupdate();
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
        dbHelper.close();
    }
    private void profileUpdate() {

        String person_name = ((EditText) findViewById(R.id.edit_Name)).getText().toString();
        String person_age = ((EditText) findViewById(R.id.edit_Age)).getText().toString();

        String petName = ((EditText) findViewById(R.id.edit_petName)).getText().toString();
        String petAge = ((EditText) findViewById(R.id.edit_petAge)).getText().toString();
        String petKind = ((EditText) findViewById(R.id.edit_petKind)).getText().toString();

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
        String petKind = ((EditText) findViewById(R.id.edit_petKind)).getText().toString();

        db = dbHelper.getWritableDatabase();
        sql = String.format("INSERT INTO info VALUES('"+person_name+"','"+person_age+"','"+petName+"','"+petAge+"','"+petKind+"',0);");

        db.execSQL(sql);
        Log.d(TAG,sql);

        // 전체 삭제
//        db = dbHelper.getWritableDatabase();
//        sql = "DELETE FROM info;";
//        db.execSQL(sql);

        // 조회
//        db = dbHelper.getReadableDatabase();
//        sql = "SELECT * FROM info;";
//        Cursor cursor = db.rawQuery(sql, null);
//        if(cursor.getCount() > 0){
//            while (cursor.moveToNext()){
//                cursor.getString(0);
//                cursor.getString(1);
//                cursor.getString(2);
//                cursor.getString(3);
//            }
//        }



    }
}
