package com.example.project2.Friend;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.project2.Chatting.StartActivity;
import com.example.project2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FriendMainActivity extends AppCompatActivity {

    PeopleFragment peopleFragment;
    ChattingFragment chattingFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainfriend);

        FragmentManager FM = getSupportFragmentManager();

        peopleFragment = new PeopleFragment();
        chattingFragment = new ChattingFragment();

        FM.beginTransaction().replace(R.id.friend_frameLayout,peopleFragment).commit();

        getSupportActionBar().hide();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case (R.id.action_people):
                        peopleFragment = new PeopleFragment();
                        FM.beginTransaction().replace(R.id.friend_frameLayout,peopleFragment).commit();
                        break;
                    case (R.id.action_chat):
                        /*
                        chattingFragment = new ChattingFragment();
                        FM.beginTransaction().replace(R.id.friend_frameLayout, chattingFragment).commit();
                        */
                        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });

    }
}