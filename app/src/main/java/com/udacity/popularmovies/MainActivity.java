package com.udacity.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    private static final int GRID_LAYOUT_SPAN_COUNT = 2;

    private boolean mSortByPopularity;
    private RecyclerView mMoviePostersRecyclerView;
    private PosterAdapter mMoviePosterAdapter;
    private List<Movie> mMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSortByPopularity = true;
        mMovies = new ArrayList<Movie>();

        queryMovies();

        GridLayoutManager layoutManager = new GridLayoutManager(this, GRID_LAYOUT_SPAN_COUNT);
        mMoviePostersRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_posters);
        mMoviePostersRecyclerView.setLayoutManager(layoutManager);
        mMoviePosterAdapter = new PosterAdapter(mMovies, this);
        mMoviePostersRecyclerView.setAdapter(mMoviePosterAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                queryMovies();
                return true;
            case R.id.menu_toggle_sort_order:
                mSortByPopularity = !mSortByPopularity;
                item.setTitle(mSortByPopularity ?
                        R.string.menu_by_vote_avg :
                        R.string.menu_by_popularity);
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

    private void queryMovies() {
        String apiKey = getResources().getString(R.string.api_key);

        URL queryUrl = null;
        try {
            queryUrl = NetworkUtils.buildUrl(apiKey, mSortByPopularity);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            showErrorToast(R.string.error_main_url);
        }

        if (queryUrl != null) {
            new MovieQueryTask().execute(queryUrl);
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
        }

    }

}
