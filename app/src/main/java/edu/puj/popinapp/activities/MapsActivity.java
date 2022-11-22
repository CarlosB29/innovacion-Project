package edu.puj.popinapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import edu.puj.popinapp.R;
import edu.puj.popinapp.databinding.ActivityMapsBinding;


public class MapsActivity<valorRandom> extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient mFusedLocationClient;
    private LatLng ubicacion;
    private MarkerOptions marcador;
    private MarkerOptions marcaBusqueda;
    private MarkerOptions marcaToque;
    private boolean settingsOK;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location nuevaUbicacion;
    private JSONArray localizaciones;
    private double radio;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private SensorEventListener lightSensorListener;

    private String direccion;
    private Geocoder mGeocoder;
    private double lowerLeftLatitude;
    private double lowerLeftLongitude;
    private double upperRightLatitude;
    private double upperRigthLongitude;
    private Address resultadoBusqueda;
    private RoadManager roadManager;
    Polyline roadOverlay;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        marcador = new MarkerOptions();
        marcaBusqueda = new MarkerOptions();
        marcaToque = new MarkerOptions();
        ubicacion = new LatLng(0,0);
        settingsOK = false;
        radio = 6378.1;
        localizaciones = new JSONArray();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        lightSensorListener = lecturaSensor;
        lowerLeftLatitude = 1.396967;
        lowerLeftLongitude= -78.903968;
        upperRightLatitude= 11.983639;
        upperRigthLongitude= -71.869905;
        mGeocoder = new Geocoder(this);
        roadManager = new OSRMRoadManager(this, "ANDROID");


        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        solicitarPermisoUbicacion.launch(Manifest.permission.ACCESS_FINE_LOCATION);

        mFusedLocationClient.getLastLocation().addOnSuccessListener(this,ubicacionObtenida);
        mLocationRequest = createLocationRequest();
        mLocationCallback = callbackUbicacion;

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        checkLocationSettings();
        sensorManager.registerListener(lightSensorListener,lightSensor,SensorManager.SENSOR_DELAY_NORMAL);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        marcador.position(ubicacion).title("Ubicacion Actual");
        mMap.addMarker(marcador);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacion));

    }


    ActivityResultLauncher<String> solicitarPermisoUbicacion = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @SuppressLint("MissingPermission")
                @Override
                public void onActivityResult(Boolean result) {
                    if(result == true){
                        mFusedLocationClient.getLastLocation().addOnSuccessListener(MapsActivity.this,ubicacionObtenida);
                    }else{
                        Toast.makeText(MapsActivity.this, "No se ha otorgado el permiso para la ubicacion.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    private int valorRandom = (int) (Math.random()*7);


    private OnSuccessListener<Location> ubicacionObtenida = new OnSuccessListener<Location>() {
        @Override
        public void onSuccess(Location location) {
            if (location != null) {
                LatLng ubiEncontrada2 = new LatLng(0,0);
                if(valorRandom==0){
                    double latitud=4.661798;
                    double longitud=-74.067107;
                    ubiEncontrada2 = new LatLng(latitud,longitud);}
                if(valorRandom==1){
                    double latitud=4.621041;
                    double longitud=-74.087766;
                    ubiEncontrada2 = new LatLng(latitud,longitud);}
                if(valorRandom==2){
                    double latitud=4.693714;
                    double longitud=-74.084815;
                    ubiEncontrada2 = new LatLng(latitud,longitud);}
                if(valorRandom==3){
                    double latitud=4.600025;
                    double longitud=-74.084343;
                    ubiEncontrada2 = new LatLng(latitud,longitud);}
                if(valorRandom==4){
                    double latitud=4.688240;
                    double longitud=-74.062303;
                    ubiEncontrada2 = new LatLng(latitud,longitud);}
                if(valorRandom==5){
                    double latitud=4.687120;
                    double longitud=-74.029650;
                    ubiEncontrada2 = new LatLng(latitud,longitud);}
                if(valorRandom==6){
                    double latitud=4.701138;
                    double longitud=-74.030111;
                    ubiEncontrada2 = new LatLng(latitud,longitud);}
                if(valorRandom==7){
                    double latitud=4.722381;
                    double longitud=-74.038318;
                    ubiEncontrada2 = new LatLng(latitud,longitud);}
                if(valorRandom==7){
                    double latitud=4.758763;
                    double longitud=-74.066221;
                    ubiEncontrada2 = new LatLng(latitud,longitud);}
                if(valorRandom==8){
                    double latitud=4.694694;
                    double longitud=-74.137701;
                    ubiEncontrada2 = new LatLng(latitud,longitud);}

                ubicacion = new LatLng(location.getLatitude(),location.getLongitude());
                onMapReady(mMap);
                drawRoute(new GeoPoint(ubicacion.latitude,ubicacion.longitude),new GeoPoint(ubiEncontrada2.latitude,ubiEncontrada2.longitude));

                marcaBusqueda.position(ubiEncontrada2).title(direccion);
                mMap.addMarker(marcador);
                mMap.addMarker(marcaBusqueda);
                mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(ubiEncontrada2));
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
        sensorManager.registerListener(lightSensorListener,lightSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        sensorManager.unregisterListener(lightSensorListener);
    }

    ActivityResultLauncher<IntentSenderRequest> getLocationSettings =
            registerForActivityResult(
                    new ActivityResultContracts.StartIntentSenderForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            Toast.makeText(MapsActivity.this, result.getResultCode(), Toast.LENGTH_SHORT).show();
                            if(result.getResultCode() == RESULT_OK){
                                settingsOK = true;
                                startLocationUpdates();
                            }else{
                                Toast.makeText(MapsActivity.this, "GPS Apagado", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

    private void checkLocationSettings(){
        LocationSettingsRequest.Builder builder = new
                LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                settingsOK = true;
                startLocationUpdates();
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(((ApiException) e).getStatusCode() == CommonStatusCodes.RESOLUTION_REQUIRED){
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    IntentSenderRequest isr = new IntentSenderRequest.Builder(resolvable.getResolution()).build();
                    getLocationSettings.launch(isr);
                }else {
                    Toast.makeText(MapsActivity.this, "No hay GPS", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private LocationRequest createLocationRequest(){
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(10000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    private void stopLocationUpdates(){
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private LocationCallback callbackUbicacion = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            nuevaUbicacion = locationResult.getLastLocation();
            if(nuevaUbicacion!=null)
            {
                if(distance(ubicacion.latitude,ubicacion.longitude,
                        nuevaUbicacion.getLatitude(),nuevaUbicacion.getLongitude()) >= 0.03)
                {
                    writeJSONObject();
                    ubicacion = new LatLng(nuevaUbicacion.getLatitude(),nuevaUbicacion.getLongitude());
                    marcador.position(ubicacion).title("Ubicacion Actual");
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacion));
                }else{
                    ubicacion = new LatLng(nuevaUbicacion.getLatitude(),nuevaUbicacion.getLongitude());
                    marcador.position(ubicacion).title("Ubicacion Actual");
                }

            }
        }
    };

    public JSONObject toJSON () {
        JSONObject obj = new JSONObject();
        try {
            obj.put("latitud", nuevaUbicacion.getLatitude());
            obj.put("longitud", nuevaUbicacion.getLongitude());
            obj.put("fecha", new Date(System.currentTimeMillis()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    private void writeJSONObject(){
        localizaciones.put(toJSON());
        Writer output = null;
        String filename= "ubicaciones.json";
        try {
            File file = new File(getBaseContext().getExternalFilesDir(null), filename);
            output = new BufferedWriter(new FileWriter(file));
            output.write(localizaciones.toString());
            output.close();
            Toast.makeText(getApplicationContext(), "Ubicacion Guardada",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double distance(double lat1, double long1, double lat2, double long2) {
        double latDistance = Math.toRadians(lat1 - lat2);
        double lngDistance = Math.toRadians(long1 - long2);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double result = radio * c;
        return Math.round(result*100.0)/100.0;
    }

    private SensorEventListener lecturaSensor = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if(mMap != null)
            {
                if(sensorEvent.values[0]<2000)
                {
                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this, R.raw.style_night));
                }else{
                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this, R.raw.style_day));
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    private void drawRoute(GeoPoint start, GeoPoint finish){
        ArrayList<GeoPoint> routePoints = new ArrayList<>();
        routePoints.add(start);
        routePoints.add(finish);
        Road road = roadManager.getRoad(routePoints);
        List<GeoPoint> camino = RoadManager.buildRoadOverlay(road).getActualPoints();
        dibujarRuta(camino);
        BigDecimal distancia = new BigDecimal(road.mLength);
        distancia = distancia.setScale(2, RoundingMode.HALF_UP);
        Toast.makeText(this, "Distancia: "+distancia+" kms.", Toast.LENGTH_SHORT).show();
    }

    private void dibujarRuta(List<GeoPoint> camino){
        Polyline ruta;
        PolylineOptions puntosruta = new PolylineOptions();
        ArrayList<LatLng> puntos = new ArrayList<LatLng>();
        for(int i=0;i<camino.size();i++)
        {
            puntos.add(new LatLng(camino.get(i).getLatitude(),camino.get(i).getLongitude()));
        }
        for(int j=0;j<puntos.size();j++)
        {
            puntosruta.add(puntos.get(j));
        }
        ruta = mMap.addPolyline(puntosruta);
        ruta.setColor(0xffff0000);
    }

}
