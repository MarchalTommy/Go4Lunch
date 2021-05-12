package com.aki.go4lunch.repositories;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.aki.go4lunch.helpers.RestaurantCalls;
import com.aki.go4lunch.models.Candidate;
import com.aki.go4lunch.models.Restaurant;
import com.aki.go4lunch.models.Result;
import com.aki.go4lunch.models.ResultDetails;
import com.aki.go4lunch.models.SearchResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class RestaurantRepository {

    private ArrayList<Result> restaurantsAround = new ArrayList<>();
    private MutableLiveData<ArrayList<Result>> resultLiveData = new MutableLiveData<>();
    private MutableLiveData<Result> restaurantFromName = new MutableLiveData<>();
    private MutableLiveData<ResultDetails> restaurantDetail = new MutableLiveData<>();

    public MutableLiveData<ArrayList<Result>> fetchRestaurantsAround(String location, Context context) {
        if (resultLiveData.getValue() == null) {
            RestaurantCalls.fetchRestaurantsAround(new RestaurantCalls.Callbacks() {
                @Override
                public void onResponse(@Nullable JsonObject jsonObject) {
                    Log.d(TAG, "onResponse: RESTAURANTS HAS BEEN FOUND");
                    restaurantsAround.clear();
                    if (jsonObject != null) {
                        Gson gson = new Gson();
                        Restaurant restaurant;
                        restaurant = gson.fromJson(jsonObject, Restaurant.class);

                        for (Result r : restaurant.getResults()) {
                            if (r.getTypes().get(0).equals("restaurant") && !r.isPermanentlyClosed()) {
                                restaurantsAround.add(r);
                            }
                        }
                        resultLiveData.setValue(restaurantsAround);
                    }
                }

                @Override
                public void onFailure() {
                    Log.d(TAG, "onFailure: RESTAURANTS NOT FOUND");
                    resultLiveData.setValue(null);
                }
            }, location, context);

        }
        return resultLiveData;
    }

    public MutableLiveData<ResultDetails> getRestaurantDetail(String id, Context context) {
        restaurantDetail.setValue(null);
        RestaurantCalls.getRestaurantDetailsByID(new RestaurantCalls.Callbacks() {
            @Override
            public void onResponse(@Nullable JsonObject jsonObject) {
                if (jsonObject != null) {
                    Gson gson = new Gson();
                    ResultDetails result = gson.fromJson(jsonObject, ResultDetails.class);
                    restaurantDetail.setValue(result);
                }
            }

            @Override
            public void onFailure() {
                restaurantDetail.setValue(null);
            }
        }, id, context);
        return restaurantDetail;
    }

    public MutableLiveData<Result> getRestaurantFromName(String id, String location, Context context) {
        restaurantFromName.setValue(null);
        RestaurantCalls.fetchRestaurantFromName(new RestaurantCalls.Callbacks() {
            @Override
            public void onResponse(@Nullable JsonObject jsonObject) {
                if (jsonObject != null) {
                    Gson gson = new Gson();
                    SearchResult searchResult = gson.fromJson(jsonObject, SearchResult.class);
                    Result result = new Result();
                    if (searchResult != null && !searchResult.getCandidates().isEmpty()) {
                        Candidate candidate = searchResult.getCandidates().get(0);
                        result.setName(candidate.getName());
                        result.setVicinity(candidate.getFormattedAddress());
                        result.setRating(candidate.getRating());
                        result.setPhotos(candidate.getPhotos());
                        result.setPlaceId(candidate.getPlaceId());

                        restaurantFromName.setValue(result);
                    }

                }
            }

            @Override
            public void onFailure() {
                restaurantFromName.setValue(null);
            }
        }, id, location, context);

        return restaurantFromName;
    }
}
