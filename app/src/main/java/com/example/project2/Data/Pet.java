package com.example.project2.Data;

public class Pet {
    public String petBirthday; // 강아지 생일로 변경
    public String petName;
    public String petKind;
    public String petWeight;

    public Pet( String petName,String petBirthday, String petKind, String petWeight) {
        this.petBirthday = petBirthday;
        this.petName = petName;
        this.petKind = petKind;
        this.petWeight = petWeight;
    }

    public String getPetBirthday(){
        return this.petBirthday;
    }

    public void setPetBirthday(String petBirthday){
        this.petBirthday = petBirthday;
    }

    public String getpetName(){
        return this.petName;
    }

    public void setpetName(String petName){
        this.petName = petName;
    }


    public String getpetKind(){
        return this.petKind;
    }

    public void setpetKind(String petKind){
        this.petKind = petKind;
    }

    public String getpetWeight(){
        return this.petWeight;
    }

    public void setpetWeight(String petWeight){
        this.petWeight = petWeight;
    }

}

