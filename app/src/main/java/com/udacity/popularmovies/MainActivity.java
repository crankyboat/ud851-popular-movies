package com.udacity.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.udacity.popularmovies.model.Movie;
import com.udacity.popularmovies.utils.JsonUtils;
import com.udacity.popularmovies.utils.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PosterAdapter.ItemViewOnClickListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int GRID_LAYOUT_SPAN_COUNT = 2;

    private boolean sortByPopularity;
    private RecyclerView moviePostersRecyclerView;
    private PosterAdapter moviePosterAdapter;
    private List<Movie> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sortByPopularity = true;
        movies = new ArrayList<Movie>();

        queryMovies();

        GridLayoutManager layoutManager = new GridLayoutManager(this, GRID_LAYOUT_SPAN_COUNT);
        moviePostersRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_posters);
        moviePostersRecyclerView.setLayoutManager(layoutManager);
        moviePosterAdapter = new PosterAdapter(movies, this);
        moviePostersRecyclerView.setAdapter(moviePosterAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                queryMovies();
                return true;
            case R.id.action_toggle_sort_order:
                sortByPopularity = !sortByPopularity;
                item.setTitle(sortByPopularity ?
                        R.string.query_by_vote_avg :
                        R.string.query_by_popularity);
                queryMovies();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemViewClick(int position) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(Movie.class.getSimpleName(), movies.get(position));
        startActivity(intent);
    }

    private void queryMovies() {
        String apiKey = getResources().getString(R.string.api_key);
        URL queryUrl = NetworkUtils.buildUrl(apiKey, sortByPopularity);
        new MovieQueryTask().execute(queryUrl);
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

            if (queryResults != null && !queryResults.equals("")) {
                try {
                    movies.clear();
                    movies.addAll(JsonUtils.parseMovieJson(queryResults));
                    moviePosterAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
