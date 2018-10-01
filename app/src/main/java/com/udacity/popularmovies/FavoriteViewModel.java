package com.udacity.popularmovies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.udacity.popularmovies.database.AppDatabase;
import com.udacity.popularmovies.database.MovieEntry;
import com.udacity.popularmovies.model.Movie;

import java.util.List;

public class FavoriteViewModel extends AndroidViewModel {

    private static final String TAG = FavoriteViewModel.class.getSimpleName();

    private LiveData<List<MovieEntry>> movieEntries;

    public FavoriteViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        movieEntries = database.taskDao().loadAllMovies();
    }

    public LiveData<List<MovieEntry>> getMovieEntries() {
        return movieEntries;
    }
}
