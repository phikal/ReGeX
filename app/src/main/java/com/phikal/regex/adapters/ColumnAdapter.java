package com.phikal.regex.adapters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.phikal.regex.R;
import com.phikal.regex.activities.GameActivity;
import com.phikal.regex.models.Collumn;
import com.phikal.regex.models.Task;

public class ColumnAdapter extends ArrayAdapter<Collumn> {

    public ColumnAdapter(GameActivity ga, Task t) {
        super(ga, 0, t.getCollumns());
    }

    @NonNull
    @Override
    public View getView(int pos, @Nullable View v, @NonNull ViewGroup p) {
        if (v == null)
            v = LayoutInflater.from(getContext()).inflate(
                    R.layout.column_layout, p, false);
        else return v;

        Collumn col = getItem(pos);
        assert col != null;


        return v;
    }
}
