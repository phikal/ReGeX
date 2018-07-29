package com.phikal.regex.Games.Match;

import android.content.Context;

import com.phikal.regex.Games.Game;
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
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public abstract class MatchGame implements Game {
    final Context ctx;
    final Progress progress;

    Collection<MatchWord> allWords;

    public MatchGame(Context ctx, Progress p) {
        this.ctx = ctx;
        this.progress = p;
    }

    protected abstract String getName();
    protected abstract Collection<MatchWord> genWords(boolean match);

    protected class MatchWord implements Word {
        public MatchCallback mn = null;
        private String word;

        public MatchWord(String word) {
            this.word = word;
        }

        @Override
        public void onMatch(MatchCallback mn) {
            assert mn != null;
            this.mn = mn;
        }
    }

    protected class MatchCollumn implements Collumn {
        Collection<MatchWord> words;
        boolean match;

        MatchCollumn(boolean match) {
            this.match = match;
        }

        @Override
        public String getHeader() {
            return ctx.getString(match ? R.string.match : R.string.dmactch);
        }

        @Override
        public Collection<Word> getWords() {
            List<Word> w = new LinkedList<>();
            w.addAll(words = genWords(match));
            return w;
        }
    }

    protected class MatchInput implements Input {
        @Override
        public Response giveInput(String str) {
            try {
                Pattern p = Pattern.compile(str);
                for (MatchWord w : allWords)
                    w.mn.match(p.matcher(w.word).matches() ?
                            Word.Matches.FULL :
                            Word.Matches.NONE);
                return Response.OK;
            } catch (PatternSyntaxException pse) {
                return Response.ERROR;
            }
        }

        @Override
        public int getLength(String str) {
            return str.length();
        }

        @Override
        public int getLimit() {
            return 0; // TODO
        }

        @Override
        public String getHint() {
            return getName();
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
}
