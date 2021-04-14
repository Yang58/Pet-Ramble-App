package com.example.project2.Chatting;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project2.Data.ChattingData;
import com.example.project2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MessageActivity extends AppCompatActivity {

    private String destination;
    private Button button;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        destination = user.getUid();

        button = (Button) findViewById(R.id.message_button);
        editText = (EditText)findViewById(R.id.message_edit);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChattingData chattingData = new ChattingData();
                chattingData.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                chattingData.destinationUid = destination;

                FirebaseDatabase.getInstance().getReference().child("chatroom").push().setValue(chattingData);

            }
        });
    }
}