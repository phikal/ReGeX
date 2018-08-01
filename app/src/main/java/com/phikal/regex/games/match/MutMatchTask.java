package com.phikal.regex.games.match;

import android.content.Context;

import com.phikal.regex.games.Game;
import com.phikal.regex.models.Progress;

import java.io.IOException;

public class MutMatchTask extends SimpleMatchTask {

    private static final double GAMMA = (1 - Math.sqrt(5)) / 2 + 1;

    private String mutateOn = null;

    public MutMatchTask(Context ctx, Game g, Progress p, Progress.ProgressCallback pc) throws IOException {
        super(ctx, g, p, pc);
    }

    @Override
    String randString() throws IOException {
        if (mutateOn == null) {
            mutateOn = super.randString();
        }
        char[] c = mutateOn.toCharArray();

        for (int i = 0; i < c.length; i++) {
            double chance = (getProgress().getDifficutly() * getProgress().getDifficutly()) * (1 - GAMMA) + GAMMA;
            if (rnd.nextDouble() > chance)
                c[i] = CHARS[rnd.nextInt(CHARS.length)];
        }

        return String.valueOf(c);
    }
}
