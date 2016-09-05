package com.phikal.regex.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.phikal.regex.R;
import com.phikal.regex.Utils.REDBList;

public class REDBListAdapter extends CursorAdapter {

    View.OnClickListener ocl;

    private REDBListAdapter(Context ctx, Cursor c, View.OnClickListener ocl) {
        super(ctx, c, 0);
        this.ocl = ocl;
    }

    public static REDBListAdapter genREDBListAdapter(Context ctx, View.OnClickListener ocl) {
        return new REDBListAdapter(ctx, new REDBList(ctx).getServerList(), ocl);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        view.setOnClickListener(ocl);
        String name = cursor.getString(cursor.getColumnIndex(REDBList.REDBSrv.HOST));
        ((TextView) view).setText(name);
        int p = (int) context.getResources().getDimension(R.dimen.padding);
        view.setPadding(p, p, p, p);
        view.setTag(name);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return new TextView(context);
    }

}
