package com.example.chessdetection;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.GeoPoint;

public class UserModel {

    private String name,email,birthdate,phone,about;
    GeoPoint latitude;

    public UserModel() {
    }

    public UserModel(String email, String name, String birthdate,String phone) {

        this.email = email;
        this.name = name;
        this.birthdate = birthdate;
        this.phone=phone;


    }



    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }
}
