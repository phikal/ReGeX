package com.phikal.regex.Games.Extract;

import android.util.Log;

import com.phikal.regex.Games.Match.SimpleMatchGame;
import com.phikal.regex.Games.TaskGenerationException;
import com.phikal.regex.Utils.Task;
import com.phikal.regex.Utils.Word;

import java.util.ArrayList;
import java.util.List;

public class SimpleMatchExtractGame extends SimpleMatchGame {

    protected List<Word> padWords(List<Word> words, int diff) {
        List<Word> nw = new ArrayList<>(words.size());
        for (Word w : words) {
            String a = genWord(diff, false).getWord(),
                    b = genWord(diff, false).getWord();
            Log.d("gen", a+" - "+b );
            nw.add(new Word(a, w.getWord(), b));

        }
        for (Word w : nw) Log.d("word", w.toString());
        return nw;
    }

    @Override
    public Task genTask(int lvl) throws TaskGenerationException {
        List<Word> toExtract = genWords((lvl + 2), null);
        List<Word> toCompare = padWords(toExtract, lvl);
        return new Task(toCompare, toExtract, null);
    }

    @Override
    public int check(Task t, Word w, boolean match, String sol) {
        int i;
        if (match) { // we're on the left-hand side = to compare
            i = t.getRight().indexOf(w);
        } else { // we're on the right-hand side =  to extract
            i = t.getWrong().indexOf(w);
        }

        return t.getRight().get(i).extracts(sol);
    }

    @Override
    public boolean pass(Task t, String sol) {
        for (Word w : t.getRight())
            if (w.extracts(sol) != 2)
                return false;
        return true;
    }

    @Override
    public int calcMax(Task t, int lvl) {
        int max = 0;
        for (Word w : t.getRight())
            max += w.getWord().length();
        return (int) Math.ceil(max + t.getRight().size()/2 + Math.sqrt(lvl));
    }
}
