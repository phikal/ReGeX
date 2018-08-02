package com.phikal.regex.games.match;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;

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
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public abstract class MatchTask extends Task {

    private Collection<MatchWord> allWords = new ArrayList<>();
    MatchInput input;
    List<Collumn> collumns;

    MatchTask(Context ctx, Game g, Progress p, Progress.ProgressCallback pc) {
        super(ctx, g, p, pc);
        this.input = new MatchInput(getContext());
        this.collumns = Arrays.asList(
                new MatchCollumn(getContext(), true),
                new MatchCollumn(getContext(), false)
        );
    }

    protected abstract List<MatchWord> genWords(boolean match);

    @Override
    public List<Collumn> getCollumns() {
        return collumns;
    }

    @Override
    public Input getInput() {
        return input;
    }

    protected class MatchWord extends Word {
        private final boolean match;
        private final String word;

        MatchWord(@NonNull String word,
                  boolean match) {
            this.word = word;
            this.match = match;
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

    protected class MatchInput extends Input {
        Context ctx;

        MatchInput(Context ctx) {
            this.ctx = ctx;
        }

        public void afterTextChanged(Editable pat) {

            Input.Response res = Input.Response.OK;

            int maxLength = (int) ((0.8 * Math.pow(getProgress().getDifficutly(), 1.5) + 0.2) * 24);
            int charsLeft = maxLength - pat.length();

            pat.setFilters(new InputFilter[]{
                    new InputFilter.LengthFilter(maxLength)
            });

            try {
                Pattern p = Pattern.compile(pat.toString());

                boolean allMatch = true, match;
                for (MatchWord w : allWords) {
                    match = p.matcher(w.word).matches();
                    allMatch &= match ^ !w.match;
                    w.setMatch(match ? (w.match ? Word.Matches.FULL : Word.Matches.ANTI_FULL) :
                            Word.Matches.NONE);
                }

                if (allMatch) {
                    getProgressCallback().progress(new Progress(ctx,
                            getGame().name(), getProgress()));
                }
            } catch (PatternSyntaxException pse) {
                for (MatchWord w : allWords)
                    w.setMatch(Word.Matches.NONE);
                res = Input.Response.ERROR;
            }

            updateStatus(res, String.valueOf(charsLeft));
        }
    }
}
