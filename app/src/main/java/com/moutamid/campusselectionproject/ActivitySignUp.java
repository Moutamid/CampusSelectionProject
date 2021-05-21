package com.moutamid.campusselectionproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class ActivitySignUp extends AppCompatActivity {
    private static final String TAG = "ActivitySignUp";

    private EditText userNameEditText, emailEditText, passwordEditText, confirmPasswordEditText, numberEditText;
    private Button signUpBtn;

    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;

    private DatabaseReference databaseReference;

    private String userNameStr;
    private Utils utils = new Utils();
    private String emailStr;

    private String passwordStr;

    private String confirmedPasswordStr;
    private String numberStr;

    private boolean isCompany = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
//        if (mAuth.getCurrentUser() != null) {
//            finish();
//            Intent intent = new Intent(ActivitySignUp.this, BottomNavigationActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//            return;
//        }

        TextView textView = findViewById(R.id.top_text_sign_up);
        if (getIntent().getStringExtra("token").equals("c")) {
            isCompany = true;
            textView.setText("Create your company's account to get all the features");
        }

//        findViewById(R.id.loginBtn_signUp).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(ActivitySignUp.this, ActivityLogin.class));
//            }
//        });

        findViewById(R.id.backbtn_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Signing you in...");

        initViews();

    }

    private View.OnClickListener signUpBtnListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                checkStatusOfEditTexts();
            }
        };
    }

    private void checkStatusOfEditTexts() {

        // Getting strings from edit texts
        userNameStr = userNameEditText.getText().toString();
        emailStr = emailEditText.getText().toString();
        passwordStr = passwordEditText.getText().toString();
        confirmedPasswordStr = confirmPasswordEditText.getText().toString();
        numberStr = numberEditText.getText().toString();

        if (TextUtils.isEmpty(userNameStr)) {
            progressDialog.dismiss();
            userNameEditText.setError("Username is empty");
            userNameEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(numberStr)) {
            progressDialog.dismiss();
            numberEditText.setError("Number is empty");
            numberEditText.requestFocus();
            return;
        }

        // Checking if Fields are empty or not
        if (!TextUtils.isEmpty(emailStr) && !TextUtils.isEmpty(passwordStr) && !TextUtils.isEmpty(confirmedPasswordStr)) {

            // Checking if passwordStr is equal to confirmed Password
            if (passwordStr.equals(confirmedPasswordStr)) {

                // Signing up user
                signUpUserWithNameAndPassword();

            } else {

                progressDialog.dismiss();
                confirmPasswordEditText.setError("Password does not match!");
                confirmPasswordEditText.requestFocus();

            }

            // User Name is Empty
        } else if (TextUtils.isEmpty(emailStr)) {

            progressDialog.dismiss();
            emailEditText.setError("Please provide a emailStr");
            emailEditText.requestFocus();


            // Password is Empty
        } else if (TextUtils.isEmpty(passwordStr)) {

            progressDialog.dismiss();
            passwordEditText.setError("Please provide a passwordStr");
            passwordEditText.requestFocus();


            // Confirm Password is Empty
        } else if (TextUtils.isEmpty(confirmedPasswordStr)) {

            progressDialog.dismiss();
            confirmPasswordEditText.setError("Please confirm your passwordStr");
            confirmPasswordEditText.requestFocus();


        }

    }

    private void signUpUserWithNameAndPassword() {

        if (!Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            //if Email Address is Invalid..

            progressDialog.dismiss();
            emailEditText.setError("Please enter a valid email with no spaces and special characters included");
            emailEditText.requestFocus();
        } else {

            mAuth.createUserWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        addUserDetailsToDatabase();

                    } else {

                        progressDialog.dismiss();
                        Toast.makeText(ActivitySignUp.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
//        } else {
//
//            mDialog.dismiss();
//            Toast.makeText(this, "You are not online", Toast.LENGTH_SHORT).show();
//        }
    }

    private static class UserDetails {

        private String name, email, profileUrl;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getProfileUrl() {
            return profileUrl;
        }

        public void setProfileUrl(String profileUrl) {
            this.profileUrl = profileUrl;
        }

        public UserDetails(String name, String email, String profileUrl) {
            this.name = name;
            this.email = email;
            this.profileUrl = profileUrl;
        }

        UserDetails() {
        }
    }

    private void addUserDetailsToDatabase() {

//        UserDetails userDetails = new UserDetails();
//        userDetails.setEmail(emailStr);
//        userDetails.setName(userNameStr);
//        userDetails.setProfileUrl("Error");

        HashMap<String, String> map = new HashMap<>();
        map.put("name", userNameStr);
        map.put("email", emailStr);
        map.put("number", numberStr);

        if (isCompany) {
            map.put("status", "company");
        } else map.put("status", "student");


        databaseReference.child("users").child(mAuth.getCurrentUser().getUid())
                .setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    new Utils().storeString(ActivitySignUp.this,
                            "nameStr", userNameStr);

                    progressDialog.dismiss();

                    Toast.makeText(ActivitySignUp.this, "You are signed up!", Toast.LENGTH_SHORT).show();

                    if (isCompany) {
                        utils.storeString(ActivitySignUp.this,
                                "token", "company");
                        finish();
                        Intent intent = new Intent(ActivitySignUp.this, CompanyActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        utils.storeString(ActivitySignUp.this,
                                "token", "student");
                        finish();
                        Intent intent = new Intent(ActivitySignUp.this, StudentActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(ActivitySignUp.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onComplete: " + task.getException().getMessage());
                }

            }
        });

    }

    private void initViews() {

        userNameEditText = findViewById(R.id.user_name_edittext_activity_sign_up);

        emailEditText = findViewById(R.id.email_edittext_activity_sign_up);
        numberEditText = findViewById(R.id.number_edittext_activity_sign_up);
        passwordEditText = findViewById(R.id.password_edittext_activity_sign_up);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edittext_activity_sign_up);

        signUpBtn = findViewById(R.id.create_btn_activity_sign_up);
        signUpBtn.setOnClickListener(signUpBtnListener());
    }

}