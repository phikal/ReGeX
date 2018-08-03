package com.phikal.regex.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import static com.phikal.regex.Util.COUNT;
import static com.phikal.regex.Util.PROGRESS;

public class Progress {

    private static final float Q = 0.01f;

    private String name;
    private transient Context ctx;
    private double difficulty;
    private int rounds;

    public Progress(Context ctx, String name) {
        SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(ctx);

        this.difficulty = pm.getFloat(name + PROGRESS, Q);
        this.rounds = pm.getInt(name + COUNT, 1);

        this.ctx = ctx;
        this.name = name;
        Log.d("progress", name + ": " + difficulty + "/" + rounds);
    }

    private Progress(String name, Context ctx, double difficulty, int rounds) {
        this.name = name;
        this.ctx = ctx;
        this.difficulty = difficulty;
        this.rounds = rounds;
    }

    public Progress next(double factor) {
        return new Progress(name, ctx,
                difficulty + factor * Q * Math.pow(1 / (Q + 1), rounds),
                rounds + 1);
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
