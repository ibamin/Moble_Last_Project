package com.nk2sp.ifra;

public class Test_Alarm { //Test_NF
    private String filename;
    private String filedate;
    //사진 추가 해야됨
    public Test_Alarm(String filename, String filedate){
        this.filename = filename;
        this.filedate = filedate;
    }
    public String getFilename(){return filename;}
    public void setFilename(String filename) {this.filename = filename;}
    public String getFiledate() {return filedate;}
    public void setFiledate(String filedate) {this.filedate = filedate;}
}