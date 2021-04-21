package com.example.project2.Login_Membership;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project2.FirebaseDB.UserLoginDB;
import com.example.project2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserJoinActivity extends AppCompatActivity {

    private Button btn_User;
    private Button btn_UserLogin;


    private FirebaseAuth mAuth;
    private static final String TAG = "UserActivityTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_join);

        mAuth = FirebaseAuth.getInstance();

        btn_User= (Button) findViewById(R.id.btn_SignUp);
        btn_User.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // 데이터 베이스 비교 후 아이디 생성 완료 되면 애완동물 정보 입력 액티비티 이동
                signUp();
            }
        });

        btn_UserLogin = (Button)findViewById(R.id.btn_UserLogin);
        btn_UserLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // 로그인 화면으로 이동
                Intent intent  = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void signUp() {

        String email = ((EditText) findViewById(R.id.edit_id)).getText().toString();
        String password = ((EditText) findViewById(R.id.edit_pw)).getText().toString();
        String passwordCheck = ((EditText) findViewById(R.id.edit_pw2)).getText().toString();

        if(email.length() > 0 && password.length() > 0 && passwordCheck.length() > 0  ){

            if(password.equals(passwordCheck)){ // 비밀번호확인

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    // 회원가입 성공 시
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    UserLoginDB userLoginDB  = new UserLoginDB(email, user.getUid(),password);
                                    FirebaseFirestore dbs = FirebaseFirestore.getInstance();
                                    dbs.collection("Login_user").document(user.getUid()).set(userLoginDB).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("UserJoin","LoginDataUploadSuccess");
                                        }
                                    });
                                    Log.d(TAG, "CreateUser With Email : success");
                                    Intent intent = new Intent(getApplicationContext(), UserinfoActivity.class);
                                    Toast.makeText(UserJoinActivity.this,"회원가입 성공 ",Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                } else { // 회원가입 실패 시
                                    if(task.getException() != null){
                                        Log.d(TAG, "CreateUser With Email : failure", task.getException());
                                        Toast.makeText(UserJoinActivity.this,"회원가입 실패 ",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }else{
                Toast.makeText(this,"비밀번호가 일치하지 않습니다. ",Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this,"이메일 또는 비밀번호를 입력해주세요 ",Toast.LENGTH_SHORT).show();
        }

    }

    //뒤로가기 눌렀을때
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        // 뒤로가기 누르면 바로 종료
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

}


