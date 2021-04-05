package com.example.project2.Community.listView;

import android.view.View;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class recyclerClass {
    private String dogName;
    private String myName;
    private String context;
    private String userUid;
    private String articleUid;
    private ArrayList<String> photoAddr;
    private String profileImage;
    private Timestamp upTime;
    private int likeNum;

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public ArrayList<String> getPhotoAddr() {
        return photoAddr;
    }

    public String getPhotoAddr(int pos) {
        return photoAddr.get(pos);
    }

    public int getPhotoAddrSize() {
        return photoAddr.size();
    }

    public void setPhotoAddr(ArrayList<String> photoAddr) {
        this.photoAddr = photoAddr;
    }

    public void addPhotoAddr(String addr) {
        this.photoAddr.add(addr);
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getArticleUid() {
        return articleUid;
    }

    public void setArticleUid(String articleUid) {
        this.articleUid = articleUid;
    }

    public String getDogName() {
        return dogName;
    }

    public void setDogName(String dogName) {
        this.dogName = dogName;
    }

    public String getMyName() {
        return myName;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Timestamp getUpTime() {
        return upTime;
    }

    public void setUpTime(Timestamp upTime) {
        this.upTime = upTime;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }
}
