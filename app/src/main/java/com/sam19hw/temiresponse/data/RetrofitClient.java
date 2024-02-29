package com.sam19hw.temiresponse.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {
    private static RetrofitClient instance = null;
    private Api myApi;
    private Unity_api unity_api;

    private String apiurl;

    private RetrofitClient(String url) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        if (url == "myAPI"){
            apiurl = myApi.BASE_URL;
        } else if (url == "unity_api") {
            apiurl = unity_api.BASE_URL;
        }


        Retrofit retrofit = new Retrofit.Builder().baseUrl(apiurl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        myApi = retrofit.create(Api.class);
        unity_api = retrofit.create((Unity_api.class));
    }

    public static synchronized RetrofitClient getInstance(String url) {
        if (instance == null) {
            instance = new RetrofitClient(url);
        }
        return instance;
    }

    public Api getMyApi() {
        return myApi;
    }
    public Unity_api getUnity_api() {
        return unity_api;
    }
}
