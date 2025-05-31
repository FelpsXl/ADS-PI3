package com.example.omnitrem.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.omnitrem.databinding.ActivityLoginBinding;
import com.example.omnitrem.network.ApiClient;
import com.example.omnitrem.network.service.UserService;
import com.example.omnitrem.network.model.login.LoginBody;
import com.example.omnitrem.network.model.login.LoginResponse;
import com.example.omnitrem.ui.TransportChoiceActivity;
import com.example.omnitrem.utils.SessionManager; // Import SessionManager

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private UserService userService;
    private SessionManager sessionManager;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userService = ApiClient.getUserService();
        sessionManager = new SessionManager(getApplicationContext());


        if (sessionManager.isLoggedIn()) {
            navigateToTransportChoice();
            return;
        }

        binding.btnLogin.setOnClickListener(v -> doLogin());
        binding.btnRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));

        binding.progressIndicator.setVisibility(View.GONE);
    }

    private void showLoading(boolean isLoading) {
        binding.progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnLogin.setEnabled(!isLoading);
        binding.btnRegister.setEnabled(!isLoading);
    }

    private void doLogin() {
        String email = Objects.requireNonNull(binding.etEmail.getText()).toString().trim();
        String pass = Objects.requireNonNull(binding.etPassword.getText()).toString().trim();

        if (email.isEmpty()) {
            binding.tilEmail.setError("Email não pode ser vazio");
            return;
        } else {
            binding.tilEmail.setError(null);
        }

        if (pass.isEmpty()) {
            binding.tilPassword.setError("Senha não pode ser vazia");
            return;
        } else {
            binding.tilPassword.setError(null);
        }

        showLoading(true);

        LoginBody loginBody = new LoginBody(email, pass);
        Call<LoginResponse> call = userService.loginUser(loginBody);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    sessionManager.saveAuthToken(loginResponse.getToken());

                    Toast.makeText(LoginActivity.this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Login SUCESSO: " + loginResponse.getMessage() + " | Token: " + loginResponse.getToken());

                    navigateToTransportChoice();

                } else {
                    var errorMessage = "Erro no login";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                            Log.e(TAG, "Erro na resposta: " + response.code() + " - " + errorMessage);
                        } catch (Exception e) {
                            Log.e(TAG, "Erro ao parsear errorBody", e);
                            errorMessage = "Falha no login (código " + response.code() + ")";
                        }
                    } else {
                        errorMessage = "Falha no login (código " + response.code() + ")";
                        Log.e(TAG, "Erro na resposta: " + response.code() + " (corpo do erro nulo)");
                    }
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                showLoading(false);
                Log.e(TAG, "Falha na chamada de login", t);
                Toast.makeText(LoginActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToTransportChoice() {
        Intent intent = new Intent(LoginActivity.this, TransportChoiceActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}