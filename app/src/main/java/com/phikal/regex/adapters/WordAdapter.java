package com.phikal.regex.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.phikal.regex.models.Collumn;
import com.phikal.regex.models.Word;
import com.phikal.regex.R;

import java.util.List;

public class WordAdapter extends ArrayAdapter<Word> {

    public WordAdapter(Context ctx, Collumn col) {
        super(ctx, 0, (List<Word>) col.getWords());
    }

    @NonNull
    @Override
    public View getView(int position, View v, ViewGroup parent) {
        if (v != null) return  v;

        final Word w = getItem(position);
        final TextView tv = new TextView(getContext());

        tv.setText(w.getString());
        w.onMatch((m) -> {
            int res;
            switch (m) {
                case FULL:
                    res = R.color.green;
                    break;
                case ANTI_FULL:
                    res = R.color.red;
                    break;
                case HALF:
                    res = R.color.cyan;
                    break;
                case ANTI_HALF:
                    res = R.color.orange;
                    break;
                default:
                case NONE:
                    res = R.color.dark_comment;
            }
            tv.setBackgroundColor(getContext().getResources().getColor(res));
        });
        int p = getContext().getResources().getDimensionPixelSize(R.dimen.padding);
        tv.setPadding(p, p, p, p);
        tv.setGravity(Gravity.CENTER);
        tv.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        return tv;
    }
}
