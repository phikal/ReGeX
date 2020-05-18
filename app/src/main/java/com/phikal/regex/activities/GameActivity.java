package com.phikal.regex.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.phikal.regex.R;
import com.phikal.regex.games.Game;
import com.phikal.regex.models.Column;
import com.phikal.regex.models.Task;
import com.phikal.regex.models.Word;

import java.util.ArrayList;
import java.util.List;

import static com.phikal.regex.Util.CHARS;
import static com.phikal.regex.Util.CHAR_BAR_ON;
import static com.phikal.regex.Util.COUNT;
import static com.phikal.regex.Util.CURRENT_INPUT;
import static com.phikal.regex.Util.CURRENT_TASK;
import static com.phikal.regex.Util.MODE;
import static com.phikal.regex.Util.PROGRESS;
import static com.phikal.regex.Util.notif;

class ColumAdapter extends ArrayAdapter {
    Context ctx;
    Column col;

    public ColumAdapter(@NonNull Context context, Column column) {
        super(context, 0);
        this.ctx = context;
        this.col = column;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView != null) {
            return convertView;
        }

        Word w = col.getWord(position);
        assert w != null;

        LayoutInflater inf = LayoutInflater.from(ctx);
        convertView = inf.inflate(R.layout.word_layout, parent);
        TextView text = convertView.findViewById(R.id.word_main);
        text.setText(w.getString());

        return convertView;
    }
}

public class GameActivity extends Activity {

    static boolean reload = false;

    SharedPreferences prefs;
    LinearLayout colums;
    EditText input;
    private Game game;
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // find or generate task
        game = Game.valueOf(prefs.getString(MODE, Game.DEFAULT_GAME.name()));

        try {
            if (savedInstanceState != null)
                task = (Task) savedInstanceState.getSerializable(CURRENT_TASK);
            if (task == null)
                task = game.nextTask(getApplicationContext(), p -> {
                    prefs.edit()
                            .putFloat(game.name() + PROGRESS, (float) p.getDifficutly())
                            .putInt(game.name() + COUNT, p.getRound())
                            .apply();
                    input.getEditableText().clear();
                    task = null;
                });
        } catch (ClassCastException cce) {
            new AlertDialog.Builder(getApplicationContext())
                    .setMessage(cce.getMessage())
                    .create();
            return;
        }

        assert task != null;
        assert task.getColumns() != null;
        assert task.getInput() != null;

        // find and setup views
        colums = findViewById(R.id.columns);
        RelativeLayout input_box = findViewById(R.id.input_box);
        Button status = input_box.findViewById(R.id.status);
        input = input_box.findViewById(R.id.input);
        ImageButton settings = input_box.findViewById(R.id.settings);
        LinearLayout charmb = findViewById(R.id.chars);

        colums.setWeightSum(task.getColumns().size());
        LayoutInflater inf = LayoutInflater.from(getApplicationContext());
        List<ArrayAdapter> adapters = new ArrayList<>(task.getColumns().size());
        for (Column col : task.getColumns()) {
            View v = inf.inflate(R.layout.column_layout, colums, false);

            TextView colName = v.findViewById(R.id.col_name);
            ListView colList = v.findViewById(R.id.col_list);

            colName.setText(col.getHeader());

            ColumAdapter ca = new ColumAdapter(getApplicationContext(), col);
            colList.setAdapter(ca);
            adapters.add(ca);

            colums.addView(v);
        }

        input.setHint(getString(game.name));
        input.addTextChangedListener(task.getInput());

        task.getInput().setStatusCallback((resp, msg) -> {
            int res;
            switch (resp) {
                case ERROR:
                    res = R.color.orange;
                    break;
                default:
                case OK:
                    res = R.color.text;
            }
            status.setTextColor(ContextCompat.getColor(this, res));
            status.setText(msg);
            for (ArrayAdapter aa : adapters)
                aa.notifyDataSetChanged();
        });

        settings.setOnClickListener($ -> {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        });
        settings.setOnLongClickListener($ -> {
            task = null;
            notif(this);
            recreate();
            return true;
        });

        for (String c : CHARS) {
            TextView v = new TextView(getApplicationContext());

            v.setText(c);
            v.setOnClickListener($ -> input.append(c));
            v.setHeight(getResources().getDimensionPixelSize(R.dimen.std));
            v.setWidth(getResources().getDimensionPixelSize(R.dimen.std));
            v.setGravity(Gravity.CENTER);

            charmb.addView(v);
        }

        input.setText(savedInstanceState == null ? "" :
                savedInstanceState.getString(CURRENT_INPUT, ""));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (reload) {
            reload = false;
            task = null;
            recreate();
        }

        boolean show = prefs.getBoolean(CHAR_BAR_ON, true);
        if (!show)
            findViewById(R.id.chars).setVisibility(View.GONE);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0,
                (int) getResources().getDimension(
                        show ? R.dimen.dstd : R.dimen.std));
        colums.setLayoutParams(params);

        input.requestFocus();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(CURRENT_TASK, task);
        if (input != null)
            outState.putString(CURRENT_INPUT, input.getText().toString());
        if (task != null)
            outState.putSerializable(CURRENT_TASK, task);
    }
}
