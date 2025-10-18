package com.example.spacexlaunches.data;

import android.util.Log;

import com.example.spacexlaunches.data.models.Launch;
import com.example.spacexlaunches.utils.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {
    public Api() {
        Log.d("MyLogApi", "Api started: " + Constants.BASE_URL);
    }

    public void getData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<List<Launch>> call = apiService.getSpaceXData();
        call.enqueue(new Callback<List<Launch>>() {
            @Override
            public void onResponse(Call<List<Launch>> call, Response<List<Launch>> response) {
                if(response.isSuccessful()) {
                    List<Launch> launches = response.body();
                    if (launches != null && !launches.isEmpty()) {
                        Log.d("MyApiLog", "Success! Received " + launches.size() + " launches");
                        Log.d("MyApiLog", "First launch: " + launches.get(0).getFailures().get(0).getReason());
                    } else {
                        Log.d("MyApiLog", "No launches received");
                    }
                } else {
                    Log.d("MyApiLog", "Error in get all launch. Code: " + response.code());
                    try {
                        Log.d("MyApiLog", "Error body: " + response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Launch>> call, Throwable t) {
                Log.d("MyApiLog", "Failure: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }
}