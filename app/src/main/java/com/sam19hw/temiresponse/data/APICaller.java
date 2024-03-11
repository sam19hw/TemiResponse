package com.sam19hw.temiresponse.data;

import android.util.Log;

import com.robotemi.sdk.navigation.model.Position;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class APICaller {
    boolean success = false;

    public boolean OpenDoor(int number, boolean state){
        // TODO add in the logic to select the correct door based on the number and then change the base URL

        Log.d("api", "Making Position API call to open door " + number);

        // reformatting due to the turn query needs on / off as opposed to true / false
        String stateString;
        if (state) {
            stateString = "on";
        }
        else {
            stateString = "off";
        }

        Call<String> call = RetrofitClient.getInstance("myAPI").getMyApi().turnDoor(stateString);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("api", "Got a response from the api on set position " + response);
                success = true;
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("api", "error: An error has occured, no response recieved");
                success = false;
            }
        });

        success = true;
        return success;
    }

    public boolean GoToPosition(Position position){

        Log.d("api", "Making Position API call with position: " + position.getX());

        Call<String> call = RetrofitClient.getInstance("unity_api").getUnity_api().setPosition(position);
        Log.d("api", "Made Position API call with position: " + position.getX());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("api", "Got a response from the api on set position " + response);

                success = true;
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("api", "error: An error has occured, no response recieved");
                success = false;
            }
        });

        return success;
    }

    public boolean GetPosition(){

        Log.d("api", "Making Position API call for position: " );

        Call<Position> call = RetrofitClient.getInstance("unity_api").getUnity_api().getTEMIPosition();
        Log.d("api", "Made Position API call for position: ");
        call.enqueue(new Callback<Position>() {
            @Override
            public void onResponse(Call<Position> call, Response<Position> response) {
                Log.d("api", "Got a response from the api");
                Position position1 = response.body();
                Log.d("api", "Got value in Response: " + position1.getX());
                success = true;
            }

            @Override
            public void onFailure(Call<Position> call, Throwable t) {
                Log.e("api", "error: An error has occured, no response recieved");
                Log.e("api", "Throwable: " + t);
                success = false;
            }

        });

        return success;
    }


}
