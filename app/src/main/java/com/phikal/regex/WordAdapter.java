package com.phikal.regex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.regex.PatternSyntaxException;


public class WordAdapter extends ArrayAdapter<String> {

    boolean right;
    String pattern = "";
    GameActivity game;

    public WordAdapter(Context context, ArrayList<String> words, boolean right) {
        super(context, 0, words);
        this.right = right;
        game = (GameActivity) context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_text, parent, false);
        }

        ((TextView) convertView).setText(getItem(position));
        try {
            if (!pattern.isEmpty() && ((TextView) convertView).getText().toString().matches(pattern))
                convertView.setBackgroundColor(getContext().getResources().getColor(right ? R.color.green : R.color.red));
            else
                convertView.setBackgroundColor(getContext().getResources().getColor(R.color.dark_comment));
            game.patternError(false);
        } catch (PatternSyntaxException pse) {
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.dark_comment));
            game.patternError(true);
        }

        return convertView;
    }

    public boolean pass() {
        try {
            for (int i = 0; i < getCount(); i++)
                if (right != getItem(i).matches(pattern)) return false;
        } catch (PatternSyntaxException pse) {
            return false;
        }
        return true;
    }

    public WordAdapter setPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

}
