package com.nk2sp.ifra;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity8 extends AppCompatActivity {
    ImageButton mpBack;
    EditText Year;
    Spinner Month, dayOM;
    ImageButton ALSearch;
    String yyyy, mm, dom; //서버에 요청조건
    RecyclerView recyclerView;
    All_LogAdapter AL_Adapter;
    List<All_Log> AL_Datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);//다크모드 삭제
        setContentView(R.layout.activity_8_manage_page);

        mpBack = findViewById(R.id.ibtn_mpBack);
        Year = findViewById(R.id.etv_mm_YEAR);
        Month = findViewById(R.id.sp_mm_Month);
        dayOM = findViewById(R.id.sp_mm_dOM);
        ALSearch = findViewById(R.id.ibtn_mm_search);
        recyclerView = findViewById(R.id.list_rv_Alllog);
        AL_Datas = new ArrayList<>();
        mpBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { finish();}
        });
        String[] mms = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
        ArrayAdapter<String> mms_adapter  = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mms);
        Month.setAdapter(mms_adapter);
        Month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                mm = mms[position].toString(); //사용자가 선택한 항목의 텍스트 가져오기
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                mm = ""; //또는 다른 기본값 설정
                Toast.makeText(getApplicationContext(), "month를 선택하시오", Toast.LENGTH_SHORT).show();
            }
        });
        String[] doms = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
                "31"};
        ArrayAdapter<String> doms_adapter  = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, doms);
        dayOM.setAdapter(doms_adapter);
        dayOM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                dom = doms[position].toString(); //사용자가 선택한 항목의 텍스트 가져오기
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Toast.makeText(getApplicationContext(), "doy of month를 선택하시오", Toast.LENGTH_SHORT).show();
            }
        });
        ALSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yyyy = Year.getText().toString();
                if(yyyy.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "년도를 입력하시오.", Toast.LENGTH_SHORT).show();
                }
                else{
                    if (!AL_Datas.isEmpty()) {AL_Datas.clear();}
                    All_log(yyyy, mm, dom);
                }
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    public void All_log(String year, String month, String dayofmonth) {
        String senddate = year + "-" + month + "-" + dayofmonth;

        Log.d("JSON Response: ", "date: " + senddate);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("date", senddate) //문자열 형태로 전송
                .build();

        Request request = new Request.Builder()
                .url("http://15.164.120.162:5000/all_log") //URL 오타 수정
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    try {
                        JSONObject json = new JSONObject(responseBody);

                        String status = json.getString("status"); //"status" 키를 사용하여 응답 상태 확인
                        if (status.equals("success")) {
                            JSONArray logsArray = json.getJSONArray("logs");

                            for (int i = 0; i < logsArray.length(); i++) {
                                JSONObject logObject = logsArray.getJSONObject(i);
                                String state = logObject.getString("state");
                                String inner_time = logObject.getString("inner_time");
                                String outter_time = logObject.getString("outter_time");
                                String confidence = logObject.getString("confidence");
                                String user_id = logObject.getString("user_id");

                                // 로그 정보를 처리하는 코드 추가
                                Log.d("JSON Response: ", "status: " + status);
                                Log.d("JSON Response: ", "inner_time: " + inner_time);
                                Log.d("JSON Response: ", "outter_time: " + outter_time);
                                Log.d("JSON Response: ", "state: " + state);
                                Log.d("JSON Response: ", "confidence: " + confidence);
                                Log.d("JSON Response: ", "user_id: " + user_id);

                                if (inner_time.equals("null")) {inner_time = "";}                   //it = null 일때
                                if (outter_time.equals("null")) {outter_time = "";}                 //ot = null 일때
                                if (state.equals("null")) {state = "";}                             //state = null 일때

                                if (confidence.equals("null")) {confidence = "";}                   //1. confidence = null 일때
                                else if (confidence.startsWith("[") && confidence.endsWith("]")) {  //2. confidence = [0.666...] 시작일때
                                    String cleanedInput = confidence.substring(1, confidence.length() - 1);
                                    double value = Double.parseDouble(cleanedInput);
                                    double roundedValue = Math.round(value * 100.0) / 100.0;
                                    confidence = String.valueOf(roundedValue);}
                                else if (confidence.startsWith("0.")) {                             //3. confidence = 0.666... 시작일때
                                    double value = Double.parseDouble(confidence);
                                    double roundedValue = Math.round(value * 100.0) / 100.0;
                                    confidence = String.valueOf(roundedValue);}

                                if (user_id.equals("null")) {user_id = "";}                         //user_id = null 일때

                                AL_Datas.add(new All_Log(Datefilter(inner_time),                    //recycerView 추가
                                        Datefilter(outter_time),
                                        confidence,
                                        user_id));
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AL_Adapter = new All_LogAdapter(AL_Datas);                      // 메인 스레드에서 UI 작업을 수행
                                    recyclerView.setAdapter(AL_Adapter);
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
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy년 MM월 dd일 EEE, HH시 mm분 ss초", Locale.KOREA);  // 필요에 따라 다른 형식으로 날짜를 출력
                String outputDateString = outputFormat.format(date);
                Result = outputDateString;
            } else { Result = "";}
        } catch (ParseException e) {
            e.printStackTrace();}
        return Result;
    }
    private void updateRecyclerView(List<My_Log> dataList) {
        AL_Datas.clear();
        AL_Datas.add(new All_Log("","","","")); //받은 데이터가 없을시.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AL_Adapter = new All_LogAdapter(AL_Datas);
                recyclerView.setAdapter(AL_Adapter);
                Toast.makeText(getApplicationContext(), "데이터가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}