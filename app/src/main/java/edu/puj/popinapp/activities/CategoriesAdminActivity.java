package edu.puj.popinapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import edu.puj.popinapp.databinding.ActivityCategoriesAdminBinding;
import edu.puj.popinapp.databinding.ActivityMainBinding;

public class CategoriesAdminActivity extends AppCompatActivity {

    private ActivityCategoriesAdminBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoriesAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.listUserButton.setOnClickListener(view->
                startActivity(new Intent(this,ListUserActivity.class)));
        binding.analiticsPopinappButton.setOnClickListener(view->
                startActivity(new Intent(this,AnaliticsPiponappActivity.class)));
    }
}