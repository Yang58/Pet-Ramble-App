package com.example.project2.FirebaseDB;

import java.util.List;

public class Friend {
    public List<String> friend;
    public List<String> friend_mail;

    public Friend(List<String> friend, List<String> friend_mail){
       this.friend=friend;
       this.friend_mail=friend_mail;
    }

        public List<String> getfriend(){
            return this.friend;
        }

        public void setfriend(List<String> friend){
            this.friend = friend;
        }

        public List<String> getfriend_mail(){
            return this.friend_mail;
        }

        public void setfriend_mail(List<String> friend_mail){
            this.friend_mail = friend_mail;
        }
    }


