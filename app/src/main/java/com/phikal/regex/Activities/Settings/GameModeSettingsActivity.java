package com.phikal.regex.Activities.Settings;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.phikal.regex.Activities.GameActivity;
import com.phikal.regex.R;

import static com.phikal.regex.Activities.GameActivity.GAME_MODE;
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

        prefs.edit().remove(REDBOptionFragment.INPUT).apply();
        prefs.edit().remove(WordOptionFragment.INPUT).apply();

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
                Fragment toReplace;
                String name, about;

                switch (i) {
                    case 2: // word-match
                        toReplace = new WordOptionFragment();
                        name = MATCH_MODE + WORD_MATCH;
                        about = getString(R.string.word_about);
                        break;
                    case 1: // redb-match
                        toReplace = new REDBOptionFragment();
                        name = MATCH_MODE + REDB_MATCH;
                        about = getString(R.string.redb_about);
                        break;
                    default:
                    case 0: // rand-match
                        toReplace = new RandomOptionFragment();
                        name = MATCH_MODE + RAND_MATCH;
                        about = getString(R.string.random_about);
                        break;
                }

                getFragmentManager().beginTransaction()
                        .replace(R.id.container, toReplace)
                        .commit();

                ((TextView) findViewById(R.id.about)).setText(about);

                prefs.edit()
                        .putString(GameActivity.GAME_MODE, name)
                        .putBoolean(GameActivity.REGEN, true)
                        .apply();
            }
        });

        switch (prefs.getString(GAME_MODE, MATCH_MODE + RAND_MATCH)) {
            case MATCH_MODE + WORD_MATCH: // word-match
                spinner.setSelection(2);
                break;
            case MATCH_MODE + REDB_MATCH: // redb-match
                spinner.setSelection(1);
                break;
            default:
            case MATCH_MODE + RAND_MATCH: // rand-match
                spinner.setSelection(0);
                break;
        }
    }
}
