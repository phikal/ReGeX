package com.phikal.regex.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.phikal.regex.R;
import com.phikal.regex.games.Game;
import com.phikal.regex.models.Progress;

import java.util.Arrays;
import java.util.Locale;

import static com.phikal.regex.Util.CHAR_BAR_ON;
import static com.phikal.regex.Util.FEEDBACK_ON;
import static com.phikal.regex.Util.MODE;
import static com.phikal.regex.Util.notif;

public class SettingsActivity extends Activity {

    SharedPreferences prefs;
    Game g;
    Progress p;

    TextView roundsText, difficultyText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // find views
        roundsText = (TextView) findViewById(R.id.round);
        difficultyText = (TextView) findViewById(R.id.diff);
        Button resetButton = (Button) findViewById(R.id.reset);
        Button charmButton = (Button) findViewById(R.id.charm);
        Button notifButton = (Button) findViewById(R.id.notif);
        Spinner gameSpinner = (Spinner) findViewById(R.id.mode_selector);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        stats();

        // reset when pressed twice
        resetButton.setOnClickListener(v -> {
            if (v.getTag() != null && (Boolean) v.getTag()) {
                resetButton.setText(R.string.clear);
                p.clear();
                v.setTag(false);
                GameActivity.reload = true;
                stats();
            } else {
                resetButton.setText(R.string.confirm);
                v.setTag(true);
            }
        });

        // turn character bar on or off
        charmButton.setOnClickListener(v -> {
            prefs.edit().putBoolean(CHAR_BAR_ON,
                    !prefs.getBoolean(CHAR_BAR_ON, true))
                    .apply();
            charmButton.setText(prefs.getBoolean(CHAR_BAR_ON, true) ?
                    R.string.char_off :
                    R.string.char_on);
            notif(this);
        });
        charmButton.setText(prefs.getBoolean(CHAR_BAR_ON, true) ?
                R.string.char_off :
                R.string.char_on);

        // turn notifications on or off
        notifButton.setOnClickListener((v) -> {
            prefs.edit().putBoolean(FEEDBACK_ON,
                    !prefs.getBoolean(FEEDBACK_ON, true))
                    .apply();
            notifButton.setText(prefs.getBoolean(FEEDBACK_ON, false) ?
                    R.string.notif_off :
                    R.string.notif_on);
            notif(this);
        });
        notifButton.setText(prefs.getBoolean(FEEDBACK_ON, false) ?
                R.string.notif_off :
                R.string.notif_on);

        // game mode selector
        gameSpinner.setAdapter(new ArrayAdapter<Game>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                Arrays.asList(Game.values())) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView != null) return convertView;
                TextView v = new TextView(getContext());
                v.setText(getString(Game.values()[position].name));
                return v;
            }
        });
        gameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                Game game = (Game) parent.getItemAtPosition(i);
                prefs.edit().putString(MODE, game.name()).apply();
                GameActivity.reload = true;
                stats();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        gameSpinner.setSelection(g.ordinal());
    }

    private void stats() {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            locale = getResources().getConfiguration().getLocales().get(0);
        else
            //noinspection deprecation
            locale = getResources().getConfiguration().locale;

        String modeName = prefs.getString(MODE, Game.DEFAULT_GAME.name());
        g = Game.valueOf(modeName);
        p = g.getProgress(getApplicationContext());

        // display progress
        roundsText.setText(String.valueOf(p.getRound()));
        difficultyText.setText(String.format(locale, "%.2f%%", p.getDifficutly() * 100));
    }
}
