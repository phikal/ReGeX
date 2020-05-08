package com.phikal.regex.games.match;

import android.content.Context;

import com.phikal.regex.games.Game;
import com.phikal.regex.models.Progress;
import com.phikal.regex.models.RegularExpression;

import static com.phikal.regex.Util.rnd;

public class MutMatchTask extends SimpleMatchTask {

    private static final double GAMMA = (1 - Math.sqrt(5)) / 2 + 1;

    private String mutateOn = null;

    MutMatchTask(Context ctx, Game g, Progress p, Progress.ProgressCallback pc) {
        super(ctx, g, p, pc);
    }

    @Override
    protected MatchWord randWord(boolean match) {
        if (mutateOn == null) {
            mutateOn = re.produceWord();
        }
        char[] c = mutateOn.toCharArray();

        for (int i = 0; i < c.length; i++) {
            double chance = (getProgress().getDifficutly() * getProgress().getDifficutly()) * (1 - GAMMA) + GAMMA;
            if (rnd.nextDouble() > chance) {
                if (rnd.nextBoolean()) {
                    int j = rnd.nextInt(c.length);
                    char tmp = c[i];
                    c[i] = c[j];
                    c[j] = tmp;
                } else {
                    int k = rnd.nextInt(RegularExpression.chars.size());
                    c[i] = RegularExpression.chars.get(k);
                }
            }
        }

        return new MatchWord(String.valueOf(c), match);
    }
}
