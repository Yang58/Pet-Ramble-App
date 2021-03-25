package com.example.project2.Data;

public class Userinfo {

    private String person_name;
    private String person_age;
    private String petName;
    private String petAge;
    private String petKind;

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

    public String returnPetinfo(){
        return petName;
    }
}
