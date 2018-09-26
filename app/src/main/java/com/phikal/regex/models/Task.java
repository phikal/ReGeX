package com.phikal.regex.models;

import android.content.Context;

import com.phikal.regex.games.Game;

import java.io.Serializable;
import java.util.List;

public abstract class Task implements Serializable {
    private final Progress p;
    private final Game g;
    private final Progress.ProgressCallback pc;
    private transient final Context ctx;

    public Task(Context ctx, Game g, Progress p, Progress.ProgressCallback pc) {
        this.ctx = ctx;
        this.g = g;
        this.p = p;
        this.pc = pc;
    }

    public Progress.ProgressCallback getProgressCallback() {
        return pc;
    }

    public Progress getProgress() {
        return p;
    }

    public Game getGame() {
        return g;
    }

    public Context getContext() {
        return ctx;
    }

    public abstract List<Collumn> getCollumns();

    public abstract Input getInput();
}
