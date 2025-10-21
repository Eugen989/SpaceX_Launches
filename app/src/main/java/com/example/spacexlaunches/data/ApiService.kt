package com.example.spacexlaunches.data;

import com.example.spacexlaunches.data.models.Launch;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("launches")
    Call<List<Launch>> getSpaceXData();
}