package com.usc.firebasepractice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Login extends AppCompatActivity {

    TextView tvForgot;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        tvForgot = findViewById(R.id.tvForgotPassword);
        btnLogin = findViewById(R.id.btnLogin);

        tvForgot.setOnClickListener(v -> {
            Toast.makeText(this, "Forgot password clicked", Toast.LENGTH_SHORT).show();
        });

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        });
    }
}