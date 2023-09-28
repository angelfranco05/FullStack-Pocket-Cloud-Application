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


public class Inventory extends AppCompatActivity {

    DatabaseReference databaseReference;

    RecyclerView recyclerView;
    ArrayList<InventoryItem> inventoryItemArrayList;
    InventoryAdapter adapter;

    Button inventoryAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        FirebaseDatabase.getInstance();
        Objects.requireNonNull(getSupportActionBar()).hide();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        inventoryItemArrayList = new ArrayList<>();

        FloatingActionButton buttonAdd = (FloatingActionButton) findViewById(R.id.buttonAdd_Inventory);
        //inventoryAdd = findViewById(R.id.buttonAdd_Inventory);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewDialogAdd viewDialogAdd = new ViewDialogAdd();
                viewDialogAdd.showDialog(Inventory.this, null);
            }
        });

        readData();
    }

    private void readData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            return;
        }

        String uid = currentUser.getUid();

        DatabaseReference userReference = databaseReference.child("INVENTORY").child(uid);

        userReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                inventoryItemArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    InventoryItem inventoryItem = dataSnapshot.getValue(InventoryItem.class);
                    inventoryItemArrayList.add(inventoryItem);
                }
                adapter = new InventoryAdapter(Inventory.this, inventoryItemArrayList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        userReference.keepSynced(true);
    }

    public class ViewDialogAdd {
        public void showDialog(Context context, InventoryItem inventoryItem) {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.inventory_update);

            EditText inventoryName = dialog.findViewById(R.id.inventoryName);
            EditText inventoryQty = dialog.findViewById(R.id.inventoryQty);
            EditText inventoryPrice = dialog.findViewById(R.id.inventoryPrice);

            Button buttonAdd = dialog.findViewById(R.id.buttonAdd3);
            Button buttonCancel = dialog.findViewById(R.id.buttonCancel3);

            if (inventoryItem != null) {
                inventoryName.setText(inventoryItem.getInventoryName());
                inventoryQty.setText(inventoryItem.getInventoryQty());
                inventoryPrice.setText(inventoryItem.getInventoryPrice());
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
                    if (inventoryItem != null) {
                        id = inventoryItem.getUserId();
                    } else {
                        id = databaseReference.child("INVENTORY").child(uid).push().getKey();
                    }

                    String name = inventoryName.getText().toString();
                    String qty = inventoryQty.getText().toString();
                    String price = inventoryPrice.getText().toString();

                    if (name.isEmpty() || qty.isEmpty() || price.isEmpty()) {
                        Toast.makeText(context, "Please Enter All data...", Toast.LENGTH_SHORT).show();
                    } else {
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            String uid = currentUser.getUid();
                            databaseReference.child("INVENTORY").child(uid).child(id).setValue(new InventoryItem(id, name, qty, price));
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