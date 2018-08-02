package com.phikal.regex.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static com.phikal.regex.Util.COUNT;
import static com.phikal.regex.Util.PROGRESS;

public class Progress {

    private static final int MAX_TASKS = 12;
    private static final double A = 1, Q = 1 / 2;

    private String name;
    private Context ctx;
    private double difficulty;
    private int rounds;

    public Progress(Context ctx, String name) {
        SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(ctx);

        this.difficulty = pm.getFloat(name + PROGRESS, 0f);
        this.rounds = pm.getInt(name + COUNT, 1);

        this.ctx = ctx;
        this.name = name;
    }

    public Progress(Context ctx, String name, Progress p) {
        this.difficulty = p.getDifficutly() + A * Math.pow(Q, p.getRound());
        this.rounds = 1;
        this.ctx = ctx;
        this.name = name;
    }

    public int getMaxTasks() {
        return (int) (Math.pow(difficulty, 2) * MAX_TASKS);
    }

    public double getDifficutly() {
        return difficulty;
    }

    public int getRound() {
        return rounds;
    }

    public void clear() {
        PreferenceManager.getDefaultSharedPreferences(ctx).edit()
                .remove(name + PROGRESS)
                .remove(name + COUNT)
                .apply();
    }

    public interface ProgressCallback {
        void progress(Progress p);
    }
}
