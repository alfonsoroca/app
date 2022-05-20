package com.example.tfgproject;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfgproject.entities.TimeCard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
// Activity que gestiona la creación de registros de un usuario
public class RegistrationActivity extends AppCompatActivity {

    private Button btn_Time, btn_BackMenu;
    private ImageButton ibtn_InputTime, ibtn_OutputTime;
    private TextView tv_Date, tv_InputTime, tv_OutputTime, tv_Time, tv_DofHol;
    private LocalDate today = LocalDate.now();
    private LocalTime localTimeObject = LocalTime.now();
    private DateTimeFormatter dtfTime = DateTimeFormatter.ofPattern("H:mm");
    private FirebaseAuth mAuth;
    private FirebaseUser fbUser;
    private FirebaseDatabase fbDatabase;
    private DatabaseReference dbRegistrationsReference;
    private int hour, minute;
    private String userId, idRegistration;
    private TimeCard timeCard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Ocultar ActionBar
        getSupportActionBar().hide();

        // Database
        loadDatabase();

        // Variables
        loadVariables();

        // Listeners
        loadListeners();

        // Carga de fichaje pendiente
        loadRegistration();
    }

    // Lógica de carga de registros abiertos, días de permiso o de vacaciones
    private void loadRegistration() {

        dbRegistrationsReference.child(userId).orderByChild("date")
                .equalTo(today.toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot s : snapshot.getChildren()) {
                        // Cargamos la TimeCard con los datos existentes
                        timeCard = s.getValue(TimeCard.class);
                        if(timeCard.isDayOff()) {
                            // Actuamos sobre las vistas
                            tv_InputTime.setVisibility(View.INVISIBLE);
                            tv_OutputTime.setVisibility(View.INVISIBLE);
                            tv_Time.setVisibility(View.INVISIBLE);
                            btn_Time.setVisibility(View.INVISIBLE);
                            ibtn_InputTime.setVisibility(View.INVISIBLE);
                            ibtn_OutputTime.setVisibility(View.INVISIBLE);
                            tv_DofHol.setText("Permiso");
                            tv_DofHol.setBackgroundColor(Color.parseColor("#546E7A"));
                            tv_DofHol.setVisibility(View.VISIBLE);
                        } else if (timeCard.isHoliday()){
                            // Actuamos sobre las vistas
                            tv_InputTime.setVisibility(View.INVISIBLE);
                            tv_OutputTime.setVisibility(View.INVISIBLE);
                            tv_Time.setVisibility(View.INVISIBLE);
                            btn_Time.setVisibility(View.INVISIBLE);
                            ibtn_InputTime.setVisibility(View.INVISIBLE);
                            ibtn_OutputTime.setVisibility(View.INVISIBLE);
                            tv_DofHol.setText("Vacaciones");
                            tv_DofHol.setBackgroundColor(Color.parseColor("#FF018786"));
                            tv_DofHol.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        dbRegistrationsReference.child(userId).orderByChild("outTime")
                .equalTo("").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for (DataSnapshot s : snapshot.getChildren()) {
                                // Cargamos la TimeCard con los datos existentes
                                timeCard = s.getValue(TimeCard.class);
                                // Vistas de salida
                                tv_InputTime.setVisibility(View.VISIBLE);
                                tv_OutputTime.setVisibility(View.VISIBLE);
                                tv_Time.setVisibility(View.VISIBLE);
                                btn_Time.setVisibility(View.VISIBLE);
                                tv_OutputTime.setVisibility(View.VISIBLE);
                                tv_OutputTime.setText("SALIDA: ");
                                ibtn_OutputTime.setVisibility(View.VISIBLE);
                                // Cargamos las vistas
                                tv_Date.setText(timeCard.getDate());
                                tv_InputTime.setText("ENTRADA: " + timeCard.getEntryTime());
                                // Ocultamos el fichaje de entrada y permisos / vacaciones
                                ibtn_InputTime.setVisibility(View.INVISIBLE);
                                tv_DofHol.setVisibility(View.INVISIBLE);
                                // Obtenemos el id del nodo
                                idRegistration = s.getKey();
                            }
                        } else  {
                            tv_OutputTime.setVisibility(View.INVISIBLE);
                            ibtn_OutputTime.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void loadListeners() {
        btn_Time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTime();
            }
        });
        ibtn_InputTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                entryTime();
            }
        });
        ibtn_OutputTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitTime();
            }
        });
        btn_BackMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this, HomeActivity.class));
            }
        });
    }

    private void loadVariables() {
        hour = localTimeObject.getHour();
        minute = localTimeObject.getMinute();
        btn_Time = (Button) findViewById(R.id.changeTimeButton);
        btn_BackMenu = (Button) findViewById(R.id.backMenuRegistrationBT);
        ibtn_InputTime = (ImageButton) findViewById(R.id.timeInButton);
        ibtn_OutputTime = (ImageButton) findViewById(R.id.timeOutButton);
        tv_Date = (TextView) findViewById(R.id.dateNowTV);
        tv_InputTime = (TextView) findViewById(R.id.inputTimeTV);
        tv_OutputTime = (TextView) findViewById(R.id.outputTimeTV);
        tv_Time = (TextView) findViewById(R.id.timeTV);
        userId = fbUser.getUid();
        tv_Date.setText(today.toString());
        tv_Time.setText("HORA SELECCIONADA: " + localTimeObject.format(dtfTime));
        tv_DofHol = (TextView) findViewById(R.id.registrationDHTV);
    }

    private void loadDatabase() {
        mAuth = FirebaseAuth.getInstance();
        fbUser = mAuth.getCurrentUser();
        fbDatabase = FirebaseDatabase
                .getInstance("https://tfg-project-alfonso-default-rtdb.europe-west1.firebasedatabase.app/");
        dbRegistrationsReference = fbDatabase.getReference("registrations");
    }

    // Lógica que gestiona el registro de la hora de salida
    private void exitTime() {

        String outTime = String.format("%02d", hour) + ":" + String.format("%02d", (minute));

        // Validación hora salida >= hora entrada
        if(outTime.compareTo(timeCard.getEntryTime()) >= 0){
            timeCard.setOutTime(outTime);
            timeCard.setReason(null);
            TimeCard tc = timeCard;
            dbRegistrationsReference.child(userId).child(idRegistration)
                    .setValue(tc).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(RegistrationActivity.this, "Salida registrada", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplication(), HomeActivity.class));
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Error al guardar el registro", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            Toast.makeText(RegistrationActivity.this, "La salida debe ser posterior a la entrada", Toast.LENGTH_SHORT).show();
        }
    }

    // Lógica que gestiona el registro de la hora de entrada
    private void entryTime() {

        String entryTime = String.format("%02d", hour) + ":" + String.format("%02d", (minute));
        TimeCard timeCard = new TimeCard();
        timeCard.setDate(today.toString());
        timeCard.setEntryTime(entryTime);
        timeCard.setOutTime("");
        timeCard.setReason(null);
        timeCard.setUserId(userId);

        dbRegistrationsReference.child(userId).push()
                .setValue(timeCard).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(RegistrationActivity.this, "Entrada registrada", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplication(), HomeActivity.class));
                } else {
                    Toast.makeText(RegistrationActivity.this, "Error al guardar el registro", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void changeTime() {

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.PickerStyle,new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker timePicker, int h, int m) {
                hour =h;
                minute = m;
                String selectedTime = String.format("%02d", h) + ":" + String.format("%02d", m);
                tv_Time.setText("HORA SELECCIONADA: " + selectedTime);
            }

        }, hour, minute, true);

        timePickerDialog.show();
    }
}