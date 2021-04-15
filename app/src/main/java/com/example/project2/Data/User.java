package com.example.project2.Data;

import com.google.firebase.database.DatabaseReference;

public class User {
    public String email;
    public String nickname;
    public String photoUrl;
    public String userid;


    public User(String email, String nickname, String userid, String photoUrl){

        this.email = email;
        this.nickname = nickname;
        this.userid = userid;
        this.photoUrl = photoUrl;
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
