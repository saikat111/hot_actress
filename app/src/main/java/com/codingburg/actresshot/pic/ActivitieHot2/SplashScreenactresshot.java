package com.codingburg.actresshot.pic.ActivitieHot2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.codingburg.actresshot.R;


public class SplashScreenactresshot extends AppCompatActivity {
    Animation top_anumation, buttom_animation, middel_animation;
    View first, secound, third, four, five, six, seven;
    Boolean isCancelled = false;
    ProgressBar progressBar;
    long nid = 0;
    String url = "";
    TextView name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        top_anumation = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        buttom_animation = AnimationUtils.loadAnimation(this,R.anim.buttom_animation);
        middel_animation = AnimationUtils.loadAnimation(this,R.anim.middel_animation);
        first = findViewById(R.id.first_line);
        secound = findViewById(R.id.secound_line);
        third = findViewById(R.id.third_line);
        four = findViewById(R.id.fourth_line);
        five = findViewById(R.id.five_line);
        six = findViewById(R.id.six_line);
        seven = findViewById(R.id.seven_line);
        name = findViewById(R.id.name);
        progressBar = findViewById(R.id.progressBar);

        first.setAnimation(top_anumation);
        secound.setAnimation(top_anumation);
        third.setAnimation(top_anumation);
        four.setAnimation(top_anumation);
        five.setAnimation(top_anumation);
        six.setAnimation(top_anumation);
        seven.setAnimation(top_anumation);
        name.setAnimation(middel_animation);
        progressBar.setAnimation(buttom_animation);

        if (getIntent().hasExtra("nid")) {
            nid = getIntent().getLongExtra("nid", 0);
            url = getIntent().getStringExtra("external_link");
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isCancelled) {
                    if (nid == 0) {
                        if (url.equals("") || url.equals("no_url")) {
                            Intent intent = new Intent(getApplicationContext(), HomeScreenactresshot.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent a = new Intent(getApplicationContext(), HomeScreenactresshot.class);
                            startActivity(a);

                            Intent b = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(b);

                            finish();
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), HomeScreenactresshot.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        }, 3000);

    }

}