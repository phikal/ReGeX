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
import java.util.regex.PatternSyntaxException;


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

        int res = w.matches(pattern);

        if (res == 2)
            convertView.setBackgroundColor(getContext().getResources().getColor(right ? R.color.green : R.color.red));
        else if (res == 1)
            convertView.setBackgroundColor(getContext().getResources().getColor(right ? R.color.cyan : R.color.orange));
        else
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.dark_comment));

        game.patternError(false);
        return convertView;
    }

    public boolean pass() {
        try {
            for (int i = 0; i < getCount(); i++)
                if (right != getItem(i).getWord().matches(pattern)) return false;
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
