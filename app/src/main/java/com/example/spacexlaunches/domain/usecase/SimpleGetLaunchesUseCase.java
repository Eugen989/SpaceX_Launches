package com.example.spacexlaunches.domain.usecase;

import android.content.Context;
import com.example.spacexlaunches.data.MainDatabase;
import com.example.spacexlaunches.data.models.LaunchEntity;
import com.example.spacexlaunches.data.repository.PaginationRepository;
import com.example.spacexlaunches.utils.NetworkUtils;
import java.util.List;

public class SimpleGetLaunchesUseCase {

    private final PaginationRepository paginationRepository;

    public SimpleGetLaunchesUseCase(PaginationRepository paginationRepository) {
        this.paginationRepository = paginationRepository;
    }

    public interface SimpleLoadDataCallback {
        void onSuccess(List<LaunchEntity> launches);
        void onError(String message);
    }

    public void loadData(Context context, SimpleLoadDataCallback callback) {
        paginationRepository.loadAllLaunchesJava(success -> {
            if (success) {
                paginationRepository.getAllLaunchesJava(callback::onSuccess);
            } else {
                if (NetworkUtils.isInternetAvailable(context)) {
                    callback.onError("Failed to load data from API");
                } else {
                    callback.onError("No cached data available");
                }
            }
        });
    }
}