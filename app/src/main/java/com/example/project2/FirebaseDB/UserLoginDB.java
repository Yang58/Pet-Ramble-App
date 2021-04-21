package com.example.project2.FirebaseDB;

public class UserLoginDB {
    private String User_UID;
    private String User_ID;
    private String User_PW;

    public UserLoginDB(String user_ID,String user_UID,String user_PW){
        this.User_ID = user_ID;
        this.User_PW = user_PW;
        this.User_UID = user_UID;
    }

    public String getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(String user_ID) {
        User_ID = user_ID;
    }

    public String getUser_PW() {
        return User_PW;
    }

    public void setUser_PW(String user_PW) {
        User_PW = user_PW;
    }

    public String getUser_UID() {
        return User_UID;
    }

    public void setUser_UID(String user_UID) {
        User_UID = user_UID;
    }
}
