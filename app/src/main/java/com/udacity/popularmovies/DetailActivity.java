package com.udacity.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final String EXTRA_MOVIE = "com.udacity.popularmovies.extras.EXTRA_MOVIE";

    private Movie mMovie;
    private boolean mIsFavorite;

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

        } else {
            closeOnError();
        }

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
        int resId = mIsFavorite
                ? R.string.detail_button_remove_from_favorites
                : R.string.detail_button_add_to_favorites;
        favoriteButton.setText(resId);
    }

    public void toggleFavoriteStatus(View view) {
        MovieEntry movieEntry = AppDatabaseUtils.getMovieEntryFromMovie(mMovie);
        String action = mIsFavorite
                ? SyncTasks.ACTION_REMOVE_FROM_FAVORITES
                : SyncTasks.ACTION_ADD_TO_FAVORITES;
        Intent intent = SyncIntentService.getStartIntent(this, action, movieEntry);
        startService(intent);
    }

}
