package com.phikal.regex.Games;

import android.app.Activity;
import android.util.Log;

import com.phikal.regex.R;
import com.phikal.regex.Utils.Task;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

// Get tasks and contribute to REDB

public class REDBGame extends Game {

    public static final String REDBURL = "http://debian:8080",
            REGEX = "regex",
            ID = Task.ID;

    public REDBGame(Activity activity) {
        super(activity);
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
            int response = conn.getResponseCode();
            InputStream input = new BufferedInputStream(conn.getInputStream());
            String res = "";
            int c;
            while ((c = input.read()) != -1)
                res += (char) c;
            input.close();
            return Task.fromJSON(res);
        } catch (Exception ie) {
            ie.printStackTrace();
            return null;
        }
    }

    @Override
    public void submit(Task task, String re) {
        try {
            JSONObject object = new JSONObject();
            object.put(ID, task.getId());
            object.put(REGEX, re);
            //TODO: Implement post request json uploader
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
}
