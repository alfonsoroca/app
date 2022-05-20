package com.example.tfgproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// Activity que gestiona la solicitud de login de un usuario
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth fbAuth;
    private FirebaseUser fbUser;
    private FirebaseDatabase fbDatabase;
    private DatabaseReference dbUsersReference;
    private TextInputEditText et_email, et_pass;
    private Button btn_login, btn_rpass;
    private String userId, email, pass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Ocultar ActionBar
        getSupportActionBar().hide();

        // Database
        loadDatabase();

        // Variables
        loadVariables();

        //Listeners
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        btn_rpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ChangePassActivity.class));
            }
        });
    }

    // Lógica del login de un usuario
    private void login() {
        email = et_email.getText().toString();
        pass = et_pass.getText().toString();

        // Comprobar contenido cajas de texto
        if (email.isEmpty()) {
            et_email.setError("Email vacío");

        }
        else if (pass.isEmpty()) {
            et_pass.setError("Password vacía");
            et_pass.requestFocus();

        } else {
            fbAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            // Login correcto
                            if(task.isSuccessful()){
                                userId = fbUser.getUid();
                                // Carga de datos de usuario
                                dbUsersReference.child(userId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        // Si los datos de usuario no existen, hay que rellenarlos
                                        if(!snapshot.exists()){
                                            startActivity(new Intent(LoginActivity.this, New_User_Activity.class));
                                        } else {
                                            Toast.makeText(LoginActivity.this,"Sesión iniciada", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                        }
                                        // Eliminamos el listener
                                        dbUsersReference.child(userId).removeEventListener(this);
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }
                            // Login erróneo
                            else{
                                Toast.makeText(LoginActivity.this,"Error: Compruebe las credenciales", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private void loadVariables() {
        et_email = (TextInputEditText) findViewById(R.id.emailBox);
        et_pass = (TextInputEditText) findViewById(R.id.passBox);
        btn_login = (Button) findViewById(R.id.loginButton);
        btn_rpass = (Button) findViewById(R.id.restorePassButton);
    }

    private void loadDatabase() {

        fbAuth = FirebaseAuth.getInstance();
        fbUser = fbAuth.getCurrentUser();
        fbDatabase = FirebaseDatabase
                .getInstance("https://tfg-project-alfonso-default-rtdb.europe-west1.firebasedatabase.app/");
        dbUsersReference = fbDatabase.getReference("users");
    }

    @Override
    public void onBackPressed() {
    }
}