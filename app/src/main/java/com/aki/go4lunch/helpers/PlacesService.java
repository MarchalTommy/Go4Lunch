package com.aki.go4lunch.helpers;

import com.google.gson.JsonObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesService {

    static PlacesService setRetrofit() {

        //HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        //interceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(PlacesService.class);
    }



    @GET("/maps/api/place/nearbysearch/json")
    Call<JsonObject> getRestaurantsAround(@Query("key") String apiKey,
                                          @Query("location") String userCoordinates,
                                          @Query("type") String type,
                                          @Query("rankby") String rankBy);

    @GET("/maps/api/place/findplacefromtext/json")
    Call<JsonObject> getRestaurantFromName(@Query("key") String apiKey,
                                           @Query("input") String placeName,
                                           @Query("inputtype") String inputType,
                                           @Query("locationbias") String circleBias,
                                           @Query("fields") String fields);

    @GET("/maps/api/place/details/json")
    Call<JsonObject> getRestaurantDetails(@Query("key") String apiKey,
                                          @Query("place_id") String placeId,
                                          @Query("region") String regionCode,
                                          @Query("fields") String fields);

}
