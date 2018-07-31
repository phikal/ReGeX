package com.phikal.regex.Games.Match;

import android.content.Context;
import android.support.annotation.NonNull;

import com.phikal.regex.Games.Games;
import com.phikal.regex.Models.Game;
import com.phikal.regex.Models.Collumn;
import com.phikal.regex.Models.Input;
import com.phikal.regex.Models.Progress;
import com.phikal.regex.Models.Task;
import com.phikal.regex.Models.Word;
import com.phikal.regex.R;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public abstract class MatchGame implements Game {
    final Context ctx;

    MatchProgress progress;
    private ProgressCallback pc = x -> {};

    Collection<MatchWord> allWords;

    MatchGame(Context ctx, MatchProgress p) {
        this.ctx = ctx;
        this.progress = p;
    }

    protected abstract String getName();
    protected abstract List<MatchWord> genWords(boolean match);
    public abstract Games getGame();

    protected class MatchWord implements Word {
        private final boolean match;
        private final String word;

        public MatchCallback mn = null;

        MatchWord(@NonNull String word,
                  boolean match) {
            this.word = word;
            this.match = match;
        }

        @Override
        public void onMatch(@NonNull MatchCallback mn) {
            this.mn = mn;
        }

        @Override
        public String getString() {
            return word;
        }
    }

    protected class MatchCollumn implements Collumn {
        private List<MatchWord> words = null;
        private boolean match;

        MatchCollumn(boolean match) {
            this.match = match;
        }

        @Override
        public String getHeader() {
            return ctx.getString(match ? R.string.match : R.string.dmactch);
        }

        @Override
        public List<? extends Word> getWords() {
            return words != null ? words : (words = genWords(match));
        }
    }

    protected class MatchInput implements Input {

        StatusCallback sc = null;

        @Override
        public void setText(String pat) {
            assert sc != null;

            int charsLeft = (int) ((0.8 * Math.pow(progress.getDifficutly(), 1.5) + 0.2) * 24)
                    - pat.length();

            try {
                Pattern p = Pattern.compile(pat);

                boolean allMatch = true;
                for (MatchWord w : allWords) {
                    allMatch &= p.matcher(w.word).matches() ^ w.match;
                    w.mn.match(p.matcher(w.word).matches() ?
                            Word.Matches.FULL :
                            Word.Matches.NONE);
                }

                sc.status(Response.OK,
                        charsLeft <= 0,
                        String.valueOf(charsLeft));

                if (allMatch) {
                    progress = new MatchProgress(
                            ctx,
                            getGame().getId(),
                            progress);
                    pc.progress(progress);
                }
            } catch (PatternSyntaxException pse) {
                sc.status(Response.ERROR,
                        charsLeft <= 0,
                        String.valueOf(charsLeft));
            }
        }

        @Override
        public void onEdit(StatusCallback sc) {
            this.sc = sc;
        }
    }

    @Override
    public Task nextTask() {
        allWords = new HashSet<>();
        return new Task() {
            @Override
            public List<Collumn> getCollumns() {
                return Arrays.asList(
                    new MatchCollumn(true),
                    new MatchCollumn(false)
                );
            }

            @Override
            public List<Input> getInputs() {
                return Collections.singletonList(
                    new MatchInput()
                );
            }
        };
    }

    @Override
    public void onProgress(@NonNull ProgressCallback pc) {
        this.pc = pc;
    }
}
