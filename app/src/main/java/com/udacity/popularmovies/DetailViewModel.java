package com.udacity.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.udacity.popularmovies.database.AppDatabase;
import com.udacity.popularmovies.database.MovieEntry;

public class DetailViewModel extends ViewModel {

    private static final String TAG = DetailViewModel.class.getSimpleName();

    private LiveData<MovieEntry> movieEntry;

    public DetailViewModel(AppDatabase database, int movieId) {
        movieEntry = database.taskDao().loadMovieById(movieId);
    }

    public LiveData<MovieEntry> getMovieEntry() {
        return movieEntry;
    }

}
