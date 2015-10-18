package com.phikal.regex.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Word {

    // using ASCII Unit Separator as delimiter when saving
    private static String delim = "\0x1F";
    private String word = null,
            ante = null,
            post = null,
            whole;

    public Word(String ante, String word, String post) {
        this.word = word;
        this.ante = ante;
        this.post = post;
        this.whole = (hasAnte() ? ante : "") + word + (hasPost() ? post : "");
    }

    public Word(String word) {
        this.whole = this.word = word;
    }

    public static Word parse(String s) {
        String[] parts = s.split(delim, -1);
        try {
            if (parts[0].isEmpty()) parts[0] = null;
            if (parts[2].isEmpty()) parts[2] = null;
            if (parts[1] == null) throw new ArrayIndexOutOfBoundsException();
            return new Word(parts[0], parts[1], parts[2]);
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            return new Word(s.replace(delim, ""));
        }
    }

    public String getAnte() {
        return hasAnte() ? ante : "";
    }

    public boolean hasAnte() {
        return ante != null;
    }

    public String getPost() {
        return hasPost() ? post : "";
    }

    public boolean hasPost() {
        return post != null;
    }

    public String getWord() {
        return word;
    }

    public int length() {
        int a, w, p;
        a = p = 0;
        w = word.length();
        if (hasAnte()) a = ante.length();
        if (hasPost()) p = post.length();
        return a + w + p;
    }

    @Override
    public String toString() {
        return getAnte() + delim + getWord() + delim + getPost();
    }

    // =matches= return value meaning
    // 0: dosen't match
    // 1: semi-match (not yet implemented)
    // 2: full match
    public int matches(String pattern) {
        Matcher m;
        try {
            m = Pattern.compile(pattern).matcher(whole);
            if (m.matches()) return 2;
        } catch (PatternSyntaxException pse) {
            // this comment is here, so Android Studio
            // doesn't complain about a empty catch block
        }
        return 0;
    }

}
