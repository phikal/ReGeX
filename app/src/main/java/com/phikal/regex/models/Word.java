package com.phikal.regex.models;

public interface Word {
    void onMatch(MatchCallback mn);

    String getString();

    enum Matches {FULL, HALF, NONE, ANTI_HALF, ANTI_FULL}

    interface MatchCallback {
        void match(Matches m);
    }
}
