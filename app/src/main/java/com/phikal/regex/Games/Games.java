package com.phikal.regex.Games;

import android.content.Context;

import com.phikal.regex.Games.Match.MatchProgress;
import com.phikal.regex.Games.Match.MutMatchGame;
import com.phikal.regex.Games.Match.SimpleMatchGame;
import com.phikal.regex.Games.Match.WordGame;
import com.phikal.regex.Models.Game;
import com.phikal.regex.Models.Progress;

import java.io.IOException;

public enum Games {

    SIMPLE_MATCH("simplematch") {
        public Game generate(Context ctx) {
            MatchProgress p = new MatchProgress(ctx, this.id);
            return new SimpleMatchGame(ctx, p);
        }
    },
    MUTATE_MATCH("mutmatch") {
        @Override
        public Game generate(Context ctx) {
            MatchProgress p = new MatchProgress(ctx, this.id);
            return new MutMatchGame(ctx, p);
        }
    },
    WORD_MATCH("wordmatch") {
        @Override
        public Game generate(Context ctx) throws IOException {
            MatchProgress p = new MatchProgress(ctx, this.id);
            return new WordGame(ctx, p);
        }
    };

    private final String id;
    Games(String id) { this.id = id; }
    public String getId() { return id; }
    public abstract Game generate(Context ctx)
            throws IOException;
}
