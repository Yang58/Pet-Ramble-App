package com.example.project2.Community.DB;

import com.google.firebase.firestore.DocumentReference;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class uploadData{
    public ArrayList<DocumentReference> relatedList;
    public DocumentReference relatedID;
    public String content;
    public int likeNum;
    public List<String> photoAddr;
    public Timestamp uptime;

    public uploadData(){

    }

    public uploadData(String content, Timestamp uptime){
        this.content=content;
        this.likeNum=0;
        this.relatedID = null;
        this.relatedList = new ArrayList<DocumentReference>();
        this.photoAddr = new ArrayList<>();
        this.uptime=uptime;
    }

    public ArrayList<DocumentReference> getRelatedList() {
        return relatedList;
    }

    public void setRelatedList(ArrayList<DocumentReference> relatedList) {
        this.relatedList = relatedList;
    }

    public DocumentReference getRelatedID() {
        return relatedID;
    }

    public void setRelatedID(DocumentReference relatedID) {
        this.relatedID = relatedID;
    }

    public Timestamp getUptime() {
        return uptime;
    }

    public void setUptime(Timestamp uptime) {
        this.uptime = uptime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public List<String> getPhotoAddr() {
        return photoAddr;
    }

    public void setPhotoAddr(List<String> photoAddr) {
        this.photoAddr = photoAddr;
    }

    public void addPhotoAddr(String photoAddr) {
        this.photoAddr.add(photoAddr);
    }
}
