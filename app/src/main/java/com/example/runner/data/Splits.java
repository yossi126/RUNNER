package com.example.runner.data;

public class Splits {

    private String time;
    private String km;

    public Splits(String time, String km) {
        this.time = time;
        this.km = km;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getKm() {
        return km;
    }

    public void setKm(String km) {
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
