package com.nk2sp.ifra;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity9 extends AppCompatActivity {
    RecyclerView recyclerView;
    Test_AlarmAdapter t_aAdapter;
    List<Test_Alarm> t_aDatas;
    ImageButton apBack, F5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_9_test_nf);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);//다크모드 삭제

        apBack = findViewById(R.id.ibtn_apBack);
        F5 = findViewById(R.id.ibtn_f5);
        recyclerView = findViewById(R.id.list_rv);
        t_aDatas = new ArrayList<>();
        apBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { finish();}
        });
        F5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "새로고침 준비중입니다.", Toast.LENGTH_SHORT).show();
                if (!t_aDatas.isEmpty()) {t_aDatas.clear();}
                Random rand = new Random();
                int num = rand.nextInt(10);
                LocalDate currentDate = LocalDate.now(); //java 8 이상
                for(int i =0; i<=num; i++){
                    t_aDatas.add(new Test_Alarm("new_face_"+i,currentDate.toString()));
                }
                t_aAdapter = new Test_AlarmAdapter(t_aDatas);
                recyclerView.setAdapter(t_aAdapter);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}