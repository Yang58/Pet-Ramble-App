package com.example.project2.Data;

public class User {

    public String person_name;
    public String petBrithday; // 강아지 생일로 변경
    public String petName;
    public String petAge;
    public String petKind;
    public String photoUrl;
    public String ID;

    public User(){
        
    }

    public User(String person_name, String petBrithday, String petName, String petAge, String petKind, String photoUrl){

        this.person_name = person_name;
        this.petBrithday = petBrithday;
        this.petName = petName;
        this.petAge = petAge;
        this.petKind = petKind;
        this.photoUrl = photoUrl;

    }


    public String getName(){
        return this.person_name;
    }

    public void setName(String person_name){
        this.person_name = person_name;
    }

    public String getPetBrithday(){
        return this.petBrithday;
    }

    public void setPetBrithday(String petBrithday){
        this.petBrithday = petBrithday;
    }

    public String getpetName(){
        return this.petName;
    }

    public void setpetName(String petName){
        this.petName = petName;
    }

    public String getpetAge(){
        return this.petAge;
    }

    public void setpetAge(String petAge){
        this.petAge = petAge;
    }

    public String getpetKind(){
        return this.petKind;
    }

    public void setpetKind(String petKind){
        this.petKind = petKind;
    }

    public String getPhotoUrl(){
        return this.photoUrl;
    }

    public void setPhotoUrl(String photoUrl){
        this.photoUrl = photoUrl;
    }

}
