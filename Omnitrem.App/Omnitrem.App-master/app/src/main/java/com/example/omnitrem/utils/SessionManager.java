package com.example.omnitrem.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "OmnitremAppPrefs";
    private static final String KEY_AUTH_TOKEN = "authToken";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_NAME = "userName";
    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    // Constructor
    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // Save auth token
    public void saveAuthToken(String token) {
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.apply(); // Use apply() for asynchronous save
    }

    // Fetch auth token
    public String getAuthToken() {
        return prefs.getString(KEY_AUTH_TOKEN, null);
    }

    // Check if user is logged in
    public boolean isLoggedIn() {
        return getAuthToken() != null;
    }

    // Clear session details (for logout)
    public void clearSession() {
        editor.remove(KEY_AUTH_TOKEN);
        editor.remove(KEY_USER_EMAIL); // Clear other details if stored
        editor.remove(KEY_USER_NAME);
        editor.apply();
    }

    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, null);
    }

    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, null);
    }
}