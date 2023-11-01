package com.nk2sp.ifra;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.*;
import okhttp3.Request;
import okhttp3.RequestBody;

import org.json.JSONObject;
import com.nk2sp.ifra.MailSender;

public class MainActivity3 extends AppCompatActivity {
    Single_ton_data mydata = Single_ton_data.getInstance();
    String directoryPath;
    File directory;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ActivityResultLauncher<Intent> cameraLauncher, cer_cameraLauncher;
    private String ocr_result;
    ImageView imageView, back;
    Button certified, submit, check_id, Email_send, Email_check;
    CheckBox checkBox, checkBox2;
    EditText id, pw, pn, em, em_pj;
    String name, identify, password, phone_num, idNumber, gender, institution;
    File user_face;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);//다크모드 삭제
        setContentView(R.layout.activity_3_jtm);

        imageView = findViewById(R.id.imageView2);
        back = findViewById(R.id.ibtn_Backlog);
        certified = findViewById(R.id.btn_cer);
        checkBox = findViewById(R.id.cb_MJ);
        checkBox2 = findViewById(R.id.cb_Email_MJ);
        Email_send = findViewById(R.id.btn_Email_send_Num);
        Email_check = findViewById(R.id.btn_Email_MJ_check);
        submit = findViewById(R.id.btn_loginGo);
        check_id = findViewById(R.id.btn_overlap);
        id = findViewById(R.id.etv_ID);
        pw = findViewById(R.id.etv_pw);
        pn = findViewById(R.id.PhoneNum);
        em = findViewById(R.id.etv_Email);
        em_pj = findViewById(R.id.etv_Email_MJ);

        Random rand = new Random();
        int num = rand.nextInt(10000);
        String snum = String.valueOf(num); //인증번호 랜덤값

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // 현재 액티비티를 종료하고 이전 화면으로 돌아가기
            }
        });
        certified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {cer_takePicture();}
        });
        check_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(id.getText().toString().equals("")){
                    Toast.makeText(MainActivity3.this, "아이디를 입력하시오", Toast.LENGTH_SHORT).show();
                }
                else{ check_id(id.getText().toString());}
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((checkBox.isChecked() == true)&&(checkBox2.isChecked() == true)){
                    identify = id.getText().toString();
                    password = pw.getText().toString();
                    phone_num = pn.getText().toString();
                    if (phone_num.length() == 11) {
                        phone_num = phone_num.substring(0, 3) + "-" + phone_num.substring(3, 7) + "-" + phone_num.substring(7);
                    }
                    //Sign_in(user_face); //얼굴 사진 나중에 AI에서 비교
                    showFinalDialog(MainActivity3.this ,identify, password, name, idNumber, phone_num, gender, institution); //최종확인 다이얼로그
                } else if ((checkBox.isChecked() == true)&&(checkBox2.isChecked() == false)) {
                    Toast.makeText(MainActivity3.this, "이메일 인증 부탁드립니다.", Toast.LENGTH_SHORT).show();
                } else if ((checkBox.isChecked() == false)&&(checkBox2.isChecked() == true)) {
                    Toast.makeText(MainActivity3.this, "신분증 확인 부탁드립니다.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity3.this, "신분증 확인과 이메일 인증 부탁드립니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
        }
        Email_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email = em.getText().toString();
                add_sms(Email, snum);
                Toast.makeText(MainActivity3.this, "인증번호 전송 완료", Toast.LENGTH_SHORT).show();
            }
        });
        Email_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email_MJnum = em_pj.getText().toString();
                if (Email_MJnum.equals(snum))
                {  Toast.makeText(MainActivity3.this, "이메일 인증 완료", Toast.LENGTH_SHORT).show();
                    checkBox2.setChecked(true);}
                else{  Toast.makeText(MainActivity3.this, "이메일 인증 실패", Toast.LENGTH_SHORT).show();
                    checkBox2.setChecked(false);}
            }
        });
        //얼굴사진_카메라
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Bundle extras = result.getData().getExtras();
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            imageView.setImageBitmap(imageBitmap);
                            directoryPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
                            directory = new File(directoryPath);

                            if (!directory.exists()) {
                                if (!directory.mkdirs()) {
                                    Log.e("Directory Creation Error", "Failed to create directory.");
                                }
                            }
                            user_face = new File(directory, "Sign_img.jpg");
                            try {
                                FileOutputStream fos = new FileOutputStream(user_face);
                                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                fos.flush();
                                fos.close();
                                Log.e("image storage", "Success: " + user_face.getAbsolutePath());
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("image storage", "Fail: " + e.getMessage());
                            }
                        }
                    }
                });

        //ocr_카메라
        cer_cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Bundle extras = result.getData().getExtras();
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            directoryPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
                            directory = new File(directoryPath);

                            if (!directory.exists()) {
                                if (!directory.mkdirs()) {
                                    Log.e("Directory Creation Error", "Failed to create directory.");
                                }
                            }

                            // 이미지를 파일로 저장
                            File internalFile = new File(directory, "my_image.jpg");
                            try {
                                FileOutputStream fos = new FileOutputStream(internalFile);
                                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                fos.flush();
                                fos.close();
                                Log.e("image storage", "Success: " + internalFile.getAbsolutePath());
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("image storage", "Fail: " + e.getMessage());
                            }
                            send2Server(internalFile); //서버로 이미지를 업로드
                            //information = ocr_result;
                            //showInfoDialog(MainActivity3.this, "정보 확인", information);
                        }
                    }
                });
    }
    public void takePicture(View view1) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(takePictureIntent);
        }
    }
    public void cer_takePicture() {
        Intent cer_takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cer_takePictureIntent.resolveActivity(getPackageManager()) != null) {
            showConfirmationDialog(MainActivity3.this, "부탁드립니다.", "현재 워터마크로 인하여 기술 부족으로 캡쳐시 배율(3x)을 높게하여 캡쳐하시기 바랍니다.");
        }
    }
    public void showInfoDialog(Context context, String name, String idNumber, String gender, String institution) { //v2
        LayoutInflater inflater = LayoutInflater.from(context);
        View viewX1 = inflater.inflate(R.layout.activity_3_x1_dialog_ocrtext, null);
        TextView tvName = viewX1.findViewById(R.id.tv_ocrresult_name);
        TextView tvIdNumber = viewX1.findViewById(R.id.tv_ocrresult_Num);
        TextView tvGender = viewX1.findViewById(R.id.tv_ocrresult_mw);
        TextView tvInstitution = viewX1.findViewById(R.id.tv_ocrresult_ca);

        tvName.setText(name);
        tvIdNumber.setText(idNumber);
        tvGender.setText(gender);
        tvInstitution.setText(institution);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
                .setView(viewX1) //이부분을 수정하여 커스텀 뷰 설정
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {Confirmation();} //사용자가 확인 클릭시 확인 함수 실행
                })
                .setNeutralButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {Editing();} //사용자가 수정 클릭시 수정 함수 실행
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {Cancellation();} //사용자가 취소 클릭시 취소 함수 실행
                })
                .show(); //다이얼로그 표시
    }
    public void showConfirmationDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent cer_takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (cer_takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
                            cer_cameraLauncher.launch(cer_takePictureIntent);
                        }
                        dialog.dismiss(); //다이얼로그를 닫기
                    }
                })
                .show(); //다이얼로그 표시
    }
    public void showEditableDialog(Context context, String edname, String edidNum, //v2
                                   String edgender, String edins, final OnValuesEditedListener listener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View viewX2 = inflater.inflate(R.layout.activity_3_x2_dialog_edit, null);
        EditText etName = viewX2.findViewById(R.id.etv_ocrresult_name);
        EditText etIdNumber = viewX2.findViewById(R.id.etv_ocrresult_num);
        EditText etGender = viewX2.findViewById(R.id.etv_ocrresult_gender);
        EditText etInstitution = viewX2.findViewById(R.id.etv_ocrresult_ca);

        etName.setText(edname);
        etIdNumber.setText(edidNum);
        etGender.setText(edgender);
        etInstitution.setText(edins);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(viewX2)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { //사용자가 수정한 값을 가져오기
                        name = etName.getText().toString();
                        idNumber = etIdNumber.getText().toString();
                        gender = etGender.getText().toString();
                        institution = etInstitution.getText().toString();

                        if (listener != null) { //listener를 통해 수정된 값을 전달
                            listener.onValues_Edited(name, idNumber, gender, institution);
                        }
                        Toast.makeText(context, "완료하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .show(); //다이얼로그 표시
    }
    public void showFinalDialog(Context context, String f_id, String f_pw, String f_name, String f_idNum, String f_pNum, String f_gender, String f_ins) { //v2
        LayoutInflater inflater = LayoutInflater.from(context);
        View viewY1 = inflater.inflate(R.layout.activity_3_y1_dialog_final_check, null);

        TextView tvId = viewY1.findViewById(R.id.tv_final_result_ID);
        TextView tvPw = viewY1.findViewById(R.id.tv_final_result_PW);
        TextView tvName = viewY1.findViewById(R.id.tv_final_result_Name);
        TextView tvGender = viewY1.findViewById(R.id.tv_final_result_Gender);
        TextView tvPNum = viewY1.findViewById(R.id.tv_mypage_rName);
        TextView tvINum = viewY1.findViewById(R.id.tv_final_result_INum);
        TextView tvIns = viewY1.findViewById(R.id.tv_final_result_Ins);

        tvId.setText(f_id);
        tvPw.setText(f_pw);
        tvName.setText(f_name);
        tvINum.setText(f_idNum);
        tvPNum.setText(f_pNum);
        tvGender.setText(f_gender);
        tvIns.setText(f_ins);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
                .setView(viewY1) //이 부분을 수정하여 커스텀 뷰를 설정
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity3.this, "가입완료 ", Toast.LENGTH_SHORT).show();
                        Sign_in(user_face);
                        go_Login();
                    }
                })
                .setNeutralButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { final_Editing();}
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Cancellation();
                    }
                })
                .show(); //다이얼로그 표시
    }
    public void showEditable_FinalDialog(Context context, String F_id, String F_pw, String F_name, String F_idNum, String F_pNum, String F_gender, String F_ins, OnValuesFinalEditedListener listener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View viewY2 = inflater.inflate(R.layout.activity_3_y2_dialog_final_edit, null);
        EditText FE_Id = viewY2.findViewById(R.id.etv_final_result_ID); //최종 아이디
        EditText FE_Pw = viewY2.findViewById(R.id.etv_final_result_PW); //최종 비번
        EditText FE_Name = viewY2.findViewById(R.id.etv_final_result_name); //최종 이름
        EditText FE_IdNumber = viewY2.findViewById(R.id.etv_final_result_num); //최종 민증번호
        EditText FE_PNumber = viewY2.findViewById(R.id.etv_final_result_PNum); //최종 폰번호
        EditText FE_Gendaer = viewY2.findViewById(R.id.etv_final_result_gender); //최종 성별
        EditText FE_Institution = viewY2.findViewById(R.id.etv_final_result_ca); //최종 인증 기관

        FE_Id.setText(F_id);
        FE_Pw.setText(F_pw);
        FE_Name.setText(F_name);
        FE_IdNumber.setText(F_idNum);
        FE_PNumber.setText(F_pNum);
        FE_Gendaer.setText(F_gender);
        FE_Institution.setText(F_ins);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(viewY2)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        identify = FE_Id.getText().toString();
                        password = FE_Pw.getText().toString();
                        name = FE_Name.getText().toString();
                        idNumber = FE_IdNumber.getText().toString();
                        phone_num = FE_PNumber.getText().toString();
                        gender = FE_Gendaer.getText().toString();
                        institution = FE_Institution.getText().toString();

                        if (listener != null) {
                            listener.onValues_FinalEdited(identify, password, name, idNumber, phone_num, gender, institution);
                            Sign_in(user_face);
                            go_Login();
                        }
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }
    public interface OnValuesEditedListener {
        void onValues_Edited(String editedName, String editedIdNumber, String editedGender, String editedInstitution);
    }
    public interface OnValuesFinalEditedListener {
        void onValues_FinalEdited(String fe_Id, String fe_Pw, String fe_Name, String fe_IdNum, String fe_Pnum, String fe_Gender, String fe_Ins);
    }
    private void Confirmation() {
        //안내문자서 확인 클릭시
        Toast.makeText(getApplicationContext(), "완료하였습니다.", Toast.LENGTH_SHORT).show();
        checkBox.setChecked(true);
    }
    private void Cancellation() {
        //안내문자서 취소 클릭시
        Toast.makeText(getApplicationContext(), "취소하였습니다.", Toast.LENGTH_SHORT).show();
        checkBox.setChecked(false);
    }
    private void Editing() {
        //안내문자서 수정 클릭시
        showEditableDialog(this, name, idNumber, gender, institution,
                new OnValuesEditedListener() {
                    @Override
                    public void onValues_Edited(String editedName, String editedIdNumber, String editedGender, String editedInstitution) {
                        //사용자가 수정한 값을 사용
                        //이곳에서 수정된 값을 사용하거나 처리
                        //editedName: 수정된 이름, editedIdNumber: 수정된 주민번호, editedGender: 수정된 성별, editedInstitution: 수정된 인증기관
                        Toast.makeText(MainActivity3.this, "수정완료 ", Toast.LENGTH_SHORT).show();
                        checkBox.setChecked(true);
                    }
                });
    }
    private void final_Editing() {
        //안내문자서 수정 클릭시
        showEditable_FinalDialog(this, identify, password, name, idNumber, phone_num, gender, institution,
                new OnValuesFinalEditedListener() {
                    @Override
                    public void onValues_FinalEdited( String fe_Id, String fe_Pw, String fe_Name, String fe_IdNum, String fe_Pnum, String fe_Gender, String fe_Ins) {
                        //사용자가 수정한 값을 사용
                        //이곳에서 수정된 값을 사용하거나 처리
                        //editedName: 수정된 이름, editedIdNumber: 수정된 주민번호, editedGender: 수정된 성별, editedInstitution: 수정된 인증기관
                        Toast.makeText(MainActivity3.this, "가입완료 ", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void go_Login() {
        Intent login = new Intent(getApplicationContext(), MainActivity2.class);
        startActivity(login);
    }
    public void send2Server(File file) { //서버로 보내기
        MediaType MEDIA_TYPE = MediaType.parse("image/jpeg");

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", file.getName(), RequestBody.create(MEDIA_TYPE, file))
                .build();

        Request request = new Request.Builder()
                .url("http://15.164.120.162:5000/ocr_test")
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
                        JSONObject json = new JSONObject(responseBody); //여기에서 JSON 데이터를 처리하고 원하는 정보 추출

                        String status = json.getString("status");
                        String result = json.getString("result");

                        Log.d("JSON Response: ", "status: " + status);
                        Log.d("JSON Response: ", "result: " + result);
                        ocr_result = result;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() { filteringData(ocr_result);} //데이터 필터링
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "인식오류! 다시 캡쳐해주세요. ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 응답이 성공적이지 않은 경우에 대한 처리
                    Log.e("Response Error", "Response Code: " + response.code());
                    Toast.makeText(getApplicationContext(), "서버 응답이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void filteringData(String result){ //데이터필터링(서버에서 받은 결과 가공)
        //String ocrResult = "주민등록증\n홍길동(빠좀께)\n800101-2345678\n세울특별시 가산디지털1로\n서울특별시 금천구청장";
        String ocrResult = result;

        // 이름 추출
        Pattern name_Pattern = Pattern.compile("(.+)\\(");
        Matcher name_Matcher = name_Pattern.matcher(ocrResult);
        name = "이름 미확인";
        if (name_Matcher.find()) { name = name_Matcher.group(1);}

        // 주민번호 추출
        Pattern id_Pattern = Pattern.compile("(\\d{6}-\\d{7})");
        Matcher id_Matcher = id_Pattern.matcher(ocrResult);
        idNumber = id_Matcher.find() ? id_Matcher.group() : "주민번호 미확인";

        // 성별 추출
        int gender_Digit = Integer.parseInt(idNumber.substring(7, 8));
        gender = (gender_Digit % 2 == 1) ? "남자" : "여자";

        // 기관 추출
        String[] words = ocrResult.split("\\s+");
        institution = words[words.length - 2] +" "+ words[words.length - 1];// 마지막 단어를 추출

        //오류
        char targetChar = '(';
        if (institution.startsWith(String.valueOf(targetChar))) {
            Toast.makeText(MainActivity3.this, "인식이 안되었습니다.", Toast.LENGTH_SHORT).show();
            Toast.makeText(MainActivity3.this, "다시 캡쳐해주세요.", Toast.LENGTH_SHORT).show();
        } else if(name.equals("이름 미확인") || idNumber.equals("주민번호 미확인")){
            Toast.makeText(MainActivity3.this, "인식이 안되었습니다.", Toast.LENGTH_SHORT).show();
            Toast.makeText(MainActivity3.this, "다시 캡쳐해주세요.", Toast.LENGTH_SHORT).show();
        }
        else {showInfoDialog(this, name, idNumber, gender, institution);}
    }

    public void Sign_in(File file) { //회원등록(서버로 보내기)
        MediaType MEDIA_TYPE = MediaType.parse("image/jpeg");

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", file.getName(), RequestBody.create(MEDIA_TYPE, file))
                .addFormDataPart("name",name)
                .addFormDataPart("jumin",idNumber)
                .addFormDataPart("gender",gender)
                .addFormDataPart("phone_number",phone_num)
                .addFormDataPart("id",identify)
                .addFormDataPart("pw",password)
                .build();

        Request request = new Request.Builder()
                .url("http://15.164.120.162:5000//Sign_up")
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

                        //여기에서 JSON 데이터를 처리하고 원하는 정보 추출
                        String status = json.getString("status");

                        Log.d("JSON Response: ", "status: " + status);

                        if (status=="success"){
                            //Toast.makeText(MainActivity3.this, "가입성공 ", Toast.LENGTH_SHORT).show();
                            Intent login = new Intent(getApplicationContext(), MainActivity2.class);
                            startActivity(login);
                        }else{
                            //Toast.makeText(MainActivity3.this, "가입실패 ", Toast.LENGTH_SHORT).show();
                        }
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
    public void check_id(String id) { //아이디 중복 확인(서버로 확인)
        Log.e("test",id);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id",id)
                .build();

        Request request = new Request.Builder()
                .url("http://15.164.120.162:5000/check")
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

                        if ("already in".equals(status)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "중복되는 아이디가 있습니다.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "아이디 사용가능 합니다.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
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
    public void add_sms(String mail,String num){ //인증문자
        new MailSender().sendEmail(mail, num);
    }
}