package edu.puj.popinapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import edu.puj.popinapp.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
private ActivityMainBinding binding;
    SensorManager mySensorManager;
    Sensor myProximitySensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mySensorManager = (SensorManager) getSystemService(
                Context.SENSOR_SERVICE);
        myProximitySensor = mySensorManager.getDefaultSensor(
                Sensor.TYPE_PROXIMITY);
        if (myProximitySensor == null) {
            Toast.makeText(this,"No Proximity Sensor!",Toast.LENGTH_SHORT).show();
        } else {
            mySensorManager.registerListener(proximitySensorEventListener,
                    myProximitySensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        binding.buttonTopleft.setOnClickListener(view ->
                startActivity(new Intent(this,listActivity.class).putExtra("id",1)));
        binding.parrilladas.setOnClickListener(view ->
                startActivity(new Intent(this,listActivity.class).putExtra("id",2)));
        binding.buttonSeaFood.setOnClickListener(view ->
                startActivity(new Intent(this,listActivity.class).putExtra("id",3)));
        binding.cakeshop.setOnClickListener(view ->
                startActivity(new Intent(this,listActivity.class).putExtra("id",4)));
        binding.bakery.setOnClickListener(view ->
                startActivity(new Intent(this,listActivity.class).putExtra("id",5)));
        binding.buttonVegetarian.setOnClickListener(view ->
                startActivity(new Intent(this,listActivity.class).putExtra("id",6)));
        binding.buttonStore.setOnClickListener(view ->
                startActivity(new Intent(this,listActivity.class).putExtra("id",7)));
        binding.colombianFood.setOnClickListener(view ->
                startActivity(new Intent(this,listActivity.class).putExtra("id",8)));
    }
    SensorEventListener proximitySensorEventListener
            = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub
        }
        @Override
        public void onSensorChanged(SensorEvent event) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                if (event.values[0] == 0) {
                    params.flags |= WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
                    params.screenBrightness = 0;
                    getWindow().setAttributes(params);

                } else {
                    params.flags |= WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
                    params.screenBrightness = -1f;
                    getWindow().setAttributes(params);
                }
            }
        }
    };

}
