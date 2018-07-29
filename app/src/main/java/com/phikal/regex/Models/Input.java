package com.phikal.regex.Models;

public interface Input {
    enum Response { OK, ERROR }

    Response giveInput(String str);

    int getLength(String str);
    int getLimit();
    String getHint();
}
