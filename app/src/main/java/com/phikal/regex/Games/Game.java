package com.phikal.regex.Games;

import com.phikal.regex.Utils.Task;

public interface Game {
    Task genTask(int lvl);

    int calcMax(Task t, int lvl);

    boolean pass(Task t, String sol);
}
