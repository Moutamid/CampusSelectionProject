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

public class UploadCvActivity extends AppCompatActivity {
    private static final String TAG = "UploadCvActivity";
    private ProgressDialog progressDialog;

    private CVModel currentCVModel = new CVModel();

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private EditText nameEditText, numberEditText, emailEditText, dateEditText,
            skillEditText, idEditText, collegeEditText, yearOfPassEditText, aggregateEditText,
            departmentEditText;

    private String nameEditTextStr, numberEditTextStr, emailEditTextStr, dateEditTextStr,
            skillEditTextStr, idEditTextStr, collegeEditTextStr, yearOfPassEditTextStr, aggregateEditTextStr,
            departmentEditTextStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_cv);

        nameEditText = findViewById(R.id.name_edittext_upload_cv);
        numberEditText = findViewById(R.id.number_edittext_upload_cv);
        emailEditText = findViewById(R.id.email_edittext_upload_cv);
        dateEditText = findViewById(R.id.dob_edittext_upload_cv);
        skillEditText = findViewById(R.id.skill_edittext_upload_cv);
        idEditText = findViewById(R.id.id_edittext_upload_cv);
        collegeEditText = findViewById(R.id.college_edittext_upload_cv);
        yearOfPassEditText = findViewById(R.id.year_of_pass_edittext_upload_cv);
        aggregateEditText = findViewById(R.id.aggregate_edittext_upload_cv);
        departmentEditText = findViewById(R.id.department_edittext_upload_cv);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        databaseReference.child("CVs").child(auth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (!snapshot.exists()) {

                            progressDialog.dismiss();
                            return;
                        }

                        currentCVModel = snapshot.getValue(CVModel.class);

                        nameEditText.setText(currentCVModel.getName());
                        numberEditText.setText(currentCVModel.getNumber());
                        emailEditText.setText(currentCVModel.getEmail());
                        dateEditText.setText(currentCVModel.getDob());
                        skillEditText.setText(currentCVModel.getSkill());
                        idEditText.setText(currentCVModel.getId());
                        collegeEditText.setText(currentCVModel.getCollege());
                        yearOfPassEditText.setText(currentCVModel.getYearOfPass());
                        aggregateEditText.setText(currentCVModel.getAggregate());
                        departmentEditText.setText(currentCVModel.getDepartment());

                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onCancelled: " + error.getMessage());
                    }
                });

        findViewById(R.id.uploadCvBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nameEditTextStr = nameEditText.getText().toString();
                numberEditTextStr = numberEditText.getText().toString();
                emailEditTextStr = emailEditText.getText().toString();
                dateEditTextStr = dateEditText.getText().toString();
                skillEditTextStr = skillEditText.getText().toString();
                idEditTextStr = idEditText.getText().toString();
                collegeEditTextStr = collegeEditText.getText().toString();
                yearOfPassEditTextStr = yearOfPassEditText.getText().toString();
                aggregateEditTextStr = aggregateEditText.getText().toString();
                departmentEditTextStr = departmentEditText.getText().toString();

                if (nameEditTextStr.isEmpty()) {
                    Toast.makeText(UploadCvActivity.this, "name is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (numberEditTextStr.isEmpty()) {
                    Toast.makeText(UploadCvActivity.this, "number is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (emailEditTextStr.isEmpty()) {
                    Toast.makeText(UploadCvActivity.this, "email is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (dateEditTextStr.isEmpty()) {
                    Toast.makeText(UploadCvActivity.this, "date is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (skillEditTextStr.isEmpty()) {
                    Toast.makeText(UploadCvActivity.this, "skill is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (idEditTextStr.isEmpty()) {
                    Toast.makeText(UploadCvActivity.this, "id is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (collegeEditTextStr.isEmpty()) {
                    Toast.makeText(UploadCvActivity.this, "university is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (yearOfPassEditTextStr.isEmpty()) {
                    Toast.makeText(UploadCvActivity.this, "year of passing is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (aggregateEditTextStr.isEmpty()) {
                    Toast.makeText(UploadCvActivity.this, "aggregate is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (departmentEditTextStr.isEmpty()) {
                    Toast.makeText(UploadCvActivity.this, "department is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                currentCVModel.setName(nameEditTextStr);
                currentCVModel.setNumber(numberEditTextStr);
                currentCVModel.setEmail(emailEditTextStr);
                currentCVModel.setDob(dateEditTextStr);
                currentCVModel.setSkill(skillEditTextStr);

                currentCVModel.setId(idEditTextStr);
                currentCVModel.setCollege(collegeEditTextStr);
                currentCVModel.setYearOfPass(yearOfPassEditTextStr);
                currentCVModel.setAggregate(aggregateEditTextStr);
                currentCVModel.setDepartment(departmentEditTextStr);

                uploadCV();
            }
        });


    }

    private void uploadCV() {
        progressDialog.show();

        databaseReference.child("CVs").child(auth.getCurrentUser().getUid())
                .setValue(currentCVModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(UploadCvActivity.this, "Done", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(UploadCvActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
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