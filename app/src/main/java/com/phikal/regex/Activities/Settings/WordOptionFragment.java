package com.phikal.regex.Activities.Settings;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.phikal.regex.Adapters.WordListAdapter;
import com.phikal.regex.R;
import com.phikal.regex.Utils.WordList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class WordOptionFragment extends Fragment {

    final static protected String INPUT = "word-option-input";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_word_option, container, false);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String chsot = prefs.getString(INPUT, "");

        ((TextView) v.findViewById(R.id.new_url)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                prefs.edit().putString(INPUT, editable.toString()).apply();
            }
        });
        ((TextView) v.findViewById(R.id.new_url)).setText(chsot);

        v.findViewById(R.id.add).setOnClickListener((_v) -> {
            try {
                URL url = new URL(((TextView) v.findViewById(R.id.new_url)).getText().toString());
                ((TextView) v.findViewById(R.id.new_url)).setError(null);
                new WordInserter(url, (CursorAdapter) ((ListView) v.findViewById(R.id.word_list)).getAdapter())
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } catch (MalformedURLException mue) {
                ((TextView) v.findViewById(R.id.new_url)).setError(getResources().getString(R.string.url_error));
            }
        });

        ((ListView) v.findViewById(R.id.word_list)).setAdapter(
                WordListAdapter.genWordListAdapter(getActivity()));

        return v;
    }

    private class WordInserter extends AsyncTask<Void, String, Void> {

        final URL url;
        final BlockingQueue<String> bq;
        final ProgressDialog pd;
        final CursorAdapter ca;

        WordInserter(URL url, CursorAdapter ca) {
            this.url = url;
            this.ca = ca;
            this.bq = new LinkedBlockingQueue<>();
            this.pd = new ProgressDialog(getActivity(), ProgressDialog.STYLE_SPINNER);
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    new WordList(getActivity()).addFromQueue(url.toString(), bq);
                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setCancelable(false);
            pd.setMessage(getString(R.string.loading_words));
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                for (String line; ((line = br.readLine())) != null; )
                    if ((line = line.trim()).matches("^\\w+$"))
                        bq.put(line);
                br.close();
                conn.disconnect();
            } catch (IOException | InterruptedException ie) {
                ie.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            bq.add(WordList.DONE);
            pd.cancel();
            ca.changeCursor(new WordList(getActivity()).getSources());
            ca.notifyDataSetChanged();
        }
    }
}
