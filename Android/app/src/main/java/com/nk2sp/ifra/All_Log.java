package com.nk2sp.ifra;

public class All_Log { //전체 로그 확인
    private String inner_time;
    private String outter_time;
    private String confidence;
    //private String state;
    private String user_id;
    //사진 추가 해야됨
    public All_Log(String inner_time, String outter_time, String confidence, String user_id){
        this.inner_time = inner_time;
        this.outter_time = outter_time;
        this.confidence = confidence;
        //this.state = state;
        this.user_id = user_id;
    }
    public String getInner_time(){return inner_time;}
    public void setInner_time(){this.inner_time = inner_time;}
    public String getOutter_time(){return outter_time;}
    public void setOutter_time(){this.outter_time = outter_time;}
    public String getConfidence() {return confidence;}
    public void setConfidence(){this.confidence = confidence;}
    //public String getState() {return state;}
    //public void setState() {this.state = state;}
    public String getUser_id() {return user_id;}
    public void setUser_id() {this.user_id = user_id;}
}
