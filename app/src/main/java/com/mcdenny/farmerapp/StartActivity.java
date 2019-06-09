package com.mcdenny.farmerapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mcdenny.farmerapp.admin.AdminLoginActivity;
import com.mcdenny.farmerapp.user.LoginActivity;

import mehdi.sakout.fancybuttons.FancyButton;

public class StartActivity extends AppCompatActivity {
    Button user;
    FancyButton admin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        user = (Button) findViewById(R.id.btn_user_login);
        admin = (FancyButton) findViewById(R.id.btn_admin_login);

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, LoginActivity.class));

            }
        });

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, AdminLoginActivity.class));

            }
        });
    }
}
