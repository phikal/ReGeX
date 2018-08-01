package com.phikal.regex.models;

import android.content.Context;

import java.io.Serializable;
import java.util.List;

public abstract class Task implements Serializable {
    private final Progress p;
    private final Progress.ProgressCallback pc;
    private final Context ctx;

    public Task(Context ctx, Progress p, Progress.ProgressCallback pc) {
        this.ctx = ctx;
        this.p = p;
        this.pc = pc;
    }

    public Progress.ProgressCallback getProgressCallback() {
        return pc;
    }

    public Progress getProgress() {
        return p;
    }

    public Context getContext() {
        return ctx;
    }

    public abstract List<Collumn> getCollumns();

    public abstract List<Input> getInputs();
}
