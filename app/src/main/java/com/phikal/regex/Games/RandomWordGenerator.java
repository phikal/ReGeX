package com.phikal.regex.Games;

import android.util.Log;

import com.phikal.regex.Utils.Word;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RandomWordGenerator extends RandomGenerator {
    List<String> words = new ArrayList<>();

    public RandomWordGenerator(InputStream w) throws IOException {
        BufferedReader reader = new BufferedReader(new BufferedReader(new InputStreamReader(w)));

        String line;
        while ((line = reader.readLine()) != null)
            words.add(line);
        reader.close();
    }

    public Word nextWord(int diff) {
        String s = words.get(r.nextInt(words.size()));
        Log.d("rwg", s);
        return new Word(s);
    }
}
