package com.example.myapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {

    Context context;
    ArrayList<CustomerItem> customerItemArrayList;
    DatabaseReference databaseReference;

    public CustomerAdapter(Context context, ArrayList<CustomerItem> customerItemArrayList) {
        this.context = context;
        this.customerItemArrayList = customerItemArrayList;
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.customer_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CustomerItem customer = customerItemArrayList.get(position);

        holder.CustomerName.setText(customer.getCustomerName());
        holder.CustomerEmail.setText("Email : " + customer.getCustomerEmail());
        holder.CustomerPhone.setText("Phone : " + customer.getCustomerPhone());
        holder.CustomerCompany.setText("Company: " + customer.getCustomerCompany());

        holder.buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewDialogUpdate viewDialogUpdate = new ViewDialogUpdate();
                viewDialogUpdate.showDialog(context, customer.getCustomerId(), customer.getCustomerName(), customer.getCustomerEmail(), customer.getCustomerPhone(), customer.getCustomerCompany());
            }
        });

        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewDialogConfirmDelete viewDialogConfirmDelete = new ViewDialogConfirmDelete();
                viewDialogConfirmDelete.showDialog(context, customer.getCustomerId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return customerItemArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView CustomerName;
        TextView CustomerEmail;
        TextView CustomerPhone;
        TextView CustomerCompany;

        Button buttonDelete;
        Button buttonUpdate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            CustomerName = itemView.findViewById(R.id.CustomerName);
            CustomerEmail = itemView.findViewById(R.id.CustomerEmail);
            CustomerPhone = itemView.findViewById(R.id.CustomerPhone);
            CustomerCompany = itemView.findViewById(R.id.CustomerCompany);

            buttonDelete = itemView.findViewById(R.id.buttonDelete2);
            buttonUpdate = itemView.findViewById(R.id.buttonUpdate2);
        }
    }

    public class ViewDialogUpdate {
        public void showDialog(Context context, String id, String name, String email, String phone, String company) {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.customer_update);

            EditText CustomerName = dialog.findViewById(R.id.CustomerName);
            EditText CustomerEmail = dialog.findViewById(R.id.CustomerEmail);
            EditText CustomerPhone= dialog.findViewById(R.id.CustomerPhone);
            EditText CustomerCompany = dialog.findViewById(R.id.CustomerCompany);

            CustomerName.setText(name);
            CustomerEmail.setText(email);
            CustomerPhone.setText(phone);
            CustomerCompany.setText(company);


            Button buttonUpdate = dialog.findViewById(R.id.button_CustomerAdd);
            Button buttonCancel = dialog.findViewById(R.id.button_CustomerCancel);

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

                    String newName = CustomerName.getText().toString();
                    String newEmail = CustomerEmail.getText().toString();
                    String newPhone = CustomerPhone.getText().toString();
                    String newCompany = CustomerCompany.getText().toString();

                    if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || company.isEmpty()) {
                        Toast.makeText(context, "Please Enter All data...", Toast.LENGTH_SHORT).show();
                    } else {

                        if (newName.equals(name) && newEmail.equals(email) && newPhone.equals(phone) && newCompany.equals(company)) {
                            Toast.makeText(context, "you don't change anything", Toast.LENGTH_SHORT).show();
                        } else {
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (currentUser != null) {
                                String uid = currentUser.getUid();
                                databaseReference.child("CUSTOMER").child(uid).child(id).setValue(new CustomerItem(id, newName, newEmail, newPhone, newCompany));
                                Toast.makeText(context, "Customer Updated successfully!", Toast.LENGTH_SHORT).show();
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
            dialog.setContentView(R.layout.customer_delete);

            Button buttonDelete = dialog.findViewById(R.id.buttonDelete_Customer);
            Button buttonCancel = dialog.findViewById(R.id.buttonCancel_Customer);

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
                            databaseReference.child("CUSTOMER").child(uid).child(id).removeValue();
                            Toast.makeText(context, "Customer Deleted successfully!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(context, "Customer not authenticated!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "No customer selected to delete!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

        }
    }
}
