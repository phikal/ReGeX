package com.phikal.regex.Activities.Settings;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import com.phikal.regex.Activities.GameActivity;
import com.phikal.regex.Activities.HelloActivity;
import com.phikal.regex.R;

import static com.phikal.regex.Activities.GameActivity.MATCH_MODE;
import static com.phikal.regex.Activities.GameActivity.RAND_MATCH;
import static com.phikal.regex.Activities.GameActivity.notif;

public class MainSettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplication());

        findViewById(R.id.clear).setOnClickListener((v) -> {
            if (v.getTag() != null && (Boolean) v.getTag()) {
                String gmode = prefs.getString(GameActivity.GAME_MODE, MATCH_MODE + RAND_MATCH);
                prefs.edit()
                        .putInt(GameActivity.GAME_ + gmode, 1)
                        .putInt(GameActivity.LVL_ + gmode, 1)
                        .putInt(GameActivity.SCORE_ + gmode, 0)
                        .putBoolean(GameActivity.REGEN, true)
                        .apply();
                ((TextView) findViewById(R.id.round)).setText("1");
                ((TextView) findViewById(R.id.diff)).setText("1");
                ((TextView) findViewById(R.id.score)).setText("0");
                notif(this);
                ((TextView) v).setText(R.string.clear);
                v.setTag(false);
            } else {
                ((TextView) v).setText(R.string.confirm);
                v.setTag(true);
            }
        });

        findViewById(R.id.charm).setOnClickListener((v) -> {
            boolean state = !prefs.getBoolean(GameActivity.CHARM, true);
            prefs.edit().putBoolean(GameActivity.CHARM, state).apply();
            ((TextView) v).setText(state ? R.string.char_off : R.string.char_on);
            notif(this);
        });
        ((TextView) findViewById(R.id.charm)).setText(
                prefs.getBoolean(GameActivity.CHARM, true) ? R.string.char_off : R.string.char_on);

        findViewById(R.id.notif).setOnClickListener((v) -> {
            boolean state = !prefs.getBoolean(GameActivity.NOFIF, true);
            prefs.edit().putBoolean(GameActivity.NOFIF, state).apply();
            ((TextView) v).setText(state ? R.string.notif_off : R.string.notif_on);
            notif(this);
        });
        ((TextView) findViewById(R.id.notif)).setText(
                prefs.getBoolean(GameActivity.NOFIF, false) ? R.string.notif_off : R.string.notif_on);

        findViewById(R.id.mode).setOnClickListener((v) -> startActivity(
                new Intent(this, GameModeSettingsActivity.class)));

        findViewById(R.id.footer).setOnClickListener((v) -> startActivity(
                new Intent(this, HelloActivity.class)));

        redraw();
    }

    @Override
    protected void onResume() {
        super.onResume();
        redraw();
    }

    public void redraw() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String gmode = prefs.getString(GameActivity.GAME_MODE, MATCH_MODE + RAND_MATCH);
        ((TextView) findViewById(R.id.round)).setText(String.valueOf(prefs.getInt(GameActivity.GAME_ + gmode, 1)));
        ((TextView) findViewById(R.id.diff)).setText(String.valueOf(prefs.getInt(GameActivity.LVL_ + gmode, 1)));
        ((TextView) findViewById(R.id.score)).setText(String.valueOf(prefs.getInt(GameActivity.SCORE_ + gmode, 0)));
    }

}
