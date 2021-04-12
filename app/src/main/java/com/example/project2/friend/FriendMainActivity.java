package com.example.project2.friend;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project2.R;

public class FriendMainActivity extends AppCompatActivity {

    PeopleFragment peopleFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainfriend);

        peopleFragment = new PeopleFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.friend_frameLayout,peopleFragment).commit();

    }
}