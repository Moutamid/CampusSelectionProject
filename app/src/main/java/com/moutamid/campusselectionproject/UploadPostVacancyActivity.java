package com.moutamid.campusselectionproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
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

public class UploadPostVacancyActivity extends AppCompatActivity {
    private static final String TAG = "UploadPostVacancyActivi";

    private ProgressDialog progressDialog;

    private CompanyDetailModel currentCVModel = new CompanyDetailModel();

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private EditText nameEditText, numberEditText, emailEditText, eligibilityEditText,
            vacanciesEditText, salaryEditText;

    private String nameEditTextStr, numberEditTextStr, emailEditTextStr, eligibilityEditTextStr,
            vacanciesEditTextStr, salaryEditTextStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_post_vacancy);

        nameEditText = findViewById(R.id.company_name_edittext_upload_cv);
        numberEditText = findViewById(R.id.company_number_edittext_upload_cv);
        emailEditText = findViewById(R.id.company_email_edittext_upload_cv);
        eligibilityEditText = findViewById(R.id.company_eligibility_edittext_upload_cv);
        vacanciesEditText = findViewById(R.id.company_vacancies_edittext_upload_cv);
        salaryEditText = findViewById(R.id.company_salary_edittext_upload_cv);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        databaseReference.child("Companies").child(auth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (!snapshot.exists()) {
                            progressDialog.dismiss();
                            return;
                        }

                        currentCVModel = snapshot.getValue(CompanyDetailModel.class);

                        nameEditText.setText(currentCVModel.getName());
                        numberEditText.setText(currentCVModel.getNumber());
                        emailEditText.setText(currentCVModel.getEmail());
                        eligibilityEditText.setText(currentCVModel.getEligibility());
                        vacanciesEditText.setText(currentCVModel.getVacancies());
                        salaryEditText.setText(currentCVModel.getSalary());

                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onCancelled: " + error.getMessage());
                    }
                });

        findViewById(R.id.company_uploadCvBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nameEditTextStr = nameEditText.getText().toString();
                numberEditTextStr = numberEditText.getText().toString();
                emailEditTextStr = emailEditText.getText().toString();
                eligibilityEditTextStr = eligibilityEditText.getText().toString();
                vacanciesEditTextStr = vacanciesEditText.getText().toString();
                salaryEditTextStr = salaryEditText.getText().toString();

                if (nameEditTextStr.isEmpty()) {
                    Toast.makeText(UploadPostVacancyActivity.this, "name is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (numberEditTextStr.isEmpty()) {
                    Toast.makeText(UploadPostVacancyActivity.this, "number is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (emailEditTextStr.isEmpty()) {
                    Toast.makeText(UploadPostVacancyActivity.this, "email is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (eligibilityEditTextStr.isEmpty()) {
                    Toast.makeText(UploadPostVacancyActivity.this, "date is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (vacanciesEditTextStr.isEmpty()) {
                    Toast.makeText(UploadPostVacancyActivity.this, "skill is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (salaryEditTextStr.isEmpty()) {
                    Toast.makeText(UploadPostVacancyActivity.this, "id is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                currentCVModel.setName(nameEditTextStr);
                currentCVModel.setNumber(numberEditTextStr);
                currentCVModel.setEmail(emailEditTextStr);
                currentCVModel.setEligibility(eligibilityEditTextStr);
                currentCVModel.setVacancies(vacanciesEditTextStr);
                currentCVModel.setSalary(salaryEditTextStr);

                uploadCV();
            }
        });


    }

    private void uploadCV() {
        progressDialog.show();

        databaseReference.child("Companies").child(auth.getCurrentUser().getUid())
                .setValue(currentCVModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(UploadPostVacancyActivity.this, "Done", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(UploadPostVacancyActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
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

}