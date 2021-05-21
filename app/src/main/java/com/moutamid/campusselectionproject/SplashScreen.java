package com.moutamid.campusselectionproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Utils utils = new Utils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

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
}






