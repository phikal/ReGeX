package com.phikal.regex.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.phikal.regex.R;

import java.util.Arrays;

public class CharAdaptor extends ArrayAdapter<String> {

    private InputAdapter ia;

    public CharAdaptor(Context context, String[] words, InputAdapter ia) {
        super(context, 0, Arrays.asList(words));
        this.ia = ia;
    }

    @NonNull
    @Override
    public View getView(final int pos, @Nullable View v, @NonNull ViewGroup p) {
        if (v != null) return v;

        Button b = new Button(getContext());
        b.setBackgroundColor(getContext().getResources()
                .getColor(R.color.comment));
        b.setHeight(getContext().getResources()
                .getDimensionPixelSize(R.dimen.std));
        b.setWidth(getContext().getResources()
                .getDimensionPixelSize(R.dimen.std));
        b.setText(getItem(pos));
        b.setOnClickListener(V -> ia.append(getItem(pos)));
        return b;
    }
}
