package com.phikal.regex.games.match;

import android.content.Context;
import android.util.Log;

import com.phikal.regex.games.Games;
import com.phikal.regex.models.Task;
import com.phikal.regex.R;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class SimpleMatchGame extends MatchGame {

    final static Random rnd = new SecureRandom(); // because why not?
    final static int MAX_LENGTH = 12;
    final static Character[] CHARS;

    static {
        List<Character> chars = new ArrayList<>();

        chars.add('_');
        for (char c = 'a'; c <= 'z'; c++) chars.add(c);
        for (char c = 'A'; c <= 'A'; c++) chars.add(c);
        for (char c = '0'; c <= '9'; c++) chars.add(c);

        Collections.shuffle(chars);
        CHARS = new Character[chars.size()];
        chars.toArray(CHARS);
    }

    private List<MatchWord>
            toMatch = null,
            notToMatch = null;

    private Set<String> words = null;

    public SimpleMatchGame(Context ctx, MatchProgress p) {
        super(ctx, p);
    }

    @Override
    protected synchronized List<MatchWord> genWords(boolean match) {
        assert toMatch != null && notToMatch != null;
        return match ? toMatch : notToMatch;
    }

    @Override
    protected String getName() {
        return ctx.getString(R.string.simple_match);
    }

    @Override
    public synchronized Task nextTask() {
        toMatch = new LinkedList<>();
        notToMatch = new LinkedList<>();
        words = new HashSet<>();

        int max = progress.getMaxTasks() + 1;
        for (int i = 0; (i/max) * (i/max) < rnd.nextGaussian(); i++)
            toMatch.add(new MatchWord(randString(), true));
        for (int i = 0; (i/max) * (i/max) < rnd.nextGaussian(); i++)
            notToMatch.add(new MatchWord(randString(), false));

        // call parent
        return super.nextTask();
    }

    String randString() {
        assert words != null;

        int len = (int) (progress.getDifficutly() * MAX_LENGTH);
        int range = (int) ((CHARS.length - 1) * 2 * (1 - progress.getDifficutly() * progress.getDifficutly()) / 3 + 1);

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

    @Override
    public Games getGame() {
        return Games.SIMPLE_MATCH;
    }
}
