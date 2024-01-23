package com.freethumbnailmaker.nowatermark.activity;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.freethumbnailmaker.nowatermark.R;

public class SplashActivity extends BaseActivity {
    RelativeLayout imageViewLogo;
    View first,second,third,fourth,fifth,sixth;
    //Animations
    Animation topAnimantion,bottomAnimation,middleAnimation;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_main);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        imageViewLogo = findViewById(R.id.imageView);
        first = findViewById(R.id.first_line);
        second = findViewById(R.id.second_line);
        third = findViewById(R.id.third_line);
        fourth = findViewById(R.id.fourth_line);
        fifth = findViewById(R.id.fifth_line);
        sixth = findViewById(R.id.sixth_line);
        //Animation Calls
        topAnimantion = AnimationUtils.loadAnimation(this, R.anim.top);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom);
//        middleAnimation = AnimationUtils.loadAnimation(this, R.anim.middle_animation);
        //-----------Setting Animations to the elements of Splash
        second.setAnimation(topAnimantion);
        third.setAnimation(topAnimantion);
        fourth.setAnimation(topAnimantion);
        fifth.setAnimation(topAnimantion);
        sixth.setAnimation(topAnimantion);
        imageViewLogo.setAnimation(middleAnimation);
        new Handler().postDelayed(this::toHome, 2000);
    }




    private void toHome() {
        runOnUiThread(() -> new Handler(Looper.myLooper()).postDelayed(() ->{
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 500));
    }




}
