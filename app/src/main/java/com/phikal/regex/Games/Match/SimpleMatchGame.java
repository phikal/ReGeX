package com.phikal.regex.Games.Match;

import android.content.Context;

import com.phikal.regex.Models.Progress;
import com.phikal.regex.Models.Task;
import com.phikal.regex.R;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

public class SimpleMatchGame extends MatchGame {

    final private Random rnd = new SecureRandom(); // because why not?
    final private int MAX_LENGTH = 12;
    final private char[] CHARS = "abcdefghijklmnopqrstuvxyz".toCharArray();

    private Collection<MatchWord>
            toMatch = null,
            notToMatch = null;

    private Set<String> words = null;

    public SimpleMatchGame(Context ctx, Progress p) {
        super(ctx, p);
    }

    @Override
    protected Collection<MatchWord> genWords(boolean match) {
        assert toMatch != null && notToMatch != null;
        return match ? toMatch : notToMatch;
    }

    @Override
    protected String getName() {
        return ctx.getString(R.string.simple_match);
    }

    @Override
    public Task nextTask() {
        toMatch = new LinkedList<>();
        notToMatch = new LinkedList<>();
        words = new HashSet<>();

        int max = progress.getMaxTasks();
        for (int i = 0; (i/max) * (i/max) < rnd.nextGaussian(); i++)
            toMatch.add(new MatchWord(randString()));
        for (int i = 0; (i/max) * (i/max) < rnd.nextGaussian(); i++)
            notToMatch.add(new MatchWord(randString()));

        // call parent
        return super.nextTask();
    }

    String randString() {
        assert words != null;

        int len = (int) (progress.getDifficutly() * MAX_LENGTH);
        int range = (int) ((CHARS.length - 1) * 2 * (progress.getDifficutly() * progress.getDifficutly() -1) / 3 + 1);

        for (;;) {
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < len; i++)
                b.append(CHARS[rnd.nextInt(range)]);
            if (words.contains(b.toString()))
                continue;
            words.add(b.toString());
            return b.toString();
        }
    }
}
