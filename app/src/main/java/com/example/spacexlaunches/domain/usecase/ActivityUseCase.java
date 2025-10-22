package com.example.spacexlaunches.domain.usecase;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.spacexlaunches.data.models.LaunchEntity;
import java.util.List;

public class ActivityUseCase {

    private final MutableLiveData<Boolean> _isSearching = new MutableLiveData<>(false);
    private final MutableLiveData<String> _searchQuery = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> _hasData = new MutableLiveData<>(false);

    public LiveData<Boolean> isSearching = _isSearching;
    public LiveData<String> searchQuery = _searchQuery;
    public LiveData<Boolean> hasData = _hasData;

    public void setupSearchLogic(TextView searchEditText, ImageButton clearSearchButton, SearchCallback searchCallback) {
        searchEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable editable) {
                String query = editable != null ? editable.toString() : "";

                _searchQuery.setValue(query);
                _isSearching.setValue(!query.trim().isEmpty());

                if (searchCallback != null) {
                    searchCallback.onSearch(query);
                }

                clearSearchButton.setVisibility(
                        !query.trim().isEmpty() ? View.VISIBLE : View.GONE
                );
            }
        });

        clearSearchButton.setOnClickListener(v -> {
            searchEditText.setText("");
            _searchQuery.setValue("");
            _isSearching.setValue(false);

            if (searchCallback != null) {
                searchCallback.onClearSearch();
            }

            clearSearchButton.setVisibility(View.GONE);
        });
    }

    public void updateSearchUI(List<LaunchEntity> launches, boolean isLoading,
                               FilterLaunchesUseCase.FilterType currentFilter,
                               TextView emptyStateText, TextView searchInfoText) {
        if (launches.isEmpty()) {
            if (Boolean.TRUE.equals(_isSearching.getValue())) {
                String query = _searchQuery.getValue();
                emptyStateText.setText("No launches found for '" + query + "'");
                emptyStateText.setVisibility(View.VISIBLE);
            } else if (!isLoading && !Boolean.TRUE.equals(_hasData.getValue())) {
                emptyStateText.setText("No launches available");
                emptyStateText.setVisibility(View.VISIBLE);
            } else if (!isLoading && Boolean.TRUE.equals(_hasData.getValue())) {
                switch (currentFilter) {
                    case UPCOMING:
                        emptyStateText.setText("No upcoming launches");
                        break;
                    case PAST:
                        emptyStateText.setText("No past launches");
                        break;
                    default:
                        emptyStateText.setText("No launches available");
                }
                emptyStateText.setVisibility(View.VISIBLE);
            }
        } else {
            emptyStateText.setVisibility(View.GONE);
        }

        if (Boolean.TRUE.equals(_isSearching.getValue())) {
            String query = _searchQuery.getValue();
            int resultCount = launches.size();

            searchInfoText.setText("Search: '" + query + "' found " + resultCount + " result(s)");
        }
    }

    public void updateNavigationButtons(int currentPage, int totalPages, boolean isLoading,
                                        Button previousButton, Button nextButton) {
        previousButton.setEnabled(!isLoading && currentPage > 1);
        nextButton.setEnabled(!isLoading && currentPage < totalPages);
    }

    public void updateFilterButtons(FilterLaunchesUseCase.FilterType selectedFilter,
                                    Button allFilterButton, Button upcomingFilterButton,
                                    Button pastFilterButton, Context context) {
        allFilterButton.setSelected(selectedFilter == FilterLaunchesUseCase.FilterType.ALL);
        upcomingFilterButton.setSelected(selectedFilter == FilterLaunchesUseCase.FilterType.UPCOMING);
        pastFilterButton.setSelected(selectedFilter == FilterLaunchesUseCase.FilterType.PAST);

        updateButtonAppearance(allFilterButton, selectedFilter == FilterLaunchesUseCase.FilterType.ALL, context);
        updateButtonAppearance(upcomingFilterButton, selectedFilter == FilterLaunchesUseCase.FilterType.UPCOMING, context);
        updateButtonAppearance(pastFilterButton, selectedFilter == FilterLaunchesUseCase.FilterType.PAST, context);
    }

    private void updateButtonAppearance(Button button, boolean isSelected, Context context) {
        if (isSelected) {
            button.setBackgroundColor(context.getColor(android.R.color.holo_blue_dark));
            button.setTextColor(context.getColor(android.R.color.white));
        } else {
            button.setBackgroundColor(context.getColor(android.R.color.darker_gray));
            button.setTextColor(context.getColor(android.R.color.black));
        }
    }

    public void setHasData(boolean hasData) {
        _hasData.setValue(hasData);
    }

    public interface SearchCallback {
        void onSearch(String query);
        void onClearSearch();
    }
}