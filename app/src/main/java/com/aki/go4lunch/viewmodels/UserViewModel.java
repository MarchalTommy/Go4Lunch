package com.aki.go4lunch.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aki.go4lunch.models.User;
import com.aki.go4lunch.repositories.UserRepository;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class UserViewModel extends ViewModel {

    //REPOSITORY
    private final UserRepository userRepository = new UserRepository();

    private final MutableLiveData<ArrayList<User>> localUsersData = new MutableLiveData<>();
    private final MutableLiveData<User> user = new MutableLiveData<>();

    //GETTING DATA
    public LiveData<User> getCurrentUser() {
        return userRepository.getCurrentUser();
    }

    public MutableLiveData<ArrayList<User>> getUsers() {
        return localUsersData;
    }

    public FirebaseUser getCurrentFirebaseUser() {
        return userRepository.getCurrentFirebaseUser();
    }

    public MutableLiveData<List<User>> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public MutableLiveData<List<User>> getUsersOnPlace(String placeName) {
        return userRepository.getUsersOnPlace(placeName);
    }

    //UPDATE DATA
    public void setLocalUsersData(ArrayList<User> usersData) {
        localUsersData.setValue(usersData);
    }

    public void setLocation(LatLng location){
        userRepository.setLocation(location);
    }

    public void logout(Context context) {
        userRepository.logout(context);
    }

    public void createCurrentUserInFirestore() {
        userRepository.createUserInFirestore();
    }

    public void createUser(String uid, String userName, String urlPicture) {
        userRepository.createUser(uid, userName, urlPicture);
    }

    public void deleteUser() {
        userRepository.deleteUser(getCurrentFirebaseUser().getUid());
    }

    public void updateHasBooked(Boolean hasBooked) {
        userRepository.updateHasBooked(hasBooked, getCurrentFirebaseUser().getUid());
    }

    public void updatePlaceBooked(String placeBooked) {
        userRepository.updatePlaceBooked(placeBooked, getCurrentFirebaseUser().getUid());
    }

    public void updatePlaceLiked(ArrayList<String> placeLiked) {
        userRepository.updatePlaceLiked(placeLiked, getCurrentFirebaseUser().getUid());
    }

    public void updateNotificationPreference(Boolean pref) {
        userRepository.updateNotificationPreference(pref);
    }



    //GETTERS



    //SETTERS

}
