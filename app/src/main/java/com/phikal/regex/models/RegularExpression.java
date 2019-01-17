package com.phikal.regex.models;

import java.util.Random;

class LiteralRE extends RegularExpression {
    private static char[] chars;

    static {
        chars = new char[
                'z' - 'a' + 1 +
                        'Z' - 'A' + 1 +
                        '0' - '9' + 1];

        int i = 0;
        for (char c = 'a'; c <= 'z'; c++) chars[i++] = c;
        for (char c = 'A'; c <= 'A'; c++) chars[i++] = c;
        for (char c = '0'; c <= '9'; c++) chars[i++] = c;
    }

    private char c;

    LiteralRE() {
        this.c = chooseChar();
    }

    @Override
    public String produceWord() {
        return toString();
    }

    @Override
    public String produceOther(double diff) {
        if (rnd.nextDouble() < diff) {
            return toString();
        } else {
            char c;
            do c = chooseChar();
            while (c != this.c);
            return Character.toString(c);
        }
    }

    @Override
    public String toString() {
        return Character.toString(c);
    }

    private char chooseChar() {
        return chars[rnd.nextInt(chars.length)];
    }
}

class EmptyRE extends RegularExpression {
    @Override
    public String produceWord() {
        return toString();
    }

    @Override
    public String produceOther(double diff) {
        if (rnd.nextDouble() < diff) {
            return toString();
        } else {
            return RegularExpression.produceRE(diff / 4).toString();
        }
    }

    @Override
    public String toString() {
        return "";
    }
}

class ConcatRE extends RegularExpression {
    private RegularExpression[] res;

    ConcatRE(RegularExpression... res) {
        this.res = res;
    }

    @Override
    public String produceWord() {
        StringBuilder b = new StringBuilder();

        for (RegularExpression re : res)
            b.append(re.produceWord());

        return b.toString();
    }

    @Override
    public String produceOther(double diff) {
        StringBuilder b = new StringBuilder();

        for (RegularExpression re : res) {
            if (diff < rnd.nextDouble()) {
                // generate other word or no word
                if (rnd.nextBoolean())
                    b.append(RegularExpression.produceRE(diff / 2));
            } else {
                // generate regular word
                b.append(re.produceWord());
            }
        }

        return b.toString();
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append('(');
        for (RegularExpression re : res)
            b.append(re);
        b.append(')');
        return b.toString();
    }
}

class AlterRE extends RegularExpression {
    private RegularExpression re1, re2;

    AlterRE(RegularExpression re1, RegularExpression re2) {
        this.re1 = re1;
        this.re2 = re2;
    }

    @Override
    public String produceWord() {
        if (rnd.nextBoolean()) {
            return re1.produceWord();
        } else {
            return re2.produceWord();
        }
    }

    @Override
    public String produceOther(double diff) {
        if (diff < rnd.nextDouble()) {
            return RegularExpression.produceRE(diff / 4).produceWord();
        } else {
            return produceWord();
        }
    }

    @Override
    public String toString() {
        return '(' + re1.toString() + '|' + re2.toString() + ')';
    }
}

class MultipleRE extends RegularExpression {
    private static final int MAX_REPET = 8;

    private RegularExpression re;

    MultipleRE(RegularExpression re) {
        this.re = re;
    }

    @Override
    public String produceWord() {
        StringBuilder b = new StringBuilder();
        int rep = rnd.nextInt(MAX_REPET + 2);

        for (int i = 2; i < rep; i++)
            b.append(re.produceWord());

        return b.toString();
    }

    @Override
    public String produceOther(double diff) {
        return null;
    }

    @Override
    public String toString() {
        return '(' + re.toString() + ')' + '*';
    }
}

abstract class RegularExpression {
    static Random rnd = new Random();

    static RegularExpression produceRE(double diff) {
        int opt = rnd.nextInt(4 + (int) Math.floor(4 * (1 - diff)));
        switch (opt < 0 ? 0 : opt) {
            case 0: {
                RegularExpression res[] = new RegularExpression[rnd.nextInt(5)];
                for (int i = 0; i < res.length; i++)
                    res[i] = produceRE(diff / 4);
                return new ConcatRE(res);
            }
            case 1:
                return new AlterRE(produceRE(diff / 2), produceRE(diff / 2));
            case 2:
                return new MultipleRE(produceRE(diff / 4));
            case 3:
                return new EmptyRE();
            default:
                return new LiteralRE();
        }
    }

    static RegularExpression produceRE() {
        return produceRE(1.25); // [0;3] - (4 * floor(1-1.25) => [0;2]
    }

    // diff -> 0.0: minimal, 1.0 maximal
    public abstract String produceWord();

    public abstract String produceOther(double diff);

    public abstract String toString();
}
