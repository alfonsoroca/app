package com.example.tfgproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfgproject.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

// Activity que gestiona la introducción de los datos de un nuevo usuario
public class New_User_Activity extends AppCompatActivity {

    private TextView tv_EmailNewUser;
    private TextInputEditText it_NifNewUser, it_NameNewUser, it_DobNewUser, it_PhoneNewUser;
    private Button btn_Validate, btn_Exit;
    private FirebaseAuth fbAuth;
    private FirebaseUser fbUser;
    private FirebaseDatabase fbDatabase;
    private DatabaseReference dbUsersReference;
    private String userEmail, userId, nif, name, dob, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        // Ocultar ActionBar
        getSupportActionBar().hide();

        // Database
        loadDatabase();

        // Variables
        loadVariables();

        // Listeners
        loadListeners();

        // Carga datos usuario
        loadUserData();
    }

    private void loadListeners() {
        btn_Validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });

        btn_Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent (New_User_Activity.this, LoginActivity.class));
            }
        });

    }

    private void loadVariables() {
        it_NifNewUser = (TextInputEditText) findViewById(R.id.nifProfileIT);
        it_NameNewUser = (TextInputEditText) findViewById(R.id.nameNewUserIT);
        it_DobNewUser = (TextInputEditText) findViewById(R.id.dobNewUserIT);
        tv_EmailNewUser = (TextView) findViewById(R.id.emailNewUserTV);
        it_PhoneNewUser = (TextInputEditText) findViewById(R.id.phoneNewUserIT);
        btn_Validate = (Button) findViewById(R.id.createNewUserBT);
        btn_Exit = (Button) findViewById(R.id.exitNewUserBT);
        userId = fbUser.getUid();
        userEmail = fbUser.getEmail();
        nif = it_NifNewUser.getText().toString().toUpperCase();
        name = it_NameNewUser.getText().toString().toUpperCase();
        dob = it_DobNewUser.getText().toString();
        phone = it_PhoneNewUser.getText().toString();
    }

    private void loadDatabase() {
        fbAuth = FirebaseAuth.getInstance();
        fbUser = fbAuth.getCurrentUser();
        fbDatabase = FirebaseDatabase
                .getInstance("https://tfg-project-alfonso-default-rtdb.europe-west1.firebasedatabase.app/");
        dbUsersReference = fbDatabase.getReference("users");
    }

    private void loadUserData() {
        Query q = dbUsersReference.child(userId);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    tv_EmailNewUser.setText(userEmail);
                    Toast.makeText(New_User_Activity.this, "Debes introducir tus datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // Lógica de la persistencia de los datos del usuario
    private void validate() {

        loadVariables();

        if (name.isEmpty()) {
            it_NameNewUser.setError("Nombre vacío");
            it_NameNewUser.requestFocus();

        } else if (nif.isEmpty()) {
            it_NifNewUser.setError("Nif vacío");
            it_NifNewUser.requestFocus();

        } else if (dob.isEmpty()) {
            it_DobNewUser.setError("Fecha nacimiento vacía");
            it_DobNewUser.requestFocus();

        } else if (phone.isEmpty()) {
            it_PhoneNewUser.setError("Teléfono vacío");
            it_PhoneNewUser.requestFocus();

        } else {
            User user = new User();
            user.setUserid(userId);
            user.setEmail(userEmail);
            user.setNif(nif);
            user.setName(name);
            user.setDateOfBirth(dob);
            user.setPhone(phone);

            dbUsersReference.child(userId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Datos del usuario registrados", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(New_User_Activity.this, HomeActivity.class));
                    } else {
                        Toast.makeText(New_User_Activity.this, "Error al guardar el registro", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}