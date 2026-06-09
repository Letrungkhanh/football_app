package com.example.football.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences("FootballPref", Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveUserRole(String role) {
        editor.putString("USER_ROLE", role);
        editor.apply();
    }

    public String getUserRole() {
        return pref.getString("USER_ROLE", "user"); // Mặc định là user
    }
}