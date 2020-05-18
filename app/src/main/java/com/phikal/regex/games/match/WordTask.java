package com.phikal.regex.games.match;

import android.content.Context;

import com.phikal.regex.R;
import com.phikal.regex.games.Game;
import com.phikal.regex.models.Progress;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static com.phikal.regex.Util.rnd;

public class WordTask extends SimpleMatchTask {

    static private List<String> words = null;
    static private int request = 0;

    WordTask(Context ctx, Game g, Progress p, Progress.ProgressCallback pc) {
        super(ctx, g, p, pc);

        try {
            words = new ArrayList<>(8711); // current length
            BufferedReader bis = new BufferedReader(new InputStreamReader(new GZIPInputStream(
                    ctx.getResources().openRawResource(R.raw.words))));
            for (String line; (line = bis.readLine()) != null; )
                words.add(line);
            Collections.shuffle(words, rnd);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new RuntimeException(ioe.getMessage());
        }
    }

    @Override
    protected MatchWord randWord(boolean match) {
        return new MatchWord(words.get(request++), match);
    }
}
