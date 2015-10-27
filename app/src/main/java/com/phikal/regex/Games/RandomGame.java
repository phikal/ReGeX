package com.phikal.regex.Games;

import android.app.Activity;

import com.phikal.regex.R;
import com.phikal.regex.Utils.Task;
import com.phikal.regex.Utils.Word;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomGame extends Game {

    RandomGenerator generator;
    Random r = new SecureRandom();

    public RandomGame(Activity activity) {
        super(activity);
        this.generator = new RandomGenerator();
    }

    private List<Word> genWords(int diff, List compare) {
        List<Word> list = new ArrayList<>();
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

    public Task genTask(int diff) {
        List<Word> right = genWords(diff, null),
                wrong = genWords(diff, right);
        return new Task(right, wrong, calcMax(right, wrong, diff));
    }

    public void submit(Task task, String re) {
        // yeah ... whatever ... for now
    }

    @Override
    public String getName() {
        return activity.getString(R.string.random_game);
    }

    public String getError() {
        return activity.getString(R.string.random_error);
    }
}
