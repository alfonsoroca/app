package com.example.tfgproject;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfgproject.adapters.Adapter;
import com.example.tfgproject.entities.TimeCard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class TimeCardListActivity extends AppCompatActivity {

    private String dateToSearch="";
    private DatePickerDialog datePickerDialog;
    private Calendar c = Calendar.getInstance();
    private List<TimeCard> list;
    private Adapter adapter;
    private FirebaseDatabase fbDatabase;
    private DatabaseReference dbRegistrationsReference;
    private RecyclerView rv;
    private Button bt_search, bt_displaySearch, bt_backMenu;
    private int day, month, year;
    private TextView tv_infoDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_card_list);

        // Ocultar ActionBar
        getSupportActionBar().hide();

        // Database
        loadDatabase();

        // Variables
        loadVariables();

        // Recycleview
        loadRecycleview();

        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        bt_displaySearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                loadTimeCardFilter(dateToSearch);
            }
        });

        bt_backMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplication(), HomeActivity.class));
            }
        });

        loadTimeCardList();

    }

    private void loadRecycleview() {
        rv.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new Adapter (list);
        rv.setAdapter(adapter);
    }

    private void loadVariables() {
        tv_infoDate = (TextView) findViewById(R.id.infoDate);
        rv = (RecyclerView) findViewById(R.id.recycler);
        bt_search = (Button)findViewById(R.id.searchByDateBT);
        bt_displaySearch =(Button)findViewById(R.id.searchTimeCardBT);
        bt_backMenu =(Button)findViewById(R.id.backMenuCardListBT);
    }

    private void loadDatabase() {
        fbDatabase = FirebaseDatabase
                .getInstance("https://tfg-project-alfonso-default-rtdb.europe-west1.firebasedatabase.app/");
        dbRegistrationsReference = fbDatabase.getReference("registrations");
    }

    private void loadTimeCardList() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String id = user.getUid();

        fbDatabase.getReference("registrations").child(id)
                .orderByChild("date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    list.clear();
                    for (DataSnapshot s: snapshot.getChildren()){
                        TimeCard timeCard = s.getValue(TimeCard.class);
                        list.add(timeCard);
                        adapter.notifyDataSetChanged();
                    }
                    Collections.reverse(list);
                    tv_infoDate.setText("");
                }else{
                    tv_infoDate.setText("No existen registros");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void showDatePickerDialog(){

        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);

       // Selector de fechas
        datePickerDialog = new DatePickerDialog(TimeCardListActivity.this,R.style.PickerStyle,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int y, int m, int d) {
                        tv_infoDate.setText(String.format("%04d", y) + "-" + String.format("%02d", m+1) + "-" + String.format("%02d", d));
                        dateToSearch= tv_infoDate.getText().toString();
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void loadTimeCardFilter(String dateToSearch){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        dbRegistrationsReference.child(userId).orderByChild("date").
                equalTo(dateToSearch).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list.clear();
                    for (DataSnapshot s : snapshot.getChildren()) {
                        TimeCard timeCard = s.getValue(TimeCard.class);
                        list.add(timeCard);
                        tv_infoDate.setText("");
                        adapter.notifyDataSetChanged();
                    }
                    Collections.reverse(list);
                } else {
                    tv_infoDate.setText("No existen registros para la fecha seleccionada");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}