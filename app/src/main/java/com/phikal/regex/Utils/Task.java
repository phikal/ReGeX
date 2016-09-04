package com.phikal.regex.Utils;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Task {

    public static final String
            GS = "\0x1D", // Group separator
            RS = "\0x1E", // Record separator
            US = "\0x1F"; // Unit separator
    private final List<Word> right, wrong;
    private final Submitter submitter;

    public Task(List<Word> right, List<Word> wrong, Submitter submitter) {
        this.right = right;
        this.wrong = wrong;
        this.submitter = submitter;
    }

    public static Task parseTask(String s) {
        String[] parts = s.split(GS, -1);
        if (parts.length == 2) return new Task(
                splitList(parts[0]),
                splitList(parts[1]),
                null);
        else return null;
    }

    public static String joinList(Collection<Word> l) {
        String r = "";
        for (Word w : l)
            r += w.toString() + RS;
        return r.substring(0, r.length() - 2);
    }

    public static List<Word> splitList(String s) {
        String[] l = s.split(RS, -1);
        List<Word> res = new ArrayList<>();
        for (String p : l)
            res.add(Word.parse(p));
        return res;
    }

    public List<Word> getRight() {
        return right;
    }

    public List<Word> getWrong() {
        return wrong;
    }

    public String toString() {
        return joinList(right) + GS + joinList(wrong);
    }

    public void submit(final String sol) {
        final Task self = this;
        if (submitter != null) new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                submitter.sumbit(self, sol);
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public interface Submitter {
        void sumbit(Task t, String sol);
    }

}