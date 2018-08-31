package com.udacity.popularmovies.utils;

import com.udacity.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    private static final String TAG = JsonUtils.class.getSimpleName();
    private static final int maxNumberOfMoviesToParse = 100;

    private static final String PAGE_RESULTS = "results";
    private static final String MOVIE_TITLE = "original_title";
    private static final String MOVIE_RELEASE_DATE = "release_date";
    private static final String MOVIE_PLOT_SYNOPSIS = "overview";
    private static final String MOVIE_POSTER_IMG_URL = "http://image.tmdb.org/t/p/";
    private static final String MOVIE_POSTER_IMG_RES = "w342";
    private static final String MOVIE_POSTER_IMG_PATH = "poster_path";
    private static final String MOVIE_VOTE_AVG = "vote_average";

    public static List<Movie> parseMovieJson(String json) throws JSONException {

        List<Movie> movies = new ArrayList<Movie>();

        JSONArray pageJsonArray = new JSONObject(json).getJSONArray(PAGE_RESULTS);
        int pageLength = Math.min(maxNumberOfMoviesToParse, pageJsonArray.length());
        for (int i = 0; i < pageLength; i++) {
            JSONObject movieJson = pageJsonArray.getJSONObject(i);

            String title = movieJson.getString(MOVIE_TITLE);
            String releaseDate = movieJson.getString(MOVIE_RELEASE_DATE);
            String plotSynopsis = movieJson.getString(MOVIE_PLOT_SYNOPSIS);
            double voteAverage = movieJson.getDouble(MOVIE_VOTE_AVG);

            String posterImageUrl = null;
            if (movieJson.getString(MOVIE_POSTER_IMG_PATH) != null) {
                posterImageUrl = MOVIE_POSTER_IMG_URL + MOVIE_POSTER_IMG_RES + movieJson.getString(MOVIE_POSTER_IMG_PATH);
            }

            movies.add(new Movie(title, releaseDate, plotSynopsis, posterImageUrl, voteAverage));
        }

        return movies;
    }
}
