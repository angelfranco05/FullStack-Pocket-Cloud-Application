package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.content.Intent;

import android.util.Log;
import android.view.MenuItem;


import com.google.android.material.navigation.NavigationView;

public class homePage extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_menu,R.string.close_menu);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);




        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.nav_home:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Log.i("Menu_DRAWER_TAG","Customers");
                        Intent intent1 = new Intent(homePage.this, Employee.class);
                        startActivity(intent1);
                        break;

                    case R.id.nav_home2:
                        Log.i("Menu_DRAWER_TAG","Inventory Opened");
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent intent2 = new Intent(homePage.this, Inventory.class);
                        startActivity(intent2);
                        break;

                    case R.id.nav_home3:
                        Log.i("Menu_DRAWER_TAG","Customer");
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent intent3 = new Intent(homePage.this, Customer.class);
                        startActivity(intent3);
                        break;

                    case R.id.nav_home4:
                        Log.i("Menu_DRAWER_TAG","Settings");
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent intent4 = new Intent(homePage.this, DLMode.class);
                        startActivity(intent4);
                        break;
                    case R.id.nav_home5:
                        Log.i("Menu_DRAWER_TAG","Settings");
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent intent5 = new Intent(homePage.this, Signout.class);
                        startActivity(intent5);
                        break;





                }
                return true;
            }
        });
    }

}