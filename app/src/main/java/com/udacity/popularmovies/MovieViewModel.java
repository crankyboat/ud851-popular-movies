package com.udacity.popularmovies;

import android.arch.lifecycle.ViewModel;

import com.udacity.popularmovies.model.Movie;

import java.util.List;

public class MovieViewModel extends ViewModel {

    private List<Movie> movies;

    public MovieViewModel() {
        movies = null;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> newMovies) {
        movies = newMovies;
    }

}
