package com.phikal.regex.Activities.Settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.phikal.regex.Activities.GameActivity;
import com.phikal.regex.Activities.HelloActivity;
import com.phikal.regex.R;

public class MainSettingsActivity extends Activity {

    SharedPreferences prefs;
    String gmode;
    private boolean confirmed = false;
    private Button clear, charm, notif, mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        findViewById(R.id.footer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HelloActivity.class));
            }
        });

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplication());

        clear = (Button) findViewById(R.id.clear);
        charm = (Button) findViewById(R.id.charm);
        notif = (Button) findViewById(R.id.notif);
        mode = (Button) findViewById(R.id.mode);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmed) {
                    prefs.edit()
                            .putInt(GameActivity.GAME + gmode, 1)
                            .putInt(GameActivity.DIFF + gmode, 1)
                            .putInt(GameActivity.SCORE + gmode, 0)
                            .putBoolean(GameActivity.REGEN, true)
                            .apply();
                    ((TextView) findViewById(R.id.round)).setText("1");
                    ((TextView) findViewById(R.id.diff)).setText("1");
                    ((TextView) findViewById(R.id.score)).setText("0");
                    notif();
                    clear.setText(R.string.clear);
                    confirmed = false;
                } else {
                    clear.setText(R.string.confirm);
                    confirmed = true;
                }
            }
        });

        charm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean state;
                prefs.edit().putBoolean(GameActivity.CHARM, state = !prefs.getBoolean(GameActivity.CHARM, true)).apply();
                charm.setText(state ? R.string.char_off : R.string.char_on);
                notif();
            }
        });
        charm.setText(prefs.getBoolean(GameActivity.CHARM, true) ? R.string.char_off : R.string.char_on);

        notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean state;
                prefs.edit().putBoolean(GameActivity.NOFIF, state = !prefs.getBoolean(GameActivity.NOFIF, true)).apply();
                notif.setText(state ? R.string.notif_off : R.string.notif_on);
                notif();
            }
        });
        notif.setText(prefs.getBoolean(GameActivity.NOFIF, false) ? R.string.notif_off : R.string.notif_on);

        mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), GameModeSettingsActivity.class));
            }
        });

        redraw();
    }

    @Override
    protected void onResume() {
        super.onResume();
        redraw();
    }

    public void redraw() {
        switch (prefs.getInt(GameActivity.GAMEMODE, GameActivity.RANDOM)) {
            case GameActivity.REDB:
                gmode = getString(R.string.redb_game);
                break;
            default:
            case GameActivity.RANDOM:
                gmode = getString(R.string.random_game);
        }
        ((TextView) findViewById(R.id.round)).setText(String.valueOf(prefs.getInt(GameActivity.GAME + gmode, 1)));
        ((TextView) findViewById(R.id.diff)).setText(String.valueOf(prefs.getInt(GameActivity.DIFF + gmode, 1)));
        ((TextView) findViewById(R.id.score)).setText(String.valueOf(prefs.getInt(GameActivity.SCORE + gmode, 0)));
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
