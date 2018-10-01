package com.udacity.popularmovies.sync;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.udacity.popularmovies.database.MovieEntry;

public class SyncIntentService extends IntentService {

    private static final String TAG = SyncIntentService.class.getSimpleName();
    private static final String EXTRA_MOVIE_ENTRY = "com.udacity.popularmovies.extras.EXTRA_MOVIE_ENTRY";

    public static Intent getStartIntent(Context context, String action, MovieEntry movieEntry) {
        Intent intent = new Intent(context, SyncIntentService.class);
        intent.setAction(action);
        intent.putExtra(EXTRA_MOVIE_ENTRY, movieEntry);
        return intent;
    }

    public SyncIntentService() {
        super(SyncIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        MovieEntry movieEntry = (MovieEntry) intent.getSerializableExtra(EXTRA_MOVIE_ENTRY);
        SyncTasks.executeTask(this, action, movieEntry);
    }
}
