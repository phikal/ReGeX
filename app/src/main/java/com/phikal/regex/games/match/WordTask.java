package com.phikal.regex.games.match;

import android.content.Context;

import com.phikal.regex.games.Game;
import com.phikal.regex.models.Progress;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class WordTask extends SimpleMatchTask {

    static private List<String> words = null;
    static private int request = 0;

    public WordTask(Context ctx, Game g, Progress p, Progress.ProgressCallback pc) throws IOException {
        super(ctx, g, p, pc);
    }

    @Override
    String randString() throws IOException {
        if (words == null) {
            words = new ArrayList<>(8711); // current length
            BufferedReader bis = new BufferedReader(new InputStreamReader(new GZIPInputStream(
                    getContext().getAssets().open("words.gz"))));
            for (String line; (line = bis.readLine()) != null; )
                words.add(line);
            Collections.shuffle(words, rnd);
        }

        return words.get(request++);
    }
}
