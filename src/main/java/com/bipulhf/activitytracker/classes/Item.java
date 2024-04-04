package com.bipulhf.activitytracker.classes;

public class Item {

    String appName;
    String startTime;
    String endTime;
    String timerText;
    int duration;

    public Item(String appName, String startTime, String endTime, int duration, String timerText) {
        this.appName = appName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.timerText= timerText;
    }

    public Item (String appName, String timerText) {
        this.appName = appName;
        this.timerText = timerText;
    }
    public String getTimerText() {
        return timerText;
    }

    public void setTimerText(String timerText) {
        this.timerText = timerText;
    }
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}