package com.example.digitrafficinspectorv0;

public class HistoryModel2 {
    private String VEHICLE_NUMBER,DATE_TIME_STAMP;
    private String SCORE;



    public void setSCORE(String SCORE) {
        this.SCORE = SCORE;
    }

    public void setVEHICLE_NUMBER(String VEHICLE_NUMBER) {
        this.VEHICLE_NUMBER = VEHICLE_NUMBER;
    }

    public void setDATE_TIME_STAMP(String DATE_TIME_STAMP) {
        this.DATE_TIME_STAMP = DATE_TIME_STAMP;
    }

    public HistoryModel2() {}

    public HistoryModel2(String VEHICLE_NUMBER, String DATE_TIME_STAMP, String SCORE) {this.VEHICLE_NUMBER = VEHICLE_NUMBER;this.DATE_TIME_STAMP = DATE_TIME_STAMP; this.SCORE = SCORE;}
    public String getVEHICLE_NUMBER() {return VEHICLE_NUMBER;}
    public String getDATE_TIME_STAMP() {return DATE_TIME_STAMP;}
    public String getSCORE() {return SCORE;}
}