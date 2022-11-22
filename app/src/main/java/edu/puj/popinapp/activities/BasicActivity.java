package edu.puj.popinapp.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import javax.inject.Inject;

import edu.puj.popinapp.App;
import edu.puj.popinapp.utils.AlertsHelper;


public class BasicActivity extends AppCompatActivity {
    @Inject
    AlertsHelper alertsHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ((App) getApplicationContext()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
    }
}