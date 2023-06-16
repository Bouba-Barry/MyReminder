package com.example.myreminder;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Entity(tableName = "Alarme")
public class Alarme {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String create_date;
    private String time;
    private boolean isTimeOut;

    public Alarme(String title, String create_date) {
        this.title = title;
        this.create_date = create_date;
    }

    public Alarme(String title, String create_date, String time) {
        this.title = title;
        this.create_date = create_date;
        this.time = time;
    }

    public Alarme() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isTimeOut() {
        return isTimeOut;
    }

    public void setTimeOut(boolean timeOut) {
        isTimeOut = timeOut;
    }

    public static List<Alarme> initAlarme(){
        List<Alarme> alarmeList = new ArrayList<>();
        alarmeList.add(new Alarme("Reviser Mobile", "10/06/2023"));
        alarmeList.add(new Alarme("Reviser IA", "18/06/2023"));
        alarmeList.add(new Alarme("Sport à la Gym", "18/06/2023"));
        alarmeList.add(new Alarme("Date Night", "10/07/2023"));
        alarmeList.add(new Alarme("plage ", "10/07/2023"));
        alarmeList.add(new Alarme("Restaurant", "19/06/2023"));
        alarmeList.add(new Alarme("regarder Serie", "16/06/2023"));
        alarmeList.add(new Alarme("Mediter", "10/08/2023"));
        return alarmeList;
    }
}
