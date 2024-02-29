package com.sam19hw.temiresponse.data;

import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {

        String BASE_URL = "192.168.2.34/";
        @POST("relay/0")
        Call<String> turnDoor(@Query("turn") String state);

    }
