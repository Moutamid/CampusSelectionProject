package com.moutamid.campusselectionproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReportActivity extends AppCompatActivity {
    private static final String TAG = "ReportActivity";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private Utils utils = new Utils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        TextView nameTextview = findViewById(R.id.student_name_ativity_report);
        nameTextview.setText(utils.getStoredString(ReportActivity.this, "nameStr"));

        EditText companyNameEditText = findViewById(R.id.edittext_company_name_report);
        EditText reportEditText = findViewById(R.id.edittext_report);

        findViewById(R.id.submitReportBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String text = companyNameEditText.getText().toString();
                String text1 = reportEditText.getText().toString();

                if (text.isEmpty()) {
                    companyNameEditText.setError("Empty!");
                    companyNameEditText.requestFocus();
                    return;
                }
                if (text1.isEmpty()) {
                    reportEditText.setError("Empty!");
                    reportEditText.requestFocus();
                    return;
                }

                // TODO: UPLOAD A NEW REPORT TO A COMPANY PROFILE

            }
        });

        findViewById(R.id.statusReportBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO: GET ALL STATUS FROM DATABASE

                utils.showDialog(ReportActivity.this,
                        "Company details",
                        "Description Description Description Description Description Description",
                        "",
                        "",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }, true);

            }
        });

    }
}