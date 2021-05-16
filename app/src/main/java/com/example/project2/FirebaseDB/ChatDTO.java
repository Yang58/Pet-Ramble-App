package com.example.project2.FirebaseDB;

public class ChatDTO {

    private String userName;
    private String message;
    private String photourl;
    private Long timestamp;

    public ChatDTO(String userName, String message,String phototurl, Long timestamp) {

        this.userName = userName;
        this.message = message;
        this.photourl = photourl;
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

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }
}