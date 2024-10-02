package com.example.myapplication;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class register extends AppCompatActivity {

    private EditText usernameInput, emailInput, passwordInput, confirmPasswordInput;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView loginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Handle edge-to-edge system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize UI components
        usernameInput = findViewById(R.id.usernameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        registerButton = findViewById(R.id.registerButton);
        loginText = findViewById(R.id.loginText);

        // Set onClickListener for register button
        registerButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            // Validate input fields
            if (validateInputs(username, email, password, confirmPassword)) {
                // Register the user with Firebase
                registerUser(username, email, password);
            }
        });

        // Set onClickListener for login text to redirect to LoginActivity
        loginText.setOnClickListener(v -> {
            startActivity(new Intent(register.this, login.class));
        });
    }

    // Method to validate the input fields
    private boolean validateInputs(String username, String email, String password, String confirmPassword) {
        if (TextUtils.isEmpty(username)) {
            usernameInput.setError("Username is required");
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            return false;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordInput.setError("Confirm password is required");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            return false;
        }
        return true;
    }

    // Method to register a new user with Firebase
    private void registerUser(String username, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registration success
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Save user details to Firebase Realtime Database
                            writeNewUser(user.getUid(), username, email);
                            Toast.makeText(register.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                            // Redirect to login screen
                            startActivity(new Intent(register.this, login.class));
                            finish(); // Finish the registration activity
                        }
                    } else {
                        // Registration failed
                        Toast.makeText(register.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to write new user to the Firebase Realtime Database
    private void writeNewUser(String userId, String username, String email) {
        User user = new User(username, email);
        mDatabase.child("users").child(userId).setValue(user);
    }
}
