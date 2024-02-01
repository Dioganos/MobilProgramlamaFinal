package com.example.mobilprogramlamafinal.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mobilprogramlamafinal.MainActivity;
import com.example.mobilprogramlamafinal.R;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScren extends AppCompatActivity {

    FirebaseAuth fbase;
    private Button LoginBtn;
    private Button SignupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_scren);

        LoginBtn = findViewById(R.id.btn_login);
        SignupBtn = findViewById(R.id.btn_signup);

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });

        SignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });

        fbase = FirebaseAuth.getInstance();

        if(fbase.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

    }
}