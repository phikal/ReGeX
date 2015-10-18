package com.phikal.regex.Games;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.phikal.regex.Utils.Task;

abstract public class Game {

    Activity activity;
    SharedPreferences prefs;

    public Game(Activity activity) {
        this.activity = activity;
        prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    private static int calcVal(String regex) {
        int v = 1;
        for (char c : regex.toCharArray())
            switch (c) {
                case '.':
                case '*':
                case '+':
                case '^':
                    v += 1;
                    break;
                case '[':
                case '(':
                case '{':
                case '?':
                    v += 2;
                    break;
                case '\\':
                    v += 4;
                    break;
            }
        return v;
    }

    public static int calcScore(String regex, int len, int max, int right, int wrong) {
        Log.d("calc", String.valueOf(((max - len) / 2 + 1) * (1 / ((Math.abs(right - wrong) + 1)) + 3 * calcVal(regex))));
        return Math.round(((max - len) / 2 + 1) * (1 / ((Math.abs(right - wrong) + 1)) + 3 * calcVal(regex)));
    }

    abstract public Task newTask(boolean force);

}
