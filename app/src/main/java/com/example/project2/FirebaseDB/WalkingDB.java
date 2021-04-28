package com.example.project2.FirebaseDB;

public class WalkingDB {

    public String Walking_Time;
    public String Walking_Count;
    public String Walking_Distance;

    public WalkingDB(String Walking_Time,String Walking_Count,String Walking_Distance){
        this.Walking_Time = Walking_Time;
        this.Walking_Count = Walking_Count;
        this.Walking_Distance = Walking_Distance;
    }

    public String getWalking_Count() {
        return Walking_Count;
    }

    public void setWalking_Count(String walking_Count) {
        Walking_Count = walking_Count;
    }

    public String getWalking_Time() {
        return Walking_Time;
    }

    public void setWalking_Time(String walking_Time) {
        Walking_Time = walking_Time;
    }

    public String getWalking_Distance() {
        return Walking_Distance;
    }

    public void setWalking_Distance(String walking_Distance) {
        Walking_Distance = walking_Distance;
    }
}
