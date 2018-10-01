package com.udacity.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.udacity.popularmovies.database.AppDatabaseUtils;
import com.udacity.popularmovies.database.MovieEntry;
import com.udacity.popularmovies.model.Movie;
import com.udacity.popularmovies.sync.SyncIntentService;
import com.udacity.popularmovies.sync.SyncTasks;
import com.udacity.popularmovies.utils.JsonUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PosterAdapter.ItemViewOnClickListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String BUNDLE_CURRENT_PAGE = "BUNDLE_CURRENT_PAGE";
    private static final String PAGE_POPULARITY = "page_popularity";
    private static final String PAGE_VOTE_AVG = "page_vote_avg";
    private static final String PAGE_FAVORITES = "page_favorites";

    public static final String ACTION_LOAD_SUCCESS = "load_success";
    public static final String ACTION_URL_ERROR = "url_error";
    public static final String ACTION_NETWORK_ERROR = "network_error";
    public static final String ACTION_JSON_ERROR = "json_error";
    public static final String ACTION_API_ERROR = "api_error";

    private String mCurrentPage;
    private RecyclerView mMoviePostersRecyclerView;
    private PosterAdapter mMoviePosterAdapter;
    private List<Movie> mMovies;
    private List<Movie> mFavoriteMovies;
    private MovieViewModel mMovieViewModel;
    private FavoriteViewModel mFavoriteViewModel;

    private BroadcastReceiver mBroadcastReceiver;
    private IntentFilter mIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Get Sort Order */
        if (savedInstanceState == null) {
            mCurrentPage = PAGE_POPULARITY;
        } else {
            mCurrentPage = savedInstanceState.getString(BUNDLE_CURRENT_PAGE);
        }

        setupMovieViewModel();
        setupFavoriteViewModel();

        /* Set up RecyclerView */
        GridLayoutManager layoutManager = new GridLayoutManager(this, calculateBestSpanCount());
        mMoviePostersRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_posters);
        mMoviePostersRecyclerView.setLayoutManager(layoutManager);
        mMoviePosterAdapter = new PosterAdapter(mMovies, this);
        mMoviePostersRecyclerView.setAdapter(mMoviePosterAdapter);

        mBroadcastReceiver = new QueryResultsBroadcastReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(SyncTasks.ACTION_LOAD_SUCCESS);
        mIntentFilter.addAction(SyncTasks.ACTION_URL_ERROR);
        mIntentFilter.addAction(SyncTasks.ACTION_NETWORK_ERROR);

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mBroadcastReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_CURRENT_PAGE, mCurrentPage);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                if (!mCurrentPage.equals(PAGE_FAVORITES)) {
                    queryMovies();
                }
                return true;
            case R.id.menu_sort_by_popularity:
                mCurrentPage = PAGE_POPULARITY;
                queryMovies();
                return true;
            case R.id.menu_sort_by_vote_avg:
                mCurrentPage = PAGE_VOTE_AVG;
                queryMovies();
                return true;
            case R.id.menu_favorites:
                mCurrentPage = PAGE_FAVORITES;
                updateMovieDataAndNotifyAdapter(mFavoriteMovies);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemViewClick(int position) {
        if (!mCurrentPage.equals(PAGE_FAVORITES)) {
            Intent intent = DetailActivity.getStartIntent(MainActivity.this, mMovies.get(position));
            startActivity(intent);
        }
    }

    private void setupMovieViewModel() {
        mMovieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        if (mMovieViewModel.getMovies() == null) {
            mMovies = new ArrayList<Movie>();
            mMovieViewModel.setMovies(mMovies);
            queryMovies();
        } else {
            mMovies = mMovieViewModel.getMovies();
        }
    }

    private void setupFavoriteViewModel() {
        mFavoriteViewModel = ViewModelProviders.of(this).get(FavoriteViewModel.class);
        mFavoriteViewModel.getMovieEntries().observe(this, new Observer<List<MovieEntry>>() {
            @Override
            public void onChanged(@Nullable List<MovieEntry> movieEntries) {
                if (mFavoriteMovies == null) {
                    mFavoriteMovies = new ArrayList<Movie>();
                } else {
                    mFavoriteMovies.clear();
                }
                for (int i = 0; i < movieEntries.size(); i++) {
                    Movie movie = AppDatabaseUtils.getMovieFromMovieEntry(movieEntries.get(i));
                    mFavoriteMovies.add(movie);
                }
            }
        });
    }

    private int calculateBestSpanCount() {
        int posterHeight = getResources().getDimensionPixelSize(R.dimen.rv_item_height);
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        return Math.round(outMetrics.widthPixels / posterHeight);
    }

    private void updateMovieDataAndNotifyAdapter(List<Movie> movies) {
        mMovies.clear();
        mMovies.addAll(movies != null ? movies : new ArrayList<Movie>());
        mMoviePosterAdapter.notifyDataSetChanged();
        mMoviePostersRecyclerView.smoothScrollToPosition(0);
    }

    private void clearMovieDataAndNotifyAdapter() {
        mMovies.clear();
        if (mMoviePosterAdapter != null) {
            mMoviePosterAdapter.notifyDataSetChanged();
        }
    }

    private void queryMovies() {
        String apiKey = BuildConfig.MOVIE_DATABASE_API_KEY;
        boolean sortByPopularity = mCurrentPage.equals(PAGE_POPULARITY);
        Intent intent = SyncIntentService.getStartIntent(
                this, SyncTasks.ACTION_LOAD_MOVIES, apiKey, sortByPopularity);
        startService(intent);
    }

    private void showErrorToast(int resourceId) {
        Toast.makeText(this, resourceId, Toast.LENGTH_SHORT).show();
    }

    private void parseAndDisplayData(String queryResults) {

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
        updateMovieDataAndNotifyAdapter(movies);

    }

    private class QueryResultsBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String results = null;
            if (intent.hasExtra(Intent.EXTRA_TEXT)) {
                results = intent.getStringExtra(Intent.EXTRA_TEXT);
            }

            switch (action) {
                case SyncTasks.ACTION_LOAD_SUCCESS:
                    if (results != null) {
                        parseAndDisplayData(results);
                    }
                    break;
                case SyncTasks.ACTION_URL_ERROR:
                    showErrorToast(R.string.error_main_url);
                    clearMovieDataAndNotifyAdapter();
                    break;
                case SyncTasks.ACTION_NETWORK_ERROR:
                    showErrorToast(R.string.error_main_network);
                    clearMovieDataAndNotifyAdapter();
                    break;
                default:
                    break;
            }
        }
    }

}
