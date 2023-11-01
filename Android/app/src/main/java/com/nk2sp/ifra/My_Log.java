package com.nk2sp.ifra;

public class My_Log { //나의로그 확인
    private String inner_time;
    private String outter_time;
    private String state;
    public My_Log(String inner_time, String outter_time, String state){
        this.inner_time = inner_time;
        this.outter_time = outter_time;
        this.state = state;
    }
    public String get_In_time(){return inner_time;}
    public void set_In_time(){this.inner_time = inner_time;}
    public String get_Out_time(){return outter_time;}
    public void set_Out_time(){this.outter_time = outter_time;}
    public String get_State() {return state;}
    public void set_State() {this.state = state;}
}
