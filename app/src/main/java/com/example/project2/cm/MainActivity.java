package com.example.project2.cm;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.project2.R;
import com.example.project2.cm.ui.main.CommunityMain;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        /* 프래그먼트를 이렇게 설정할 수도 있음.*/
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, new CommunityMain(), "cmMain");
            transaction.commitNow();
        }

        /*
        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.fragment_test, container, true);
        ProgressBar pb = findViewById(R.id.progressBar);
        SeekBar sb = findViewById(R.id.seekBar);
        pb.setMax(sb.getMax());
        pb.setProgress(20);
        WebView wv = findViewById(R.id.webV);
        wv.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        wv.setWebViewClient(new WebViewClient());
        wv.loadUrl("https://google.com");
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pb.setProgress(sb.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }
        });*/
    }

    public void onClickButton(View view){
        //Toast t = Toast.makeText(this.getApplicationContext(), "눌림", Toast.LENGTH_SHORT);
        //t.show();
        //LinearLayout container = (LinearLayout) findViewById(R.id.container);
        //container.removeAllViews();
        //LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //inflater.inflate(R.layout.fragment_community_main,container,true);
        /*
        FragmentManager fm = getSupportFragmentManager();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fm.findFragmentByTag("fragment_community_main"))
                .commitNow();
                */
    }
}