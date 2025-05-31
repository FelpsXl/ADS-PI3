package com.example.omnitrem.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.omnitrem.databinding.ActivityRegisterBinding;
import com.example.omnitrem.network.ApiClient;
import com.example.omnitrem.network.service.UserService;
import com.example.omnitrem.network.model.register.RegisterBody;
import com.example.omnitrem.network.model.register.RegisterResponse;
import com.google.android.material.snackbar.Snackbar;


import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private UserService userService;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userService = ApiClient.getUserService();

        setSupportActionBar(binding.toolbarRegister);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        binding.toolbarRegister.setNavigationOnClickListener(v -> finish());

        binding.btnCadastrar.setOnClickListener(v -> doRegister());

        binding.tvLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        binding.progressIndicatorRegister.setVisibility(View.GONE);
    }

    private void showLoading(boolean isLoading) {
        binding.progressIndicatorRegister.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnCadastrar.setEnabled(!isLoading);
        binding.tvLoginLink.setEnabled(!isLoading);
    }

    private boolean validateInputs(String name, String email, String password, String confirmPassword) {
        boolean isValid = true;

        if (name.isEmpty()) {
            binding.tilName.setError("Nome não pode ser vazio");
            isValid = false;
        } else {
            binding.tilName.setError(null);
        }

        if (email.isEmpty()) {
            binding.tilEmailRegister.setError("Email não pode ser vazio");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmailRegister.setError("Insira um email válido");
            isValid = false;
        } else {
            binding.tilEmailRegister.setError(null);
        }

        if (password.isEmpty()) {
            binding.tilPasswordRegister.setError("Senha não pode ser vazia");
            isValid = false;
        } else if (password.length() < 6) {
            binding.tilPasswordRegister.setError("Senha deve ter no mínimo 6 caracteres");
            isValid = false;
        } else {
            binding.tilPasswordRegister.setError(null);
        }

        if (confirmPassword.isEmpty()) {
            binding.tilConfirmPassword.setError("Confirme a senha");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            binding.tilConfirmPassword.setError("As senhas não coincidem");
            isValid = false;
        } else {
            binding.tilConfirmPassword.setError(null);
        }

        return isValid;
    }

    private void doRegister() {
        String name = Objects.requireNonNull(binding.etName.getText()).toString().trim();
        String email = Objects.requireNonNull(binding.etEmailRegister.getText()).toString().trim();
        String password = Objects.requireNonNull(binding.etPasswordRegister.getText()).toString();
        String confirmPassword = Objects.requireNonNull(binding.etConfirmPassword.getText()).toString();

        if (!validateInputs(name, email, password, confirmPassword)) {
            return;
        }

        showLoading(true);

        RegisterBody registerBody = new RegisterBody(name, email, password);
        Call<RegisterResponse> call = userService.registerUser(registerBody);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RegisterResponse> call, @NonNull Response<RegisterResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse registerResponse = response.body();
                    Log.d(TAG, "Registro SUCESSO: " + registerResponse.getMessage());
                    Toast.makeText(RegisterActivity.this, "Registro realizado com sucesso!", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("REGISTRATION_EMAIL", email);
                    startActivity(intent);
                    finish();

                } else {
                    String errorMessage = "Falha no registro.";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                            Log.e(TAG, "Erro na resposta de registro: " + response.code() + " - " + errorMessage);
                        } catch (Exception e) {
                            Log.e(TAG, "Erro ao parsear errorBody do registro", e);
                        }
                    } else {
                        Log.e(TAG, "Erro na resposta de registro: " + response.code() + " (corpo do erro nulo)");
                    }
                    Snackbar.make(binding.getRoot(), "Falha no registro: " + errorMessage, Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RegisterResponse> call, @NonNull Throwable t) {
                showLoading(false);
                Log.e(TAG, "Falha na chamada de registro", t);
                Snackbar.make(binding.getRoot(), "Erro de conexão: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }
}