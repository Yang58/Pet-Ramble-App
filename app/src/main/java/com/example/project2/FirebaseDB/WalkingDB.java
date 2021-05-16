package com.example.project2.FirebaseDB;

public class WalkingDB {

    private String Walking_Time_h;
    private String Walking_Time_m;
    private String Walking_Count;
    private String Walking_Distance;

    public WalkingDB(String Walking_Time_h, String Walking_Time_m, String Walking_Count,String Walking_Distance){
        this.Walking_Time_h = Walking_Time_h;
        this.Walking_Time_m = Walking_Time_m;
        this.Walking_Count = Walking_Count;
        this.Walking_Distance = Walking_Distance;
    }
    public String getWalking_Time_h() {
        return Walking_Time_h;
    }

    public void setWalking_Time_h(String Walking_Time_h) {
        Walking_Time_h = Walking_Time_h;
    }

    public String getWalking_Time_m() {
        return Walking_Time_m;
    }

    public void setWalking_Time_m(String walking_Time_m) {
        Walking_Time_m = walking_Time_m;
    }

    public String getWalking_Count() {
        return Walking_Count;
    }

    public void setWalking_Count(String walking_Count) {
        Walking_Count = walking_Count;
    }


    public String getWalking_Distance() {
        return Walking_Distance;
    }

    public void setWalking_Distance(String walking_Distance) {
        Walking_Distance = walking_Distance;
    }
}
