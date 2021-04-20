package com.example.project2.Data;

import com.google.firebase.database.DatabaseReference;

public class User {
    public String email;
    public String nickname;
    public String photoUrl;
    public String userid;
    public String name;
    public String petName;
    public String petKind;
    public int petAge;

    public User(){
        this.email = "null";
        this.nickname = "null";
        this.photoUrl = "null";
        this.userid = "null";
        this.name = "null";
        this.petName = "null";
        this.petKind = "null";
        this.petAge = 0;
    }

    public User(String email, String nickname, String userid, String photoUrl){

        this.email = email;
        this.nickname = nickname;
        this.userid = userid;
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetKind() {
        return petKind;
    }

    public void setPetKind(String petKind) {
        this.petKind = petKind;
    }

    public int getPetAge() {
        return petAge;
    }

    public void setPetAge(int petAge) {
        this.petAge = petAge;
    }

    public String getEmail(){
        return this.email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getNickname(){
        return this.nickname;
    }

    public void setNickname(String nickname){
        this.email = nickname;
    }

    public String getUserid(){
        return this.userid;
    }

    public void setUserid(String userid){
        this.email = userid;
    }

    public String getPhotoUrl(){
        return this.photoUrl;
    }

    public void setPhotoUrl(String photoUrl){
        this.photoUrl = photoUrl;
    }

}
