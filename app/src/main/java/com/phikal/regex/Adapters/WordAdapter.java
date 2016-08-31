package com.phikal.regex.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.phikal.regex.Activities.GameActivity;
import com.phikal.regex.R;
import com.phikal.regex.Utils.Word;

import java.util.List;

public class WordAdapter extends ArrayAdapter<Word> {
    boolean right;
    String pattern = "";
    GameActivity game;

    public WordAdapter(Context context, List<Word> words, boolean right) {
        super(context, 0, words);
        this.right = right;
        game = (GameActivity) context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.word_layout, parent, false);

        Word w = getItem(position);

        ((TextView) convertView.findViewById(R.id.text)).setText(w.getWord());
        if (w.hasAnte()) ((TextView) convertView.findViewById(R.id.ante)).setText(w.getAnte());
        if (w.hasPost()) ((TextView) convertView.findViewById(R.id.post)).setText(w.getPost());
        convertView.setBackgroundColor(getContext().getResources().getColor(
                getColor(game.getGame().check(w, right, pattern))));

        return convertView;
    }

    int getColor(int matches) {
        switch (matches) {
            case 2:
                return R.color.green;
            case -2:
                return R.color.red;
            case 1:
                return R.color.cyan;
            case -1:
                return R.color.orange;
            default: // mainly 0
                return R.color.dark_comment;
        }
    }

    public WordAdapter setPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }
}
