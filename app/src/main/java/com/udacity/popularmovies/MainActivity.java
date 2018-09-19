package com.udacity.popularmovies;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.udacity.popularmovies.model.Movie;
import com.udacity.popularmovies.utils.JsonUtils;
import com.udacity.popularmovies.utils.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PosterAdapter.ItemViewOnClickListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String BUNDLE_SORT_BY_POPULARITY = "BUNDLE_SORT_BY_POPULARITY";

    private boolean mSortByPopularity;
    private RecyclerView mMoviePostersRecyclerView;
    private PosterAdapter mMoviePosterAdapter;
    private List<Movie> mMovies;
    private MovieViewModel mMovieViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Get Sort Order */
        if (savedInstanceState == null) {
            mSortByPopularity = true;
        } else {
            mSortByPopularity = savedInstanceState.getBoolean(BUNDLE_SORT_BY_POPULARITY);
        }

        /* Get Movie Data */
        mMovieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        if (mMovieViewModel.getMovies() == null) {
            mMovies = new ArrayList<Movie>();
            mMovieViewModel.setMovies(mMovies);
            queryMovies();
        } else {
            mMovies = mMovieViewModel.getMovies();
        }

        /* Set up RecyclerView */
        GridLayoutManager layoutManager = new GridLayoutManager(this, calculateBestSpanCount());
        mMoviePostersRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_posters);
        mMoviePostersRecyclerView.setLayoutManager(layoutManager);
        mMoviePosterAdapter = new PosterAdapter(mMovies, this);
        mMoviePostersRecyclerView.setAdapter(mMoviePosterAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_toggle_sort_order);
        menuItem.setTitle(mSortByPopularity
                ? R.string.menu_by_vote_avg
                : R.string.menu_by_popularity);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BUNDLE_SORT_BY_POPULARITY, mSortByPopularity);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                queryMovies();
                return true;
            case R.id.menu_toggle_sort_order:
                mSortByPopularity = !mSortByPopularity;
                item.setTitle(mSortByPopularity
                        ? R.string.menu_by_vote_avg
                        : R.string.menu_by_popularity);
                queryMovies();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemViewClick(int position) {
        Intent intent = DetailActivity.getStartIntent(MainActivity.this, mMovies.get(position));
        startActivity(intent);
    }

    private int calculateBestSpanCount() {
        int posterHeight = getResources().getDimensionPixelSize(R.dimen.rv_item_height);
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        return Math.round(outMetrics.widthPixels / posterHeight);
    }

    private void queryMovies() {
        String apiKey = BuildConfig.MOVIE_DATABASE_API_KEY;

        if (NetworkUtils.isConnectedToInternet(this)) {

            URL queryUrl = null;
            try {
                queryUrl = NetworkUtils.buildUrl(apiKey, mSortByPopularity);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                showErrorToast(R.string.error_main_url);

                mMovies.clear();
                if (mMoviePosterAdapter != null) {
                    mMoviePosterAdapter.notifyDataSetChanged();
                }
            }

            if (queryUrl != null) {
                new MovieQueryTask().execute(queryUrl);
            }

        } else {
            showErrorToast(R.string.error_main_network);

            mMovies.clear();
            if (mMoviePosterAdapter != null) {
                mMoviePosterAdapter.notifyDataSetChanged();
            }
        }
    }

    private void showErrorToast(int resourceId) {
        Toast.makeText(this, resourceId, Toast.LENGTH_SHORT).show();
    }

    private class MovieQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {

            URL queryUrl = urls[0];
            String queryResults = null;
            try {
                queryResults = NetworkUtils.getResponseFromHttpUrl(queryUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return queryResults;
        }

        @Override
        protected void onPostExecute(String queryResults) {
            super.onPostExecute(queryResults);

            List<Movie> movies = null;
            if (queryResults != null && !queryResults.equals("")) {
                try {
                    movies = JsonUtils.parseMovieJson(queryResults);
                } catch (JSONException e) {
                    e.printStackTrace();
                    showErrorToast(R.string.error_main_json);
                }
            } else {
                showErrorToast(R.string.error_main_api);
            }

            mMovies.clear();
            mMovies.addAll(movies != null ? movies : new ArrayList<Movie>());
            mMoviePosterAdapter.notifyDataSetChanged();
            mMoviePostersRecyclerView.smoothScrollToPosition(0);
        }

    }

}
