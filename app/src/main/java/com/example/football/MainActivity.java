package com.example.football;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.football.models.Field;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Spinner citySpinner;
    private RecyclerView fieldRecyclerView;

    private List<Field> fieldList;
    private FieldAdapter fieldAdapter;

    private DatabaseReference fieldDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        citySpinner = findViewById(R.id.citySpinner);
        fieldRecyclerView = findViewById(R.id.fieldRecyclerView);

        fieldList = new ArrayList<>();
        fieldAdapter = new FieldAdapter(this, fieldList);

        fieldRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        fieldRecyclerView.setAdapter(fieldAdapter);

        fieldDatabase = FirebaseDatabase.getInstance().getReference("fields");

        // Spinner khu vực
        String[] cities = {"Tất cả", "Vinh"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                cities
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(spinnerAdapter);

        loadFields();
    }

    private void loadFields() {
        fieldDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fieldList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Field field = data.getValue(Field.class);
                    if (field != null) {
                        fieldList.add(field);
                    }
                }

                fieldAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}