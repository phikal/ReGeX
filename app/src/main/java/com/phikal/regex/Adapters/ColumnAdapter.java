package com.phikal.regex.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.phikal.regex.Models.Collumn;
import com.phikal.regex.R;

import java.util.List;

public class ColumnAdapter extends ArrayAdapter<Collumn> {

    public ColumnAdapter(@NonNull Context ctx,
                         int resource,
                         List<Collumn> cols) {
        super(ctx, R.layout.column_layout, cols);
    }

    @NonNull
    @Override
    public View getView(int pos, @Nullable View v, @NonNull ViewGroup p) {
        if (v == null)
            v = LayoutInflater.from(getContext()).inflate(
                    R.layout.column_layout, p,false);
        else return v;

        Collumn col = getItem(pos);
        assert col != null;

        TextView colName = (TextView) v.findViewById(R.id.col_name);
        ListView colList = (ListView) v.findViewById(R.id.col_list);

        colName.setText(col.getHeader());
        colList.setAdapter(new WordAdapter(getContext(), col));

        return v;
    }
}
