package com.example.chessdetection;

public class UserDetails {
    public String userName, email, dob, mobile;

    public UserDetails(){

    }

    public UserDetails(String userName, String email, String textDob, String textMobile) {
        this.userName = userName;
        this.email = email;
        this.dob = textDob;

        this.mobile = textMobile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
