package com.nk2sp.ifra;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabItem;
import org.json.JSONObject;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity6 extends AppCompatActivity {
    Single_ton_data mydata = Single_ton_data.getInstance();
    ImageButton ibtn_logout;
    ImageButton ibtn_Back;
    Button edit_Mypageinfo;
    TabItem Tab_myinfo;
    Spinner S_Grp;
    String E_Grp;
    TextView tv_Name, tv_name, tv_id, tv_pw, tv_grp, tv_pnum;
    EditText E_Name, E_PNum;
    String name, id, pw, grp, grp_num, pnum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);//다크모드 삭제
        setContentView(R.layout.activity_6_mypage);

        ibtn_logout = findViewById(R.id.ibtn_logout);
        ibtn_Back = findViewById(R.id.ibtn_ulcBack);
        edit_Mypageinfo = findViewById(R.id.btn_mypage_edit);
        Tab_myinfo = findViewById(R.id.Tab_myinfo);
        showMyInfo();
        ibtn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), MainActivity2.class);
                startActivity(intent);
            }
        });
        ibtn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            } // 현재 액티비티를 종료하고 이전 화면으로 돌아가기
        });
        if (Tab_myinfo != null) {
            Tab_myinfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "새로고침 완료", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e("MainActivity6", "TabItem is null. Check the ID in your XML layout.");
            //Toast.makeText(getApplicationContext(), "탭버튼이 null", Toast.LENGTH_SHORT).show();
        }
        edit_Mypageinfo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showEditableDialog(this, name, grp, pnum,
                        new MainActivity6.OnValuesEditedListener() {
                            @Override
                            public void onValues_Edited(String editedName, String editedGroup, String editedPnum) {
                                // 사용자가 수정한 값을 사용
                                // 이곳에서 수정된 값을 사용하거나 처리
                                Toast.makeText(MainActivity6.this, "수정완료 ", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
    public void showMyInfo(){ //나의정보 확인
        tv_Name = findViewById(R.id.tv_Name); //이름
        tv_name = findViewById(R.id.tv_mypage_rName); //나의정보 이름
        tv_id = findViewById(R.id.tv_mypage_rID); //나의정보 아이디
        tv_pw = findViewById(R.id.tv_mypage_rPW); //나의정보 비번
        tv_grp = findViewById(R.id.tv_mypage_rGroup); //나의정보 그룹
        tv_pnum = findViewById(R.id.tv_mypage_rPNum); //나의정보 폰번

        name = mydata.getUser_name(); //sgt_mydata에서 가져옴
        id = mydata.getUser_id();
        pw = mydata.getUser_pass();
        if (mydata.getUser_group().equals("1")){grp = "Class C";}
        else if (mydata.getUser_group().equals("2")) {grp = "Class Java";}
        else if (mydata.getUser_group().equals("")||mydata.getUser_group().equals("null")) {grp = "none";}
        else {grp = mydata.getUser_group();}
        pnum = mydata.getUser_phoneNum();
        if (pnum.length() == 11) { //자리수 11개일씨 - 추가
            pnum = pnum.substring(0, 3) + "-" + pnum.substring(3, 7) + "-" + pnum.substring(7);
        }
        tv_Name.setText(name);
        tv_name.setText(name);
        tv_id.setText(id);
        tv_pw.setText(pw);
        tv_grp.setText(grp);
        tv_pnum.setText(pnum);

        String originalPw = tv_pw.getText().toString(); // tv_pw의 현재 문자열을 가져옴
        int length = originalPw.length();
        String maskedText = new String(new char[length]).replace("\0", "*"); // 길이만큼 '*'로 채운 문자열 생성
        tv_pw.setText(maskedText);
    }
    public void showEditableDialog(View.OnClickListener context, String edName, //v2
                                   String edGroup, String edPNum, final MainActivity6.OnValuesEditedListener listener) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View viewV1 = inflater.inflate(R.layout.activity_6_mypage_edit, null);
        E_Name = viewV1.findViewById(R.id.etv_mypage_ename);
        E_Name.setText(edName); //이름

        S_Grp = viewV1.findViewById(R.id.sp_mypage_grp); //그룹
        String[] options = {"미정","Class C", "Class Java"}; //1, 2, 3
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        S_Grp.setAdapter(adapter);
        S_Grp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // 사용자가 선택한 항목의 텍스트 가져오기
                E_Grp = edGroup.toString();
                E_Grp = parentView.getItemAtPosition(position).toString();
                if (E_Grp == "미정"){ E_Grp="none"; }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // 사용자가 아무것도 선택하지 않았을 때의 동작 정의
                Toast.makeText(getApplicationContext(), "항목을 선택하세요.", Toast.LENGTH_SHORT).show();
            }
        });

        E_PNum = viewV1.findViewById(R.id.etv_mypage_ePNum);
        E_PNum.setText(edPNum);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(viewV1)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 사용자가 수정한 값을 가져옵니다.
                        mydata.setUser_name(E_Name.getText().toString());
                        mydata.setUser_group(E_Grp);
                        mydata.setUser_phoneNum(E_PNum.getText().toString());

                        name = mydata.getUser_name();
                        grp = mydata.getUser_group();
                        pnum = mydata.getUser_phoneNum();
                        if (pnum.length() == 11) {
                            pnum = pnum.substring(0, 3) + "-" + pnum.substring(3, 7) + "-" + pnum.substring(7);
                        }

                        if (listener != null) {
                            listener.onValues_Edited(name, grp, pnum);
                            Send2_edinfo(); // 수정한 정보 업데이트
                            finish();
                            startActivity(getIntent());
                        }
                        Confirmation();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Cancellation();
                    }
                })
                .show(); //다이얼로그 표시
    }
    public interface OnValuesEditedListener {
        void onValues_Edited(String editedName, String editedGroup, String editedPnum);
    }
    private void Confirmation() {
        //안내문자서 확인 클릭시
        Toast.makeText(getApplicationContext(), "완료하였습니다.", Toast.LENGTH_SHORT).show();
    }
    private void Cancellation() {
        //안내문자서 취소 클릭시
        Toast.makeText(getApplicationContext(), "취소하였습니다.", Toast.LENGTH_SHORT).show();
    }
    public void Send2_edinfo() { //정보수정(서버로 보내기)
        if (grp.equals("Class C")) { grp_num = "1";}
        else if (grp.equals("Class Java")) { grp_num = "2";}
        else {grp_num = "none";}
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id",id)//아이디
                .addFormDataPart("name",name) //이름
                .addFormDataPart("group",grp_num) //그룹 번호 1:Class C, 2:Class Java
                .addFormDataPart("pnum",pnum) //폰넘
                .build();

        Request request = new Request.Builder()
                .url("http://15.164.120.162:5000//alter")
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

                        // 여기에서 JSON 데이터를 처리하고 원하는 정보 추출
                        String status = json.getString("status");

                        Log.d("JSON Response: ", "status: " + status);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // 응답이 성공적이지 않은 경우에 대한 처리
                    Log.e("Response Error", "Response Code: " + response.code());
                }
            }
        });
    }
}