package com.phikal.regex.games.match;

import android.content.Context;

import com.phikal.regex.games.Game;
import com.phikal.regex.models.Progress;
import com.phikal.regex.models.RegularExpression;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import static com.phikal.regex.Util.rnd;

public class SimpleMatchTask extends MatchTask implements Serializable {

    private RegularExpression re;
    private List<MatchWord>
            toMatch = new LinkedList<>(),
            notToMatch = new LinkedList<>();

    SimpleMatchTask(Context ctx, Game g, Progress p, Progress.ProgressCallback pc) {
        super(ctx, g, p, pc);
        int i;

        re = RegularExpression.produceRE();
        int max = (int) Math.sqrt(getProgress().getRound() + 1);

        i = 0;
        do {
            toMatch.add(randWord(true));
            i++;
        } while ((i / max) * (i / max) < rnd.nextDouble());

        i = 0;
        do {
            notToMatch.add(randWord(false));
            i++;
        } while ((i / max) * (i / max) < rnd.nextDouble());
    }

    @Override
    protected synchronized List<MatchWord> genWords(boolean match) {
        return match ? toMatch : notToMatch;
    }

    protected MatchWord randWord(boolean match) {
        return new MatchWord(match ? re.produceWord() : re.produceOther(1.0), match);
    }
}
