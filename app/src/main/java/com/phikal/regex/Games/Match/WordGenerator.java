package com.phikal.regex.Games.Match;

import android.app.Activity;
import android.content.Context;

import com.phikal.regex.Games.TaskGenerationException;
import com.phikal.regex.Utils.Task;
import com.phikal.regex.Utils.WordList;

public class WordGenerator extends RandomGenerator {

    Context ctx;
    WordList wlist;

    public WordGenerator(final Activity activity) {
        ctx = activity;
        wlist = new WordList(activity);
    }

    protected String tryWord(int lvl) {
        return wlist.rndWord((int) Math.ceil(Math.sqrt(lvl * 4 - 4) * Math.log(lvl + 10)));
    }

    @Override
    public Task genTask(int lvl) throws TaskGenerationException {
        return super.genTask(lvl);
    }
}
