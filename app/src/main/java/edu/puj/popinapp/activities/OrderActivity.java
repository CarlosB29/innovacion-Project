package edu.puj.popinapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.puj.popinapp.R;


public class OrderActivity extends AppCompatActivity {

    Button buttonPedir, buttonGeo, buttonCamera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        buttonPedir = findViewById(R.id.botonPedir);
        buttonPedir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MapsActivity.class);
                startActivity(intent);
            }
        });
        buttonGeo = findViewById(R.id.botonGeo);
        double lat = 4.62867;
        double lng =-74.06461;
        double lat1=4.677148;
        double lng1=-74.103737;
        buttonGeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new
                        Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?" +
                        "saddr=" + lat + "," + lng + "&daddr=" + lat1 + "," +
                        lng1));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        });
        buttonCamera = findViewById(R.id.buttonCamara);
        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(view.getContext(), CamaraActivity.class);
                startActivity(intent);
            }
        });

    }
}