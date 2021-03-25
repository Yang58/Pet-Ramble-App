package com.example.project2.Login_Membership;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class ResetpasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repassword);

        mAuth = FirebaseAuth.getInstance();

        Button btn_send = (Button)findViewById(R.id.btn_reset_send);
        btn_send.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                send();
            }
        });

    }


    // 개인 사용자 Login
    private void send() {

        String email = ((EditText) findViewById(R.id.edit_reset_Email)).getText().toString();

        if (email.length() > 0 ) {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ResetpasswordActivity.this,"이메일 전송완료",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else{
            Toast.makeText(ResetpasswordActivity.this,"이메일을 입력해주세요요",Toast.LENGTH_SHORT).show();

        }
    }
}
