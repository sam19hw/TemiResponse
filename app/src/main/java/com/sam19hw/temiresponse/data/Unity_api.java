package com.sam19hw.temiresponse.data;

import com.robotemi.sdk.navigation.model.Position;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Unity_api {

    String BASE_URL = "http://192.168.2.126:8080/";


    @GET("color/Door")
    Call<Colour> getColour();

    @GET("position/Door")
    Call<Position> getDoorPosition();

    @POST("position/Door")
    Call<String> doorPosition();

    @GET("position/TEMI")
    Call<Position> getTEMIPosition();

    @POST("position/TEMI")
    Call<String> setPosition(@Body Position position);


}

