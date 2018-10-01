package com.udacity.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.database.AppDatabase;
import com.udacity.popularmovies.database.AppDatabaseUtils;
import com.udacity.popularmovies.database.MovieEntry;
import com.udacity.popularmovies.model.Movie;
import com.udacity.popularmovies.sync.SyncIntentService;
import com.udacity.popularmovies.sync.SyncTasks;
import com.udacity.popularmovies.utils.JsonUtils;
import com.udacity.popularmovies.utils.NetworkUtils;

import org.json.JSONException;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final String EXTRA_MOVIE = "com.udacity.popularmovies.extras.EXTRA_MOVIE";

    private Movie mMovie;
    private boolean mIsFavorite;

    private BroadcastReceiver mBroadcastReceiver;
    private IntentFilter mIntentFilter;

    public static Intent getStartIntent(Context context, Movie movie) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(EXTRA_MOVIE, movie);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mIsFavorite = false;

        Intent intent = getIntent();
        mMovie = null;
        if (intent != null && intent.hasExtra(EXTRA_MOVIE)) {
            mMovie = (Movie) intent.getSerializableExtra(EXTRA_MOVIE);
        } else {
            closeOnError();
        }

        if (mMovie != null) {
            setupDetailViewModel();
            populateViews();
            setTitle(mMovie.getTitle());

            mBroadcastReceiver = new QueryResultsBroadcastReceiver();
            mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(SyncTasks.ACTION_LOAD_SUCCESS);
            mIntentFilter.addAction(SyncTasks.ACTION_URL_ERROR);
            mIntentFilter.addAction(SyncTasks.ACTION_NETWORK_ERROR);

            queryTrailers();

        } else {
            closeOnError();
        }

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

    private void populateViews() {

        TextView titleTextView = (TextView) findViewById(R.id.tv_title);
        TextView releaseDateTextView = (TextView) findViewById(R.id.tv_release_date);
        TextView plotSynopsisTextView = (TextView) findViewById(R.id.tv_plot_synopsis);
        TextView voteAverageTextView = (TextView) findViewById(R.id.tv_vote_avg);

        titleTextView.setText(mMovie.getTitle());
        releaseDateTextView.setText(mMovie.getReleaseDate());
        plotSynopsisTextView.setText(mMovie.getPlotSynopsis());
        voteAverageTextView.setText(String.valueOf(mMovie.getVoteAverage()));

        ImageView detailImageView = (ImageView) findViewById(R.id.iv_movie_detail);
        Picasso.with(this)
                .load(mMovie.getPosterImageUrl())
                .placeholder(R.drawable.placeholder_poster)
                .into(detailImageView);

    }

    private void closeOnError() {
        Toast.makeText(this, R.string.error_detail, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void setupDetailViewModel() {
        AppDatabase database = AppDatabase.getInstance(this);
        int movieId = mMovie.getId();
        DetailViewModelFactory factory = new DetailViewModelFactory(database, movieId);
        DetailViewModel viewModel = ViewModelProviders.of(this, factory).get(DetailViewModel.class);
        viewModel.getMovieEntry().observe(this, new Observer<MovieEntry>() {
            @Override
            public void onChanged(@Nullable MovieEntry MovieEntry) {
                mIsFavorite = MovieEntry != null;
                updateFavoriteButtonText();
            }
        });
    }

    private void updateFavoriteButtonText() {
        Button favoriteButton = (Button) findViewById(R.id.button_toggle_favorite_status);
        int resourceId = mIsFavorite
                ? R.string.detail_button_remove_from_favorites
                : R.string.detail_button_add_to_favorites;
        favoriteButton.setText(resourceId);
    }

    public void toggleFavoriteStatus(View view) {
        MovieEntry movieEntry = AppDatabaseUtils.getMovieEntryFromMovie(mMovie);
        String action = mIsFavorite
                ? SyncTasks.ACTION_REMOVE_FROM_FAVORITES
                : SyncTasks.ACTION_ADD_TO_FAVORITES;
        Intent intent = SyncIntentService.getStartIntent(this, action, movieEntry);
        startService(intent);
    }

    private void queryTrailers() {
        String apiKey = BuildConfig.MOVIE_DATABASE_API_KEY;
        Intent intent = SyncIntentService.getStartIntent(
                this, SyncTasks.ACTION_LOAD_MOVIE_VIDEOS, apiKey, mMovie.getId(),
                NetworkUtils.VIDEOS);
        startService(intent);
    }

    private void parseAndUpdateButtonClickHandler(String queryResults) {

        if (queryResults != null && !queryResults.equals("")) {
            try {
                final String trailerKey = JsonUtils.parseMovieTrailer(queryResults);

                if (trailerKey != null) {
                    Button button = (Button) findViewById(R.id.button_play_trailer);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Uri uri = Uri.parse("https://www.youtube.com/watch?v=" + trailerKey);
                            if (BuildConfig.DEBUG) { Log.d(TAG, "Uri " + uri.toString()); }
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivity(intent);
                            }
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {

        }

    }

    private class QueryResultsBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action) {
                case SyncTasks.ACTION_LOAD_SUCCESS:
                    if (intent.hasExtra(Intent.EXTRA_TEXT)) {
                        String results = intent.getStringExtra(Intent.EXTRA_TEXT);
                        parseAndUpdateButtonClickHandler(results);
                    }
                    break;
                case SyncTasks.ACTION_URL_ERROR:
                    break;
                case SyncTasks.ACTION_NETWORK_ERROR:
                    break;
                default:
                    break;
            }
        }
    }

}
