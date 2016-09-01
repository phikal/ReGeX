package com.phikal.regex.Activities;

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
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.phikal.regex.Activities.Settings.MainSettingsActivity;
import com.phikal.regex.Adapters.CharAdaptor;
import com.phikal.regex.Adapters.WordAdapter;
import com.phikal.regex.Games.Game;
import com.phikal.regex.Games.Match.REDBGenerator;
import com.phikal.regex.Games.Match.RandomGenerator;
import com.phikal.regex.Games.Match.WordGenerator;
import com.phikal.regex.Games.TaskGenerationException;
import com.phikal.regex.R;
import com.phikal.regex.Utils.Calc;
import com.phikal.regex.Utils.Task;

public class GameActivity extends Activity {

    public static final String // preference names
            GAME_ = "game_",
            LVL_ = "diff_",
            SCORE_ = "score_",
            CHARM = "charm",
            NOFIF = "notif",
            INPUT_ = "input_",
            VERS = "vers",
            REGEN = "regen",
            POSITION_S_ = "position_s_",
            POSITION_E_ = "position_e_",
            GAME_MODE = "gamemode",
            MATCH_MODE = "matchmode-",
            RAND_MATCH = "random",
            WORD_MATCH = "word",
            REDB_MATCH = "redb",
            EXTRACT_MODE = "extractmode-",
            REPLACE_MODE = "replacemode-",
            CACHE_ = "cache_",
            REDB_SERVER = "redb_server";

    private static final String[]
            chars = {"[", "]", "(", ")", ".", "*", "+", "?", "^", "|", "{", "}", "-", "\\"};
    private boolean running;
    private Game currentGame;
    private Task currentTask;

    public static void notif(Context ctx) {
        if (PreferenceManager.getDefaultSharedPreferences(ctx).
                getBoolean(GameActivity.NOFIF, false)) try {
            (RingtoneManager.getRingtone(ctx,
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))).play();
            ((Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(250);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // SETUP UI
        findViewById(R.id.settings).setOnClickListener((v) -> startActivity(
                new Intent(getApplicationContext(), MainSettingsActivity.class)));

        findViewById(R.id.charsleft).setOnLongClickListener((v) -> {
            notif(this);
            String gname = prefs.getString(GAME_MODE, MATCH_MODE + RAND_MATCH);
            int score = prefs.getInt(SCORE_ + gname, 0);
            prefs.edit().putInt(SCORE_ + gname, score - score / 10).apply();
            newRoundOrRegen(true);
            prefs.edit().putInt(LVL_ + gname, (int) Math.round(Math.sqrt((prefs.getInt(SCORE_ + gname, 0) * 1.1 + 1) /
                    (prefs.getInt(GAME_ + gname, 0) + 1)))).apply();
            return true;
        });

        ((TextView) findViewById(R.id.editText)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Game game = getGame();
                Task task = getTask();

                if (isRunning() || task == null) return;

                patternError(game.valid(s.toString()));
                rematchUI();
                ((TextView) findViewById(R.id.charsleft)).setText(String.valueOf(
                        game.calcMax(task, getLvl()) - game.length(s.toString())));

                if (game.length(s.toString()) > 0 && game.pass(task, s.toString())) {

                    int games = prefs.getInt(GAME_ + getName(), 0) + 1,
                            score = Calc.calcScore(s.toString(), task, game, getLvl()),
                            lvl = Calc.calcDiff(prefs.getInt(SCORE_ + getName(), 0), score, games);

                    Toast.makeText(getApplication(),
                            getResources().getString(R.string.solved) + ' ' + s + " (+" + score + ")",
                            Toast.LENGTH_SHORT).show();

                    if (lvl > getLvl())
                        Toast.makeText(getApplication(), getResources().getString(R.string.lvlup) +
                                        ' ' + getLvl() + " -> " + lvl,
                                Toast.LENGTH_SHORT).show();

                    getTask().submit(s.toString());
                    s.clear();

                    prefs.edit()
                            .putInt(GAME_ + getName(), games)
                            .putInt(SCORE_ + getName(), prefs.getInt(SCORE_ + getName(), 0) + score)
                            .putInt(LVL_ + getName(), lvl).apply();
                    newRoundOrRegen(true);
                    notif(getApplicationContext());
                }
            }
        });

        CharAdaptor adaptor = new CharAdaptor(this, chars);
        LinearLayout linear = (LinearLayout) findViewById(R.id.chars);
        for (int i = 0; i < adaptor.getCount(); i++) {
            linear.addView(adaptor.getView(i, null, linear));
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);


        boolean show = prefs.getBoolean(CHARM, true);
        findViewById(R.id.chars)
                .setVisibility(show ? View.VISIBLE : View.GONE);
        params.setMargins(0, 0, 0, (int) getResources().getDimension(
                show ? R.dimen.dstd : R.dimen.std));

        findViewById(R.id.main_layout).setLayoutParams(params);

        setupGame();
        newRoundOrRegen(false);

        // CHECK VERSION
        try {
            String vers = prefs.getString(VERS, null);
            String cvers = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            if (vers == null)
                startActivity(new Intent(getApplicationContext(), HelloActivity.class));
            prefs.edit().putString(VERS, cvers).apply();
        } catch (PackageManager.NameNotFoundException nnfe) {
            nnfe.printStackTrace();
        }
    }

    private void rematchUI() {
        ListView right = (ListView) findViewById(R.id.right),
                wrong = (ListView) findViewById(R.id.wrong);
        EditText input = (EditText) findViewById(R.id.editText);

        if (right.getAdapter() != null)
            ((WordAdapter) right.getAdapter()).setPattern(input.getText().toString()).notifyDataSetChanged();
        if (wrong.getAdapter() != null)
            ((WordAdapter) wrong.getAdapter()).setPattern(input.getText().toString()).notifyDataSetChanged();
    }

    public void setupGame() {
        switch (PreferenceManager.getDefaultSharedPreferences(this)
                .getString(GAME_MODE, MATCH_MODE + RAND_MATCH)) {
            case MATCH_MODE + REDB_MATCH:
                currentGame = new REDBGenerator(this);
                break;
            case MATCH_MODE + WORD_MATCH:
                currentGame = new WordGenerator(this);
                break;
            default:
            case MATCH_MODE + RAND_MATCH:
                currentGame = new RandomGenerator();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupGame();
        rematchUI();
        newRoundOrRegen(false);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        EditText input = (EditText) findViewById(R.id.editText);

        input.setText(prefs.getString(INPUT_ + getName(), ""));
        input.setSelection(prefs.getInt(POSITION_S_ + getName(), 0),
                prefs.getInt(POSITION_E_ + getName(), 0));
    }

    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        EditText input = (EditText) findViewById(R.id.editText);

        prefs.edit()
                .putString(INPUT_ + getName(), input.getText().toString())
                .putInt(POSITION_S_ + getName(), input.getSelectionStart())
                .putInt(POSITION_E_ + getName(), input.getSelectionEnd())
                .apply();
    }

    public int getLvl() {
        return PreferenceManager.getDefaultSharedPreferences(this).getInt(LVL_ + getName(), 1);
    }

    public Game getGame() {
        return currentGame;
    }

    private Task getTask() {
        return currentTask;
    }

    private void setTask(@NonNull Task t) {
        currentTask = t;
    }

    private boolean isRunning() {
        return running;
    }

    private void setRunning(boolean r) {
        running = r;
    }

    public void newRoundOrRegen(boolean regen) {
        if (isRunning()) return;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (regen || prefs.getBoolean(REGEN, false)) {
            prefs.edit().putBoolean(REGEN, false).apply();
            new TaskManager(this, null).execute();
        } else {
            String cache = prefs.getString(CACHE_ + getName(), null);
            if (getTask() == null) newRoundOrRegen(true);
            new TaskManager(this, cache).execute();
        }
    }

    public void click(String c) {
        EditText input = (EditText) findViewById(R.id.editText);
        if (!isRunning())
            input.getText().insert(input.getSelectionStart(), String.valueOf(c));
    }

    private String getName() {
        return PreferenceManager.getDefaultSharedPreferences(this).getString(GAME_MODE, MATCH_MODE + RAND_MATCH);
    }

    public void patternError(boolean b) {
        if (!isRunning()) ((TextView) findViewById(R.id.charsleft))
                .setTextColor(getResources().getColor(b ? R.color.white : R.color.red));
    }

    private class TaskManager extends AsyncTask<Void, Void, Task> {

        private GameActivity activity;
        private String cache;
        private String error;

        public TaskManager(GameActivity activity, String cache) {
            super();
            this.activity = activity;
            this.cache = cache;
            error = getString(R.string.random_error);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            findViewById(R.id.progress).setVisibility(View.VISIBLE);
            findViewById(R.id.charsleft).setVisibility(View.GONE);

            setRunning(true);
        }

        @Override
        protected Task doInBackground(Void... args) {
            if (cache != null)
                return Task.parseTask(cache);
            try {
                return getGame().genTask(getLvl());
            } catch (TaskGenerationException tge) {
                error = tge.getMessage();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Task task) {
            super.onPostExecute(task);
            if (task != null) {
                int max = getGame().calcMax(task, getLvl());

                ((ListView) findViewById(R.id.right))
                        .setAdapter(new WordAdapter(activity, task.getRight(), true));
                ((ListView) findViewById(R.id.wrong))
                        .setAdapter(new WordAdapter(activity, task.getWrong(), false));

                ((TextView) findViewById(R.id.charsleft))
                        .setText((max > 0) ? String.valueOf(max) : "∞");

                ((EditText) findViewById(R.id.editText))
                        .setFilters((max > 0) ?
                                new InputFilter[]{new InputFilter.LengthFilter(max)} :
                                new InputFilter[]{});

                findViewById(R.id.progress).setVisibility(View.GONE);
                findViewById(R.id.charsleft).setVisibility(View.VISIBLE);
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                        .putString(CACHE_ + getName(), task.toString()).apply();
                setTask(task);
            } else {
                Toast.makeText(getApplication(), error, Toast.LENGTH_LONG).show();
            }
            rematchUI();
            setRunning(false);
        }
    }
}