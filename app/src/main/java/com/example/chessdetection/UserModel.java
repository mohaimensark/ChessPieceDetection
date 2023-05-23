package com.example.chessdetection;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.GeoPoint;

public class UserModel {

    private String email,name,birthdate,phone,about,profession,country;
    private String emailVerified,phoneVerified;

    public UserModel() {
    }


    public UserModel(String email, String name, String birthdate, String phone, String about, String profession, String country,String emailVerified,String phoneVerified) {
        this.email = email;
        this.name = name;
        this.birthdate = birthdate;
        this.phone = phone;
        this.about = about;
        this.profession = profession;
        this.country = country;
        this.emailVerified=emailVerified;
        this.phoneVerified=phoneVerified;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(String emailVerified) {
        emailVerified = emailVerified;
    }

    public String phoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(String phoneVerified) {
        phoneVerified = phoneVerified;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getProfess() {
        return profession;
    }

    public void setProfess(String profession) {
        this.profession = profession;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
