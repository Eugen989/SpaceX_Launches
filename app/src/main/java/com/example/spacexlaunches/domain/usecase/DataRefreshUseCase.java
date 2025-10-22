package com.example.spacexlaunches.domain.usecase;

public class DataRefreshUseCase {

    private long lastRefreshTime = 0;
    private static final long REFRESH_INTERVAL = 5 * 60 * 1000; // 5 minutes

    public DataRefreshUseCase() {
        lastRefreshTime = System.currentTimeMillis();
    }

    public void updateRefreshTime() {
        lastRefreshTime = System.currentTimeMillis();
    }

    public boolean shouldRefreshData(boolean hasData) {
        return !hasData || isDataStale();
    }

    private boolean isDataStale() {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastRefresh = currentTime - lastRefreshTime;

        return timeSinceLastRefresh > REFRESH_INTERVAL;
    }

    public long getTimeSinceLastRefresh() {
        return System.currentTimeMillis() - lastRefreshTime;
    }

    public long getRefreshInterval() {
        return REFRESH_INTERVAL;
    }
}