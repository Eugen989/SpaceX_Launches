package com.example.spacexlaunches.domain.usecase;

import android.content.Context;
import com.example.spacexlaunches.data.MainDatabase;
import com.example.spacexlaunches.data.models.LaunchEntity;
import com.example.spacexlaunches.data.repository.PaginationRepository;
import com.example.spacexlaunches.utils.NetworkUtils;
import java.util.List;

public class GetLaunchesUseCase {

    private final PaginationRepository paginationRepository;
    private final MainDatabase database;

    public GetLaunchesUseCase(PaginationRepository paginationRepository, MainDatabase database) {
        this.paginationRepository = paginationRepository;
        this.database = database;
    }

    public interface LoadDataCallback {
        void onSuccess(List<LaunchEntity> launches);
        void onError(String message);
        void onLoading(boolean isLoading);
    }

    public void loadData(Context context, LoadDataCallback callback) {
        callback.onLoading(true);

        if (NetworkUtils.isInternetAvailable(context)) {
            paginationRepository.loadAllLaunchesJava(success -> {
                if (success) {
                    paginationRepository.getAllLaunchesJava(launches -> {
                        callback.onSuccess(launches);
                        callback.onLoading(false);
                    });
                } else {
                    callback.onError("Failed to load data from API");
                    callback.onLoading(false);
                }
            });
        } else {
            paginationRepository.loadAllLaunchesJava(success -> {
                if (success) {
                    paginationRepository.getAllLaunchesJava(launches -> {
                        callback.onSuccess(launches);
                        callback.onLoading(false);
                    });
                } else {
                    callback.onError("No internet connection and no cached data available");
                    callback.onLoading(false);
                }
            });
        }
    }

    public List<LaunchEntity> getPage(int page) {
        return paginationRepository.getPage(page);
    }

    public int getTotalPages() {
        return paginationRepository.getTotalPages();
    }

    public String getCurrentPageInfo(int page) {
        return paginationRepository.getCurrentPageInfo(page);
    }
}