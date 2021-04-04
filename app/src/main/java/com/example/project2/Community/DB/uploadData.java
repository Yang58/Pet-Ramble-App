package com.example.project2.Community.DB;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class uploadData{
    public Map<String,ArrayList<String>> relatedList;
    public ArrayList<String> relatedID;
    public String content;
    public int likeNum;
    public List<String> photoAddr = new ArrayList<>();
    public Timestamp uptime;

    public uploadData(){

    }

    public uploadData(String content, Timestamp uptime){
        this.content=content;
        this.likeNum=0;
        this.relatedID = new ArrayList<String>();
        this.relatedList = new HashMap<String, ArrayList<String>>();
        this.photoAddr.add("noData");
        this.uptime=uptime;
    }

    public Map<String, ArrayList<String>> getRelatedList() {
        return relatedList;
    }

    public void setRelatedList(Map<String, ArrayList<String>> relatedList) {
        this.relatedList = relatedList;
    }

    public ArrayList<String> getRelatedID() {
        return relatedID;
    }

    public void setRelatedID(ArrayList<String> relatedID) {
        this.relatedID = relatedID;
    }

    public void addRelatedID(String uid){
        this.relatedID.add(uid);
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
}
