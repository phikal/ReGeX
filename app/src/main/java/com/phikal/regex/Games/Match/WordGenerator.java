package com.phikal.regex.Games.Match;

import android.app.Activity;
import android.content.Context;

import com.phikal.regex.Games.TaskGenerationException;
import com.phikal.regex.Utils.Task;
import com.phikal.regex.Utils.Word;
import com.phikal.regex.Utils.WordList;

import java.util.ArrayList;
import java.util.List;

public class WordGenerator extends RandomGenerator {

    final Context ctx;
    final WordList wlist;

    public WordGenerator(final Activity activity) {
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
        int tma = ((int) Math.ceil(1.5 * Math.log10(2 + r.nextInt(lvl * lvl)))) + r.nextInt(2),
                dma = ((int) Math.ceil(1.25 * Math.log10(2 + r.nextInt(lvl * lvl)))) + r.nextInt(2),
                len = ((int) Math.ceil(Math.sqrt(lvl * 4 - 4) * Math.log(lvl + 10)));
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
