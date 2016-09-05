package com.phikal.regex.Adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.phikal.regex.R;
import com.phikal.regex.Utils.WordList;

public class WordListAdapter extends CursorAdapter {

    WordList wl;

    private WordListAdapter(Context ctx, Cursor c) {
        super(ctx, c, true);
        wl = new WordList(ctx);
    }

    public static WordListAdapter genWordListAdapter(Context ctx) {
        return new WordListAdapter(ctx, new WordList(ctx).getSources());
    }

    @Override
    public void bindView(View v, Context context, Cursor cursor) {
        ((TextView) v.findViewById(R.id.name)).setText(
                cursor.getString(cursor.getColumnIndex(WordList.SourceColumn.NAME)));
        ((TextView) v.findViewById(R.id.count)).setText(String.valueOf(
                cursor.getLong(cursor.getColumnIndex(WordList.SourceColumn.COUNT))));

        v.setOnLongClickListener((v_) -> {
            new AlertDialog.Builder(context)
                    .setMessage(R.string.del_record)
                    .setPositiveButton(android.R.string.yes, (v__, w) -> {
                        if (w == DialogInterface.BUTTON_POSITIVE) {
                            if (wl.getSourcesCount() > 1)
                                new AsyncTask<Void, Void, Void>() {

                                    ProgressDialog pd;

                                    @Override
                                    protected void onPreExecute() {
                                        super.onPreExecute();
                                        this.pd = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
                                        pd.setCancelable(false);
                                        pd.show();
                                    }

                                    @Override
                                    protected Void doInBackground(Void... params) {
                                        wl.deleteSource(cursor.getLong(cursor.getColumnIndex(
                                                WordList.SourceColumn.ID)));
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Void aVoid) {
                                        super.onPostExecute(aVoid);
                                        changeCursor(WordListAdapter.genWordListAdapter(context).getCursor());
                                        notifyDataSetChanged();
                                        pd.cancel();
                                    }
                                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            else
                                Toast.makeText(context, R.string.cannot_del, Toast.LENGTH_LONG).show();
                        }
                    })
                    .show();
            return true;
        });
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inf = LayoutInflater.from(context);
        return inf.inflate(R.layout.word_source, parent, false);
    }
}
