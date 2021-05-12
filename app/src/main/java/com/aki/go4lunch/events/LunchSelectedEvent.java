package com.aki.go4lunch.events;

import com.aki.go4lunch.models.User;

import java.util.ArrayList;

public class LunchSelectedEvent {

    public ArrayList<User> userList;
    public String name, formattedAddress;

    public LunchSelectedEvent(ArrayList<User> userList, String name, String formattedAddress) {
        this.userList = userList;
        this.name = name;
        this.formattedAddress = formattedAddress;
    }
}
