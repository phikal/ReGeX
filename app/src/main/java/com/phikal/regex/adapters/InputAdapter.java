package com.phikal.regex.adapters;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.phikal.regex.activities.GameActivity;
import com.phikal.regex.activities.SettingsActivity;
import com.phikal.regex.models.Input;
import com.phikal.regex.R;
import com.phikal.regex.models.Task;

import java.util.List;

public class InputAdapter extends ArrayAdapter<Input> {

    EditText focused = null;

    public InputAdapter(GameActivity game, Task t) {
        super(game, 0, t.getInputs());
    }

    @NonNull
    @Override
    public View getView(int pos, @Nullable View v, @NonNull ViewGroup p) {
        if (v == null)
            v = LayoutInflater.from(getContext()).inflate(
                    R.layout.input_layout, p,false);
            else return v;

        Input input = getItem(pos);
        assert input != null;

        ImageButton settings = (ImageButton) v.findViewById(R.id.settings);
        TextView status = (TextView) v.findViewById(R.id.status);
        EditText gInput = (EditText) v.findViewById(R.id.input);

        input.onEdit((r, max, msg) -> {
            int res;
            gInput.setFilters(new InputFilter[] {
                    (src, s, e, dst, ds, de) -> max ? dst : src
            });
            switch (r) {
                case ERROR:
                    res = R.color.red;
                    break;
                case OK:
                default:
                    res = R.color.comment;
            }
            status.setTextColor(getContext().getResources().getColor(res));
            status.setText(msg);
        });

        gInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                input.setText(editable.toString());
            }
        });

        gInput.setOnFocusChangeListener((view, hasfocus) -> {
            if (hasfocus)
                focused = (EditText) view;
        });

        if (pos == getCount() - 1) {// only display this for the last input
            settings.setOnClickListener($ -> {
                Intent i = new Intent(getContext(), SettingsActivity.class);
                getContext().startActivity(i);
            });
            v.requestFocus();
        }
        else settings.setVisibility(View.GONE);

        return v;
    }

    public void append(String c) {
        if (focused != null)
            focused.getEditableText().append(c);
    }
}
