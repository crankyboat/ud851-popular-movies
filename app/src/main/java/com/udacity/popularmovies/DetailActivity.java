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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.udacity.popularmovies.model.MovieReview;
import com.udacity.popularmovies.model.MovieVideo;
import com.udacity.popularmovies.sync.SyncIntentService;
import com.udacity.popularmovies.sync.SyncTasks;
import com.udacity.popularmovies.utils.JsonUtils;
import com.udacity.popularmovies.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements ItemViewOnClickListener {

    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final String EXTRA_MOVIE = "com.udacity.popularmovies.extras.EXTRA_MOVIE";

    private Movie mMovie;
    private List<MovieVideo> mMovieVideos;
    private List<MovieReview> mMovieReviews;
    private boolean mIsFavorite;

    private RecyclerView mMovieVideosRecyclerView;
    private VideoAdapter mMovieVideoAdapter;
    private RecyclerView mMovieReviewsRecyclerView;
    private ReviewAdapter mMovieReviewAdapter;

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

            LinearLayoutManager videoLayoutManager = new LinearLayoutManager(this);
            mMovieVideosRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_videos);
            mMovieVideosRecyclerView.setLayoutManager(videoLayoutManager);
            mMovieVideoAdapter = new VideoAdapter(mMovieVideos, this);
            mMovieVideosRecyclerView.setAdapter(mMovieVideoAdapter);

            LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this);
            mMovieReviewsRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_reviews);
            mMovieReviewsRecyclerView.setLayoutManager(reviewLayoutManager);
            mMovieReviewAdapter = new ReviewAdapter(mMovieReviews);
            mMovieReviewsRecyclerView.setAdapter(mMovieReviewAdapter);

            mBroadcastReceiver = new QueryResultsBroadcastReceiver();
            mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(SyncTasks.ACTION_LOAD_VIDEOS_SUCCESS);
            mIntentFilter.addAction(SyncTasks.ACTION_LOAD_REVIEWS_SUCCESS);
            mIntentFilter.addAction(SyncTasks.ACTION_URL_ERROR);
            mIntentFilter.addAction(SyncTasks.ACTION_NETWORK_ERROR);

            queryMovieResources();

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

        if (viewModel.getMovieVideos() != null) {
            mMovieVideos = viewModel.getMovieVideos();
        } else {
            mMovieVideos = new ArrayList<MovieVideo>();
            viewModel.setMovieVideos(mMovieVideos);
        }

        if (viewModel.getMovieReviews() != null) {
            mMovieReviews = viewModel.getMovieReviews();
        } else {
            mMovieReviews = new ArrayList<MovieReview>();
            viewModel.setMovieReviews(mMovieReviews);
        }

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

    private void queryMovieResources() {
        String apiKey = BuildConfig.MOVIE_DATABASE_API_KEY;

        // Videos
        Intent videosIntent = SyncIntentService.getStartIntent(
                this, SyncTasks.ACTION_LOAD_MOVIE_VIDEOS, apiKey, mMovie.getId(),
                NetworkUtils.VIDEOS);
        startService(videosIntent);

        // Reviews
        Intent reviewsIntent = SyncIntentService.getStartIntent(
                this, SyncTasks.ACTION_LOAD_MOVIE_REVIEWS, apiKey, mMovie.getId(),
                NetworkUtils.REVIEWS);
        startService(reviewsIntent);
    }

    private void parseMovieResourcesQueryResults(String queryResults, String action) {
        if (queryResults != null && !queryResults.equals("")) {
            switch (action) {
                case SyncTasks.ACTION_LOAD_VIDEOS_SUCCESS:
                    List<MovieVideo> movieVideos = JsonUtils.parseMovieVideos(queryResults);
                    mMovieVideos.clear();
                    mMovieVideos.addAll(movieVideos != null ? movieVideos : new ArrayList<MovieVideo>());
                    mMovieVideoAdapter.notifyDataSetChanged();
                    break;
                case SyncTasks.ACTION_LOAD_REVIEWS_SUCCESS:
                    List<MovieReview> movieReviews = JsonUtils.parseMovieReviews(queryResults);
                    mMovieReviews.clear();
                    mMovieReviews.addAll(movieReviews != null ? movieReviews : new ArrayList<MovieReview>());
                    mMovieReviewAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        } else {
            // TODO
        }
    }

    @Override
    public void onItemViewClick(int position) {
        final String videoKey = mMovieVideos.get(position).getKey();
        if (videoKey != null) {
            Uri uri = Uri.parse("https://www.youtube.com/watch?v=" + videoKey);
            if (BuildConfig.DEBUG) { Log.d(TAG, "Uri " + uri.toString()); }
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }

    private class QueryResultsBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action) {
                case SyncTasks.ACTION_LOAD_VIDEOS_SUCCESS:
                    if (intent.hasExtra(Intent.EXTRA_TEXT)) {
                        String results = intent.getStringExtra(Intent.EXTRA_TEXT);
                        parseMovieResourcesQueryResults(results, action);
                    }
                    break;
                case SyncTasks.ACTION_LOAD_REVIEWS_SUCCESS:
                    if (intent.hasExtra(Intent.EXTRA_TEXT)) {
                        String results = intent.getStringExtra(Intent.EXTRA_TEXT);
                        parseMovieResourcesQueryResults(results, action);
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
