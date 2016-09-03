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
import android.util.Log;
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
    SharedPreferences prefs;
    private boolean running = false;
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
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // SETUP UI
        findViewById(R.id.settings).setOnClickListener((v) -> startActivity(
                new Intent(getApplicationContext(), MainSettingsActivity.class)));

        findViewById(R.id.charsleft).setOnLongClickListener((v) -> {
            int score = prefs.getInt(SCORE_ + getName(), 0);
            prefs.edit().putInt(SCORE_ + getName(), score - score / 10).apply();
            prefs.edit().putInt(LVL_ + getName(), (int) Math.round(Math.sqrt(
                    (prefs.getInt(SCORE_ + getName(), 0) * 1.1 + 1) /
                            (prefs.getInt(GAME_ + getName(), 0) + 1)))).apply();
            newRoundOrRegen(true);
            notif(this);
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

                if (game.calcMax(task, getLvl()) > 0)
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
                            .putInt(LVL_ + getName(), lvl)
                            .putString(CACHE_ + getName(), null)
                            .apply();
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

        // CHECK VERSION
        try {
            String vers = prefs.getString(VERS, null);
            String cvers = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            if (vers == null)
                startActivity(new Intent(getApplicationContext(), HelloActivity.class));
            prefs.edit().putString(VERS, cvers).apply();
        } catch (PackageManager.NameNotFoundException nnfe) {
            // ignore error
        }
    }

    private void rematchUI() {
        ListView right = (ListView) findViewById(R.id.right),
                wrong = (ListView) findViewById(R.id.wrong);
        String input = ((TextView) findViewById(R.id.editText)).getText().toString();

        if (right.getAdapter() != null)
            ((WordAdapter) right.getAdapter()).setPattern(input).notifyDataSetChanged();
        if (wrong.getAdapter() != null)
            ((WordAdapter) wrong.getAdapter()).setPattern(input).notifyDataSetChanged();
    }

    public void setupGame() {
        String name;
        switch (getName()) {
            case MATCH_MODE + REDB_MATCH:
                currentGame = new REDBGenerator(this);
                name = getString(R.string.redb_game);
                break;
            case MATCH_MODE + WORD_MATCH:
                currentGame = new WordGenerator(this);
                name = getString(R.string.word_game);
                break;
            default:
            case MATCH_MODE + RAND_MATCH:
                currentGame = new RandomGenerator();
                name = getString(R.string.random_game);
                break;
        }
        ((TextView) findViewById(R.id.editText)).setHint(getString(R.string.app_name) + ": " + name);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupGame();
        newRoundOrRegen(false);
        rematchUI();

        LinearLayout linear = (LinearLayout) findViewById(R.id.chars);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        boolean show = prefs.getBoolean(CHARM, true);
        linear.setVisibility(show ? View.VISIBLE : View.GONE);
        params.setMargins(0, 0, 0, (int) getResources().getDimension(
                show ? R.dimen.dstd : R.dimen.std));

        findViewById(R.id.main_layout).setLayoutParams(params);

        EditText input = (EditText) findViewById(R.id.editText);

        input.setText(prefs.getString(INPUT_ + getName(), ""));
        input.setSelection(prefs.getInt(POSITION_S_ + getName(), 0),
                prefs.getInt(POSITION_E_ + getName(), 0));
    }

    protected void onPause() {
        super.onPause();
        EditText input = (EditText) findViewById(R.id.editText);

        prefs.edit()
                .putString(INPUT_ + getName(), input.getText().toString())
                .putInt(POSITION_S_ + getName(), input.getSelectionStart())
                .putInt(POSITION_E_ + getName(), input.getSelectionEnd())
                .apply();
    }

    public int getLvl() {
        return prefs.getInt(LVL_ + getName(), 1);
    }

    public Game getGame() {
        return currentGame;
    }

    private Task getTask() {
        return currentTask;
    }

    private void setTask(@NonNull Task t) {
        int max = getGame().calcMax(t, getLvl());

        ((ListView) findViewById(R.id.right))
                .setAdapter(new WordAdapter(this, t.getRight(), true));
        ((ListView) findViewById(R.id.wrong))
                .setAdapter(new WordAdapter(this, t.getWrong(), false));

        ((TextView) findViewById(R.id.charsleft))
                .setText((max > 0) ? String.valueOf(max) : "inf");

        ((EditText) findViewById(R.id.editText))
                .setFilters((max > 0) ?
                        new InputFilter[]{new InputFilter.LengthFilter(max)} :
                        new InputFilter[]{});

        rematchUI();
        prefs.edit().putString(CACHE_ + getName(), t.toString()).apply();
        currentTask = t;
    }

    private boolean isRunning() {
        return running;
    }

    private void setRunning(boolean r) {
        running = r;
    }

    public void newRoundOrRegen(boolean force) {
        if (isRunning()) return;

        if (force || prefs.getBoolean(REGEN, false)) {
            prefs.edit().putBoolean(REGEN, false).apply();
            new TaskManager().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            String cache = prefs.getString(CACHE_ + getName(), null);
            if (cache == null)
                newRoundOrRegen(true);
            else
                setTask(Task.parseTask(cache));
        }
    }

    public void click(String c) {
        EditText input = (EditText) findViewById(R.id.editText);
        if (!isRunning())
            input.getText().insert(input.getSelectionStart(), String.valueOf(c));
    }

    private String getName() {
        return prefs.getString(GAME_MODE, MATCH_MODE + RAND_MATCH);
    }

    public void patternError(boolean b) {
        if (!isRunning()) ((TextView) findViewById(R.id.charsleft))
                .setTextColor(getResources().getColor(b ? R.color.white : R.color.red));
    }

    private class TaskManager extends AsyncTask<Void, String, Task> {

        public TaskManager() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            findViewById(R.id.progress).setVisibility(View.VISIBLE);
            findViewById(R.id.charsleft).setVisibility(View.GONE);
            // ((EditText) findViewById(R.id.editText)).setEnabled(false);
            setRunning(true);
        }

        @Override
        protected Task doInBackground(Void... args) {
            try {
                Log.d("redb", "renerating new task with " + getName());
                return getGame().genTask(getLvl());
            } catch (TaskGenerationException tge) {
                publishProgress(tge.getMessage());
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(String... errors) {
            super.onProgressUpdate(errors);
            Toast.makeText(getApplication(), errors[0], Toast.LENGTH_LONG).show();
            ((EditText) findViewById(R.id.editText)).setEnabled(true);
        }

        @Override
        protected void onPostExecute(Task task) {
            super.onPostExecute(task);
            if (task != null) setTask(task);

            findViewById(R.id.progress).setVisibility(View.GONE);
            findViewById(R.id.charsleft).setVisibility(View.VISIBLE);
            // ((EditText) findViewById(R.id.editText)).setEnabled(true);
            setRunning(false);
        }
    }
}