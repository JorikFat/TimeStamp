package dev.jorik.timestamp.model.entities;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;

//todo убрать интерфейс Comparable в Interactor.Comparator
public class TimeStamp implements Comparable<TimeStamp> {

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
