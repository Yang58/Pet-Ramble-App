package com.example.project2.FirebaseDB;


import java.util.Date;

public class ChatDTO {
    private String userName;
    private String message;
    private Long timestamp;

    public ChatDTO(String userName, String message,Long timestamp) {
        this.userName = userName;
        this.message = message;
        this.timestamp = timestamp;

    }
    public ChatDTO() {
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserName() {
        return userName;
    }

    public String getMessage() {
        return message;
    }

    public Long getTimestamp() {
        return timestamp;
    }


}