package com.example.project2.Data;

public class Userinfo {

    public String person_name;
    public String person_age; // 강아지 생일로 변경
    public String petName;
    public String petAge;
    public String petKind;
    public String photoUrl;

    public Userinfo(String person_name, String person_age, String petName, String petAge, String petKind, String photoUrl){

        this.person_name = person_name;
        this.person_age = person_age;
        this.petName = petName;
        this.petAge = petAge;
        this.petKind = petKind;
        this.photoUrl = photoUrl;

    }

    public Userinfo(String person_name, String person_age, String petName, String petAge, String petKind){

        this.person_name = person_name;
        this.person_age = person_age;
        this.petName = petName;
        this.petAge = petAge;
        this.petKind = petKind;

    }

    public String getName(){
        return this.person_name;
    }

    public void getName(String person_name){
        this.person_name = person_name;
    }

    public String getPerson_age(){
        return this.person_age;
    }

    public void getPerson_age(String person_age){
        this.person_age = person_age;
    }

    public String getpetName(){
        return this.petName;
    }

    public void getpetName(String petName){
        this.petName = petName;
    }

    public String getpetAge(){
        return this.petAge;
    }

    public void getpetAge(String petAge){
        this.petAge = petAge;
    }

    public String getpetKind(){
        return this.petKind;
    }

    public void getpetKind(String petKind){
        this.petKind = petKind;
    }

    public String getPhotoUrl(){
        return this.photoUrl;
    }

    public void getPhotoUrl(String photoUrl){
        this.photoUrl = photoUrl;
    }

}
