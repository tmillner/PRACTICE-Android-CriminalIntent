package com.trevor.showcase.criminalintent.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.trevor.showcase.criminalintent.database.CrimeDbSchema.CrimeTable;

/**
 * Created by trevormillner on 12/17/17.
 */

public class CrimeBaseHelper extends SQLiteOpenHelper {

    private static final Integer VERSION = 1;
    private static final String DB_NAME = "crimeBase.db";

    public CrimeBaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + CrimeTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                CrimeTable.Columns.UUID + ", " +
                CrimeTable.Columns.TITLE + ", " +
                CrimeTable.Columns.DATE + ", " +
                CrimeTable.Columns.SOLVED + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
