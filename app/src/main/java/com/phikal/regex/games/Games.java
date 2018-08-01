package com.phikal.regex.games;

import android.content.Context;

import com.phikal.regex.R;
import com.phikal.regex.games.match.MatchProgress;
import com.phikal.regex.games.match.MutMatchGame;
import com.phikal.regex.games.match.SimpleMatchGame;
import com.phikal.regex.games.match.WordGame;
import com.phikal.regex.models.Game;
import com.phikal.regex.models.Progress;

import java.io.IOException;

public enum Games {

    SIMPLE_MATCH("simplematch") {
        public Game getGame(Context ctx) {
            return new SimpleMatchGame(ctx, (MatchProgress) getProgress(ctx));
        }

        @Override
        public Progress getProgress(Context ctx) {
            return new MatchProgress(ctx, this.id);
        }

        @Override
        public int getName() {
            return R.string.simple_match;
        }
    },
    MUTATE_MATCH("mutmatch") {
        @Override
        public Game getGame(Context ctx) {
            return new MutMatchGame(ctx, (MatchProgress) getProgress(ctx));
        }

        @Override
        public Progress getProgress(Context ctx) {
            return new MatchProgress(ctx, this.id);
        }

        @Override
        public int getName() {
            return R.string.mutate_match;
        }
    },
    WORD_MATCH("wordmatch") {
        @Override
        public Game getGame(Context ctx) throws IOException {
            return new WordGame(ctx, (MatchProgress) getProgress(ctx));
        }

        @Override
        public Progress getProgress(Context ctx) {
            return new MatchProgress(ctx, this.id);
        }

        @Override
        public int getName() {
            return R.string.word_match;
        }
    };

    protected final String id;

    Games(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public abstract Game getGame(Context ctx)
            throws IOException;

    public abstract Progress getProgress(Context ctx);

    public abstract int getName();
}
