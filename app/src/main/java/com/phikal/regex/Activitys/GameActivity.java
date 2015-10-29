package com.phikal.regex.Activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.os.AsyncTask;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.phikal.regex.Adapters.CharAdaptor;
import com.phikal.regex.Adapters.WordAdapter;
import com.phikal.regex.Games.Game;
import com.phikal.regex.Games.REDBGame;
import com.phikal.regex.Games.RandomGame;
import com.phikal.regex.R;
import com.phikal.regex.Utils.Task;


public class GameActivity extends Activity {

    public static final String // preference names
            GAME = "game",
            DIFF = "diff",
            SCORE = "score",
            CHARM = "charm",
            NOFIF = "notif",
            INPUT = "input",
            VERS = "vers",
            POSITION_S = "position_s",
            POSITION_E = "position_e",
            GAMEMODE = "gamemode";
    public static final int // game types
            RANDOM = 0,
            REDB = 1;

    private static final String[]
            chars = {"[", "]", "(", ")", ".", "*", "+", "?", "^", "|", "{", "}", "-", "\\"};
    public static boolean running = false;
    private Game game;
    private Task task;
    private ListView right, wrong;
    private EditText input;
    private LinearLayout linear;
    private Button charsleft;
    private ProgressBar progress;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        versionCheck();
        setupGUI();
        game = setupGame();
    }

    private void versionCheck() {
        try {
            String vers = prefs.getString(VERS, null);
            String cvers = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            if (vers == null)
                startActivity(new Intent(getApplicationContext(), HelloActivity.class));
            //else if (!vers.equals(cvers))
            //    startActivity(new Intent(getApplicationContext(), NewActivity.class));
            prefs.edit().putString(VERS, cvers).apply();
        } catch (PackageManager.NameNotFoundException nnfe) {
            nnfe.printStackTrace();
        }
    }

    private void setupGUI() {
        right = (ListView) findViewById(R.id.right);
        wrong = (ListView) findViewById(R.id.wrong);
        FrameLayout state = (FrameLayout) findViewById(R.id.state);
        charsleft = (Button) state.findViewById(R.id.charsleft);
        progress = (ProgressBar) state.findViewById(R.id.progress);
        ImageButton settings = (ImageButton) findViewById(R.id.settings);
        input = (EditText) findViewById(R.id.editText);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(i);
            }
        });

        charsleft.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                notif();
                int score = prefs.getInt(SCORE, 0);
                prefs.edit().putInt(SCORE, score - score / 10).apply();
                newRound(true);
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
                if (running || task == null) return;
                charsleft.setText(String.valueOf(task.getMax() - s.length()));
                update();
                if (s.length() > 0 && ((WordAdapter) right.getAdapter()).pass() && ((WordAdapter) wrong.getAdapter()).pass()) {
                    int games = prefs.getInt(GAME, 0) + 1,
                            score = Game.calcScore(s.toString(), task),
                            diff = Game.calcDiff(prefs.getInt(SCORE, 0), score, games);

                    game.submit(task, s.toString());

                    Toast.makeText(getApplication(),
                            getResources().getString(R.string.solved) + ' ' + s + " (+" + score + ")",
                            Toast.LENGTH_SHORT).show();

                    if (diff > prefs.getInt(DIFF, 0)) Toast.makeText(getApplication(),
                            getResources().getString(R.string.lvlup) + ' ' + (prefs.getInt(DIFF, 0)) + " -> " + diff,
                            Toast.LENGTH_SHORT).show();

                    prefs.edit()
                            .putInt(GAME, games)
                            .putInt(SCORE, prefs.getInt(SCORE, 0) + score)
                            .putInt(DIFF, diff).apply();
                    newRound(true);
                    notif();
                }
            }
        });

        CharAdaptor adaptor = new CharAdaptor(this, chars);
        linear = (LinearLayout) findViewById(R.id.chars);
        for (int i = 0; i < adaptor.getCount(); i++) {
            linear.addView(adaptor.getView(i, null, linear));
        }
    }

    private void update() {
        if (right.getAdapter() != null)
            ((WordAdapter) right.getAdapter()).setPattern(input.getText().toString()).notifyDataSetChanged();
        if (wrong.getAdapter() != null)
            ((WordAdapter) wrong.getAdapter()).setPattern(input.getText().toString()).notifyDataSetChanged();
    }

    private Game setupGame() {
        switch (prefs.getInt(GAMEMODE, REDB)) {
            case REDB:
                return new REDBGame(this);
            case RANDOM:
            default:
                return new RandomGame(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCharm();
        newRound(false);
        input.setText(prefs.getString(INPUT, ""));
        input.setSelection(prefs.getInt(POSITION_S, 0),
                prefs.getInt(POSITION_E, 0));
        update();
    }

    protected void onPause() {
        super.onPause();
        prefs.edit().putString(INPUT, input.getText().toString()).apply();
        prefs.edit().putInt(POSITION_S, input.getSelectionStart()).apply();
        prefs.edit().putInt(POSITION_E, input.getSelectionEnd()).apply();
    }

    public void newRound(boolean force) {
        try {
            if (!running)
                new TaskManager(force, this).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void click(String c) {
        if (!running)
            input.getText().insert(input.getSelectionStart(), String.valueOf(c));
    }

    public void patternError(boolean b) {
        if (!running)
            charsleft.setTextColor(getResources().getColor(b ? R.color.red : R.color.text));
    }

    public void setCharm() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if (prefs.getBoolean(CHARM, true)) {
            linear.setVisibility(View.VISIBLE);
            params.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.dstd));
        } else {
            linear.setVisibility(View.GONE);
            params.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.std));
        }
        findViewById(R.id.main_layout).setLayoutParams(params);
    }

    public void notif() {
        if (prefs.getBoolean(GameActivity.NOFIF, false)) try {
            RingtoneManager.getRingtone(getApplicationContext(),
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).play();
            ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(250);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class TaskManager extends AsyncTask<Void, Void, Task> {

        boolean force;
        GameActivity activity;

        public TaskManager(boolean force, GameActivity activity) {
            super();
            this.force = force;
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("TaskManager", "running: " + running + ", force: " + force + ", gametype: " + game.getName());
            running = true;
            if (force)
                input.setText("");
            progress.setVisibility(View.VISIBLE);
            charsleft.setVisibility(View.GONE);
        }

        @Override
        protected Task doInBackground(Void... args) {
            return game.newTask(force);
        }

        @Override
        protected void onPostExecute(Task t) {
            super.onPostExecute(task = t);
            if (task != null) {
                right.setAdapter(new WordAdapter(activity, task.getRight(), true));
                wrong.setAdapter(new WordAdapter(activity, task.getWrong(), false));
                if (task.getMax() > 0) {
                    charsleft.setText(String.valueOf(task.getMax()));
                    input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(task.getMax())});
                } else {
                    charsleft.setText("∞");
                    input.setFilters(new InputFilter[]{});
                }
                progress.setVisibility(View.GONE);
                charsleft.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getApplication(),
                        game.getError(),
                        Toast.LENGTH_LONG).show();
            }
            update();
            running = false;
        }
    }

}