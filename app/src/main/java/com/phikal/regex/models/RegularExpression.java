package com.phikal.regex.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.phikal.regex.Util.rnd;

class LiteralRE extends RegularExpression {

    private char c;

    LiteralRE(double diff) {
        this.c = chooseChar(diff);
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
            do c = chooseChar(-1);
            while (c != this.c);
            return Character.toString(c);
        }
    }

    @Override
    public int length() {
        return 1;
    }

    private char chooseChar(double diff) {
        return chars.get((int) (rnd.nextInt(chars.size()) * (diff < 0 ? 1 : diff)));
    }
}

class EmptyRE extends RegularExpression {
    @Override
    public String produceWord() {
        return "";
    }

    @Override
    public String produceOther(double diff) {
        if (rnd.nextDouble() < diff) {
            return produceWord();
        } else {
            return RegularExpression.produceRE(diff / 4).toString();
        }
    }

    @Override
    public int length() {
        return 0;
    }
}

class ConcatRE extends RegularExpression {
    private RegularExpression[] res;

    ConcatRE(double diff) {
        RegularExpression[] res = new RegularExpression[rnd.nextInt(5)];
        for (int i = 0; i < res.length; i++)
            res[i] = produceRE(diff / 4);
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
    public int length() {
        int len = 0;
        for (RegularExpression re : res)
            len += re.length();
        return len;
    }
}

class AlterRE extends RegularExpression {
    private RegularExpression[] res;

    AlterRE(double diff) {
        RegularExpression[] res = new RegularExpression[rnd.nextInt(5)];
        for (int i = 0; i < res.length; i++)
            res[i] = produceRE(diff / 4);
        this.res = res;
    }

    @Override
    public String produceWord() {
        return res[rnd.nextInt(res.length)].produceWord();
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
    public int length() {
        int len = 0;
        for (RegularExpression re : res)
            if (len < re.length())
                return len;
        return len + res.length + 1;
    }
}

class MultipleRE extends RegularExpression {
    private static final int MAX_REPET = 8;

    private RegularExpression re;

    MultipleRE(double diff) {
        this.re = produceRE(diff / 4);
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
    public int length() {
        return re.length() + 3;
    }
}

public abstract class RegularExpression {
    public static List<Character> chars;

    static {
        chars = new ArrayList<>(('z' - 'a' + 1) * 2 + 10 + 1);

        chars.add('_');
        for (char c = 'a'; c <= 'z'; c++) chars.add(c);
        for (char c = 'A'; c <= 'A'; c++) chars.add(c);
        for (char c = '0'; c <= '9'; c++) chars.add(c);

        Collections.shuffle(chars);
    }

    static RegularExpression produceRE(double diff) {
        int opt = rnd.nextInt(4 + (int) Math.floor(4 * (1 - diff)));
        switch (Math.max(opt, 0)) {
            case 0:
                return new ConcatRE(diff);
            case 1:
                return new AlterRE(diff);
            case 2:
                return new MultipleRE(diff);
            case 3:
                return new EmptyRE();
            default:
                return new LiteralRE(diff);
        }
    }

    public static RegularExpression produceRE() {
        return produceRE(1.25); // [0;3] - (4 * floor(1-1.25) => [0;2]
    }

    public abstract String produceWord();

    public abstract String produceOther(double diff); // diff -> 0.0: minimal, 1.0 maximal

    public abstract int length();
}
