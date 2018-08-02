package com.phikal.regex.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.phikal.regex.R;
import com.phikal.regex.models.Collumn;
import com.phikal.regex.models.Word;

import java.util.List;

public class WordAdapter extends ArrayAdapter<Word> {

    public WordAdapter(Context ctx, Collumn col) {
        super(ctx, 0, (List<Word>) col.getWords());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View v, @NonNull ViewGroup parent) {
        // if (v != null) return v;

        final Word w = getItem(position);
        assert w != null;
        final TextView tv = new TextView(getContext());

        tv.setText(w.getString());

        int res;
        switch (w.getMatch()) {
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
                res = R.color.comment;
        }
        tv.setBackgroundColor(ContextCompat.getColor(getContext(), res));

        int p = getContext().getResources().getDimensionPixelSize(R.dimen.padding);
        tv.setPadding(p, p, p, p);
        tv.setGravity(Gravity.CENTER);
        tv.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        return tv;
    }
}
