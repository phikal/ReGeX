package com.phikal.regex.Activities.Settings;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.phikal.regex.Activities.GameActivity;
import com.phikal.regex.R;

import static com.phikal.regex.Activities.GameActivity.MATCH_MODE;
import static com.phikal.regex.Activities.GameActivity.RAND_MATCH;
import static com.phikal.regex.Activities.GameActivity.REDB_MATCH;
import static com.phikal.regex.Activities.GameActivity.WORD_MATCH;

public class GameModeSettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_mode_settings);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplication());

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.game_modes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                Fragment toReplace;
                String name;

                switch (i) {
                    case 2: // word-match
                        toReplace = new WordOptionFragment();
                        name = MATCH_MODE + WORD_MATCH;
                        break;
                    case 1: // redb-match
                        toReplace = new REDBOptionFragment();
                        name = MATCH_MODE + REDB_MATCH;
                        break;
                    default:
                    case 0: // rand-match
                        toReplace = new RandomOptionFragment();
                        name = MATCH_MODE + RAND_MATCH;
                        break;
                }

                ft.replace(R.id.container, toReplace);
                ft.commit();

                prefs.edit()
                        .putString(GameActivity.GAME_MODE, name)
                        .putBoolean(GameActivity.REGEN, true)
                        .apply();
            }
        });

    }
}
