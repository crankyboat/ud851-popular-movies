package com.udacity.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.model.Movie;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final String EXTRA_MOVIE = "com.udacity.popularmovies.extras.EXTRA_MOVIE";

    public static Intent getStartIntent(Context context, Movie movie) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(EXTRA_MOVIE, movie);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        Movie movie = null;
        if (intent != null && intent.hasExtra(EXTRA_MOVIE)) {
            movie = (Movie) intent.getSerializableExtra(EXTRA_MOVIE);
        } else {
            closeOnError();
        }

        if (movie != null) {
            populateViews(movie);
            setTitle(movie.getTitle());
        } else {
            closeOnError();
        }
    }

    private void populateViews(Movie movie) {

        TextView titleTextView = (TextView) findViewById(R.id.tv_title);
        TextView releaseDateTextView = (TextView) findViewById(R.id.tv_release_date);
        TextView plotSynopsisTextView = (TextView) findViewById(R.id.tv_plot_synopsis);
        TextView voteAverageTextView = (TextView) findViewById(R.id.tv_vote_avg);

        titleTextView.setText(movie.getTitle());
        releaseDateTextView.setText(movie.getReleaseDate());
        plotSynopsisTextView.setText(movie.getPlotSynopsis());
        voteAverageTextView.setText(String.valueOf(movie.getVoteAverage()));

        ImageView detailImageView = (ImageView) findViewById(R.id.iv_movie_detail);
        Picasso.with(this)
                .load(movie.getPosterImageUrl())
                .placeholder(R.drawable.placeholder_poster)
                .into(detailImageView);

    }

    private void closeOnError() {
        Toast.makeText(this, R.string.error_detail, Toast.LENGTH_LONG).show();
        finish();
    }

}
