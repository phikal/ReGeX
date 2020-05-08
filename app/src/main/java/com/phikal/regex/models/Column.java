package com.phikal.regex.models;

public interface Column {
    String getHeader();

    // List<? extends Word> getWords();
    Word getWord(int i);
}
