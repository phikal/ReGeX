package com.phikal.regex.Games.Match;

import android.app.Activity;

import com.phikal.regex.Utils.WordList;

public class WordGenerator extends RandomGenerator {
    WordList wlist;

    public WordGenerator(final Activity activity) {
        wlist = new WordList(activity);
    }

    protected String tryWord(int lvl) {
        return wlist.rndWord((int) Math.ceil(Math.sqrt(lvl * 4 - 4) * Math.log(lvl + 10)));
    }
}
