package com.example.mobilprogramlamafinal.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mobilprogramlamafinal.R;
import com.example.mobilprogramlamafinal.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    EditText isim,soyisim,mail,sifre;
    Button kaydol,girisyap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        isim = findViewById(R.id.name);
        soyisim = findViewById(R.id.lastname);
        mail = findViewById(R.id.mail);
        sifre = findViewById(R.id.password);
        kaydol = findViewById(R.id.register);
        girisyap = findViewById(R.id.login);

        girisyap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });
        kaydol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = isim.getText().toString();
                String lastname = soyisim.getText().toString();
                String email = mail.getText().toString();
                String password = sifre.getText().toString();

                FirebaseAuth auth = FirebaseAuth.getInstance();

                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            String uid = task.getResult().getUser().getUid();

                            Toast.makeText(RegisterActivity.this, "Kayıt başarıyla oluşturuldu, lütfen giriş yapınız!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            FirebaseFirestore dB = FirebaseFirestore.getInstance();
                            CollectionReference ref = dB.collection("Users");
                            User user = new User(email,lastname,name);
                            ref.add(user);
                            startActivity(intent);
                        }
                    }
                });
            }
        });

    }
}