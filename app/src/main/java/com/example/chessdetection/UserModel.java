package com.example.chessdetection;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.GeoPoint;

public class UserModel {

    private String email,name,birthdate,phone,about,profession,country;


    public UserModel() {
    }


    public UserModel(String email, String name, String birthdate, String phone, String about, String profession, String country) {
        this.email = email;
        this.name = name;
        this.birthdate = birthdate;
        this.phone = phone;
        this.about = about;
        this.profession = profession;
        this.country = country;
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
