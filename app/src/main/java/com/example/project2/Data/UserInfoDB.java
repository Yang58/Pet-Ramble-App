package com.example.project2.Data;

public class UserInfoDB {
    public String user_name;
    public String user_nickname;
    public String user_phoneNumber;
    public String user_profile;

    public UserInfoDB(String user_name,String user_nickname,String user_profile,String user_phoneNumber){
        this.user_name = user_name;
        this.user_nickname = user_nickname;
        this.user_profile = user_profile;
        this.user_phoneNumber = user_phoneNumber;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getUser_profile() {
        return user_profile;
    }

    public void setUser_profile(String user_profile) {
        this.user_profile = user_profile;
    }

    public String getUser_phoneNumber() {
        return user_phoneNumber;
    }

    public void setUser_phoneNumber(String user_phoneNumber) {
        this.user_phoneNumber = user_phoneNumber;
    }

}
