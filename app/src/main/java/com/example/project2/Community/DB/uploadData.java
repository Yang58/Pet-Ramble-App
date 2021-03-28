package com.example.project2.Community.DB;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class uploadData{
    public String relatedID;
    public String content;
    public int likeNum;
    public List<String> photoAddr = new ArrayList<>();
    public Timestamp uptime;

    public uploadData(){

    }

    public uploadData(String content, Timestamp uptime){
        this.content=content;
        this.likeNum=0;
        this.relatedID="noRelated";
        this.photoAddr.add("noData");
        this.uptime=uptime;
    }

    public Timestamp getUptime() {
        return uptime;
    }

    public void setUptime(Timestamp uptime) {
        this.uptime = uptime;
    }

    public String getRelatedID() {
        return relatedID;
    }

    public void setRelatedID(String relatedID) {
        this.relatedID = relatedID;
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
