package com.example.project2.Friend;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.project2.Chatting.PublicChatStartFragment;
import com.example.project2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FriendMainActivity extends AppCompatActivity {

    PeopleFragment peopleFragment;
    PublicChatStartFragment publicChatStartFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainfriend);

        FragmentManager FM = getSupportFragmentManager();

        peopleFragment = new PeopleFragment();
        publicChatStartFragment = new PublicChatStartFragment();

        FM.beginTransaction().replace(R.id.friend_frameLayout1,peopleFragment).commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case (R.id.action_people):
                        peopleFragment = new PeopleFragment();
                        FM.beginTransaction().replace(R.id.friend_frameLayout1,peopleFragment).commit();
                        break;
                    case (R.id.action_chat):
                        publicChatStartFragment = new PublicChatStartFragment();
                        FM.beginTransaction().replace(R.id.friend_frameLayout1, publicChatStartFragment).commit();
                        /*
                        Intent intent = new Intent(getApplicationContext(), PublicChatStartFragment.class);
                        startActivity(intent);
                        */
                        break;
                }
                return false;
            }
        });

    }
}