package com.jadyn.rocketdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SmokeActivity extends AppCompatActivity {

    @Bind(R.id.smoke_iv_top)
    ImageView smokeIvTop;
    @Bind(R.id.smoke_iv_bottom)
    ImageView smokeIvBottom;
    @Bind(R.id.smoke_contain)
    RelativeLayout smokeContain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smoke);
        ButterKnife.bind(this);


        ObjectAnimator smokeBottom = ObjectAnimator.ofFloat(smokeIvBottom, "alpha", 1.0f, 0);
        smokeBottom.setDuration(1200);
        ObjectAnimator smokeTop = ObjectAnimator.ofFloat(smokeIvTop, "alpha", 0, 1.0f);
        smokeTop.setDuration(1200);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(smokeBottom,smokeTop);
        animatorSet.start();
        
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                finish();
            }
        });
    }
}
