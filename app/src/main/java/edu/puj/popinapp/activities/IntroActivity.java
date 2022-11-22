package edu.puj.popinapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import edu.puj.popinapp.R;
import edu.puj.popinapp.databinding.ActivityIntroBinding;


public class IntroActivity extends AppCompatActivity {

    private ActivityIntroBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Add animations
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.scroll_up);

        binding.imageView.setAnimation(animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(IntroActivity.this, OpcionUserStartActivity.class);
                startActivity(intent);
                finish();
            }
        },2000);


    }
}