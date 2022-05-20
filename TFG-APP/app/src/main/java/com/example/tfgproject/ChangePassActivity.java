package com.example.tfgproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

// Activity que gestiona la solicitud de cambio de contraseña de un usuario
public class ChangePassActivity extends AppCompatActivity {

    private Button btn_changePass, btn_back;
    private EditText et_email;
    private FirebaseAuth fbAuth;
    private String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        // Ocultar ActionBar
        getSupportActionBar().hide();

        // Autenticación
        fbAuth = FirebaseAuth.getInstance();

        // Variables
        et_email = (EditText) findViewById(R.id.emailForgotET);
        btn_changePass = (Button) findViewById(R.id.changePassBT);
        btn_back = (Button) findViewById(R.id.changePassBackBT);

        //Listeners
        btn_changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = et_email.getText().toString();
                if (!email.isEmpty()) {
                    Toast.makeText(ChangePassActivity.this, "Mandando solicitud ", Toast.LENGTH_SHORT).show();
                    resetPassword();
                } else {
                    Toast.makeText(ChangePassActivity.this, "Introduce el e-mail", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChangePassActivity.this, LoginActivity.class));
            }
        });
    }

    // Lógica de la solicitud del cambio de contraseña
    private void resetPassword() {

        fbAuth.setLanguageCode("es");
        fbAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(ChangePassActivity.this, LoginActivity.class));
                }else{
                    Toast.makeText(ChangePassActivity.this, "Error al reestablecer contraseña", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}