package com.example.project2.Login_Membership;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project2.Main.MainActivity;
import com.example.project2.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class LoginActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;

    private int RC_SIGN_IN = 10;
    private FirebaseAuth mAuth;
    private static final String TAG = "LoginActivityTAG";

    private Button btn_login; // 로그인 버튼
    private TextView btn_User; // 회원가입 버튼
    private TextView btn_resetPassword; // 비밀번호 변경버튼

    private EditText id ;
    private EditText pw ;

    // Firebase 회원정보
    DocumentReference mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();


        id = (EditText) findViewById(R.id.edit_Login_id);
        pw  = (EditText) findViewById(R.id.edit_Login_pw);

        id.setInputType(EditorInfo.TYPE_NULL);
        id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((EditText)view).setInputType(EditorInfo.TYPE_CLASS_TEXT);
            }
        });
        id.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String inText = textView.getText().toString();
                // Do Something...
                textView.setInputType(EditorInfo.TYPE_NULL);
                return true;
            }
        });

        getSupportActionBar().setTitle("AppName");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff000000));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // firebaes google 로그인
        SignInButton button = (SignInButton) findViewById(R.id.btn_googlelogin);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        // 로그인 버튼 클릭  firebase 서버 이용해서 로그인 가능하게
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id.length() > 0 && pw.length() > 0) {
                    Login();
                } else {
                    Toast.makeText(LoginActivity.this, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 회원가입 버튼 클릭
        btn_User = (TextView) findViewById(R.id.btn_User);
        btn_User.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입 버튼 클릭시 회원가입 액티비티 이동 회원가입 종료시 로그인 화면으로 돌아와서 로그인 가능하게 해야함
                Intent intent = new Intent(getApplicationContext(), UserJoinActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 비밀번호찾기 버튼 클릭
        btn_resetPassword = (TextView) findViewById(R.id.btn_resetPassword);
        btn_resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), ResetpasswordActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {

            }
        }
    }
    // Google ID Login
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Google Login 실패", Toast.LENGTH_SHORT).show();
                        } else {
                            UserUpload();
                            // Sign in success, update UI with the signed-in user's information 구글 로그인 성공
                            Toast.makeText(LoginActivity.this, "Google Login 성공", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // 개인 사용자 Login
    private void Login() {

        final String email = ((EditText) findViewById(R.id.edit_Login_id)).getText().toString();
        final String password = ((EditText) findViewById(R.id.edit_Login_pw)).getText().toString();

        if (email.length() > 0 && password.length() > 0) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(LoginActivity.this, "로그인에 성공했습니다. ", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                if (task.getException() != null) {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginActivity.this, "아이디 비밀번호를 확인해주세요 ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "이메일 또는 비밀번호를 입력해주세요 ", Toast.LENGTH_SHORT).show();
        }
    }

    public void UserUpload(){
        // Firebase 회원정보

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(user.getUid());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document != null){
                        if (document.exists()) {

                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            Log.d(TAG, "No such document");
                            Intent intent = new Intent(getApplicationContext(), UserJoinActivity.class);
                            startActivity(intent);
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        // 뒤로가기 누르면 바로 종료
        moveTaskToBack(true);
        System.exit(1);
    }
}
