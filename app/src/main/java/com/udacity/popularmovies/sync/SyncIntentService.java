package com.udacity.popularmovies.sync;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.udacity.popularmovies.database.MovieEntry;

public class SyncIntentService extends IntentService {

    private static final String TAG = SyncIntentService.class.getSimpleName();
    private static final String EXTRA_MOVIE_ENTRY = "com.udacity.popularmovies.extras.EXTRA_MOVIE_ENTRY";
    private static final String EXTRA_QUERY_API_KEY = "com.udacity.popularmovies.extras.EXTRA_QUERY_API_KEY";
    private static final String EXTRA_QUERY_SORT_PARAM = "com.udacity.popularmovies.extras.EXTRA_QUERY_SORT_PARAM";
    private static final String EXTRA_QUERY_MOVIE_ID = "com.udacity.popularmovies.extras.EXTRA_QUERY_MOVIE_ID";
    private static final String EXTRA_QUERY_RESOURCE_TYPE = "com.udacity.popularmovies.extras.EXTRA_QUERY_RESOURCE_TYPE";

    public static Intent getStartIntent(Context context, String action, MovieEntry movieEntry) {
        Intent intent = new Intent(context, SyncIntentService.class);
        intent.setAction(action);
        intent.putExtra(EXTRA_MOVIE_ENTRY, movieEntry);
        return intent;
    }

    public static Intent getStartIntent(Context context, String action, String apiKey,
                                        boolean sortByPopularity) {
        Intent intent = new Intent(context, SyncIntentService.class);
        intent.setAction(action);
        intent.putExtra(EXTRA_QUERY_API_KEY, apiKey);
        intent.putExtra(EXTRA_QUERY_SORT_PARAM, sortByPopularity);
        return intent;
    }

    public static Intent getStartIntent(Context context, String action, String apiKey,
                                        int movieId, String resourceType) {
        Intent intent = new Intent(context, SyncIntentService.class);
        intent.setAction(action);
        intent.putExtra(EXTRA_QUERY_API_KEY, apiKey);
        intent.putExtra(EXTRA_QUERY_MOVIE_ID, movieId);
        intent.putExtra(EXTRA_QUERY_RESOURCE_TYPE, resourceType);
        return intent;
    }

    public SyncIntentService() {
        super(SyncIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if (intent.hasExtra(EXTRA_QUERY_API_KEY) && intent.hasExtra(EXTRA_QUERY_SORT_PARAM)) {
            String apiKey = intent.getStringExtra(EXTRA_QUERY_API_KEY);
            boolean sortByPopularity = intent.getBooleanExtra(EXTRA_QUERY_SORT_PARAM, true);
            SyncTasks.executeTask(this, action, apiKey, sortByPopularity, -1, null);
        } else if (intent.hasExtra(EXTRA_QUERY_MOVIE_ID) && intent.hasExtra(EXTRA_QUERY_RESOURCE_TYPE)) {
            String apiKey = intent.getStringExtra(EXTRA_QUERY_API_KEY);
            int movieId = intent.getIntExtra(EXTRA_QUERY_MOVIE_ID, -1);
            String resourceType = intent.getStringExtra(EXTRA_QUERY_RESOURCE_TYPE);
            SyncTasks.executeTask(this, action, apiKey, false, movieId, resourceType);
        } else if (intent.hasExtra(EXTRA_MOVIE_ENTRY)) {
            MovieEntry movieEntry = (MovieEntry) intent.getSerializableExtra(EXTRA_MOVIE_ENTRY);
            SyncTasks.executeTask(this, action, movieEntry);
        }
    }
}
