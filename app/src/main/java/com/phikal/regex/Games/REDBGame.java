package com.phikal.regex.Games;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.phikal.regex.Activities.GameActivity;
import com.phikal.regex.R;
import com.phikal.regex.Utils.Task;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

// Get tasks and contribute to REDB

public class REDBGame extends Game {

    public static final String stdURL = "http://redb.uk",
            REGEX = "regex",
            ID = Task.ID;

    private String REDBURL;

    public REDBGame(Activity activity) {
        super(activity);
        REDBURL = prefs.getString(GameActivity.REDB_SERVER, stdURL);
    }

    public Task genTask(int diff) {
        URL url;
        Log.d("REDB", "connecting to " + REDBURL + "/newtask?diff=" + diff);
        try {
            url = new URL(REDBURL + "/newtask?diff=" + diff);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            InputStream input = new BufferedInputStream(conn.getInputStream());
            StringBuilder builder = new StringBuilder();
            int c;
            while ((c = input.read()) != -1)
                builder.append((char) c);
            input.close();
            return Task.fromJSON(builder.toString());
        } catch (Exception ie) {
            ie.printStackTrace();
            return null;
        }
    }

    @Override
    public void submit(final Task task, final String re) {
        if (!prefs.getBoolean(GameActivity.REDB_CONRTIB, true))
            return;
        try {
            JSONObject object = new JSONObject();
            object.put(ID, task.getId());
            object.put(REGEX, re);
            new Submitter().execute(object);
        } catch (Exception ie) {
            ie.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return activity.getString(R.string.redb_game);
    }

    public String getError() {
        return activity.getString(R.string.redb_error);
    }

    public class Submitter extends AsyncTask<JSONObject, Void, Void> {
        @Override
        protected Void doInBackground(JSONObject... json) {
            try {
                Log.d("submitting", json[0].toString());
                HttpURLConnection con = (HttpURLConnection) new URL(REDBURL + "/submit").openConnection();

                con.setDoOutput(false);
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                con.connect();

                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(json[0].toString());
                wr.flush();
                wr.close();
                Log.d("submitting", "finished with " + con.getResponseCode());
            } catch (Exception ie) {
                ie.printStackTrace();
                return null;
            }
            return null;
        }
    }
}
