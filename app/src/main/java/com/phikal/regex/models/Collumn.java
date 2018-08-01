package com.phikal.regex.models;

import java.util.List;

public interface Collumn {
    String getHeader();

    List<? extends Word> getWords();
}
