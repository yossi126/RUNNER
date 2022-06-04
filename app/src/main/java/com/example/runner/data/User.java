package com.example.runner.data;

public class User {

    private String name;
    private String email;
    private String pass;
    private String uid;
    private String date;
    private String height;
    private String weight;
    private String gender;

    private boolean isConnected;

    public User() {
    }

    public User(String name, String email, String pass) {
        this.name = name;
        this.email = email;
        this.pass = pass;
    }

    public User(String name, String email, String pass, boolean isConnected) {
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.isConnected = isConnected;
    }

    public User(String name, String email, String pass, String uid, String date,
                String height, String weight, String gender) {
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.uid = uid;
        this.date=date;
        this.height=height;
        this.weight= weight;
        this.gender=gender;
    }

    public boolean getIsConnected() {
        return isConnected;
    }

    public void setIsConnected(boolean online) {
        isConnected = online;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
