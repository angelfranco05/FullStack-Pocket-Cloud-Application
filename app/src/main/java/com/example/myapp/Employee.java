package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;


public class Employee extends AppCompatActivity {

    DatabaseReference databaseReference;

    RecyclerView recyclerView;
    ArrayList<EmployeeItem> employeeItemArrayList;
    EmployeeAdapter adapter;

    Button buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);
        FirebaseDatabase.getInstance();
        Objects.requireNonNull(getSupportActionBar()).hide();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        employeeItemArrayList = new ArrayList<>();


        FloatingActionButton buttonAdd = (FloatingActionButton) findViewById(R.id.buttonAdd_Employee);
        //buttonAdd = findViewById(R.id.buttonAdd_Employee);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EmployeeAdd employeeAdd = new EmployeeAdd();
                employeeAdd.showDialog(Employee.this, null);
            }
        });

        readData();
    }

    private void readData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            // user is not authenticated
            return;
        }

        String uid = currentUser.getUid();

        DatabaseReference userReference = databaseReference.child("EMPLOYEE").child(uid);

        userReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                employeeItemArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    EmployeeItem employeeItem = dataSnapshot.getValue(EmployeeItem.class);
                    employeeItemArrayList.add(employeeItem);
                }
                adapter = new EmployeeAdapter(Employee.this, employeeItemArrayList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        userReference.keepSynced(true);
    }

    public class EmployeeAdd {
        public void showDialog(Context context, EmployeeItem employeeItem) {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.employee_update);


            EditText textName = dialog.findViewById(R.id.textName);
            EditText textEmail = dialog.findViewById(R.id.textEmail);
            EditText textPhone = dialog.findViewById(R.id.textPhone);

            Button buttonAdd = dialog.findViewById(R.id.buttonAdd);
            Button buttonCancel = dialog.findViewById(R.id.buttonCancel);


            if (employeeItem != null) {
                textName.setText(employeeItem.getUserName());
                textEmail.setText(employeeItem.getUserEmail());
                textPhone.setText(employeeItem.getUserPhone());
                buttonAdd.setText("UPDATE");
            } else {
                buttonAdd.setText("ADD");
            }

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(context, "You must be logged in to perform this action", Toast.LENGTH_SHORT).show();
                return;
            }
            String uid = currentUser.getUid();

            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String id;
                    if (employeeItem != null) {
                        id = employeeItem.getUserId();
                    } else {
                        id = databaseReference.child("EMPLOYEE").child(uid).push().getKey();
                    }

                    String name = textName.getText().toString();
                    String email = textEmail.getText().toString();
                    String phone = textPhone.getText().toString();

                    if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                        Toast.makeText(context, "Please Enter All data...", Toast.LENGTH_SHORT).show();
                    } else {
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            String uid = currentUser.getUid();
                            databaseReference.child("EMPLOYEE").child(uid).child(id).setValue(new EmployeeItem(id, name, email, phone));
                            Toast.makeText(context, "DONE!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(context, "User not authenticated!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }
}