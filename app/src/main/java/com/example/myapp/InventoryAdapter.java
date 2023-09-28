package com.example.myapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    Context context;
    ArrayList<InventoryItem> inventoryItemArrayList;
    DatabaseReference databaseReference;

    public InventoryAdapter(Context context, ArrayList<InventoryItem> inventoryItemArrayList) {
        this.context = context;
        this.inventoryItemArrayList = inventoryItemArrayList;
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.inventory_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        InventoryItem inventory = inventoryItemArrayList.get(position);

        holder.inventoryName.setText(inventory.getInventoryName());
        holder.inventoryQty.setText("Quantity : " + inventory.getInventoryQty());
        holder.inventoryPrice.setText("CPU : " + inventory.getInventoryPrice());


        holder.buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewDialogUpdate viewDialogUpdate = new ViewDialogUpdate();
                viewDialogUpdate.showDialog(context, inventory.getUserId(), inventory.getInventoryName(), inventory.getInventoryQty(), inventory.getInventoryPrice());
            }
        });

        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewDialogConfirmDelete viewDialogConfirmDelete = new ViewDialogConfirmDelete();
                viewDialogConfirmDelete.showDialog(context, inventory.getUserId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return inventoryItemArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView inventoryName;
        TextView inventoryQty;
        TextView inventoryPrice;

        Button buttonDelete;
        Button buttonUpdate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            inventoryName = itemView.findViewById(R.id.inventoryName);
            inventoryQty = itemView.findViewById(R.id.inventoryQty);
            inventoryPrice = itemView.findViewById(R.id.inventoryPrice);

            buttonDelete = itemView.findViewById(R.id.buttonDelete3);
            buttonUpdate = itemView.findViewById(R.id.buttonUpdate3);
        }
    }

    public class ViewDialogUpdate {
        public void showDialog(Context context, String id, String name, String qty, String price) {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.inventory_update);

            EditText inventoryName = dialog.findViewById(R.id.inventoryName);
            EditText inventoryQty = dialog.findViewById(R.id.inventoryQty);
            EditText inventoryPrice = dialog.findViewById(R.id.inventoryPrice);

            inventoryName.setText(name);
            inventoryQty.setText(qty);
            inventoryPrice.setText(price);


            Button buttonUpdate = dialog.findViewById(R.id.buttonAdd3);
            Button buttonCancel = dialog.findViewById(R.id.buttonCancel3);

            buttonUpdate.setText("UPDATE");

            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            buttonUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String newName = inventoryName.getText().toString();
                    String newQty = inventoryQty.getText().toString();
                    String newPrice = inventoryPrice.getText().toString();

                    if (newName.isEmpty() || newQty.isEmpty() || newPrice.isEmpty()) {
                        Toast.makeText(context, "Please Enter All data...", Toast.LENGTH_SHORT).show();
                    } else {

                        if (newName.equals(name) && newQty.equals(qty) && newPrice.equals(price)) {
                            Toast.makeText(context, "You didn't change anything", Toast.LENGTH_SHORT).show();
                        } else {
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (currentUser != null) {
                                String uid = currentUser.getUid();
                                databaseReference.child("INVENTORY").child(uid).child(id).setValue(new InventoryItem(id, newName, newQty, newPrice));
                                Toast.makeText(context, "Inventory Updated successfully!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(context, "User not authenticated!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

        }
    }


    public class ViewDialogConfirmDelete {
        public void showDialog(Context context, String id) {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.inventory_delete);

            Button buttonDelete = dialog.findViewById(R.id.buttonDelete3);
            Button buttonCancel = dialog.findViewById(R.id.buttonCancel3);

            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (id != null) {
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            String uid = currentUser.getUid();
                            databaseReference.child("INVENTORY").child(uid).child(id).removeValue();
                            Toast.makeText(context, "Inventory Deleted successfully!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(context, "Inventory not authenticated!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "No inventory selected to delete!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

        }
    }
}
