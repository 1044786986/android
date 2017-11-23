package com.example.ljh.wechat;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.nostra13.universalimageloader.utils.L;

/**
 * Created by ljh on 2017/10/31.
 */

public class AnimationActivity extends AppCompatActivity{
    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);
        getSupportActionBar().hide();

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.welcome);
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(2000);
                    Intent intent = new Intent(AnimationActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        /*AlphaAnimation alphaAnimation = new AlphaAnimation(0.3f,0.1f);
        alphaAnimation.setDuration(2000);
        imageView.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                imageView.setImageResource(R.drawable.welcome);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(AnimationActivity.this,LoginActivity.class);
                startActivity(intent);
                AnimationActivity.this.finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });*/
    }
}
