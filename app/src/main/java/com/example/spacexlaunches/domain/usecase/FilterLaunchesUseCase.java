package com.example.spacexlaunches.domain.usecase;

import com.example.spacexlaunches.data.models.LaunchEntity;
import java.util.ArrayList;
import java.util.List;

public class FilterLaunchesUseCase {

    public enum FilterType {
        ALL, UPCOMING, PAST
    }

    public List<LaunchEntity> filterLaunches(List<LaunchEntity> allLaunches, FilterType filterType) {
        switch (filterType) {
            case UPCOMING:
                return filterUpcomingLaunches(allLaunches);
            case PAST:
                return filterPastLaunches(allLaunches);
            case ALL:
            default:
                return allLaunches;
        }
    }

    private List<LaunchEntity> filterUpcomingLaunches(List<LaunchEntity> launches) {
        List<LaunchEntity> filtered = new ArrayList<>();
        for (LaunchEntity launch : launches) {
            if (launch.getUpcoming()) {
                filtered.add(launch);
            }
        }
        return filtered;
    }

    private List<LaunchEntity> filterPastLaunches(List<LaunchEntity> launches) {
        List<LaunchEntity> filtered = new ArrayList<>();
        for (LaunchEntity launch : launches) {
            if (!launch.getUpcoming()) {
                filtered.add(launch);
            }
        }
        return filtered;
    }

    public List<LaunchEntity> searchLaunches(List<LaunchEntity> launches, String query) {
        if (query == null || query.trim().isEmpty()) {
            return launches;
        }

        List<LaunchEntity> filtered = new ArrayList<>();
        String lowerCaseQuery = query.toLowerCase();

        for (LaunchEntity launch : launches) {
            if (containsQuery(launch, lowerCaseQuery)) {
                filtered.add(launch);
            }
        }

        return filtered;
    }

    private boolean containsQuery(LaunchEntity launch, String query) {
        return (launch.getName() != null && launch.getName().toLowerCase().contains(query)) ||
                (launch.getDetails() != null && launch.getDetails().toLowerCase().contains(query)) ||
                (launch.getRocketType() != null && launch.getRocketType().toLowerCase().contains(query)) ||
                (launch.getRocketName() != null && launch.getRocketName().toLowerCase().contains(query)) ||
                (launch.getRocketCompany() != null && launch.getRocketCompany().toLowerCase().contains(query));
    }

    public List<LaunchEntity> getPaginatedData(List<LaunchEntity> launches, int page, int pageSize) {
        if (launches.isEmpty()) {
            return new ArrayList<>();
        }

        int start = (page - 1) * pageSize;
        if (start >= launches.size()) {
            return new ArrayList<>();
        }

        int end = Math.min(start + pageSize, launches.size());
        return launches.subList(start, end);
    }

    public int calculateTotalPages(List<LaunchEntity> launches, int pageSize) {
        if (launches.isEmpty()) {
            return 1;
        }
        return (launches.size() + pageSize - 1) / pageSize;
    }

    public String generatePageInfo(List<LaunchEntity> allLaunches, List<LaunchEntity> pageLaunches, int page, int pageSize) {
        if (allLaunches.isEmpty()) {
            return "No launches";
        }

        int startItem = (page - 1) * pageSize + 1;
        int endItem = Math.min(page * pageSize, allLaunches.size());

        return "Showing " + startItem + "-" + endItem + " of " + allLaunches.size() + " launches";
    }
}