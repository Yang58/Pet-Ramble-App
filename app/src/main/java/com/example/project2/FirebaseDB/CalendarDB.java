package com.example.project2.FirebaseDB;

public class CalendarDB {
    private String Date;
    private String Time;
    private String Content;
    private Boolean Public;

    public CalendarDB(String Date,String Time, String Content, Boolean Public) {
        this.Date = Date;
        this.Time = Time;
        this.Content = Content;
        this.Public = Public;
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



}
