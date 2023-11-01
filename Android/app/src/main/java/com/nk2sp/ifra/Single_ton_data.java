package com.nk2sp.ifra;

public class Single_ton_data { //서버로 요청시 로그인동안 결과 저장하는 임시저장소
    private static Single_ton_data instance;
    private String sharedVariable;
    private String user_id;
    private String user_pass;
    private String user_group;
    private String user_name;
    private String user_phoneNum;

    private Single_ton_data() {
        user_id="";
        user_pass="";
        user_group="";
        user_name="";
        user_phoneNum="";

    }

    public static Single_ton_data getInstance() {
        if (instance == null) {
            instance = new Single_ton_data();
        }
        return instance;
    }
    public String getSharedVariable() {
        return sharedVariable;
    }
    public void setSharedVariable(String value) {
        sharedVariable = value;
    }

    public String getUser_id() {
        return user_id;
    }
    public void setUser_id(String u_id){
        user_id=u_id;
    }

    public String getUser_pass() {
        return user_pass;
    }
    public void setUser_pass(String u_pass){
        user_pass=u_pass;
    }

    public String getUser_group() { return user_group;}
    public void setUser_group(String u_group){user_group=u_group;}

    public String getUser_name() {return user_name;}
    public void  setUser_name(String u_name) {user_name=u_name;}

    public String getUser_phoneNum() {return user_phoneNum;}
    public void setUser_phoneNum(String u_phoneNum){user_phoneNum = u_phoneNum;}
}
