package com.udacity.popularmovies.utils;

import android.net.Uri;
import android.util.Log;

import com.udacity.popularmovies.BuildConfig;

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

    public static URL buildUrl(String apiKey, boolean sortByPopularity) throws MalformedURLException {

        Uri builtUri = Uri.parse(MOVIE_API_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_APIKEY, apiKey)
                .appendQueryParameter(PARAM_SORT, sortByPopularity ? SORT_ORDER_POPULARITY : SORT_ORDER_VOTE)
                .build();

        if (BuildConfig.DEBUG) { Log.d(TAG, "Uri " + builtUri.toString()); }

        return new URL(builtUri.toString());

    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setUseCaches(false);
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            String response = null;
            if (scanner.hasNext()) {
                response = scanner.next();
            }

            urlConnection.disconnect();
            return response;

        } catch (IOException e) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            throw new IOException(e);
        }
    }

}
