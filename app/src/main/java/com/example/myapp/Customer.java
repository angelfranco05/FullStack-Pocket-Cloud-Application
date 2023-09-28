package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

public class Customer extends AppCompatActivity {

    DatabaseReference databaseReference;

    RecyclerView recyclerView;
    ArrayList<CustomerItem> customerItemArrayList;
    CustomerAdapter adapter;

    Button buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        FirebaseDatabase.getInstance();
        Objects.requireNonNull(getSupportActionBar()).hide();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        customerItemArrayList = new ArrayList<>();

        FloatingActionButton buttonAdd = (FloatingActionButton) findViewById(R.id.buttonAdd_Customer);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewDialogAdd viewDialogAdd = new ViewDialogAdd();
                viewDialogAdd.showDialog(Customer.this, null);
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

        DatabaseReference customerReference = databaseReference.child("CUSTOMER").child(uid);

        customerReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                customerItemArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CustomerItem customer = dataSnapshot.getValue(CustomerItem.class);
                    customerItemArrayList.add(customer);
                }
                adapter = new CustomerAdapter(Customer.this, customerItemArrayList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        customerReference.keepSynced(true);

    }

    public class ViewDialogAdd {
        public void showDialog(Context context, CustomerItem customerItem) {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.customer_update);

            EditText CustomerName = dialog.findViewById(R.id.CustomerName);
            EditText CustomerEmail = dialog.findViewById(R.id.CustomerEmail);
            EditText CustomerPhone = dialog.findViewById(R.id.CustomerPhone);
            EditText CustomerCompany = dialog.findViewById(R.id.CustomerCompany);


            Button buttonAdd = dialog.findViewById(R.id.button_CustomerAdd);
            Button buttonCancel = dialog.findViewById(R.id.button_CustomerCancel);

            if (customerItem != null) {
                CustomerName.setText(customerItem.getCustomerName());
                CustomerEmail.setText(customerItem.getCustomerEmail());
                CustomerPhone.setText(customerItem.getCustomerPhone());
                CustomerCompany.setText(customerItem.getCustomerCompany());
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
                    if (customerItem != null) {
                        id = customerItem.getCustomerId();
                    } else {
                        id = databaseReference.child("CUSTOMER").child(uid).push().getKey();
                    }

                    String name = CustomerName.getText().toString();
                    String email = CustomerEmail.getText().toString();
                    String phone = CustomerPhone.getText().toString();
                    String company = CustomerCompany.getText().toString();

                    if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                        Toast.makeText(context, "Please Enter All data...", Toast.LENGTH_SHORT).show();
                    } else {
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            String uid = currentUser.getUid();
                            databaseReference.child("CUSTOMER").child(uid).child(id).setValue(new CustomerItem(id, name, email, phone, company));
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