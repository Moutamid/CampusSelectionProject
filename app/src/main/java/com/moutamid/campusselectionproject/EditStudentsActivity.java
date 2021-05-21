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

public class EditStudentsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private Utils utils = new Utils();
    private ProgressDialog progressDialog;
    private AccountModel currentAccountModel = new AccountModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_students);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");


        setDeleteBtnClickListener();

        setViewCompaniesBtnClickListener();
    }

    private void setDeleteBtnClickListener() {
        EditText emailEditText = findViewById(R.id.edittext_edit_students);

        findViewById(R.id.deletestudentsBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String studentEmailStr = emailEditText.getText().toString();

                if (studentEmailStr.isEmpty()) {
                    emailEditText.setError("Empty!");
                    emailEditText.requestFocus();
                    return;
                }

                progressDialog.show();
                databaseReference.child("users")
                        .orderByChild("email")
                        .equalTo(studentEmailStr)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (!snapshot.exists()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(EditStudentsActivity.this, "No company exist with this name!", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                String key = snapshot.getKey();

                                databaseReference.child("users").child(key)
                                        .child("isDeleted").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            progressDialog.dismiss();
                                            Toast.makeText(EditStudentsActivity.this, "Company deleted!", Toast.LENGTH_SHORT).show();

                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(EditStudentsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                progressDialog.dismiss();
                                Toast.makeText(EditStudentsActivity.this, error.toException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
    }

    private void setViewCompaniesBtnClickListener() {
        findViewById(R.id.view_studentsBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.show();
                databaseReference.child("users")
                        .orderByChild("status")
                        .equalTo("company")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String details = "No students exist";

                                if (!snapshot.exists()) {
                                    progressDialog.dismiss();
                                    showListDialog(details);
                                    return;
                                }
                                details = "";

                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                    currentAccountModel.setName(dataSnapshot.child("name").getValue(String.class));
                                    currentAccountModel.setEmail(dataSnapshot.child("email").getValue(String.class));
                                    currentAccountModel.setStatus(dataSnapshot.child("status").getValue(String.class));
                                    currentAccountModel.setDeleted(false);
                                    if (dataSnapshot.child("isDeleted").exists())
                                        currentAccountModel.setDeleted(dataSnapshot.child("isDeleted").getValue(Boolean.class));

                                    details = details + accountDetails();

                                }
                                progressDialog.dismiss();
                                showListDialog(details);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                progressDialog.dismiss();
                                Toast.makeText(EditStudentsActivity.this, error.toException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

    }

    private String accountDetails() {
        return "Company name: " + currentAccountModel.getName() + "\n" +
                "Email: " + currentAccountModel.getEmail() + "\n" +
                "Account deleted: " + currentAccountModel.isDeleted() + "\n\n";
    }

    private void showListDialog(String details) {
        utils.showDialog(EditStudentsActivity.this,
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

    private static class AccountModel {

        private String name, email, status;
        private boolean isDeleted;

        public AccountModel(String name, String email, String status, boolean isDeleted) {
            this.name = name;
            this.email = email;
            this.status = status;
            this.isDeleted = isDeleted;
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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public boolean isDeleted() {
            return isDeleted;
        }

        public void setDeleted(boolean deleted) {
            isDeleted = deleted;
        }

        AccountModel() {
        }
    }


}