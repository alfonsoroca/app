package com.example.tfgproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Ocultar ActionBar
        getSupportActionBar().hide();

        // Listeners
        findViewById(R.id.registrationHomeBT).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, RegistrationActivity.class));
            }
        });

        findViewById(R.id.listHomeBT).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, TimeCardListActivity.class));
            }
        });

        findViewById(R.id.registrationDofBT).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, DayOffActivity.class));
            }
        });

        findViewById(R.id.holidayHomeBT).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, HolidayActivity.class));
            }
        });

        findViewById(R.id.profileHomeBT).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            }
        });

        findViewById(R.id.exitHomeBT).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent (HomeActivity.this, LoginActivity.class));
            }
        });
    }
}