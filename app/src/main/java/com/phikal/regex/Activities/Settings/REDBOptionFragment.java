package com.phikal.regex.Activities.Settings;


import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.phikal.regex.Activities.GameActivity;
import com.phikal.regex.Adapters.REDBListAdapter;
import com.phikal.regex.Games.Match.REDBGenerator;
import com.phikal.regex.R;
import com.phikal.regex.Utils.REDBList;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class REDBOptionFragment extends Fragment {

    public REDBOptionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_redb_option, container, false);

        String chsot = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(GameActivity.REDB_SERVER, REDBGenerator.stdAddr);
        ((TextView) v.findViewById(R.id.new_url)).setText(chsot);
        new REDBList(getActivity()).addServer(chsot);

        ((ListView) v.findViewById(R.id.prev_servers)).setAdapter(
                REDBListAdapter.genREDBListAdapter(getActivity(), (v_) -> {
                    String host = (String) v_.getTag();
                    PreferenceManager.getDefaultSharedPreferences(getActivity())
                            .edit().putString(GameActivity.REDB_SERVER, host)
                            .apply();
                    ((TextView) v.findViewById(R.id.new_url)).setText(host);
                }));

        v.findViewById(R.id.add).setOnClickListener((v_) -> new HostChecker((EditText) v.findViewById(R.id.new_url),
                ((CursorAdapter) ((ListView) v.findViewById(R.id.prev_servers)).getAdapter()))
                .execute(((TextView) v.findViewById(R.id.new_url)).getText().toString()));

        return v;
    }

    private class HostChecker extends AsyncTask<String, Void, String> {

        EditText text;
        CursorAdapter adapter;

        HostChecker(EditText text, CursorAdapter adapter) {
            this.text = text;
            this.adapter = adapter;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String addr = new Socket(InetAddress.getByName(params[0]), 25921)
                        .getInetAddress()
                        .getCanonicalHostName();
                new REDBList(getActivity()).addServer(addr);
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                        .putString(GameActivity.REDB_SERVER, addr).apply();
                return null;
            } catch (IOException ioe) {
                ioe.printStackTrace();
                return getContext().getString(R.string.conn_error);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            text.setError(s);
            if (s == null) {
                Toast.makeText(getActivity(), R.string.conn_ok, Toast.LENGTH_SHORT).show();
                adapter.changeCursor(new REDBList(getActivity()).getServerList());
                adapter.notifyDataSetChanged();
            }
        }
    }
}

