package com.phikal.regex.Utils;

import java.util.ArrayList;

public class Task {

    private ArrayList<Word> right, wrong;
    private int max;

    public Task(ArrayList<Word> right, ArrayList<Word> wrong, int max) {
        this.right = right;
        this.wrong = wrong;
        this.max = max;
    }

    public ArrayList<Word> getRight() {
        return right;
    }

    public ArrayList<Word> getWrong() {
        return wrong;
    }

    public int getMax() {
        return max;
    }

}
