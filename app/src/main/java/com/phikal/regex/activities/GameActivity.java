package com.phikal.regex.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.phikal.regex.R;
import com.phikal.regex.adapters.ColumnAdapter;
import com.phikal.regex.adapters.InputAdapter;
import com.phikal.regex.adapters.WordAdapter;
import com.phikal.regex.games.Games;
import com.phikal.regex.games.TaskGenerationException;
import com.phikal.regex.models.Collumn;
import com.phikal.regex.models.Game;
import com.phikal.regex.models.Task;

import java.io.IOException;

import static com.phikal.regex.Util.CHARS;
import static com.phikal.regex.Util.CHAR_BAR_ON;
import static com.phikal.regex.Util.COUNT;
import static com.phikal.regex.Util.CURRENT_TASK;
import static com.phikal.regex.Util.MODE;
import static com.phikal.regex.Util.PROGRESS;
import static com.phikal.regex.Util.VERSION;

public class GameActivity extends Activity {

    SharedPreferences prefs;
    ColumnAdapter columnAdapter;
    InputAdapter inputAdapter;
    private Game game;
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // find or generate task
        String modeName = prefs.getString(MODE, Games.SIMPLE_MATCH.name());

        try {
            game = Games.valueOf(modeName).getGame(getApplicationContext());
        } catch (IOException ioe) {
            new AlertDialog.Builder(getApplicationContext())
                    .setMessage(ioe.getMessage())
                    .create();
            return;
        }

        try {
            if (savedInstanceState != null)
                task = (Task) savedInstanceState.getSerializable(CURRENT_TASK);
            else {
                task = game.nextTask();
            }
        } catch (ClassCastException cce) {
            new AlertDialog.Builder(getApplicationContext())
                    .setMessage(cce.getMessage())
                    .create();
            return;
        } catch (TaskGenerationException tge) {
            return;
        }
        assert task != null;
        assert task.getCollumns() != null;
        assert task.getInputs() != null;

        // find and setup views
        LinearLayout colums = (LinearLayout) findViewById(R.id.columns);
        ListView inputs = (ListView) findViewById(R.id.inputs);
        LinearLayout charmb = (LinearLayout) findViewById(R.id.chars);

        colums.setWeightSum(task.getCollumns().size());
        LayoutInflater inf = LayoutInflater.from(getApplicationContext());
        for (Collumn c : task.getCollumns()) {
            View v = inf.inflate(R.layout.column_layout, colums, false);

            TextView colName = (TextView) v.findViewById(R.id.col_name);
            ListView colList = (ListView) v.findViewById(R.id.col_list);

            colName.setText(c.getHeader());
            colList.setAdapter(new WordAdapter(getApplicationContext(), c));

            colums.addView(v);
        }

        inputs.setAdapter(inputAdapter = new InputAdapter(this, task));

        for (String c : CHARS) {
            TextView v = new TextView(getApplicationContext());

            v.setText(c);
            v.setOnClickListener($ -> inputAdapter.append(c));
            v.setHeight(getResources().getDimensionPixelSize(R.dimen.std));
            v.setWidth(getResources().getDimensionPixelSize(R.dimen.std));
            v.setGravity(Gravity.CENTER);

            charmb.addView(v);
        }

        // setup callbacks
        game.onProgress(p -> {
            prefs.edit()
                    .putFloat(game.getGame().getId() + PROGRESS, (float) p.getDifficutly())
                    .putInt(game.getGame().getId() + COUNT, p.getRound())
                    .apply();
            recreate();
        });

        // check version changes
        try {
            String vers = prefs.getString(VERSION, null);
            String cvers = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            if (vers == null)
                startActivity(new Intent(getApplicationContext(), HelloActivity.class));
            prefs.edit().putString(VERSION, cvers).apply();
        } catch (PackageManager.NameNotFoundException nnfe) {
            // ignore error
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean show = prefs.getBoolean(CHAR_BAR_ON, true);
        if (!show) findViewById(R.id.chars).setVisibility(View.GONE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(CURRENT_TASK, task);
    }
}