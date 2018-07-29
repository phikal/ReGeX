package com.phikal.regex.Games.Match;

import android.content.Context;

import com.phikal.regex.Models.Progress;
import com.phikal.regex.Models.Task;

import java.util.Collection;

public class MutMatchGame extends MatchGame {

    public MutMatchGame(Context ctx, Progress p) {
        super(ctx, p);
    }

    @Override
    protected String getName() {
        return null;
    }

    @Override
    public Task nextTask() {
        return super.nextTask();
    }

    @Override
    protected Collection<MatchWord> genWords(boolean match) {
        return null;
    }
}
