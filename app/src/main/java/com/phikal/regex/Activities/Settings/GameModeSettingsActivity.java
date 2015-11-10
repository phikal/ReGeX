package com.phikal.regex.Activities.Settings;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.phikal.regex.Activities.GameActivity;
import com.phikal.regex.R;

public class GameModeSettingsActivity extends MainSettingsActivity {

    Spinner spinner;
    LinearLayout random, redb;

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
                Log.d("newmode", i + "");
                prefs.edit().putInt(GameActivity.GAMEMODE, i).apply();
                redraw();
            }
        });

        random = (LinearLayout) findViewById(R.id.random);
        redb = (LinearLayout) findViewById(R.id.redb);

        redraw();
    }

    public void redraw() {
        int mode = prefs.getInt(GameActivity.GAMEMODE, GameActivity.RANDOM);

        random.setVisibility(View.GONE);
        redb.setVisibility(View.GONE);

        switch (mode) {
            case GameActivity.REDB:
                redb.setVisibility(View.VISIBLE);
                spinner.setPromptId(R.id.redb);
                break;
            case GameActivity.RANDOM:
            default:
                random.setVisibility(View.VISIBLE);
                spinner.setPromptId(R.id.random);
                break;
        }
    }

}
