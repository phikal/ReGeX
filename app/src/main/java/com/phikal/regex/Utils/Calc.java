package com.phikal.regex.Utils;

import com.phikal.regex.Games.Game;

public final class Calc {

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

    public static int calcScore(String regex, Task task, Game game, int lvl) {
        if (task == null || regex == null) return 0;
        int len = regex.length(),
                max = game.calcMax(task, lvl),
                right = task.getRight().size(),
                wrong = task.getWrong().size();
        return (int) Math.round((max < 0) ? (Math.pow(len, 2) / 4 + 1) : ((max - len) / 2 + 1) *
                (1 / ((Math.abs(right - wrong) + 1)) + 3 * calcVal(regex)));
    }

    public static int calcDiff(int score, int nscore, int games) {
        return (int) Math.round(1.2 * Math.sqrt((nscore + score * 1.1 + 1) / (games + 1)));
    }
}
