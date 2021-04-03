package com.example.project2.Data;

public class Userinfo {

    private String person_name;
    private String person_age; // 강아지 생일로 변경
    private String petName;
    private String petAge;
    private String petKind;
    private String photoUrl;

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

    public void setName(String person_name){
        this.person_name = person_name;
    }

    public String getPerson_age(){
        return this.person_age;
    }

    public void setPerson_age(String person_age){
        this.person_age = person_age;
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
