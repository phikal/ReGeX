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
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WordList {

    public static final String DONE = "";
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
        return list;
    }

    public void addFromQueue(String url, BlockingQueue<String> queue) {
        SQLiteDatabase db = wdb.getReadableDatabase();
        addFromQueueWithDb(url, queue, db);
    }

    public void addFromQueueWithDb(String url, BlockingQueue<String> queue, SQLiteDatabase db) {
        String word;
        ContentValues values = new ContentValues();
        values.put(SourceColumn.NAME, url);
        long sid = db.insert(WordDatabase.TSOURCE, null, values);
        db.beginTransaction();
        try {
            while ((word = queue.take()) != null && word != DONE) {
                values = new ContentValues();
                values.put(WordColumn.SRC, sid);
                values.put(WordColumn.WORD, word.trim());
                db.insert(WordDatabase.TWORDS, null, values);
            }
            db.setTransactionSuccessful();
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getSources() {
        SQLiteDatabase db = wdb.getReadableDatabase();
        return db.rawQuery("SELECT " +
                        WordDatabase.TSOURCE + "." + SourceColumn.ID + ", " +
                        SourceColumn.NAME + ", " +
                        " COUNT(" + WordDatabase.TWORDS + "." + WordColumn.ID + ")" +
                        " AS " + SourceColumn.COUNT +
                        " FROM " + WordDatabase.TSOURCE +
                        " LEFT OUTER JOIN " + WordDatabase.TWORDS +
                        " ON " + WordDatabase.TWORDS + "." + WordColumn.SRC +
                        " = " + WordDatabase.TSOURCE + "." + SourceColumn.ID +
                        " GROUP BY " + WordDatabase.TSOURCE + "." + SourceColumn.ID + ";",
                new String[]{});
    }

    public long getSourcesCount() {
        Cursor c = getSources();
        long count = c.getCount();
        c.close();
        return count;
    }

    public void deleteSource(long id) {
        SQLiteDatabase db = wdb.getWritableDatabase();
        db.delete(WordDatabase.TSOURCE,
                SourceColumn.ID + " = ?",
                new String[]{String.valueOf(id)});
        db.delete(WordDatabase.TWORDS,
                WordColumn.SRC + " = ?",
                new String[]{String.valueOf(id)});
    }

    public class WordColumn {
        public final static String
                ID = "_id",
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

    public class SourceColumn {
        static public final String
                ID = "_id",
                NAME = "url",
                COUNT = "count";

        private final long id;
        private final String name;

        public SourceColumn(long id, String name) {
            this.id = id;
            this.name = name;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public class WordDatabase extends SQLiteOpenHelper {

        private static final int DBVERS = 1;
        private static final String
                DBNAME = "wordlist",
                TWORDS = "words",
                TSOURCE = "source",
                STMT_CREATE_WDB = "CREATE TABLE " + TWORDS + "(" +
                        WordColumn.ID + " INTEGER PRIMARY KEY," +
                        WordColumn.SRC + " INTEGER REFERENCES " +
                        TSOURCE + "(" + SourceColumn.ID + ")," +
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
                BlockingQueue<String> words = new LinkedBlockingQueue<>();
                String line;
                while ((line = br.readLine()) != null)
                    words.add(line.trim());
                words.add(DONE);
                br.close();
                addFromQueueWithDb("DEFAULT", words, db);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
