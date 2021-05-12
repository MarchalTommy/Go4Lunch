package com.aki.go4lunch.models;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class User {

    private static final User userInstance = new User();

    public static User getInstance() {
        return userInstance;
    }

    public Object readResolve() {
        return User.getInstance();
    }

    private String uid, username;
    private Boolean hasBooked = false;
    @Nullable
    private String urlPicture;
    private String placeBooked = "null";
    private String location = "";
    private ArrayList<String> placeLiked = new ArrayList<>();
    private Boolean notificationPreference;

    public User() {
    }

    public User(String uid, String username, @Nullable String urlPicture, Boolean hasBooked, String placeBooked, String location) {
        this.uid = uid;
        this.urlPicture = urlPicture;
        this.username = username;
        this.hasBooked = hasBooked;
        this.placeBooked = placeBooked;
        this.location = location;
    }

    public void setNotificationPreference(Boolean preference) {
        this.notificationPreference = preference;
    }

    public Boolean getNotificationPreference() {
        return notificationPreference;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public String getPlaceBooked() {
        return placeBooked;
    }

    public void setPlaceBooked(String resultBooked) {
        this.placeBooked = resultBooked;
    }

    public ArrayList<String> getPlaceLiked() {
        return placeLiked;
    }

    public void setPlaceLiked (ArrayList<String> placeLiked) {
        this.placeLiked = placeLiked;
    }

    public void addPlaceLiked(String placeLikedID) {
        placeLiked.add(placeLikedID);
    }

    public void removePlaceLiked(String placeLikedID) {
        placeLiked.remove(placeLikedID);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getHasBooked() {
        return hasBooked;
    }

    public void setHasBooked(Boolean hasBooked) {
        this.hasBooked = hasBooked;
    }

    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(@Nullable String urlPicture) {
        this.urlPicture = urlPicture;
    }
}
