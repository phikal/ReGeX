package com.phikal.regex.Games.Match;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.phikal.regex.Models.Progress;

public class MatchProgress implements Progress {

    private static final String
            PREFIX = "match-",
            COUNT = "-count",
            PROGRESS = "-progress";
    private static final int MAX_TASKS = 12;
    private static final double A = 1, Q = 1/2;

    private double difficulty;
    private int n;

    public MatchProgress(Context ctx, String name) {
        SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(ctx);

        this.difficulty = pm.getFloat(PREFIX + name + PROGRESS, 0.1f);
        this.n = pm.getInt(PREFIX + name + COUNT, 1);
    }

    protected MatchProgress(Context ctx, String name, MatchProgress p) {
        this.difficulty = p.difficulty + A * Math.pow(Q, p.n);
        this.n = 1;
        PreferenceManager.getDefaultSharedPreferences(ctx).edit()
                .putFloat(PREFIX + name + PROGRESS, (float) difficulty)
                .putInt(PREFIX + name + COUNT, n)
                .apply();
    }

    @Override
    public int getMaxTasks() {
        return (int) (Math.pow(difficulty, 2) * MAX_TASKS);
    }

    @Override
    public double getDifficutly() {
        return difficulty;
    }

    public static void clear(Context ctx, String name) {
        PreferenceManager.getDefaultSharedPreferences(ctx).edit()
                .remove(PREFIX + name + PROGRESS)
                .remove(PREFIX + name + COUNT)
                .apply();
    }
}
