package com.phikal.regex.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.phikal.regex.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class WordList {

    private final Context ctx;
    private final WordDatabase wdb;

    public WordList(Context ctx) {
        wdb = new WordDatabase(this.ctx = ctx);
    }

    public List<String> rndWord(int len, int itms) {
        SQLiteDatabase db = wdb.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + WordColumn.WORD +
                        " FROM " + WordDatabase.TWORDS +
                        " WHERE length(" + WordColumn.WORD + ") <= ?" +
                        " ORDER BY RANDOM()" + // probably inefficient
                        " LIMIT ?;",
                new String[]{String.valueOf(len), String.valueOf(itms)});
        List<String> list = new ArrayList<>(itms);
        if (c.moveToFirst())
            do list.add(c.getString(0));
            while (c.moveToNext());
        c.close();
        db.close();
        return list;
    }

    public synchronized void addFromQueue(String url, Queue<String> queue) {
        String word;
        SQLiteDatabase db = wdb.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SourceColumn.NAME, url);
        long sid = db.insert(WordDatabase.TSOURCE, null, values);
        db.beginTransaction();
        while ((word = queue.poll()) != null) {
            values = new ContentValues();
            values.put(WordColumn.SRC, sid);
            values.put(WordColumn.WORD, word.trim());
            db.insert(WordDatabase.TWORDS, null, values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public long getWordCount(SourceColumn col) {
        SQLiteDatabase db = wdb.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT 1 FROM " + WordDatabase.TWORDS +
                        " WHERE " + WordColumn.SRC + " = ?;",
                new String[]{String.valueOf(col.getId())});
        c.moveToFirst();
        int count = c.getColumnCount();
        c.close();
        db.close();
        return count;
    }

    public List<SourceColumn> getSources() {
        SQLiteDatabase db = wdb.getReadableDatabase();
        List<SourceColumn> list = new LinkedList<>();

        Cursor c = db.query(WordDatabase.TSOURCE, null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int idi = c.getColumnIndex(SourceColumn.ID),
                    urli = c.getColumnIndex(SourceColumn.NAME);
            do list.add(new SourceColumn(c.getLong(idi), c.getString(urli)));
            while (c.moveToNext());
        }
        c.close();
        db.close();

        return list;
    }

    public void deleteSource(SourceColumn col) {
        SQLiteDatabase db = wdb.getWritableDatabase();
        db.delete(WordDatabase.TSOURCE,
                SourceColumn.ID + " = ?",
                new String[]{String.valueOf(col.getId())});
        db.delete(WordDatabase.TWORDS,
                WordColumn.ID + " = ?",
                new String[]{String.valueOf(col.getId())});
        db.close();
    }

    private class WordColumn {
        private final static String
                ID = "rowid",
                SRC = "src",
                WORD = "word";

        private final long id, src;
        private final String word;

        public WordColumn(int id, int src, String word) {
            this.id = id;
            this.src = src;
            this.word = word;
        }

        public long getId() {
            return id;
        }

        public long getSrc() {
            return src;
        }

        public String getWord() {
            return word;
        }
    }

    private class SourceColumn {
        private static final String
                ID = "rowid",
                NAME = "url";

        private final long id;
        private final String name;

        public SourceColumn(long id, String name) {
            this.id = id;
            this.name = name;
        }

        public long getId() {
            return id;
        }

        public String getUrl() {
            return name;
        }
    }

    private class WordDatabase extends SQLiteOpenHelper {

        private static final int DBVERS = 1;
        private static final String
                DBNAME = "wordlist",
                TWORDS = "words",
                TSOURCE = "source",
                STMT_CREATE_WDB = "CREATE TABLE " + TWORDS + "(" +
                        WordColumn.ID + " INTEGER PRIMARY KEY," +
                        WordColumn.SRC + " INTEGER," +
                        WordColumn.WORD + " TEXT);",
                STMT_CREATE_SDB = "CREATE TABLE " + TSOURCE + "(" +
                        SourceColumn.ID + " INTEGER PRIMARY KEY," +
                        SourceColumn.NAME + " TEXT);";

        public WordDatabase(Context context) {
            super(context, DBNAME, null, DBVERS);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(STMT_CREATE_SDB);
            db.execSQL(STMT_CREATE_WDB);
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        ctx.getResources().openRawResource(R.raw.words)));
                Queue<String> words = new LinkedList<>();
                String line;
                while ((line = br.readLine()) != null)
                    if (!(line = line.trim()).startsWith("#"))
                        words.add(line);
                br.close();
                addFromQueue("DEFALT", words);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
