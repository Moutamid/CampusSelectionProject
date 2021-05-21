package com.moutamid.campusselectionproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Utils utils = new Utils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (mAuth.getCurrentUser() == null) {
                    finish();
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    return;
                }

                String status = utils.getStoredString(SplashScreen.this, "token");

                if (status.equals("admin")) {
                    // MOVE TO ADMIN ACTIVITY
                    finish();
                    startActivity(new Intent(SplashScreen.this, AdminActivity.class));
                    return;
                }

                if (status.equals("student")) {
                    finish();
                    startActivity(new Intent(SplashScreen.this, StudentActivity.class));
                    return;
                }

                if (status.equals("company")) {
                    finish();
                    startActivity(new Intent(SplashScreen.this, CompanyActivity.class));
                }
            }
        }, 2000);
    }
}






