package com.phikal.regex.games.match;

import android.content.Context;

import com.phikal.regex.games.Games;
import com.phikal.regex.models.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class WordGame extends SimpleMatchGame {

    final private List<String> words;
    private int request;

    public WordGame(Context ctx, MatchProgress p) throws IOException {
        super(ctx, p);
        words = new ArrayList<>(8711); // current length
        BufferedReader bis = new BufferedReader(new InputStreamReader(new GZIPInputStream(
                ctx.getAssets().open("words.gz"))));
        for (String line; (line = bis.readLine()) != null; )
            words.add(line);
    }

    @Override
    public synchronized Task nextTask() {
        request = 0;
        Collections.shuffle(words, rnd);
        return super.nextTask();
    }

    @Override
    String randString() {
        return words.get(request++);
    }

    @Override
    public Games getGame() {
        return Games.WORD_MATCH;
    }
}
