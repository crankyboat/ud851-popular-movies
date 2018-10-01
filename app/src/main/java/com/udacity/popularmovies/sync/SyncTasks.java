package com.udacity.popularmovies.sync;

import android.content.Context;
import android.content.Intent;

import com.udacity.popularmovies.database.AppDatabaseUtils;
import com.udacity.popularmovies.database.MovieEntry;

public class SyncTasks {

    private static final String TAG = SyncTasks.class.getSimpleName();

    public static final String ACTION_ADD_TO_FAVORITES = "add_to_favorites";
    public static final String ACTION_REMOVE_FROM_FAVORITES = "remove_from_favorites";
    public static final String ACTION_LOAD_MOVIES = "load";
    public static final String ACTION_LOAD_MOVIE_VIDEOS = "load_videos";
    public static final String ACTION_LOAD_MOVIE_REVIEWS = "load_reviews";

    protected static void executeTask(Context context, String action, MovieEntry movieEntry) {

        switch (action) {
            case ACTION_ADD_TO_FAVORITES:
                addToFavorites(context, movieEntry);
                break;
            case ACTION_REMOVE_FROM_FAVORITES:
                removeFromFavorites(context, movieEntry);
                break;
            case ACTION_LOAD_MOVIES:
                loadMovies(context);
                break;
            case ACTION_LOAD_MOVIE_VIDEOS:
                loadMovieVideos(context);
                break;
            case ACTION_LOAD_MOVIE_REVIEWS:
                loadMovieReviews(context);
                break;
            default:
                break;
        }
    }

    private static void sendBroadcast(Context context, String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        context.sendBroadcast(intent);
    }

    private static void addToFavorites(Context context, MovieEntry movieEntry) {
        AppDatabaseUtils.addMovie(context, movieEntry);
    }

    private static void removeFromFavorites(Context context, MovieEntry movieEntry) {
        AppDatabaseUtils.deleteMovie(context, movieEntry);
    }

    private static void loadMovies(Context context) {

    }

    private static void loadMovieVideos(Context context) {

    }

    private static void loadMovieReviews(Context context) {

    }
}
