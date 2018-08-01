package com.phikal.regex.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.phikal.regex.adapters.GameAdaptor;
import com.phikal.regex.games.Games;
import com.phikal.regex.models.Progress;
import com.phikal.regex.R;

import java.util.Locale;
import java.util.concurrent.Callable;

import static com.phikal.regex.Util.*;

public class SettingsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // find views
        TextView roundsText = (TextView) findViewById(R.id.round);
        TextView difficultyText = (TextView) findViewById(R.id.diff);
        Button resetButton = (Button) findViewById(R.id.reset);
        Button charmButton = (Button) findViewById(R.id.charm);
        Button notifButton = (Button) findViewById(R.id.notif);
        Spinner gameSpinner = (Spinner) findViewById(R.id.mode_selector);

        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            locale = getResources().getConfiguration().getLocales().get(0);
        else
            //noinspection deprecation
            locale = getResources().getConfiguration().locale;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplication());
        String modeName = prefs.getString(MODE, Games.SIMPLE_MATCH.name());
        Games g = Games.valueOf(modeName);
        Progress p = g.getProgress(getApplicationContext());

        // display progress
        roundsText.setText(String.valueOf(p.getRound()));
        difficultyText.setText(String.format(locale, "%.2f%%", p.getDifficutly() * 100));

        // reset when pressed twice
        resetButton.setOnClickListener(v -> {
            if (v.getTag() != null && (Boolean) v.getTag()) {
                p.clear();
                v.setTag(false);
                recreate();
            } else {
                ((TextView) v).setText(R.string.confirm);
                v.setTag(true);
            }
        });

        // turn character bar on or off
        charmButton.setOnClickListener(v -> {
            prefs.edit().putBoolean(CHAR_BAR_ON,
                    !prefs.getBoolean(CHAR_BAR_ON, true))
                    .apply();
            notif(this);
            recreate();
        });
        charmButton.setText(prefs.getBoolean(CHAR_BAR_ON, true) ?
                R.string.char_off :
                R.string.char_on);

        // turn notifications on or off
        notifButton.setOnClickListener((v) -> {
            prefs.edit().putBoolean(FEEDBACK_ON,
                    !prefs.getBoolean(FEEDBACK_ON, true))
                    .apply();
            notif(this);
            recreate();
        });
        notifButton.setText(prefs.getBoolean(FEEDBACK_ON, false) ?
                R.string.notif_off :
                R.string.notif_on);

        // game mode selector
        gameSpinner.setAdapter(new GameAdaptor(getApplicationContext()));
        gameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                Games game = (Games) parent.getItemAtPosition(i);
                prefs.edit().putString(MODE, game.name()).apply();
                recreate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }
}
