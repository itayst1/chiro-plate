package com.example.plateapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private Button login;
    private TextView userId, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.LoginButton);
        userId = findViewById(R.id.userId);
        password = findViewById(R.id.password);
    }

    public void onLoginClick(View v){
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
    }

}
