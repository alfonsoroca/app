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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
// Activity que gestiona la creación de vacaciones de un usuario
public class HolidayActivity extends AppCompatActivity {

    private Button btn_selectStart, btn_selectEnd, btn_confirm, btn_menu;
    private TextView tv_startHoliday, tv_endtHoliday;
    private FirebaseAuth fbAuth;
    private FirebaseUser fbUser;
    private FirebaseDatabase fbDatabase;
    private DatabaseReference dbRegistrationsReference;
    private Calendar c = Calendar.getInstance();
    private DatePickerDialog dpd_startHoliday, dpd_endHoliday;
    private int day = c.get(Calendar.DAY_OF_MONTH);
    private int month = c.get(Calendar.MONTH);
    private int year = c.get(Calendar.YEAR);
    private LocalDate today = LocalDate.now();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holiday);

        // Ocultar ActionBar
        getSupportActionBar().hide();

        // Database
        loadDatabase();

        // Variables
        loadVariables();

        // Listeners
        loadListeners();

    }

    private void loadDatabase() {
        fbAuth = FirebaseAuth.getInstance();
        fbUser = fbAuth.getCurrentUser();
        fbDatabase = FirebaseDatabase
                .getInstance("https://tfg-project-alfonso-default-rtdb.europe-west1.firebasedatabase.app/");
        dbRegistrationsReference = fbDatabase.getReference("registrations");
    }

    private void loadVariables() {
        btn_selectStart = (Button) findViewById(R.id.holidaySelectStartDateBT);
        btn_selectEnd = (Button) findViewById(R.id.holidaySelectEndDateBT);
        btn_menu = (Button) findViewById(R.id.holidayBackMenuBT);
        btn_confirm = (Button) findViewById(R.id.holidayConfirmDateBT);
        tv_startHoliday = (TextView) findViewById(R.id.holidayStartDateTV);
        tv_endtHoliday = (TextView) findViewById(R.id.holidayEndDateTV);
    }

    private void loadListeners() {
        btn_selectStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectStartHoliday();
            }
        });

        btn_selectEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectEndHoliday();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmHolidays();
            }
        });

        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HolidayActivity.this, HomeActivity.class));
            }
        });
    }
    // Lógica que crea días de vacaciones
    private void confirmHolidays() {

        loadVariables();

        String startHoliday = tv_startHoliday.getText().toString();
        String endHoliday = tv_endtHoliday.getText().toString();
        String userId = fbUser.getUid();
        List<String> holidays = new ArrayList<>();

        if (startHoliday.equals("")) {
            Toast.makeText(this, "Debes indicar el día de inicio de vacaciones", Toast.LENGTH_SHORT).show();
        }
        else if (endHoliday.isEmpty()) {
            Toast.makeText(this, "Debes indicar el día de fin de vacaciones", Toast.LENGTH_SHORT).show();
        }

        else if ((startHoliday.compareTo(today.toString())) == 0) {
            Toast.makeText(HolidayActivity.this, "Hoy no pueden comenzar tus vacaciones", Toast.LENGTH_SHORT).show();
        }

        // Las vacaciones no se pueden pedir con retroactividad, hay que solicitarlas con antelación de al menos un día
        // y hay que validar las fechas de inicio y de fin
        else if (startHoliday.compareTo(endHoliday) <= 0 && (startHoliday.compareTo(today.toString())) > 0){

            // Cargamos un array con las fechas solicitadas de vacaciones
            LocalDate ld_startHoliday = LocalDate.parse(startHoliday);
            LocalDate ld_endHoliday = LocalDate.parse(endHoliday);

            while (!ld_startHoliday.isAfter(ld_endHoliday)) {
                holidays.add(ld_startHoliday.toString());
                ld_startHoliday = ld_startHoliday.plusDays(1);
            }

            for (String d : holidays) {

                dbRegistrationsReference.child(userId).orderByChild("date")
                        .equalTo(d).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            TimeCard tc = new TimeCard();
                            tc.setDate(d);
                            tc.setEntryTime(null);
                            tc.setOutTime(null);
                            tc.setReason(null);
                            tc.setDayOff(false);
                            tc.setHoliday(true);
                            tc.setUserId(userId);

                            dbRegistrationsReference.child(userId).push()
                                    .setValue(tc).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(!task.isComplete()){
                                        Toast.makeText(HolidayActivity.this, "Error en registro", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        // Eliminamos el listener
                        dbRegistrationsReference.child(userId).removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            Toast.makeText(this, "Vacaciones registradas", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(HolidayActivity.this, HomeActivity.class));

        }  else { Toast.makeText(HolidayActivity.this, "Revisa las fechas solicitadas", Toast.LENGTH_SHORT).show();}
    }


    private void selectEndHoliday() {
        // Selector de fechas
        dpd_endHoliday = new DatePickerDialog(HolidayActivity.this,R.style.PickerStyle,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int y, int m, int d) {
                        tv_endtHoliday.setText(String.format("%04d", y) + "-" + String.format("%02d", m+1) + "-" + String.format("%02d", d));
                    }
                }, year, month, day);
        dpd_endHoliday.show();
    }

    private void selectStartHoliday() {
        // Selector de fechas
        dpd_startHoliday = new DatePickerDialog(HolidayActivity.this,R.style.PickerStyle,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int y, int m, int d) {
                        tv_startHoliday.setText(String.format("%04d", y) + "-" + String.format("%02d", m+1) + "-" + String.format("%02d", d));
                    }
                }, year, month, day);
        dpd_startHoliday.show();
    }
}