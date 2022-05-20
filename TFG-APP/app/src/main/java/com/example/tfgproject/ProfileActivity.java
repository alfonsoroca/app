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

// Activity que gestiona la modificación de los datos de un usuario
public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText it_NameProfile, it_NifProfile, it_DobProfile, it_PhoneProfile;
    private TextView tv_EmailProfile;
    private Button btn_ModifyProfile, btn_BackMenu;
    private FirebaseAuth mAuth;
    private FirebaseUser fbUserId;
    private FirebaseDatabase fbDatabase;
    private DatabaseReference dbUsersReference;
    private String userId, userEmail, email, name, nif, dob, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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

    private void loadUserData() {
        Query q = dbUsersReference.child(userId);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User u = snapshot.getValue(User.class);
                    tv_EmailProfile.setText(u.getEmail());
                    it_NameProfile.setText(u.getName());
                    it_NifProfile.setText(u.getNif());
                    it_DobProfile.setText(u.getDateOfBirth());
                    it_PhoneProfile.setText(u.getPhone());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void loadListeners() {
        btn_ModifyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyProfile();
            }
        });
        btn_BackMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            }
        });
    }

    private void loadVariables() {
        tv_EmailProfile = (TextView) findViewById(R.id.emailProfileTV);
        it_NameProfile = (TextInputEditText) findViewById(R.id.nameProfileTI);
        it_NifProfile = (TextInputEditText) findViewById(R.id.nifProfileTI);
        it_DobProfile = (TextInputEditText) findViewById(R.id.dobProfileTI);
        it_PhoneProfile = (TextInputEditText) findViewById(R.id.phoneProfileTI);
        btn_ModifyProfile = (Button) findViewById(R.id.modifyProfileBT);
        btn_BackMenu = (Button) findViewById(R.id.backMenuProfileBT);
        userId = fbUserId.getUid();
        userEmail = fbUserId.getEmail();
        email = tv_EmailProfile.getText().toString().toLowerCase();
        name = it_NameProfile.getText().toString().toUpperCase();
        nif = it_NifProfile.getText().toString().toUpperCase();
        dob = it_DobProfile.getText().toString();
        phone = it_PhoneProfile.getText().toString();
    }

    private void loadDatabase() {
        mAuth = FirebaseAuth.getInstance();
        fbUserId = mAuth.getCurrentUser();
        fbDatabase = FirebaseDatabase
                .getInstance("https://tfg-project-alfonso-default-rtdb.europe-west1.firebasedatabase.app/");
        dbUsersReference = fbDatabase.getReference("users");
    }

    // Lógica de la persistencia de los nuevos datos del usuario
    private void modifyProfile() {

        loadVariables();

       if (name.isEmpty()) {
            it_NameProfile.setError("Nombre vacío");
            it_NameProfile.requestFocus();

        } else if (nif.isEmpty()) {
            it_NifProfile.setError("Nif vacío");
            it_NifProfile.requestFocus();

        } else if (dob.isEmpty()) {
            it_DobProfile.setError("Fecha nacimiento vacía");
            it_DobProfile.requestFocus();

        } else if (phone.isEmpty()) {
            it_PhoneProfile.setError("Teléfono vacío");
            it_PhoneProfile.requestFocus();

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
                        Toast.makeText(getApplication(), "Usuario modificado", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    } else {
                        Toast.makeText(getApplication(), "Error al guardar el registro", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}