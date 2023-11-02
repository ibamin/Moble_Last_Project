package com.nk2sp.ifra;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity7 extends AppCompatActivity {
    Single_ton_data mydata = Single_ton_data.getInstance();
    ImageButton ulc_Mypage, ulc_Back;
    CalendarView calendarview;
    TextView S_Date;
    String State, IN_time, OUT_time; //server data
    String selectedDate, logDate;
    RecyclerView recyclerView;
    My_LogAdapter ML_Adapter;
    List<My_Log> ML_Datas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);//다크모드 삭제
        setContentView(R.layout.activity_7_my_log_check);
        //-------------------------------------------------
        ulc_Back = findViewById(R.id.ibtn_ulcBack);
        //-------------------------------------------------
        ulc_Mypage = findViewById(R.id.ibtn_ulcmypage);
        //-------------------------------------------------
        calendarview = findViewById(R.id.calendarView);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(year, month, day);
        long millis = calendar.getTimeInMillis();
        calendarview.setDate(millis);
        S_Date = findViewById(R.id.tv_LselectDate_result);
        recyclerView = findViewById(R.id.list_rv_mylog);
        ML_Datas = new ArrayList<>();
        ulc_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { finish();}
        });
        ulc_Mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ulcmypage=new Intent(getApplicationContext(), MainActivity6.class);
                startActivity(ulcmypage);
            }
        });

        calendarview.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                //선택된 날짜 정보를 가지고 원하는 동작 수행
                //String senddate = Integer.toString(year) + "-" + Integer.toString(month + 1) + "-" + Integer.toString(dayOfMonth) ;
                String senddate = Integer.toString(year) + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth);
                selectedDate = year + "년 " + (month + 1) + "월 " + dayOfMonth + "일 ";
                S_Date.setText(selectedDate);
                if (!ML_Datas.isEmpty()) {ML_Datas.clear();}
                get_log(senddate);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    public void get_log(String sedate) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id",mydata.getUser_id())
                .addFormDataPart("date",sedate)
                .build();

        Request request = new Request.Builder()
                .url("http://15.164.120.162:5000/log")
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { e.printStackTrace();}
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    try {
                        JSONObject json = new JSONObject(responseBody);

                        // "status" 키를 사용하여 응답 상태 확인
                        String status = json.getString("status");
                        if (status.equals("success")) {
                            JSONArray logsArray = json.getJSONArray("logs");

                            for (int i = 0; i < logsArray.length(); i++) {
                                JSONObject logObject = logsArray.getJSONObject(i);
                                String state = logObject.getString("state");
                                String inner_time = logObject.getString("inner_time");
                                String outter_time = logObject.getString("outter_time");

                                // 로그 정보를 처리하는 코드 추가
                                Log.d("JSON Response: ", "status: " + status);
                                Log.d("JSON Response: ", "inner_time: " + inner_time);
                                Log.d("JSON Response: ", "outter_time: " + outter_time);
                                Log.d("JSON Response: ", "state: " + state);

                                if (inner_time.equals("null")) {inner_time = "";}                   //it = null 일때
                                if (outter_time.equals("null")) {outter_time = "";}                 //ot = null 일때
                                if (state.equals("null")) {state = "";}                             //state = null 일때

                                IN_time = inner_time;
                                OUT_time = outter_time;
                                State = state;

                                ML_Datas.add(new My_Log(Datefilter(inner_time), Datefilter(outter_time), state));
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ML_Adapter = new My_LogAdapter(ML_Datas);                       // 메인 스레드에서 UI 작업을 수행
                                    recyclerView.setAdapter(ML_Adapter);
                                }
                            });
                        } else if (status.equals("no_logs")) {
                            // 로그 없음 처리
                            updateRecyclerView(new ArrayList<>());
                            Log.d("JSON Response: ", "No logs available for the given date.");
                        } else if (status.equals("error")) {
                            // 오류 처리
                            updateRecyclerView(new ArrayList<>());
                            Log.e("Response Error", "Error occurred while fetching logs.");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("JSON Error", "Error parsing JSON response: " + e.getMessage());
                    }
                } else {
                    // 응답이 성공적이지 않은 경우에 대한 처리
                    Log.e("Response Error", "Response Code: " + response.code());
                }
            }
        });
    }
    public String Datefilter(String time_data){
        //String dateString = "Wed, 18 Oct 2023 09:10:00 GMT";
        String dateString = time_data;
        String Result = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);


        try {
            if (dateString != null && !dateString.isEmpty()) {
                Date date = dateFormat.parse(dateString); // 문자열을 Date 객체로 파싱
                SimpleDateFormat outputFormat = new SimpleDateFormat("HH시 mm분 ss초", Locale.KOREA);  // 필요에 따라 다른 형식으로 날짜를 출력
                String outputDateString = outputFormat.format(date);
                Result = outputDateString;
            } else { Result = "";}
        } catch (ParseException e) {
            e.printStackTrace();}
        return Result;
    }
    private void updateRecyclerView(List<My_Log> dataList) {
        ML_Datas.clear();
        ML_Datas.add(new My_Log("", "", "")); //받은 데이터가 없을시.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ML_Adapter = new My_LogAdapter(ML_Datas);
                recyclerView.setAdapter(ML_Adapter);
                Toast.makeText(getApplicationContext(), "데이터가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}