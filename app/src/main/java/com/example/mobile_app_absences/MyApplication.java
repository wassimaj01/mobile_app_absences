package com.example.mobile_app_absences;

import android.app.Application;

public class MyApplication extends Application {

    public static User currentUser;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the currentUser variable
        currentUser = null; // or initialize with default values if needed
    }
}
