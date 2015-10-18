package com.phikal.regex.Games;

import android.app.Activity;
import android.util.Log;

import com.phikal.regex.Activitys.GameActivity;
import com.phikal.regex.Utils.RandomGenerator;
import com.phikal.regex.Utils.Task;
import com.phikal.regex.Utils.Word;

import java.util.ArrayList;
import java.util.Random;

public class RandomGame extends Game {

    public final static String
            DIFF = GameActivity.DIFF,
            R_CA = "right",
            W_CA = "wrong";


    RandomGenerator generator;
    Random r = new Random();
    // using ASCII Record Separator as delimiter when saving
    private String split = "\0x1E";

    public RandomGame(Activity activity) {
        super(activity);
        this.generator = new RandomGenerator();
    }

    private ArrayList<Word> genWords(int diff, ArrayList compare) {
        ArrayList<Word> list = new ArrayList<>();
        if (compare == null) compare = new ArrayList();
        for (int i = ((int) Math.ceil(Math.log10(1 + r.nextInt(diff * diff + 1)))) + r.nextInt(1); i >= 0; i--) {
            Word word;
            do
                word = generator.nextWord(diff);
            while (list.contains(word) || compare.contains(word));
            list.add(word);
        }
        return list;
    }

    private String join(ArrayList<Word> list) {
        String s = "";
        for (Word w : list)
            s += w.toString() + split;
        return s;
    }

    private ArrayList<Word> parseWord(String str) {
        ArrayList<Word> words = new ArrayList<>();
        for (String s : str.split(split))
            words.add(Word.parse(s));
        return words;
    }

    public Task newTask(boolean force) {
        int diff = Math.abs(prefs.getInt(DIFF, 1));
        String r_ca, w_ca;
        ArrayList<Word> right, wrong;

        if (force && (r_ca = prefs.getString(R_CA, null)) != null)
            right = parseWord(r_ca);
        else prefs.edit().putString(R_CA, join(right = genWords(diff, null))).apply();

        if (force && (w_ca = prefs.getString(W_CA, null)) != null)
            wrong = parseWord(w_ca);
        else prefs.edit().putString(W_CA, join(wrong = genWords(diff, right))).apply();

        for (Word w : right) Log.d("word", w.toString());
        for (Word w : wrong) Log.d("word", w.toString());

        return new Task(right, wrong, generator.calcMax(right, wrong, diff));
    }

}
