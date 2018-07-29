package com.phikal.regex.Games.Match;

import android.app.Activity;
import android.content.Context;

import com.phikal.regex.Games.TaskGenerationException;
import com.phikal.regex.Utils.Task;
import com.phikal.regex.Utils.Word;
import com.phikal.regex.Utils.WordList;

import java.util.ArrayList;
import java.util.List;

public class WordGame extends SimpleMatchGame {

    final Context ctx;
    final WordList wlist;

    public WordGame(final Activity activity) {
        ctx = activity;
        wlist = new WordList(activity);
    }

    @Override
    public int calcMax(Task t, int lvl) {
        return (int) Math.ceil(Math.sqrt(super.calcMax(t, lvl)) * 1.75);
    }

    @Override
    public Task genTask(int lvl) throws TaskGenerationException {
        lvl += 5;
        int tma = Calc.calcRWLCount(lvl, 1.75),
                dma = Calc.calcRWLCount(lvl, 1.25),
                len = Calc.calcRWLen(lvl);
        List<Word> words = new ArrayList<>(tma + dma);
        for (String s : wlist.rndWord(len, tma + dma))
            words.add(new Word(s.trim()));
        try {
            return new Task(words.subList(0, tma), words.subList(tma + 1, tma + dma), null);
        } catch (IndexOutOfBoundsException iae) {
            return genTask(lvl + 2); // retry with higher level
        }
    }
}
