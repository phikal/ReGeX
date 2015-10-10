package com.phikal.regex.Utils;

import java.util.ArrayList;
import java.util.Random;

public class WordGenerator {

    public final static String d1 = "aeuioxyz",
            d2 = d1 + "bcdfghjklmnpqrstvw",
            d3 = d2 + "0123456789_",
            d4 = d3 + d2.toUpperCase();

    /* diff levels:
     *  1: aeiouxyz
     *  2: a-z
     *  3: a-z + 0-9 + _
     *  4: a-z + 0-9 + A-Z
     * +5: length & count
     */

    Random r = new Random();

    private char[] getRange(int diff) {
        if (diff < 3) return d1.toCharArray();
        if (diff < 9) return d2.toCharArray();
        if (diff < 12) return d3.toCharArray();
        else return d4.toCharArray();
    }

    private String genWord(int diff) {
        String s = "";
        char[] chars = getRange(diff);
        for (int i = ((int) Math.round(1.5 * Math.log(r.nextInt(diff + 1) + 1) + r.nextInt(1))); i >= 0; i--)
            s += chars[r.nextInt(chars.length)];
        return s;
    }

    public String nextWord(int diff) {
        String s;
        do {
            s = genWord(diff);
        } while (s.isEmpty());
        return s;
    }

    public int calcMax(ArrayList<String> right, ArrayList<String> wrong, int diff) {
        int clen_right = 0;
        for (String s : right) clen_right += s.length();
        return (int) Math.floor(clen_right + right.size() + diff / 5);
    }

}
