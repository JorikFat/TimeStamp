package dev.jorik.timestamp.model;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeStamp implements Comparable<TimeStamp> {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");

    private long id;
    private Date time;
    private String name;

    public TimeStamp(){}

    public TimeStamp(Date date) {
        this.time = date;
        this.name = "";
    }

    public TimeStamp(Date time, String name) {
        this.time = time;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(@NonNull TimeStamp o) {
        return this.time.compareTo(o.getTime());
    }
}
