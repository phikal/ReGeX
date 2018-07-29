package com.phikal.regex.Models;

public interface Word {
    enum Matches { FULL, HALF, NONE }

    interface MatchCallback {
        void match(Matches m);
    }

    void onMatch(MatchCallback mn);
}
