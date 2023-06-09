package com.example.chessdetection;


public class ImageCollector {
    String ProfileUrl;
    public ImageCollector() {
    }

    public ImageCollector(String profileUrl) {
        this.ProfileUrl = profileUrl;
    }


    public String getProfileUrl() {
        return ProfileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        ProfileUrl = profileUrl;
    }
}
