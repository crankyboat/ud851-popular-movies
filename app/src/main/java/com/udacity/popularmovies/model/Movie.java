package com.udacity.popularmovies.model;

import java.io.Serializable;

public class Movie implements Serializable {

    private static final String TAG = Movie.class.getSimpleName();
    private static final String NOT_AVAILABLE_STRING = "N/A";

    private String title;
    private String releaseDate;
    private String plotSynopsis;
    private String posterImageUrl;
    private double voteAverage;

    public Movie(String title, String releaseDate, String plotSynopsis, String posterImageUrl, double voteAverage) {
        this.title = title;
        this.releaseDate = releaseDate != null && !releaseDate.isEmpty() ? releaseDate : NOT_AVAILABLE_STRING;
        this.plotSynopsis = plotSynopsis != null && !plotSynopsis.isEmpty() ? plotSynopsis : NOT_AVAILABLE_STRING;
        this.posterImageUrl = posterImageUrl != null && !posterImageUrl.isEmpty() ? posterImageUrl : null;
        this.voteAverage = voteAverage;
    }

    public String getTitle() { return this.title; }
    public String getReleaseDate() { return this.releaseDate; }
    public String getPlotSynopsis() { return this.plotSynopsis; }
    public String getPosterImageUrl() { return this.posterImageUrl; }
    public double getVoteAverage() { return this.voteAverage; }

}
