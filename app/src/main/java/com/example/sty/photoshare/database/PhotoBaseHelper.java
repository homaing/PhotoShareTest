package com.example.sty.photoshare.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.sty.photoshare.database.PhotoDbSchema.PhotoTable;

public class PhotoBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "crimeBase.db";
    private static final int VERSION = 1;

    public PhotoBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + PhotoTable.NAME + "(" +
                "_id integer primary key autoincrement, " +
                PhotoTable.Cols.UUID + "," +
                PhotoTable.Cols.TITLE + "," +
                PhotoTable.Cols.DATE + "," +
                PhotoTable.Cols.SHARED + "," +
                PhotoTable.Cols.CONTACT + "," +
                PhotoTable.Cols.COMMENT +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
