package com.udacity.popularmovies.sync;

import android.content.Context;
import android.content.Intent;

import com.udacity.popularmovies.database.AppDatabaseUtils;
import com.udacity.popularmovies.database.MovieEntry;
import com.udacity.popularmovies.utils.NetworkUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class SyncTasks {

    private static final String TAG = SyncTasks.class.getSimpleName();

    public static final String ACTION_ADD_TO_FAVORITES = "add_to_favorites";
    public static final String ACTION_REMOVE_FROM_FAVORITES = "remove_from_favorites";
    public static final String ACTION_LOAD_MOVIES = "load";
    public static final String ACTION_LOAD_MOVIE_VIDEOS = "load_videos";
    public static final String ACTION_LOAD_MOVIE_REVIEWS = "load_reviews";

    public static final String ACTION_LOAD_SUCCESS = "load_success";
    public static final String ACTION_LOAD_VIDEOS_SUCCESS = "load_videos_success";
    public static final String ACTION_LOAD_REVIEWS_SUCCESS = "load_reviews_success";
    public static final String ACTION_URL_ERROR = "url_error";
    public static final String ACTION_NETWORK_ERROR = "network_error";

    protected static void executeTask(Context context, String action, MovieEntry movieEntry) {

        switch (action) {
            case ACTION_ADD_TO_FAVORITES:
                addToFavorites(context, movieEntry);
                break;
            case ACTION_REMOVE_FROM_FAVORITES:
                removeFromFavorites(context, movieEntry);
                break;
            default:
                break;
        }
    }

    protected static void executeTask(Context context, String action, String apiKey,
                                      boolean sortByPopularity, int movieId, String resourceType) {

        switch (action) {
            case ACTION_LOAD_MOVIES:
                loadMovies(context, apiKey, sortByPopularity);
                break;
            case ACTION_LOAD_MOVIE_VIDEOS:
            case ACTION_LOAD_MOVIE_REVIEWS:
                loadMovieResources(context, apiKey, resourceType, movieId);
                break;
            default:
                break;
        }
    }

    private static void sendBroadcast(Context context, String action, String results) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(Intent.EXTRA_TEXT, results);
        context.sendBroadcast(intent);
    }

    private static void addToFavorites(Context context, MovieEntry movieEntry) {
        AppDatabaseUtils.addMovie(context, movieEntry);
    }

    private static void removeFromFavorites(Context context, MovieEntry movieEntry) {
        AppDatabaseUtils.deleteMovie(context, movieEntry);
    }

    private static void loadMovies(Context context, String apiKey, boolean sortByPopularity) {

        String action = ACTION_LOAD_SUCCESS;
        String queryResults = null;

        if (NetworkUtils.isConnectedToInternet(context)) {

            URL queryUrl = null;
            try {
                queryUrl = NetworkUtils.buildUrl(apiKey, sortByPopularity);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                action = ACTION_URL_ERROR;
            }

            if (queryUrl != null) {
                try {
                    queryResults = NetworkUtils.getResponseFromHttpUrl(queryUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                    action = ACTION_NETWORK_ERROR;
                }
            }

        } else {
            action = ACTION_NETWORK_ERROR;
        }
        sendBroadcast(context, action, queryResults);
    }

    private static void loadMovieResources(Context context, String apiKey, String resourceType,
                                           int movieId) {

        String action = null;
        switch (resourceType) {
            case NetworkUtils.VIDEOS:
                action = ACTION_LOAD_VIDEOS_SUCCESS;
                break;
            case NetworkUtils.REVIEWS:
                action = ACTION_LOAD_REVIEWS_SUCCESS;
                break;
            default:
                break;
        }
        String queryResults = null;

        if (NetworkUtils.isConnectedToInternet(context)) {

            URL queryUrl = null;
            try {
                queryUrl = NetworkUtils.buildGetMoreResourcesUrl(apiKey, resourceType, movieId);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                action = ACTION_URL_ERROR;
            }

            if (queryUrl != null) {
                try {
                    queryResults = NetworkUtils.getResponseFromHttpUrl(queryUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                    action = ACTION_NETWORK_ERROR;
                }
            }

        } else {
            action = ACTION_NETWORK_ERROR;
        }
        sendBroadcast(context, action, queryResults);

    }

}
