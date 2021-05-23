package com.example.project2.Friend;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.project2.Chatting.PublicChatStartFragment;
import com.example.project2.Main.MainActivity;
import com.example.project2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FriendFragment extends Fragment {

    MainActivity activity;

    PeopleFragment peopleFragment;
    PublicChatStartFragment publicChatStartFragment;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //이 메소드가 호출될떄는 프래그먼트가 엑티비티위에 올라와있는거니깐 getActivity메소드로 엑티비티참조가능
        activity = (MainActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //이제 더이상 엑티비티 참초가안됨
        activity = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //프래그먼트 메인을 인플레이트해주고 컨테이너에 붙여달라는 뜻임
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.friend__fragement , container, false);
        FragmentManager FM = getChildFragmentManager();

        peopleFragment = new PeopleFragment();
        publicChatStartFragment = new PublicChatStartFragment();
        FM.beginTransaction().replace(R.id.friend_frameLayout1,peopleFragment).commit();
        

        BottomNavigationView bottomNavigationView = v.findViewById(R.id.bottomnav1);
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


        return v;
    }

}
