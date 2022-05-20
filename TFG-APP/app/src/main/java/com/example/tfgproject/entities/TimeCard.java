package com.example.tfgproject.entities;


import java.io.Serializable;

public class TimeCard implements Serializable {

    private String date;
    private String entryTime, outTime;
    private String userId;
    private boolean dayOff;
    private String reason;
    private boolean holiday;


    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getEntryTime() {
        return entryTime;
    }
    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime;
    }

    public String getOutTime() {
        return outTime;
    }
    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isDayOff() { return dayOff; }
    public void setDayOff(boolean dayOff) {  this.dayOff = dayOff; }

    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isHoliday() { return holiday; }
    public void setHoliday(boolean holiday) { this.holiday = holiday; }

    public TimeCard() {}

    public TimeCard(String day, String entryTime, String outTime, String userId, boolean dayOff, String reason, boolean holiday) {
        this.date = day;
        this.entryTime = entryTime;
        this.outTime = outTime;
        this.userId = userId;
        this.dayOff = dayOff;
        this.reason = reason;
        this.holiday = holiday;
    }
}