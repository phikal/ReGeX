package com.phikal.regex.Adapters;

import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import com.phikal.regex.Activities.Settings.MainSettingsActivity;
import com.phikal.regex.Models.Input;
import com.phikal.regex.R;

import java.util.List;

public class InputAdapter extends ArrayAdapter<Input> {

    public InputAdapter(@NonNull Context context,
                        int resource,
                        @NonNull List<Input> inputs) {
        super(context, resource, inputs);
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

        Button settings = (Button) v.findViewById(R.id.settings);
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

        if (pos == getCount() - 1) // only display this for the last input
            settings.setOnClickListener($ -> {
                Intent i = new Intent(getContext(), MainSettingsActivity.class);
                getContext().startActivity(i);
            });
        else settings.setVisibility(View.GONE);

        return v;
    }
}
