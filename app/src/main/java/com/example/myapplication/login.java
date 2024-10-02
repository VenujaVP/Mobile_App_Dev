package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView registerText;
    private TextView forgotPasswordText;
    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerText = findViewById(R.id.registerText);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);

        // Set onClickListener for login button
        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            // Validate input fields
            if (validateInputs(email, password)) {
                // Login user with Firebase
                loginUser(email, password);
            }
        });

        // Set onClickListener for register link
        registerText.setOnClickListener(v -> {
            // Redirect to RegisterActivity
            startActivity(new Intent(login.this, register.class));
        });

        // Set onClickListener for forgot password link
        forgotPasswordText.setOnClickListener(v -> {
            // Redirect to ForgotPasswordActivity
            startActivity(new Intent(login.this, ForgetPassword.class));
        });
    }

    // Method to validate input fields
    private boolean validateInputs(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            return false;
        }
        return true;
    }

    // Method to log in the user with Firebase
    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login success
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(login.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        // Redirect to MainActivity
                        startActivity(new Intent(login.this, MainActivity.class));
                        finish(); // Finish the login activity
                    } else {
                        // Login failed
                        Toast.makeText(login.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
