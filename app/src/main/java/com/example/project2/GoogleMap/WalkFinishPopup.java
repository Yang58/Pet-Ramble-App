package com.example.project2.GoogleMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.project2.R;

public class WalkFinishPopup extends Activity {

    TextView TV_WalkTime;

    Button close;
    Button restart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_walk_finish_popup);

        TV_WalkTime = (TextView)findViewById(R.id.TV_WalkTime);

        close = (Button)findViewById(R.id.btn_close);

        //데이터 가져오기
        Intent intent = getIntent();
        String sec = intent.getStringExtra("sec");
        String min = intent.getStringExtra("min");
        String hour = intent.getStringExtra("hour");

        TV_WalkTime.setText(hour+"시간  "+min+"분  "+sec+"초"+"산책 하였습니다.");

        close.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}