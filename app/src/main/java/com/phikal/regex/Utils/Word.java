package com.phikal.regex.Utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Word {

    // using ASCII Unit Separator as delimiter when saving
    private static String US = Task.US;
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
        String[] parts = s.split(US, -1);
        try {
            if (parts[0].isEmpty()) parts[0] = null;
            if (parts[2].isEmpty()) parts[2] = null;
            if (parts[1] == null) throw new ArrayIndexOutOfBoundsException();
            return new Word(parts[0], parts[1], parts[2]);
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            return new Word(s.replace(US, ""));
        }
    }

    public static Word fromJSON(JSONArray array) {
        try {
            if (array.length() == 1) return new Word(array.getString(0));
            else if (array.length() == 3)
                return new Word(array.getString(0), array.getString(1), array.getString(1));
        } catch (JSONException je) {
            je.printStackTrace();
        }
        return null;
    }

    public static JSONArray toJSON(Word word) {
        JSONArray array = new JSONArray();
        array.put(word.getAnte());
        array.put(word.getWord());
        array.put(word.getPost());
        return array;
    }

    public String getAnte() {
        return hasAnte() ? ante : "";
    }

    public boolean hasAnte() {
        return !(ante == null || !ante.isEmpty());
    }

    public String getPost() {
        return hasPost() ? post : "";
    }

    public boolean hasPost() {
        return !(post == null || !post.isEmpty());
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
        return getAnte() + US + getWord() + US + getPost();
    }

    public JSONArray toJSON() {
        return toJSON(this);
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

    @Override
    public boolean equals(Object o) {
        try {
            Word c = (Word) o;
            return c.getAnte().equals(getAnte()) &&
                    c.getWord().equals(getWord()) &&
                    c.getPost().equals(getPost());
        } catch (Exception e) {
            return false;
        }
    }
}
