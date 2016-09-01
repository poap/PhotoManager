package com.poap.photomanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StoryDB extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Story.db";

    public StoryDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE story (_id INTEGER PRIMARY KEY, title TEXT, memo TEXT, " +
                "edited TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
        db.execSQL("CREATE TABLE picture (_id INTEGER PRIMARY KEY, path TEXT, story INTEGER NOT NULL, FOREIGN KEY(story) REFERENCES story(_id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO: implement migration stuff
    }

}
