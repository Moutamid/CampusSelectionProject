package com.moutamid.campusselectionproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReportActivity extends AppCompatActivity {
    private static final String TAG = "ReportActivity";
    private ProgressDialog progressDialog;
    private ReportModel currentReportmodel;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private Utils utils = new Utils();
    private  String studentNameStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        TextView nameTextview = findViewById(R.id.student_name_ativity_report);
        nameTextview.setText(utils.getStoredString(ReportActivity.this, "nameStr"));

        studentNameStr = utils.getStoredString(ReportActivity.this, "nameStr");

        setSubmitReportBtnClickListener();

        setStatusReportClickListener();
    }

    private void setStatusReportClickListener() {
        findViewById(R.id.statusReportBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                databaseReference.child("reports")
                        .orderByChild("studentName")
                        .equalTo(studentNameStr)
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
                                Toast.makeText(ReportActivity.this, error.toException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }

    private void setSubmitReportBtnClickListener() {
        EditText companyNameEditText = findViewById(R.id.edittext_company_name_report);
        EditText reportEditText = findViewById(R.id.edittext_report);

        findViewById(R.id.submitReportBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String companyNameStr = companyNameEditText.getText().toString();
                String reportStr = reportEditText.getText().toString();

                if (companyNameStr.isEmpty()) {
                    companyNameEditText.setError("Empty!");
                    companyNameEditText.requestFocus();
                    return;
                }
                if (reportStr.isEmpty()) {
                    reportEditText.setError("Empty!");
                    reportEditText.requestFocus();
                    return;
                }

                String statusStr = "null";
                String key = databaseReference.child("reports").push().getKey();

                ReportModel model = new ReportModel();
                model.setCompanyName(companyNameStr);
                model.setKey(key);
                model.setReport(reportStr);
                model.setStatus(statusStr);
                model.setStudentName(studentNameStr);

                uploadReport(model);

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
        utils.showDialog(ReportActivity.this,
                "Student reports",
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

    private void uploadReport(ReportModel model) {
        progressDialog.show();

        databaseReference.child("reports").child(model.getKey())
                .setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(ReportActivity.this, "Done", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(ReportActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
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