package com.phikal.regex.Utils;

import com.phikal.regex.Games.Game;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Random mathematical nonsense that looked good in GNUPlot
 * feel free to fix anything.
 */

public final class Calc {

    final public static Random rand = new SecureRandom();

    /** Calculate the value of a expression. The more symbols it uses, the better */
    public static int calcVal(String regex) {
        int v = 1;
        for (char c : regex.toCharArray())
            switch (c) {
                case '\\':
                    v += 2;
                case '[':
                case '(':
                case '{':
                case '?':
                    v += 1;
                case '.':
                case '*':
                case '+':
                case '^':
                    v += 1;
            }
        return v;
    }

    /** Calculuate score of new solution, based on task, game and level */
    public static int calcScore(String regex, Task task, Game game, int lvl) {
        if (task == null || regex == null) return 0;
        int len = regex.length(),
                max = game.calcMax(task, lvl),
                right = task.getRight().size(),
                wrong = task.getWrong().size();
        return (int) Math.round((max < 0) ? (Math.pow(len, 2) / 4 + 1) : ((max - len) / 2 + 1) *
                (1 / ((Math.abs(right - wrong) + 1)) + 3 * calcVal(regex)));
    }

    /** Calculate difficult (level) based on score difference and ammount of games */
    public static int calcDiff(int score, int nscore, int games) {
        return (int) Math.round(1.2 * Math.sqrt((nscore + score * 1.1 + 1) / (games + 1)));
    }

    /**
     * Substring src according to the current level
     */
    public static char[] getRange(int lvl, String src) {
        double a = src.length() - 4, b = (src.length() + 5 - rand.nextInt(4)) / 2;
        int i = (int) Math.round(a - b * Math.pow((lvl + Math.pow(b / a, 2)), -0.5)) + 3;
        return src.substring(0, i).toCharArray();
    }

    /**
     * Calculate random word count, ie. how many words to generate for a random task
     */
    public static int calcRWCount(int lvl) {
        return (int) Math.ceil(Math.log10(1 + rand.nextInt(lvl * lvl + 1))) + rand.nextInt(1);
    }

    /**
     * Calculate length of randomly generated char sequence.
     */
    public static int calcRLen(int diff) {
        return (int) Math.round(1.5 * Math.log(rand.nextInt(diff + 1) + 1) + rand.nextInt(1));
    }

    /**
     * Calculate amount of words of one set for rand. word game, with a factor
     */
    public static int calcRWLCount(int lvl, double factor) {
        return ((int) Math.ceil(factor * Math.log10(2 + rand.nextInt(lvl * lvl)))) + rand.nextInt(2);
    }

    /**
     * Calculate maximal length of Word in rand. word game
     */
    public static int calcRWLen(int lvl) {
        return (int) Math.ceil(Math.sqrt(lvl * 4 - 4) * Math.log(lvl + 10));
    }
}
