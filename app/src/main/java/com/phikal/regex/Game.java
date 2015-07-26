package com.phikal.regex;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Game {

    public final static String
            DIFF = GameActivity.DIFF,
            R_CA = "right",
            W_CA = "wrong";

    Activity activity;
    SharedPreferences prefs;
    WordGenerator generator;
    Random r = new Random();
    String split = ";";

    public Game(Activity activity) {
        this.activity = activity;
        prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        this.generator = new WordGenerator();
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
        Log.d("calc", String.valueOf(((max - len )/2+1) * (1 / ((Math.abs(right - wrong) + 1)) + 3 * calcVal(regex))));
        return Math.round(((max - len)/2+1) * (1 / ((Math.abs(right - wrong) + 1)) + 3 * calcVal(regex)));
    }

    private ArrayList<String> genWords(int diff, ArrayList compare) {
        ArrayList<String> list = new ArrayList<>();
        if (compare == null) compare = new ArrayList();
        for (int i = ((int) Math.ceil(Math.log10(1 + r.nextInt(diff * diff + 1)))) + r.nextInt(1); i >= 0; i--) {
            String word;
            do
                word = generator.nextWord(diff);
            while (list.contains(word) || compare.contains(word));
            list.add(word);
        }
        return list;
    }

    private String join(ArrayList<String> list) {
        String s = "";
        for (String i : list)
            s += i + split;
        return s.replaceAll(split + "$", "");
    }

    public Task newTask(boolean force) {
        int diff = Math.abs(prefs.getInt(DIFF, 1));
        String r_ca, w_ca;
        ArrayList<String> right, wrong;

        if (force && (r_ca = prefs.getString(R_CA, null)) != null)
            right = new ArrayList<>(Arrays.asList(r_ca.split(split)));
        else prefs.edit().putString(R_CA, join(right = genWords(diff, null))).apply();

        if (force && (w_ca = prefs.getString(W_CA, null)) != null)
            wrong = new ArrayList<>(Arrays.asList(w_ca.split(split)));
        else prefs.edit().putString(W_CA, join(wrong = genWords(diff, right))).apply();

        return new Task(right, wrong, generator.calcMax(right, wrong, diff));
    }

    public static class Task {

        private ArrayList<String> right, wrong;
        private int max;

        public Task(ArrayList<String> right, ArrayList<String> wrong, int max) {
            this.right = right;
            this.wrong = wrong;
            this.max = max;
        }

        public ArrayList<String> getRight() {
            return right;
        }

        public ArrayList<String> getWrong() {
            return wrong;
        }

        public int getMax() {
            return max;
        }
    }


}
