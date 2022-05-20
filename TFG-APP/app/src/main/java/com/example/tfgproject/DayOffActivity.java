package com.example.tfgproject;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfgproject.entities.TimeCard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.Calendar;
// Activity que gestiona la creación de permisos de un usuario
public class DayOffActivity extends AppCompatActivity {

    private TextView tv_date;
    private TextInputEditText it_reason;
    private Button btn_selectDate, btn_confirm, btn_backMenu;
    private Calendar c = Calendar.getInstance();
    private FirebaseAuth mAuth;
    private FirebaseUser fbUser;
    private FirebaseDatabase fbDatabase;
    private DatabaseReference dbRegistrationsReference;
    private DatePickerDialog datePickerDialog;
    private int day, month, year;
    private String today;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_off);

        // Ocultar ActionBar
        getSupportActionBar().hide();

        // Database
        loadDatabase();

        // Variables
        loadVariables();

        // Listeners
        loadListeners();
    }

    private void loadListeners() {
        btn_selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate();
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmation();
            }
        });
        btn_backMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DayOffActivity.this, HomeActivity.class));
            }
        });
    }

    // Lógica que crea días de permiso
    private void confirmation() {

        String date = tv_date.getText().toString();
        String reason = it_reason.getText().toString();
        String userId = fbUser.getUid();

        if (reason.isEmpty()) {
            it_reason.setError("Debes especificar el motivo");
            it_reason.requestFocus();
        }
        else if (date.isEmpty()) {
            Toast.makeText(this, "Debes indicar el día para el permiso",Toast.LENGTH_SHORT).show();
        }
        // No se pueden solicitar permisos con retroactividad
        else if (today.compareTo(date) <= 0){

            dbRegistrationsReference.child(userId).orderByChild("date")
                    .equalTo(date).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // No se pueden solicitar permisos en fechas que ya tienen registros
                    if (!snapshot.exists()){
                        TimeCard tc = new TimeCard();
                        tc.setDate(date);
                        tc.setEntryTime(null);
                        tc.setOutTime(null);
                        tc.setDayOff(true);
                        tc.setReason(reason);
                        tc.setUserId(userId);

                        dbRegistrationsReference.child(userId).push()
                                .setValue(tc).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(DayOffActivity.this, "Permiso registrado", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                }
                            }
                        });
                    } else {
                        Toast.makeText(DayOffActivity.this, "Ya existe un registro para ese día", Toast.LENGTH_SHORT).show();
                    }
                    // Eliminamos el listener
                    dbRegistrationsReference.child(userId).removeEventListener(this);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

        } else { Toast.makeText(DayOffActivity.this, "La fecha debe ser igual o mayor a hoy", Toast.LENGTH_SHORT).show(); }
    }

    private void selectDate() {
        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);

        datePickerDialog = new DatePickerDialog(DayOffActivity.this,R.style.PickerStyle,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int y, int m, int d) {
                        tv_date.setText(String.format("%04d", y) + "-" + String.format("%02d", m+1) + "-" + String.format("%02d", d));
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void loadVariables() {
        tv_date = (TextView) findViewById(R.id.dofDateTV);
        it_reason = (TextInputEditText) findViewById(R.id.dofReasonIT);
        btn_backMenu = (Button) findViewById(R.id.dofBackMenuBT);
        btn_selectDate = (Button) findViewById(R.id.dofSelectDateBT);
        btn_confirm = (Button) findViewById(R.id.dofConfirmDateBT);
        today = LocalDate.now().toString();
    }

    private void loadDatabase() {
        mAuth = FirebaseAuth.getInstance();
        fbUser = mAuth.getCurrentUser();
        fbDatabase = FirebaseDatabase.getInstance("https://tfg-project-alfonso-default-rtdb.europe-west1.firebasedatabase.app/");
        dbRegistrationsReference = fbDatabase.getReference("registrations");
    }
}