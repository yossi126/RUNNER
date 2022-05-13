package com.example.runner.data;

public class User {

    private String name;
    private String email;
    private String pass;
    private String uid;
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

    public User(String name, String email, String pass, String uid) {
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.uid = uid;
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
}
