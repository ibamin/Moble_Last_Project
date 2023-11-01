package com.nk2sp.ifra;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity5 extends AppCompatActivity {
    ImageButton mm_Mypage; //mm_Mypage : Manager Main -> Mypage
    Button Test;
    Button Manage;
    CountDownTimer countDownTimer;
    TextView timeTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);//다크모드 삭제
        setContentView(R.layout.activity_5_manager_main);

        mm_Mypage = findViewById(R.id.ibtn_mypage);
        Test = findViewById(R.id.btn_Test);
        Manage = findViewById(R.id.btn_Manage);
        timeTextView = findViewById(R.id.tv_Time);
        mm_Mypage.setOnClickListener(new View.OnClickListener() { //마이페이지 바로가기
            @Override
            public void onClick(View view) {
                Intent mmmypage=new Intent(getApplicationContext(), MainActivity6.class);
                startActivity(mmmypage);
            }
        });
        Test.setOnClickListener(new View.OnClickListener() { //Test_NF 바로가기(목업)
            @Override
            public void onClick(View view) {
                Intent Test = new Intent(getApplicationContext(), MainActivity9.class);
                startActivity(Test);
            }
        });
        Manage.setOnClickListener(new View.OnClickListener() { //관리페이지(All_Log) 바로가기
            @Override
            public void onClick(View view) {
                Intent manager=new Intent(getApplicationContext(), MainActivity8.class);
                startActivity(manager);
            }
        });
        countDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) { //메인페이지 현재날짜띄우기
            public void onTick(long millisUntilFinished) {
                updateTime();
            }
            public void onFinish() {
                // 작업을 종료할 때의 동작
            }
        }.start();
    }
    private void updateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault());
        String cur_Date = dateFormat.format(new Date());
        timeTextView.setText(cur_Date);
    }
}