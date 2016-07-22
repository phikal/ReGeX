package com.phikal.regex.Games;

import android.app.Activity;

import com.phikal.regex.R;

import java.io.IOException;
import java.io.InputStream;

public class RandomWordGame extends RandomGame {

    public RandomWordGame(Activity activity, InputStream words) throws IOException {
        super(activity);
        this.generator = new RandomWordGenerator(words);
    }

    @Override
    public String getName() {
        return activity.getString(R.string.rword_game);
    }

}
