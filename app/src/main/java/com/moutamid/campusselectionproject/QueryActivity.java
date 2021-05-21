package com.moutamid.campusselectionproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class QueryActivity extends AppCompatActivity {
    private static final String TAG = "QueryActivity";
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private Utils utils = new Utils();
    private String companyNameStr;
    private ReportModel currentReportmodel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        companyNameStr = utils.getStoredString(QueryActivity.this, "nameStr");

        setSubmitQueryBtnClickListener();

        setViewReportsBtnClickListener();

    }

    private void setViewReportsBtnClickListener() {
        findViewById(R.id.viewqueryBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.show();
                databaseReference.child("reports")
                        .orderByChild("companyName")
                        .equalTo(companyNameStr)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String details = "No reports exist";

                                if (!snapshot.exists()) {
                                    progressDialog.dismiss();
                                    showListDialog(details);
                                    return;
                                }
                                details = "";

                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                    currentReportmodel = dataSnapshot.getValue(ReportModel.class);

                                    details = details + reportDetails();

                                }
                                progressDialog.dismiss();
                                showListDialog(details);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                progressDialog.dismiss();
                                Toast.makeText(QueryActivity.this, error.toException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
    }

    private void setSubmitQueryBtnClickListener() {
        EditText reportNameEditText = findViewById(R.id.edittext_company_name_query);
        EditText statusEditText = findViewById(R.id.edittext_query);

        findViewById(R.id.submitqueryBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String reportNameStr = reportNameEditText.getText().toString();
                String statusStr = statusEditText.getText().toString();

                if (reportNameStr.isEmpty()) {
                    reportNameEditText.setError("Empty!");
                    reportNameEditText.requestFocus();
                    return;
                }
                if (statusStr.isEmpty()) {
                    statusEditText.setError("Empty!");
                    statusEditText.requestFocus();
                    return;
                }

                searchByCompanyName(reportNameStr, statusStr);

            }
        });
    }

    private void searchByCompanyName(String reportNameStr, String statusStr) {
        progressDialog.show();
        databaseReference.child("reports")
                .orderByChild("report")
                .equalTo(reportNameStr)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            progressDialog.dismiss();
                            Toast.makeText(QueryActivity.this, "No report exist!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String key = snapshot.child("key").getValue(String.class);

                        updateStatusValue(key, statusStr);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(QueryActivity.this, error.toException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateStatusValue(String key, String statusStr) {
        databaseReference.child("reports").child(key).child("status")
                .setValue(statusStr)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            progressDialog.dismiss();
                            Toast.makeText(QueryActivity.this, "Query updated successfully", Toast.LENGTH_SHORT).show();

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(QueryActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private String reportDetails() {
        return "Company name: " + currentReportmodel.getCompanyName() + "\n" +
                "Student name: " + currentReportmodel.getStudentName() + "\n" +
                "Report: " + currentReportmodel.getReport() + "\n" +
                "Status: " + currentReportmodel.getStatus() + "\n\n";
    }

    private void showListDialog(String details) {
        utils.showDialog(QueryActivity.this,
                "Report details",
                details,
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

    private static class ReportModel {

        private String companyName, studentName, report, status, key;

        public ReportModel(String companyName, String studentName, String report, String status, String key) {
            this.companyName = companyName;
            this.studentName = studentName;
            this.report = report;
            this.status = status;
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getStudentName() {
            return studentName;
        }

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }

        public String getReport() {
            return report;
        }

        public void setReport(String report) {
            this.report = report;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        ReportModel() {
        }
    }
}