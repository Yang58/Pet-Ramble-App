package com.example.project2.FirebaseDB;

public class MyPetDB {
    public String petBrithday;
    public String petName;
    public String petAge;
    public String petKind;
    public String petWeight;
    public String petGender;
    public String petNeutralization; // 중성화 여부
    public String petVaccination; // 예방접종 여부


    public MyPetDB(String petName, String petAge, String petBrithday, String petKind,String petWeight,String petGender, String petNeutralization, String petVaccination){
        this.petName = petName;
        this.petAge =  petAge;
        this.petBrithday = petBrithday;
        this.petKind = petKind;
        this.petWeight = petWeight;
        this.petGender = petGender;
        this.petNeutralization = petNeutralization;
        this.petVaccination = petVaccination;
    }

    public MyPetDB(String petName, String petAge, String petBrithday, String petKind,String petWeight){
        this.petName = petName;
        this.petAge =  petAge;
        this.petBrithday = petBrithday;
        this.petKind = petKind;
        this.petWeight = petWeight;
    }

    public String getPetAge() {
        return petAge;
    }

    public void setPetAge(String petAge) {
        this.petAge = petAge;
    }

    public String getPetBrithday() {
        return petBrithday;
    }

    public void setPetBrithday(String petBrithday) {
        this.petBrithday = petBrithday;
    }

    public String getPetKind() {
        return petKind;
    }

    public void setPetKind(String petKind) {
        this.petKind = petKind;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetWeight() {
        return petWeight;
    }

    public void setPetWeight(String petWeight) {
        this.petWeight = petWeight;
    }

    public String getPetGender() {
        return petGender;
    }

    public void setPetGender(String petGender) {
        this.petGender = petGender;
    }

    public String getPetNeutralization() {
        return petNeutralization;
    }

    public void setPetNeutralization(String petNeutralization) {
        this.petNeutralization = petNeutralization;
    }

    public String getPetVaccination() {
        return petVaccination;
    }

    public void setPetVaccination(String petVaccination) {
        this.petVaccination = petVaccination;
    }
}
