package com.moutamid.campusselectionproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CompanyActivity extends AppCompatActivity {
    private static final String TAG = "CompanyActivity";
    private ProgressDialog progressDialog;
    private CVModel currentCVmodel;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference databaseReference;
    private Utils utils = new Utils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_company);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        findViewById(R.id.logoutBtn_company).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                utils.removeSharedPref(CompanyActivity.this);
                Intent intent = new Intent(CompanyActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent);
            }
        });

        TextView nameTextview = findViewById(R.id.company_name_ativity);
        nameTextview.setText(utils.getStoredString(CompanyActivity.this, "nameStr"));

        findViewById(R.id.view_vacancy_cardview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utils.showDialog(CompanyActivity.this,
                        "Company details",
                        "Description of all companies Description Description Description Description Description",
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

        findViewById(R.id.view_student_details_cardview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("CVs").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressDialog.show();
                        String details = "No students exist";

                        if (!snapshot.exists()) {
                            progressDialog.dismiss();
                            showListDialog(details);
                            return;
                        }
                        details = "";

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            currentCVmodel = dataSnapshot.getValue(CVModel.class);

                            details = details + studentDetails();

                        }
                        progressDialog.dismiss();
                        showListDialog(details);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(CompanyActivity.this, error.toException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        findViewById(R.id.postVacancyCardview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CompanyActivity.this, UploadPostVacancyActivity.class));

            }
        });

        findViewById(R.id.query_cardview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CompanyActivity.this, QueryActivity.class));
            }
        });
    }

    private String studentDetails() {
        return "Name: " + currentCVmodel.getName() + "\n" +
                "Id: " + currentCVmodel.getId() + "\n" +
                "Email: " + currentCVmodel.getEmail() + "\n" +
                "Year of passing: " + currentCVmodel.getYearOfPass() + "\n" +
                "Aggregate: " + currentCVmodel.getAggregate() + "\n" +
                "Department: " + currentCVmodel.getDepartment() + "\n" +
                "Skills: " + currentCVmodel.getSkill() + "\n\n";
    }

    private void showListDialog(String details) {
        utils.showDialog(CompanyActivity.this,
                "Student details",
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

    private static class CVModel {

        private String name, email, number, dob, skill, id, college, yearOfPass,
                aggregate, department;

        public CVModel(String name, String email, String number, String dob, String skill, String id, String college, String yearOfPass, String aggregate, String department) {
            this.name = name;
            this.email = email;
            this.number = number;
            this.dob = dob;
            this.skill = skill;
            this.id = id;
            this.college = college;
            this.yearOfPass = yearOfPass;
            this.aggregate = aggregate;
            this.department = department;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCollege() {
            return college;
        }

        public void setCollege(String college) {
            this.college = college;
        }

        public String getYearOfPass() {
            return yearOfPass;
        }

        public void setYearOfPass(String yearOfPass) {
            this.yearOfPass = yearOfPass;
        }

        public String getAggregate() {
            return aggregate;
        }

        public void setAggregate(String aggregate) {
            this.aggregate = aggregate;
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

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

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getSkill() {
            return skill;
        }

        public void setSkill(String skill) {
            this.skill = skill;
        }

        CVModel() {
        }
    }
}