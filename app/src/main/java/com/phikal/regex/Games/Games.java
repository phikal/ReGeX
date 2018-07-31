package com.phikal.regex.Games;

import android.content.Context;

import com.phikal.regex.Games.Match.MutMatchGame;
import com.phikal.regex.Games.Match.SimpleMatchGame;
import com.phikal.regex.Games.Match.WordGame;
import com.phikal.regex.Models.Game;
import com.phikal.regex.Models.Progress;

import java.io.IOException;

public enum Games {

    SIMPLE_MATCH {
        public Game generate(Context ctx, Progress p) {
            return new SimpleMatchGame(ctx, p);
        }
    },
    MUTATE_MATCH {
        @Override
        public Game generate(Context ctx, Progress p) {
            return new MutMatchGame(ctx, p);
        }
    },
    WORD_MATCH {
        @Override
        public Game generate(Context ctx, Progress p) throws IOException {
            return new WordGame(ctx, p);
        }
    };

    public abstract Game generate(Context ctx, Progress p)
            throws IOException;
}
