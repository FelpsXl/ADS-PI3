package com.example.omnitrem;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.omnitrem.databinding.ActivityMainBinding;
import com.example.omnitrem.ui.user.LoginActivity;
import com.example.omnitrem.ui.user.RegisterActivity;
import com.example.omnitrem.ui.TransportChoiceActivity; // Import TransportChoiceActivity
import com.example.omnitrem.utils.SessionManager;    // Import SessionManager

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager sessionManager = new SessionManager(getApplicationContext());

        if (sessionManager.isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, TransportChoiceActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // If not logged in, proceed with normal MainActivity setup
        EdgeToEdge.enable(this);
        com.example.omnitrem.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnLoginWelcome.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        binding.btnRegisterWelcome.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}