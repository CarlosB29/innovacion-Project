package edu.puj.popinapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.Objects;

import edu.puj.popinapp.R;
import edu.puj.popinapp.databinding.ActivityLoginBinding;
import edu.puj.popinapp.models.DatabaseRoutes;


public class AuthenticationActivity extends BasicActivity {
    public static final String TAG = AuthenticationActivity.class.getName();
    ActivityLoginBinding binding;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        binding.loginButton.setOnClickListener(view -> doLogin());
        binding.signupButton.setOnClickListener(view -> startActivity(new Intent(this, RegisterActivity.class)));
        binding.forgotButton.setOnClickListener(view -> doPassReset());
    }

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, AuthenticationActivity.class);
    }

    private void doLogin() {
        String email = Objects.requireNonNull(binding.loginEmail.getEditText()).getText().toString();
        String pass = Objects.requireNonNull(binding.loginPass.getEditText()).getText().toString();

        if (email.isEmpty()) {
            alertsHelper.shortSimpleSnackbar(binding.getRoot(), getString(R.string.mail_error_label));
            binding.loginEmail.setErrorEnabled(true);
            binding.loginEmail.setError(getString(R.string.mail_error_label));
            return;
        }

        if (pass.isEmpty()) {
            alertsHelper.shortSimpleSnackbar(binding.getRoot(), getString(R.string.error_pass_label));
            binding.loginPass.setErrorEnabled(true);
            binding.loginPass.setError(getString(R.string.error_pass_label));
            return;
        }

        mAuth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener(authResult -> {
                if(email.equals("admin_admin@admin.com")){
                    DatabaseReference reference = mDatabase.getReference(DatabaseRoutes.getUser(authResult.getUser().getUid()) + "/lastLogin");
                    reference.setValue(new Date().getTime()).addOnSuccessListener(runnable -> startActivity(new Intent(AuthenticationActivity.this, CategoriesAdminActivity.class)));
                    finish();
                }else {
                    DatabaseReference reference = mDatabase.getReference(DatabaseRoutes.getUser(authResult.getUser().getUid()) + "/lastLogin");
                    reference.setValue(new Date().getTime()).addOnSuccessListener(runnable -> startActivity(new Intent(AuthenticationActivity.this, MainActivity.class)));
                    finish();
                }
            })
            .addOnFailureListener(e ->
                    alertsHelper.shortSimpleSnackbar(binding.getRoot(), e.getLocalizedMessage()));
    }

    private void doPassReset() {
        String email = Objects.requireNonNull(binding.loginEmail.getEditText()).getText().toString();

        if (email.isEmpty()) {
            alertsHelper.shortSimpleSnackbar(binding.getRoot(), getString(R.string.mail_error_label));
            binding.loginEmail.setErrorEnabled(true);
            binding.loginEmail.setError(getString(R.string.mail_error_label));
            return;
        }

        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(authResult ->
                    alertsHelper.shortSimpleSnackbar(binding.getRoot(),"Revise su correo."))
                .addOnFailureListener(e ->
                        alertsHelper.shortSimpleSnackbar(binding.getRoot(), e.getLocalizedMessage()));
    }
}