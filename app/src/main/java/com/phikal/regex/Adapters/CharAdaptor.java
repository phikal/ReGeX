package com.phikal.regex.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.phikal.regex.Activities.GameActivity;
import com.phikal.regex.R;

import java.util.Arrays;

public class CharAdaptor extends ArrayAdapter<String> {

    GameActivity game;

    public CharAdaptor(Context context, String[] words) {
        super(context, 0, Arrays.asList(words));
        game = (GameActivity) context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.char_button, parent, false);
            ((Button) convertView).setText(String.valueOf(getItem(position)));
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    game.click(getItem(position));
                }
            });
        }
        return convertView;
    }
}
