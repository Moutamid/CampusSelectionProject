package com.moutamid.campusselectionproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private CompanyDetailModel currentCVmodel;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference databaseReference;
    private Utils utils = new Utils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_student);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        findViewById(R.id.logoutBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                utils.removeSharedPref(StudentActivity.this);
                Intent intent = new Intent(StudentActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(intent);
            }
        });

        TextView nameTextview = findViewById(R.id.student_name_ativity);
        nameTextview.setText(utils.getStoredString(StudentActivity.this, "nameStr"));


        EditText editText = findViewById(R.id.edittext_company_name);
        findViewById(R.id.companySearchBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String text = editText.getText().toString();

                if (text.isEmpty()) {
                    editText.setError("Empty!");
                    editText.requestFocus();
                    return;
                }
                progressDialog.show();
                databaseReference.child("Companies")
                        .orderByChild("name")
                        .equalTo(text)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (!snapshot.exists()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(StudentActivity.this, "No company exist with this name!", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {

                                    currentCVmodel = dataSnapshot.getValue(CompanyDetailModel.class);

                                    progressDialog.dismiss();

                                    String details = companyDetails();

                                    showListDialog1(details);

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                progressDialog.dismiss();
                                Toast.makeText(StudentActivity.this, error.toException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        findViewById(R.id.uploadCvCardview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StudentActivity.this, UploadCvActivity.class));
            }
        });

        findViewById(R.id.view_placements_cardview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();

                databaseReference.child("Companies").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String details = "No companies exist";

                        if (!snapshot.exists()) {
                            progressDialog.dismiss();
                            showListDialog(details);
                            return;
                        }
                        details = "";

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            currentCVmodel = dataSnapshot.getValue(CompanyDetailModel.class);

                            details = details + companyDetails();

                        }
                        progressDialog.dismiss();
                        showListDialog(details);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(StudentActivity.this, error.toException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        findViewById(R.id.report_cardview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StudentActivity.this, ReportActivity.class));
            }
        });

    }

    private static class CompanyDetailModel {

        private String name, number, email, eligibility, vacancies, salary;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getEligibility() {
            return eligibility;
        }

        public void setEligibility(String eligibility) {
            this.eligibility = eligibility;
        }

        public String getVacancies() {
            return vacancies;
        }

        public void setVacancies(String vacancies) {
            this.vacancies = vacancies;
        }

        public String getSalary() {
            return salary;
        }

        public void setSalary(String salary) {
            this.salary = salary;
        }

        public CompanyDetailModel(String name, String number, String email, String eligibility, String vacancies, String salary) {
            this.name = name;
            this.number = number;
            this.email = email;
            this.eligibility = eligibility;
            this.vacancies = vacancies;
            this.salary = salary;
        }

        CompanyDetailModel() {
        }
    }

    private String companyDetails() {
        return "Name: " + currentCVmodel.getName() + "\n" +
                "Email: " + currentCVmodel.getEmail() + "\n" +
                "Mobile No: " + currentCVmodel.getNumber() + "\n" +
                "Eligibility: " + currentCVmodel.getEligibility() + "\n" +
                "Vacancy: " + currentCVmodel.getVacancies() + "\n" +
                "Salary: " + currentCVmodel.getSalary() + "\n\n";
    }

    private void showListDialog(String details) {
        utils.showDialog(StudentActivity.this,
                "Company details",
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

    private void showListDialog1(String details) {
        utils.showDialog(StudentActivity.this,
                "Company details",
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

//        Dialog dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.dialog_moutamid);
//        dialog.setCancelable(true);
//        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//        layoutParams.copyFrom(dialog.getWindow().getAttributes());
//        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//
//        dialog.findViewById(R.id.okayBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // CODE HERE
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
//        dialog.getWindow().setAttributes(layoutParams);
}