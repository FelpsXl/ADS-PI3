package com.example.omnitrem.network.service;

import com.example.omnitrem.network.model.login.LoginBody;
import com.example.omnitrem.network.model.login.LoginResponse;
import com.example.omnitrem.network.model.register.RegisterBody;
import com.example.omnitrem.network.model.register.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {
    @POST("/Auth/login")
    Call<LoginResponse> loginUser(@Body LoginBody loginBody);

    @POST("/Auth/register")
    Call<RegisterResponse> registerUser(@Body RegisterBody registerBody);
}