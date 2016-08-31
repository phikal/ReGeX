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
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
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

import com.phikal.regex.Activities.Settings.MainSettingsActivity;
import com.phikal.regex.Adapters.CharAdaptor;
import com.phikal.regex.Adapters.WordAdapter;
import com.phikal.regex.Games.Game;
import com.phikal.regex.Games.Match.REDBGenerator;
import com.phikal.regex.Games.Match.RandomGenerator;
import com.phikal.regex.Games.Match.WordGenerator;
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
            REDB_SERVER = "redb_server",
            REDB_CONRTIB = "redb_contrib";

    private static final String[]
            chars = {"[", "]", "(", ")", ".", "*", "+", "?", "$", "^", "|", "{", "}", "-", "\\"};
    public static boolean running = false;

    private Game game;
    private Task task;
    private String name;
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

        setupUI();
        setupGame();
        newRoundOrRegen(false);
        versionCheck();
    }

    private void versionCheck() {
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

    public void setupUI() {
        right = (ListView) findViewById(R.id.right);
        wrong = (ListView) findViewById(R.id.wrong);
        FrameLayout state = (FrameLayout) findViewById(R.id.state);
        charsleft = (Button) state.findViewById(R.id.charsleft);
        progress = (ProgressBar) state.findViewById(R.id.progress);
        ImageButton settings = (ImageButton) findViewById(R.id.settings);
        input = (EditText) findViewById(R.id.editText);

        settings.setOnClickListener((v) -> startActivity(
                new Intent(getApplicationContext(), MainSettingsActivity.class)));

        charsleft.setOnLongClickListener((v) -> {
                notif();
            int score = prefs.getInt(SCORE_ + name, 0);
            prefs.edit().putInt(SCORE_ + name, score - score / 10).apply();
            newRoundOrRegen(true);
            prefs.edit().putInt(LVL_ + name, (int) Math.round(Math.sqrt((prefs.getInt(SCORE_ + name, 0) * 1.1 + 1) /
                    (prefs.getInt(GAME_ + name, 0) + 1)))).apply();
                return true;
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

                patternError(game.valid(s.toString()));
                rematchUI();
                charsleft.setText(String.valueOf(
                        game.calcMax(task, getLvl()) - game.length(s.toString())));

                if (game.length(s.toString()) > 0 && game.pass(task, s.toString())) {

                    int games = prefs.getInt(GAME_ + name, 0) + 1,
                            score = Calc.calcScore(s.toString(), task, game, getLvl()),
                            lvl = Calc.calcDiff(prefs.getInt(SCORE_ + name, 0), score, games);

                    Toast.makeText(getApplication(),
                            getResources().getString(R.string.solved) + ' ' + s + " (+" + score + ")",
                            Toast.LENGTH_SHORT).show();

                    if (lvl > getLvl())
                        Toast.makeText(getApplication(), getResources().getString(R.string.lvlup) +
                                        ' ' + getLvl() + " -> " + lvl,
                            Toast.LENGTH_SHORT).show();

                    task.submit(s.toString());
                    s.clear();

                    prefs.edit()
                            .putInt(GAME_ + name, games)
                            .putInt(SCORE_ + name, prefs.getInt(SCORE_ + name, 0) + score)
                            .putInt(LVL_ + name, lvl).apply();
                    newRoundOrRegen(true);
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

    private void rematchUI() {
        if (right.getAdapter() != null)
            ((WordAdapter) right.getAdapter()).setPattern(input.getText().toString()).notifyDataSetChanged();
        if (wrong.getAdapter() != null)
            ((WordAdapter) wrong.getAdapter()).setPattern(input.getText().toString()).notifyDataSetChanged();
    }

    public void setupGame() {
        switch (name = prefs.getString(GAME_MODE, MATCH_MODE + RAND_MATCH)) {
            case MATCH_MODE + REDB_MATCH:
                game = new REDBGenerator(this);
                break;
            case MATCH_MODE + WORD_MATCH:
                game = new WordGenerator(this);
                break;
            default:
            case MATCH_MODE + RAND_MATCH:
                game = new RandomGenerator();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupGame();
        setCharm();
        rematchUI();
        newRoundOrRegen(false);
        input.setText(prefs.getString(INPUT_ + name, ""));
        input.setSelection(prefs.getInt(POSITION_S_ + name, 0),
                prefs.getInt(POSITION_E_ + name, 0));
    }

    protected void onPause() {
        super.onPause();
        prefs.edit().putString(INPUT_ + name, input.getText().toString()).apply();
        prefs.edit().putInt(POSITION_S_ + name, input.getSelectionStart()).apply();
        prefs.edit().putInt(POSITION_E_ + name, input.getSelectionEnd()).apply();
    }

    public int getLvl() {
        return prefs.getInt(LVL_ + name, 0);
    }

    public void newRoundOrRegen(boolean regen) {
        if (running) return;
        if (regen)
            new TaskManager(this).execute();
        else {
            String cache = prefs.getString(CACHE_ + name, null);
            if (task == null) newRoundOrRegen(true);
            new TaskManager(this).execute(cache);
        }
    }

    public void click(String c) {
        if (!running)
            input.getText().insert(input.getSelectionStart(), String.valueOf(c));
    }

    public void patternError(boolean b) {
        if (!running)
            charsleft.setTextColor(getResources().getColor(b ? R.color.white : R.color.red));
    }

    public void setCharm() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        boolean show = prefs.getBoolean(CHARM, true);
        linear.setVisibility(show ? View.VISIBLE : View.GONE);
        params.setMargins(0, 0, 0, (int) getResources().getDimension(
                show ? R.dimen.dstd : R.dimen.std));

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

    public Game getGame() {
        return game;
    }

    private class TaskManager extends AsyncTask<String, Void, Task> {

        GameActivity activity;

        public TaskManager(GameActivity activity) {
            super();
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            running = true;
            progress.setVisibility(View.VISIBLE);
            charsleft.setVisibility(View.GONE);
        }

        @Override
        protected Task doInBackground(String... args) {
            if (args.length > 0)
                return Task.parseTask(args[0]);
            return game.genTask(getLvl());
        }

        @Override
        protected void onPostExecute(Task t) {
            super.onPostExecute(task = t);
            if (task != null) {
                int max = game.calcMax(t, getLvl());
                right.setAdapter(new WordAdapter(activity, task.getRight(), true));
                wrong.setAdapter(new WordAdapter(activity, task.getWrong(), false));
                if (max > 0) {
                    charsleft.setText(String.valueOf(max));
                    input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(max)});
                } else {
                    charsleft.setText("∞");
                    input.setFilters(new InputFilter[]{});
                }
                progress.setVisibility(View.GONE);
                charsleft.setVisibility(View.VISIBLE);
                prefs.edit().putString(CACHE_ + name, task.toString()).apply();
            } else {
                Toast.makeText(getApplication(),
                        "An error occured",
                        Toast.LENGTH_LONG).show();
            }
            rematchUI();
            running = false;
        }
    }
}