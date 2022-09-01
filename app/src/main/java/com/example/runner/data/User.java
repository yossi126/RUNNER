package com.example.runner.data;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class User {

    private String name;
    private String email;
    private String pass;
    private String uid;
    private String date;
    private String height;
    private String weight;
    private String gender;
    private String profilePhoto;
    private String coverPhoto;
    private String lastLogin;
    private String logOut;
    private boolean isConnected;
    private String letsRun;
    private boolean invitedToRun;
    private String currentKm;
    private boolean hasFinished;
    private String myChrono;



    public User() {
    }

    //NEW USER C'TOR
    public User(String name, String email, String pass, String uid, String date,
                String height, String weight, String gender, String profilePhoto,
                String coverPhoto, String lastLogin,String logOut,String letsRun,boolean invitedToRun,
                String currentKm, boolean hasFinished,String myChrono) {
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.uid = uid;
        this.date = date;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.profilePhoto = profilePhoto;
        this.coverPhoto = coverPhoto;
        this.lastLogin = lastLogin;
        this.logOut = logOut;
        this.letsRun = letsRun;
        this.invitedToRun = invitedToRun;
        this.currentKm = currentKm;
        this.myChrono = myChrono;

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

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(String coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getLogOut() {
        return logOut;
    }

    public void setLogOut(String logOut) {
        this.logOut = logOut;
    }

    public String getLetsRun() {
        return letsRun;
    }

    public void setLetsRun(String letsRun) {
        this.letsRun = letsRun;
    }

    public boolean getInvitedToRun() {
        return invitedToRun;
    }

    public void setInvitedToRun(boolean invitedToRun) {
        this.invitedToRun = invitedToRun;
    }

    public String getCurrentKm() {
        return currentKm;
    }

    public void setCurrentKm(String currentKm) {
        this.currentKm = currentKm;
    }


    public boolean isHasFinished() {
        return hasFinished;
    }

    public void setHasFinished(boolean hasFinished) {
        this.hasFinished = hasFinished;
    }


    public String getMyChrono() {
        return myChrono;
    }

    public void setMyChrono(String myChrono) {
        this.myChrono = myChrono;
    }
}
