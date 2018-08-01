package com.phikal.regex.games.match;

import android.content.Context;
import android.support.annotation.NonNull;

import com.phikal.regex.R;
import com.phikal.regex.games.Game;
import com.phikal.regex.models.Collumn;
import com.phikal.regex.models.Input;
import com.phikal.regex.models.Progress;
import com.phikal.regex.models.Task;
import com.phikal.regex.models.Word;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public abstract class MatchTask extends Task {

    private Collection<MatchWord> allWords = new ArrayList<>();

    MatchTask(Context ctx, Progress p, Progress.ProgressCallback pc) {
        super(ctx, p, pc);
    }

    protected abstract List<MatchWord> genWords(boolean match);

    @Override
    public List<Collumn> getCollumns() {
        return Arrays.asList(
                new MatchCollumn(getContext(), true),
                new MatchCollumn(getContext(), false)
        );
    }

    @Override
    public List<Input> getInputs() {
        return Collections.singletonList(
                new MatchInput(getContext())
        );
    }

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
        private Context ctx;
        private List<MatchWord> words = null;
        private boolean match;

        MatchCollumn(Context ctx, boolean match) {
            this.ctx = ctx;
            this.match = match;
        }

        @Override
        public String getHeader() {
            return ctx.getString(match ? R.string.match : R.string.dmactch);
        }

        @Override
        public List<? extends Word> getWords() {
            if (words == null) {
                words = genWords(match);
                allWords.addAll(words);
            }
            return words;
        }
    }

    protected class MatchInput implements Input {

        Context ctx;
        StatusCallback sc = null;

        public MatchInput(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public void setText(String pat) {
            assert sc != null;

            int charsLeft = (int) ((0.8 * Math.pow(getProgress().getDifficutly(), 1.5) + 0.2) * 24)
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
                    Game g = Game.getGame(MatchTask.this.getClass());
                    assert g != null;
                    getProgressCallback().progress(
                            new Progress(ctx, g.name(), getProgress()));
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
}
