package com.example.spacexlaunches.domain.usecase;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.spacexlaunches.data.ApiService;
import com.example.spacexlaunches.data.MainDatabase;
import com.example.spacexlaunches.data.repository.PaginationRepository;
import com.example.spacexlaunches.data.repository.Repository;
import com.example.spacexlaunches.presentation.MainViewModel;

public class MainViewModelFactory implements ViewModelProvider.Factory {

    private final MainDatabase database;
    private final ApiService apiService;

    public MainViewModelFactory(MainDatabase database, ApiService apiService) {
        this.database = database;
        this.apiService = apiService;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            PaginationRepository paginationRepository = new PaginationRepository(database, apiService);
            Repository repository = new Repository(database);

            GetLaunchesUseCase getLaunchesUseCase = new GetLaunchesUseCase(paginationRepository, database);
            FilterLaunchesUseCase filterLaunchesUseCase = new FilterLaunchesUseCase();
            RefreshDataUseCase refreshDataUseCase = new RefreshDataUseCase(repository, getLaunchesUseCase);

            return (T) new MainViewModel(getLaunchesUseCase, filterLaunchesUseCase, refreshDataUseCase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}