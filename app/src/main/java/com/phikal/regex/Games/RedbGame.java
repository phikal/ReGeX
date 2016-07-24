package com.phikal.regex.Games;

import android.app.Activity;
import android.os.AsyncTask;

import com.phikal.regex.Activities.GameActivity;
import com.phikal.regex.R;
import com.phikal.regex.Utils.Task;
import com.phikal.regex.Utils.Word;

import java.io.IOException;
import java.util.Collection;

// Get tasks and contribute to REDB

public class RedbGame extends Game {

    public static final String stdHost = "redb.org.uk",
            REGEX = "regex",
            ID = Task.ID;

    private RedbProto conn;

    public RedbGame(Activity activity) throws IOException, RedbProto.RedbError {
        super(activity);
        String host = prefs.getString(GameActivity.REDB_SERVER, stdHost);
        conn = new RedbProto(host);
    }

    public Task genTask(int diff) {
        return conn.requestTask(diff);
    }

    @Override
    public void submit(final Task task, final String re) {
        new Submitter().execute(re);
    }

    @Override
    public int calcMax(Collection<Word> right, Collection<Word> wrong, int diff) {
        return -1;
    }

    @Override
    public String getName() {
        return activity.getString(R.string.redb_game);
    }

    public String getError() {
        return activity.getString(R.string.redb_error);
    }

    public class Submitter extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... sol) {
            for (int i = 0; i < sol.length; i++)
                conn.submitSolution(sol[i]);
            return null;
        }
    }
}
