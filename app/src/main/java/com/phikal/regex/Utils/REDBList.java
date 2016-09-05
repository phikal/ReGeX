package com.phikal.regex.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class REDBList {

    REDBDatabase rdb;

    public REDBList(Context ctx) {
        rdb = new REDBDatabase(ctx);
    }

    public Cursor getServerList() {
        return rdb.getReadableDatabase().query(REDBDatabase.TLIST, null, null, null, null, null, REDBSrv.ID + " DESC", "20");
    }

    public void addServer(String url) {
        SQLiteDatabase db = rdb.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(REDBSrv.HOST, url);
        db.insertWithOnConflict(REDBDatabase.TLIST, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public static class REDBSrv {
        public static final String
                ID = "_id",
                HOST = "host";

        private final long id;
        private final String host;

        public REDBSrv(long id, String url) {
            this.id = id;
            this.host = url;
        }

        public long getId() {
            return id;
        }

        public String getHost() {
            return host;
        }
    }


    public class REDBDatabase extends SQLiteOpenHelper {

        public static final int DBVERS = 1;
        public static final String
                DBNAME = "redb-list",
                TLIST = "tlist",
                TCREATE = "CREATE TABLE " + TLIST + "(" +
                        REDBSrv.ID + " INTEGER PRIMARY KEY," +
                        REDBSrv.HOST + " TEXT UNIQUE NOT NULL);";

        public REDBDatabase(Context ctx) {
            super(ctx, DBNAME, null, DBVERS);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TCREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}
