package com.phikal.regex.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.phikal.regex.Games.Games;

import java.util.Arrays;

public class GameAdaptor extends ArrayAdapter<Games> {

    public GameAdaptor(@NonNull Context context) {
        super(context, 0, Arrays.asList(Games.values()));
    }

    @NonNull
    @Override
    public View getView(int pos, @Nullable View v, @NonNull ViewGroup p) {
        if (v != null)
            return  v;

        TextView tv = new TextView(getContext());
        tv.setText(getItem(pos).getName());
        return tv;
    }
}
