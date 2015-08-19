package com.phikal.regex;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class GameActivity extends Activity {

    public static final String
            GAME = "game",
            DIFF = "diff",
            SCORE = "score",
            CHARM = "charm",
            NOFIF = "notif",
            INPUT = "input";
    Game.Task task;
    Game game;
    ListView right, wrong;
    Button state;
    ImageButton settings;
    EditText input;
    LinearLayout linearLayout;
    SharedPreferences prefs;
    String[] chars = {"[", "]", "(", ")", ".", "*", "+", "?", "^", "|", "{", "}", "-", "\\"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplication());

        right = (ListView) findViewById(R.id.right);
        wrong = (ListView) findViewById(R.id.wrong);
        state = (Button) findViewById(R.id.state);
        settings = (ImageButton) findViewById(R.id.settings);
        input = (EditText) findViewById(R.id.editText);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(i);
            }
        });
        state.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                notif();
                int score = prefs.getInt(SCORE, 0);
                prefs.edit().putInt(SCORE, score - score / 10).apply();
                newRound(false);
                prefs.edit().putInt(DIFF, (int) Math.round(Math.sqrt((prefs.getInt(SCORE, 0) * 1.1 + 1) / (prefs.getInt(GAME, 0) + 1)))).apply();
                return true;
            }
        });

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (task == null) newRound(false);
                state.setText(String.valueOf(task.getMax() - input.length()));
                ((WordAdapter) right.getAdapter()).setPattern(input.getText().toString()).notifyDataSetChanged();
                ((WordAdapter) wrong.getAdapter()).setPattern(input.getText().toString()).notifyDataSetChanged();
                if (input.length() > 0 && ((WordAdapter) right.getAdapter()).pass() && ((WordAdapter) wrong.getAdapter()).pass()) {

                    int game = prefs.getInt(GAME, 0) + 1,
                            score = Game.calcScore(
                                    input.getText().toString(),
                                    input.length(),
                                    task.getMax(),
                                    right.getAdapter().getCount(),
                                    wrong.getAdapter().getCount()),
                            diff = (int) Math.round(1.2 * Math.sqrt((prefs.getInt(SCORE, 0) + score * 1.1 + 1) / (game + 1)));


                    Toast.makeText(getApplication(),
                            getResources().getString(R.string.solved) + ' ' + input.getText() + " (+" + score + ")",
                            Toast.LENGTH_SHORT).show();

                    Log.d("calc", getResources().getString(R.string.lvlup) + ' ' + (prefs.getInt(DIFF, 0)) + " -> " + diff);

                    if (diff > prefs.getInt(DIFF, 0)) Toast.makeText(getApplication(),
                            getResources().getString(R.string.lvlup) + ' ' + (prefs.getInt(DIFF, 0)) + " -> " + diff,
                            Toast.LENGTH_SHORT).show();

                    prefs.edit()
                            .putInt(GAME, game)
                            .putInt(SCORE, prefs.getInt(SCORE, 0) + score)
                            .putInt(DIFF, diff).apply();
                    newRound(false);
                    notif();
                }
            }
        });

        CharAdaptor adaptor = new CharAdaptor(this, chars);
        linearLayout = (LinearLayout) findViewById(R.id.chars);
        for (int i = 0; i < adaptor.getCount(); i++) {
            linearLayout.addView(adaptor.getView(i, null, linearLayout));
        }

        game = new Game(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCharm();
        newRound(true);
        if (prefs.getString(INPUT, null) != null)
            input.setText(prefs.getString(INPUT, null));
        input.requestFocus();
    }

    protected void onPause() {
        super.onPause();
        prefs.edit().putString(INPUT, input.getText().toString()).apply();
    }

    public void newRound(boolean force) {
        task = game.newTask(force);
        right.setAdapter(new WordAdapter(this, task.getRight(), true));
        wrong.setAdapter(new WordAdapter(this, task.getWrong(), false));
        state.setText(String.valueOf(task.getMax()));
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(task.getMax())});
        input.setText("");
    }

    public void click(String c) {
        input.getText().insert(input.getSelectionStart(), String.valueOf(c));
    }

    public void patternError(boolean b) {
        state.setTextColor(getResources().getColor(b ? R.color.red : R.color.text));
    }

    public void setCharm() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if (prefs.getBoolean(CHARM, true)) {
            linearLayout.setVisibility(View.VISIBLE);
            params.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.d_heigth));
        } else {
            linearLayout.setVisibility(View.GONE);
            params.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.heigth));
        }
        findViewById(R.id.main_layout).setLayoutParams(params);
    }

    public void notif() {
        if (prefs.getBoolean(GameActivity.NOFIF, true)) try {
            RingtoneManager.getRingtone(getApplicationContext(),
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).play();
            ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(250);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
