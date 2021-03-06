package com.udacity.popularmovies.model;

import java.io.Serializable;

public class Movie implements Serializable {

    private static final String TAG = Movie.class.getSimpleName();
    private static final String NOT_AVAILABLE_STRING = "N/A";

    private int mId;
    private String mTitle;
    private String mReleaseDate;
    private String mPlotSynopsis;
    private String mPosterImageUrl;
    private double mVoteAverage;

    public Movie(int id, String title, String releaseDate, String plotSynopsis, String posterImageUrl, double voteAverage) {
        this.mId = id;
        this.mTitle = title != null && !title.isEmpty() ? title : NOT_AVAILABLE_STRING;
        this.mReleaseDate = releaseDate != null && !releaseDate.isEmpty() ? releaseDate : NOT_AVAILABLE_STRING;
        this.mPlotSynopsis = plotSynopsis != null && !plotSynopsis.isEmpty() ? plotSynopsis : NOT_AVAILABLE_STRING;
        this.mPosterImageUrl = posterImageUrl != null && !posterImageUrl.isEmpty() ? posterImageUrl : null;
        this.mVoteAverage = voteAverage;
    }

    public int getId() { return this.mId; }
    public String getTitle() { return this.mTitle; }
    public String getReleaseDate() { return this.mReleaseDate; }
    public String getPlotSynopsis() { return this.mPlotSynopsis; }
    public String getPosterImageUrl() { return this.mPosterImageUrl; }
    public double getVoteAverage() { return this.mVoteAverage; }

}
