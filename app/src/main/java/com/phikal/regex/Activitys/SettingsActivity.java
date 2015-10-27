package com.phikal.regex.Activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.phikal.regex.R;

public class SettingsActivity extends Activity {

    SharedPreferences prefs;

    boolean confirmed = false;

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
        ((TextView) findViewById(R.id.round)).setText(String.valueOf(prefs.getInt(GameActivity.GAME, 1)));
        ((TextView) findViewById(R.id.diff)).setText(String.valueOf(prefs.getInt(GameActivity.DIFF, 1)));
        ((TextView) findViewById(R.id.score)).setText(String.valueOf(prefs.getInt(GameActivity.SCORE, 0)));

        final Button clear = (Button) findViewById(R.id.clear),
                charm = (Button) findViewById(R.id.charm),
                notif = (Button) findViewById(R.id.notif);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmed) {
                    prefs.edit()
                            .putInt(GameActivity.GAME, 1)
                            .putInt(GameActivity.DIFF, 1)
                            .putInt(GameActivity.SCORE, 0)
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
    }

    public void notif() {
        if (prefs.getBoolean(GameActivity.NOFIF, false)) try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(250);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
