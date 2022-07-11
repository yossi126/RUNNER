package com.example.runner.data;

import java.io.Serializable;

public class Splits implements Serializable {

    private String time;
    private int km;

    public Splits(String time, int km) {
        this.time = time;
        this.km = km;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getKm() {
        return km;
    }

    public void setKm(int km) {
        this.km = km;
    }

    @Override
    public String toString() {
        return "Splits{" +
                "time='" + time + '\'' +
                ", km='" + km + '\'' +
                '}';
    }
}
