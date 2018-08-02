package com.phikal.regex.models;

public abstract class Word {
    private Matches match;

    public Matches getMatch() {
        return match;
    }

    public void setMatch(Matches match) {
        this.match = match;
    }

    public abstract String getString();

    public enum Matches {FULL, HALF, NONE, ANTI_HALF, ANTI_FULL}
}
