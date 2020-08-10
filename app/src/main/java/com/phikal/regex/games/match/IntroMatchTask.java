package com.phikal.regex.games.match;

import android.content.Context;

import com.phikal.regex.games.Game;
import com.phikal.regex.models.Progress;

import static com.phikal.regex.Util.rnd;

class Example {
    private final String[] match, other;
    private final String solution;
    private int mi = 0, oi = 0; // internal counters

    Example(String[] match, String[] other, String solution) {
        this.match = match;
        this.other = other;
        this.solution = solution;
    }

    String nextMatch() {
        if (mi >= match.length)
            throw new NoMoreWordsException();
        return match[mi++];
    }

    String nextOther() {
        if (oi >= other.length)
            throw new NoMoreWordsException();
        return match[oi++];
    }

    public String getSolution() {
        return solution;
    }
}

public class IntroMatchTask extends SimpleMatchTask {

    // example tasks, ordered by difficulty
    private static final Example[] examples = {
            // lesson: basic matching
            new Example(
                    new String[]{"a"},
                    new String[]{"b"},
                    "a"),
            // lesson: char classes, case difference
            new Example(
                    new String[]{"a", "b"},
                    new String[]{"A"},
                    "[ab]"),
            // lesson: question-mark operator
            new Example(
                    new String[]{"a", "ac", "abc"},
                    new String[]{"abc", "bc"},
                    "ab?c?"),
            // lesson: dot operator
            new Example(
                    new String[]{"ac", "ab"},
                    new String[]{"ca", "a"},
                    "a."),
            // lesson: asterisk operator
            new Example(
                    new String[]{"aaaa", "aaa", "aa", ""},
                    new String[]{"ab", "ba", "bbbb"},
                    "a*"),
            // lesson: char class, asterisk operator
            new Example(
                    new String[]{"acc", "bc", "bccc"},
                    new String[]{"abc", "Accc", "accb"},
                    "[ab]c+"),
            // lesson: or operator, groups
            new Example(
                    new String[]{"adbg", "aceg"},
                    new String[]{"acdg", "adeg"},
                    "a(db|ce)g"),
    };

    private final Example example;

    IntroMatchTask(Context ctx, Game g, Progress p, Progress.ProgressCallback pc) {
        super(ctx, g, p, pc);

        int pos = ((int) (rnd.nextInt(examples.length) * (g.getProgress(ctx).getDifficulty() * 3 / 4 + 1 / 4)) + 1) % examples.length;
        this.example = examples[pos];
    }

    @Override
    protected MatchWord randWord(boolean match) {
        if (match) return new MatchWord(example.nextMatch(), true);
        else return new MatchWord(example.nextOther(), false);
    }
}
