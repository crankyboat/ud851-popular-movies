package com.udacity.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.udacity.popularmovies.database.AppDatabase;
import com.udacity.popularmovies.database.MovieEntry;
import com.udacity.popularmovies.model.MovieReview;
import com.udacity.popularmovies.model.MovieVideo;

import java.util.List;

public class DetailViewModel extends ViewModel {

    private static final String TAG = DetailViewModel.class.getSimpleName();

    private LiveData<MovieEntry> movieEntry;
    private List<MovieVideo> movieVideos;
    private List<MovieReview> movieReviews;

    public DetailViewModel(AppDatabase database, int movieId) {
        movieEntry = database.taskDao().loadMovieById(movieId);
    }

    public LiveData<MovieEntry> getMovieEntry() {
        return movieEntry;
    }

    public void setMovieVideos(List<MovieVideo> movieVideos) {
        this.movieVideos = movieVideos;
    }

    public List<MovieVideo> getMovieVideos() {
        return movieVideos;
    }

    public void setMovieReviews(List<MovieReview> movieReviews) {
        this.movieReviews = movieReviews;
    }

    public List<MovieReview> getMovieReviews() {
        return movieReviews;
    }

}
