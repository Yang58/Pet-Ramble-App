package com.example.project2.FirebaseDB;

import android.location.Location;

import com.google.firebase.firestore.GeoPoint;

public class CalendarDB {
    private String Date;
    private String Time;
    private String Content;
    private Boolean Public;
    private Location Savepoint;

    public CalendarDB(String Date,String Time, String Content, Boolean Public, Location Savepoint) {
        this.Date = Date;
        this.Time = Time;
        this.Content = Content;
        this.Public = Public;
        this.Savepoint = Savepoint;
    }

    public CalendarDB() {
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public Boolean getPublic() {
        return Public;
    }

    public void setPublic(Boolean aPublic) {
        Public = aPublic;
    }

    public Location getSavepoint() {
        return Savepoint;
    }

    public void setSavepoint(Location savepoint) {
        Savepoint = savepoint;
    }
}
