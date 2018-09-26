package com.phikal.regex.games.match;

import android.content.Context;

import com.phikal.regex.games.Game;
import com.phikal.regex.models.Progress;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.phikal.regex.Util.*;

public class SimpleMatchTask extends MatchTask implements Serializable {

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
            toMatch = new LinkedList<>(),
            notToMatch = new LinkedList<>();

    private transient Set<String> words = new HashSet<>();

    SimpleMatchTask(Context ctx, Game g, Progress p, Progress.ProgressCallback pc) {
        super(ctx, g, p, pc);

        int max = (int) (Math.pow(getProgress().getDifficutly(),3)+
                Math.sqrt(getProgress().getRound()+1));
        int i = 0;
        do {
            toMatch.add(new MatchWord(randString(), true));
            i++;
        } while ((i / max) * (i / max) < rnd.nextDouble());

        i = 0;
        do {
            notToMatch.add(new MatchWord(randString(), false));
            i++;
        } while ((i / max) * (i / max) < rnd.nextDouble());
    }

    @Override
    protected synchronized List<MatchWord> genWords(boolean match) {
        assert toMatch != null && notToMatch != null;
        return match ? toMatch : notToMatch;
    }

    String randString() {
        assert words != null;

        int len = (int) (getProgress().getDifficutly() * MAX_LENGTH);
        int range = (int) ((CHARS.length - 1) * 2 * (1 - getProgress().getDifficutly() * getProgress().getDifficutly()) / 3 + 1);

        for (; ; ) {
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < len * rnd.nextDouble() + 1; i++)
                b.append(CHARS[rnd.nextInt(range)]);
            if (words.contains(b.toString()))
                continue;
            words.add(b.toString());
            return b.toString();
        }
    }
}
