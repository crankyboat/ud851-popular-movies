package com.udacity.popularmovies.database;

import android.content.Context;

import com.udacity.popularmovies.model.Movie;

import java.util.Date;

public class AppDatabaseUtils {

    private static final String TAG = AppDatabaseUtils.class.getSimpleName();

    public static Movie getMovieFromMovieEntry(MovieEntry movieEntry) {
        Movie movie = null;
        if (movieEntry != null) {
            movie = new Movie(
                    movieEntry.getId(),
                    movieEntry.getTitle(),
                    null,
                    null,
                    movieEntry.getImageUrl(),
                    0);
        }
        return movie;
    }

    public static MovieEntry getMovieEntryFromMovie(Movie movie) {
        MovieEntry movieEntry = null;
        if (movie != null) {
            movieEntry = new MovieEntry(
                    movie.getId(),
                    movie.getTitle(),
                    movie.getPosterImageUrl(),
                    new Date());
        }
        return movieEntry;
    }

    public static void addMovie(Context context, MovieEntry movieEntry) {
        AppDatabase database = AppDatabase.getInstance(context);
        database.taskDao().insertMovie(movieEntry);
    }

    public static void deleteMovie(Context context, MovieEntry movieEntry) {
        AppDatabase database = AppDatabase.getInstance(context);
        database.taskDao().deleteMovie(movieEntry);
    }


}
