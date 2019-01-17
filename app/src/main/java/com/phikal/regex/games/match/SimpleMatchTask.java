package com.phikal.regex.games.match;

import android.content.Context;

import com.phikal.regex.games.Game;
import com.phikal.regex.models.Progress;
import com.phikal.regex.models.RegularExpression;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import static com.phikal.regex.Util.rnd;

class NoMoreWordsException extends RuntimeException {
}

public class SimpleMatchTask extends MatchTask implements Serializable {

    final RegularExpression re;
    private final List<MatchWord>
            toMatch = new LinkedList<>(),
            notToMatch = new LinkedList<>();

    SimpleMatchTask(Context ctx, Game g, Progress p, Progress.ProgressCallback pc) {
        super(ctx, g, p, pc);

        int i, max;
        re = RegularExpression.produceRE();
        max = (int) Math.sqrt(getProgress().getRound() + 1);

        try {
            i = 0;
            do toMatch.add(randWord(true));
            while (max * max < i++ / (rnd.nextDouble() * 3 / 4 + 1 / 4));
        } catch (NoMoreWordsException nmwe) {
            if (toMatch.size() == 0)
                throw new RuntimeException(nmwe);
        }

        try {
            i = 0;
            do notToMatch.add(randWord(false));
            while (max * max < i++ / (rnd.nextDouble() * 3 / 4 + 1 / 4));
        } catch (NoMoreWordsException nmwe) {
            if (notToMatch.size() == 0)
                throw new RuntimeException(nmwe);
        }
    }

    @Override
    protected synchronized List<MatchWord> genWords(boolean match) {
        return match ? toMatch : notToMatch;
    }

    protected MatchWord randWord(boolean match) {
        return new MatchWord(match ? re.produceWord() : re.produceOther(1.0), match);
    }
}
