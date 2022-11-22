package edu.puj.popinapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import edu.puj.popinapp.databinding.ActivityOpcionUserStartBinding;


public class OpcionUserStartActivity extends AppCompatActivity {

    private ActivityOpcionUserStartBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOpcionUserStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.buttonSignup.setOnClickListener(view->
                startActivity(new Intent(this,RegisterActivity.class)));

    binding.buttonLogin.setOnClickListener(view->startActivity(new Intent(this,AuthenticationActivity.class)));

    }
}