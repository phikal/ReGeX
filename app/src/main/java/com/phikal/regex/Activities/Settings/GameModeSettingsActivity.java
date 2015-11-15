package com.phikal.regex.Activities.Settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.phikal.regex.Activities.GameActivity;
import com.phikal.regex.Games.REDBGame;
import com.phikal.regex.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class GameModeSettingsActivity extends Activity {

    SharedPreferences prefs;

    Spinner spinner;
    LinearLayout random, redb;
    TextView about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_mode_settings);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplication());

        spinner = (Spinner) findViewById(R.id.spinner);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (this, R.array.game_modes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                prefs.edit()
                        .putInt(GameActivity.GAMEMODE, i)
                        .putBoolean(GameActivity.REGEN, true)
                        .apply();
                redraw();
            }
        });

        random = (LinearLayout) findViewById(R.id.random);
        redb = (LinearLayout) findViewById(R.id.redb);
        about = (TextView) findViewById(R.id.about);

        // RANDOM settings

        // settings not implemented yet

        // REDB settings

        final TextView url = (TextView) redb.findViewById(R.id.url);
        url.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Warn about invalid URLs
                if (!Patterns.WEB_URL.matcher(url.getText().toString()).matches())
                    url.setError(getResources().getString(R.string.url_error));

                prefs.edit().putBoolean(GameActivity.REGEN, true).apply();

                // Complain about Broken URLs
                new AsyncTask<String, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(String... url) {
                        try {
                            Log.d("check", "checking if " + url[0] + " is running...");
                            HttpURLConnection connection = (HttpURLConnection) new URL(url[0] + "/newtask").openConnection();
                            connection.setRequestMethod("GET");
                            connection.connect();
                            Log.d("resp", "" + connection.getResponseCode());
                            if (connection.getResponseCode() != 200)
                                return true;
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                            return true;
                        }
                        return false;
                    }

                    @Override
                    protected void onPostExecute(Boolean failed) {
                        super.onPostExecute(failed);
                        if (failed)
                            url.setError(getResources().getString(R.string.conn_error));
                        else {
                            prefs.edit().putString(GameActivity.REDB_SERVER, url.getText().toString()).apply();
                            url.setError(null);
                        }
                    }
                }.execute(url.getText().toString());
            }
        });
        url.setText(prefs.getString(GameActivity.REDB_SERVER, REDBGame.stdURL));

        final Button reset = (Button) redb.findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url.setText(REDBGame.stdURL);
            }
        });

        final Button contrib = (Button) redb.findViewById(R.id.contrib);
        contrib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean state;
                prefs.edit().putBoolean(GameActivity.REDB_CONRTIB, state = !prefs.getBoolean(GameActivity.REDB_CONRTIB, true)).apply();
                contrib.setText(state ? R.string.contrib_off : R.string.contrib_on);
                notif();
            }
        });
        contrib.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.info_contrib),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        contrib.setText(prefs.getBoolean(GameActivity.REDB_CONRTIB, true) ? R.string.contrib_off : R.string.contrib_on);

        redraw();
    }

    public void redraw() {
        int mode = prefs.getInt(GameActivity.GAMEMODE, GameActivity.RANDOM);
        Log.d("loadmode", mode + "");

        random.setVisibility(View.GONE);
        redb.setVisibility(View.GONE);

        switch (mode) {
            case GameActivity.REDB:
                redb.setVisibility(View.VISIBLE);
                spinner.setSelection(GameActivity.REDB);
                about.setText(getResources().getText(R.string.redb_about));
                break;
            case GameActivity.RANDOM:
            default:
                random.setVisibility(View.VISIBLE);
                spinner.setSelection(GameActivity.RANDOM);
                about.setText(getResources().getText(R.string.random_about));
                break;
        }
    }

    public void notif() {
        if (prefs.getBoolean(GameActivity.NOFIF, false)) try {
            (RingtoneManager.getRingtone(getApplicationContext(),
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))).play();
            ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(250);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
