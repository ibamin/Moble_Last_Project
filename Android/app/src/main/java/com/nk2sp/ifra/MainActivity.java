package com.nk2sp.ifra;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);//다크모드 삭제
        setContentView(R.layout.activity_1_standby_screen);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() { // 몇초 뒤에 다음 화면으로 이동하는 Runnable을 실행
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        }, 3000);
    }
}