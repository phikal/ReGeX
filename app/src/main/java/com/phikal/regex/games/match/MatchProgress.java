package com.phikal.regex.games.match;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.phikal.regex.models.Progress;

import static com.phikal.regex.Util.*;

public class MatchProgress implements Progress {

    private static final int MAX_TASKS = 12;
    private static final double A = 1, Q = 1/2;

    private String name;
    private Context ctx;
    private double difficulty;
    private int rounds;

    public MatchProgress(Context ctx, String name) {
        SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(ctx);

        this.difficulty = pm.getFloat(name + PROGRESS, 0.1f);
        this.rounds = pm.getInt(name + COUNT, 1);

        this.ctx = ctx;
        this.name = name;
    }

    MatchProgress(Context ctx, String name, MatchProgress p) {
        this.difficulty = p.difficulty + A * Math.pow(Q, p.rounds);
        this.rounds = 1;
        this.ctx = ctx;
        this.name = name;
    }

    @Override
    public int getMaxTasks() {
        return (int) (Math.pow(difficulty, 2) * MAX_TASKS);
    }

    @Override
    public double getDifficutly() {
        return difficulty;
    }

    @Override
    public int getRound() {
        return rounds;
    }

    public void clear() {
        PreferenceManager.getDefaultSharedPreferences(ctx).edit()
                .remove(name + PROGRESS)
                .remove(name + COUNT)
                .apply();
    }
}
