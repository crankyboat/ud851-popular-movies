package com.udacity.popularmovies.utils;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String MOVIE_API_BASE_URL = "https://api.themoviedb.org/3/discover/movie";
    private static final String PARAM_APIKEY = "api_key";
    private static final String PARAM_SORT = "sort_by";
    private static final String SORT_ORDER_POPULARITY = "popularity.desc";
    private static final String SORT_ORDER_VOTE = "vote_average.desc";

    public static URL buildUrl(String apiKey, boolean sortByPopularity) {

        Uri builtUri = Uri.parse(MOVIE_API_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_APIKEY, apiKey)
                .appendQueryParameter(PARAM_SORT, sortByPopularity ? SORT_ORDER_POPULARITY : SORT_ORDER_VOTE)
                .build();

        Log.d(TAG, "Uri " + builtUri.toString());

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
