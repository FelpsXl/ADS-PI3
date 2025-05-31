package com.example.omnitrem.network.model.login;

public class LoginBody {
    private final String email;
    private final String password;

    public LoginBody(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}