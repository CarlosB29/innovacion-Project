package edu.puj.popinapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.javafaker.Faker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import edu.puj.popinapp.R;
import edu.puj.popinapp.databinding.ActivityRegisterBinding;
import edu.puj.popinapp.models.DatabaseRoutes;
import edu.puj.popinapp.models.UserInfo;


public class RegisterActivity extends BasicActivity {
    public static final String TAG = AuthenticationActivity.class.getName();
    private ActivityRegisterBinding binding;
    private DatePickerDialog picker;
    private FusedLocationProviderClient myLocation;
    private Double latitude,longitude;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private String gender;
    private String typeClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        giveLatLong();

        binding.btnMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedGender("hombre");
            }
        });
        binding.btnFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedGender("mujer");
            }
        });
        binding.btnOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedGender("otro");
            }
        });
        binding.btnclient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selesctedStatus("cliente");
            }
        });
        binding.btnowner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selesctedStatus("dueño");
            }
        });


        binding.createButton.setOnClickListener(view -> doSignup());
        binding.createDisplayBirthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                picker = new DatePickerDialog(RegisterActivity.this,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayofmonth){
                        binding.createDisplayBirthDay.setText(dayofmonth + "/" + (month + 1) + "/" + year);
                    }
                },year, month, day);
                picker.show();


            }
        });


    }

    private void selesctedStatus(String type) {
        typeClient = type;
    }

    private void selectedGender(String gender) {
        this.gender = gender;
    }

    private void giveLatLong() {
        if(ContextCompat.checkSelfPermission(RegisterActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"Se accedio al permiso de ubicacion." , Toast.LENGTH_SHORT).show();

        }else{
            ActivityCompat.requestPermissions(RegisterActivity.this,new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION},1);
        }

        myLocation = LocationServices.getFusedLocationProviderClient(RegisterActivity.this);

        myLocation.getLastLocation().addOnSuccessListener(RegisterActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    latitude = location.getLatitude();
                    longitude  = location.getLongitude();


                }
            }

        });
    }
    private void doSignup() {
        String email = Objects.requireNonNull(binding.createEmail.getEditText()).getText().toString();
        String pass = Objects.requireNonNull(binding.createPass.getEditText()).getText().toString();
        String name = Objects.requireNonNull(binding.createDisplayName.getEditText()).getText().toString();
        String lasNam = Objects.requireNonNull(binding.createDisplaylastName.getEditText()).getText().toString();
        String direction = Objects.requireNonNull(binding.createDisplayDirection.getEditText()).getText().toString();

        if (email.isEmpty()) {
            alertsHelper.shortSimpleSnackbar(binding.getRoot(), getString(R.string.mail_error_label));
            binding.createEmail.setErrorEnabled(true);
            binding.createEmail.setError(getString(R.string.mail_error_label));
            return;
        }

        if (pass.isEmpty()) {
            alertsHelper.shortSimpleSnackbar(binding.getRoot(), getString(R.string.error_pass_label));
            binding.createPass.setErrorEnabled(true);
            binding.createPass.setError(getString(R.string.error_pass_label));
            return;
        }
        if (name.isEmpty()) {
            alertsHelper.shortSimpleSnackbar(binding.getRoot(), getString(R.string.error_name_label));
            binding.createDisplayName.setErrorEnabled(true);
            binding.createDisplayName.setError(getString(R.string.error_name_label));
            return;
        }
        if (lasNam.isEmpty()) {
            alertsHelper.shortSimpleSnackbar(binding.getRoot(), getString(R.string.error_lastName_label));
            binding.createDisplaylastName.setErrorEnabled(true);
            binding.createDisplaylastName.setError(getString(R.string.error_lastName_label));
            return;
        }
        if (direction.isEmpty()) {
            alertsHelper.shortSimpleSnackbar(binding.getRoot(), getString(R.string.error_direction_label));
            binding.createDisplayDirection.setErrorEnabled(true);
            binding.createDisplayDirection.setError(getString(R.string.error_direction_label));
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(authResult -> {
                    DatabaseReference reference = mDatabase.getReference(DatabaseRoutes.getUser(authResult.getUser().getUid()));
                    UserInfo tmpUser = new UserInfo(
                            binding.createEmail.getEditText().getText().toString(),
                            binding.createDisplayName.getEditText().getText().toString(),
                            binding.createDisplaylastName.getEditText().getText().toString(),
                            binding.createDisplayDirection.getEditText().getText().toString(),
                            binding.createDisplayBirthDay.getEditableText().toString(),gender,
                            Long.parseLong(binding.createPhone.getEditText().getText().toString()),
                            typeClient,
                            latitude,
                            longitude,
                            null);
                    reference.setValue(tmpUser).addOnSuccessListener(unused ->
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class)))
                            .addOnFailureListener(e ->
                                    alertsHelper.shortSimpleSnackbar(binding.getRoot(), e.getLocalizedMessage()));
                    finish();
                })
                .addOnFailureListener(e ->
                        alertsHelper.shortSimpleSnackbar(binding.getRoot(), e.getLocalizedMessage()));
    }
}