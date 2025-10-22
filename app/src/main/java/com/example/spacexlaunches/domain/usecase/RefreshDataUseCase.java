package com.example.spacexlaunches.domain.usecase;

import android.content.Context;
import com.example.spacexlaunches.data.models.LaunchEntity;
import com.example.spacexlaunches.data.repository.Repository;
import com.example.spacexlaunches.utils.NetworkUtils;
import java.util.List;

public class RefreshDataUseCase {

    private final Repository repository;
    private final GetLaunchesUseCase getLaunchesUseCase;

    public RefreshDataUseCase(Repository repository, GetLaunchesUseCase getLaunchesUseCase) {
        this.repository = repository;
        this.getLaunchesUseCase = getLaunchesUseCase;
    }

    public interface RefreshCallback {
        void onSuccess();
        void onError(String message);
        void onLoading(boolean isLoading);
    }

    public void refreshData(Context context, RefreshCallback callback) {
        callback.onLoading(true);

        if (NetworkUtils.isInternetAvailable(context)) {
            repository.clearAllLaunchesJava(() -> {
                getLaunchesUseCase.loadData(context, new GetLaunchesUseCase.LoadDataCallback() {
                    @Override
                    public void onSuccess(List<LaunchEntity> launches) {
                        callback.onSuccess();
                        callback.onLoading(false);
                    }

                    @Override
                    public void onError(String message) {
                        callback.onError(message);
                        callback.onLoading(false);
                    }

                    @Override
                    public void onLoading(boolean isLoading) {
                        callback.onLoading(isLoading);
                    }
                });
            });
        } else {
            callback.onError("No internet connection - cannot refresh data");
            callback.onLoading(false);
        }
    }

    public void clearDatabase(Runnable onComplete) {
        repository.clearAllLaunchesJava(onComplete::run);
    }
}