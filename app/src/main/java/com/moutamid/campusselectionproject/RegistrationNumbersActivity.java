package com.moutamid.campusselectionproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RegistrationNumbersActivity extends AppCompatActivity {
    private static final String TAG = "RegistrationNumbersActi";
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private ArrayList<RegistrationNumbersModel> registrationNumbersList = new ArrayList<>();
    private RecyclerView conversationRecyclerView;
    private RecyclerViewAdapterMessages adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_numbers);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        databaseReference.child("registration_numbers")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (!snapshot.exists()) {
                            progressDialog.dismiss();
                            return;
                        }

                        registrationNumbersList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            String number = dataSnapshot.child("number").getValue(String.class);
                            RegistrationNumbersModel model = new RegistrationNumbersModel();
                            model.setNumber(number);
                            model.setKey(dataSnapshot.getKey());

                            registrationNumbersList.add(model);

//                            registrationNumbersList.add(number);
//                            Log.d(TAG, "onDataChange: " + number);

                        }
                        progressDialog.dismiss();

                        initRecyclerView();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(RegistrationNumbersActivity.this,
                                error.toException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });

        setAddBtnCLickLIstener();

//        ListView simpleList;
//        String countryList[] = {"India", "China", "Australia"};
//        simpleList = findViewById(R.id.registration_number_recyclerview);
//
//        ArrayAdapter adapter = new ArrayAdapter<String>(
//                RegistrationNumbersActivity.this,
//                R.layout.activity_listview, countryList);
//
//        simpleList.setAdapter(adapter);
    }


    private void initRecyclerView() {

        conversationRecyclerView = findViewById(R.id.registration_number_recyclerview);
        conversationRecyclerView.addItemDecoration(new DividerItemDecoration(conversationRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        adapter = new RecyclerViewAdapterMessages();
        //        LinearLayoutManager layoutManagerUserFriends = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        //    int numberOfColumns = 3;
        //int mNoOfColumns = calculateNoOfColumns(getApplicationContext(), 50);
        //  recyclerView.setLayoutManager(new GridLayoutManager(this, mNoOfColumns));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        conversationRecyclerView.setLayoutManager(linearLayoutManager);
        conversationRecyclerView.setHasFixedSize(true);
        conversationRecyclerView.setNestedScrollingEnabled(false);

        conversationRecyclerView.setAdapter(adapter);

        if (adapter.getItemCount() != 0) {

            //        noChatsLayout.setVisibility(View.GONE);
            //        chatsRecyclerView.setVisibility(View.VISIBLE);

        }

    }


    private class RecyclerViewAdapterMessages extends RecyclerView.Adapter
            <RecyclerViewAdapterMessages.ViewHolderRightMessage> {

        @NonNull
        @Override
        public ViewHolderRightMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_listview, parent, false);
            return new ViewHolderRightMessage(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolderRightMessage holder, int position) {

            holder.title.setText(registrationNumbersList.get(position).getNumber());

            holder.title.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    new Utils()
                            .showDialog(
                                    RegistrationNumbersActivity.this,
                                    "Are you sure?",
                                    "Do you really want to delete "
                                            + registrationNumbersList.get(position).getNumber(),
                                    "Yes",
                                    "No",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            deleteRegistrationNumber(position, registrationNumbersList.get(position).getKey(), dialogInterface);
                                        }
                                    }, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    }, true);

                    return false;
                }
            });

//            holder.title.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Toast.makeText(RegistrationNumbersActivity.this,
//                            registrationNumbersList.get(position).getKey()
//                            , Toast.LENGTH_SHORT).show();
//
//                }
//            });

        }

        private void deleteRegistrationNumber(int position, String key, DialogInterface dialogInterface) {

            registrationNumbersList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());

            databaseReference.child("registration_numbers").child(key).removeValue();

            Toast.makeText(RegistrationNumbersActivity.this, "Done", Toast.LENGTH_SHORT).show();

            dialogInterface.dismiss();
        }

        @Override
        public int getItemCount() {
            if (registrationNumbersList == null)
                return 0;
            return registrationNumbersList.size();
        }

        public class ViewHolderRightMessage extends RecyclerView.ViewHolder {

            TextView title;

            public ViewHolderRightMessage(@NonNull View v) {
                super(v);
                title = v.findViewById(R.id.itemList);

            }
        }

    }

    private void setAddBtnCLickLIstener() {
        findViewById(R.id.add_data_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog dialog = new Dialog(RegistrationNumbersActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_registration_numbers);
                dialog.setCancelable(true);
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

                dialog.findViewById(R.id.saveBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // CODE HERE
                        EditText editText = dialog.findViewById(R.id.nameEdittext);
                        saveNameData(editText.getText().toString());

                        dialog.dismiss();
                    }
                });
                dialog.show();
                dialog.getWindow().setAttributes(layoutParams);

            }
        });
    }

    private void saveNameData(String number) {
        if (number.isEmpty()){
            Toast.makeText(this, "Please enter a number!", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference.child("registration_numbers").push()
                .child("number").setValue(number);

    }

    private static class RegistrationNumbersModel {

        private String number, key;

        public RegistrationNumbersModel(String number, String key) {
            this.number = number;
            this.key = key;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        RegistrationNumbersModel() {
        }
    }

}