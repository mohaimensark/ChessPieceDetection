package com.example.chessdetection;

public class RatingModel {

    String Userid, UserImage, Text, UserName;
    int givenRating;

    public RatingModel() {
    }

    public RatingModel(String Userid, String UserImage, String Text, String UserName, int givenRating) {
        this.Userid = Userid;
        this.UserImage = UserImage;
        this.Text = Text;
        this.UserName = UserName;
        this.givenRating = givenRating;
    }

    public String getUserid() {
        return Userid;
    }

    public void setUserid(String userid) {
        Userid = userid;
    }

    public String getUserImage() {
        return UserImage;
    }

    public void setUserImage(String userImage) {
        UserImage = userImage;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public int getGivenRating() {
        return givenRating;
    }

    public void setGivenRating(int givenRating) {
        this.givenRating = givenRating;
    }
}
