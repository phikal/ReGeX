package com.phikal.regex.Games;

import com.phikal.regex.Utils.Task;
import com.phikal.regex.Utils.Word;

public interface Game {
    Task genTask(int lvl) throws TaskGenerationException;

    int calcMax(Task t, int lvl);

    boolean pass(Task t, String pat);

    int check(Word w, boolean match, String pat);

    int length(String pat);

    boolean valid(String pat);
}
