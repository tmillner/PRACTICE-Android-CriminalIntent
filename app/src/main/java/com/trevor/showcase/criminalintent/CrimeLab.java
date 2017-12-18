package com.trevor.showcase.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import com.trevor.showcase.criminalintent.database.CrimeBaseHelper;
import com.trevor.showcase.criminalintent.database.CrimeCursorWrapper;
import com.trevor.showcase.criminalintent.database.CrimeDbSchema;
import com.trevor.showcase.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by trevormillner on 12/16/17.
 */

public class CrimeLab {

    private static CrimeLab sCrimeLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private static ContentValues getContentValues(Crime crime) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CrimeTable.Columns.TITLE, crime.getTitle());
        contentValues.put(CrimeTable.Columns.UUID, crime.getUUID().toString());
        contentValues.put(CrimeTable.Columns.SOLVED, crime.isCrimeSolved() ? 1 : 0);
        contentValues.put(CrimeTable.Columns.DATE, crime.getCrimeDate().getTime());


        return contentValues;
    }

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }

    public void addCrime(Crime crime) {
        ContentValues values = getContentValues(crime);
        mDatabase.insert(CrimeTable.NAME, null, values);
    }

    // Not yet sure why context is passed in...
    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper cursorWrapper = queryCrimes(null, null);
        try {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()) {
                crimes.add(cursorWrapper.getCrime());
                cursorWrapper.moveToNext();
            }
        } finally {
            cursorWrapper.close();
        }

        return crimes;
    }

    public Crime getCrime(UUID uuid) {
        Crime crime;

        CrimeCursorWrapper cursorWrapper = queryCrimes(CrimeTable.Columns.UUID + " = ?",
                new String[] {uuid.toString()});
        try {
            if (cursorWrapper.getCount() == 0) return null;
            cursorWrapper.moveToFirst();
            return cursorWrapper.getCrime();
        } finally {
            cursorWrapper.close();
        }
    }

    public void updateCrime(Crime crime) {
        String crimeId = crime.getUUID().toString();
        ContentValues contentValues = getContentValues(crime);

        mDatabase.update(CrimeTable.NAME, contentValues,
                CrimeTable.Columns.UUID + " = ?",
                new String[] { crimeId });
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(CrimeTable.NAME,
                null, /* all columns */
                whereClause,
                whereArgs,
                null,
                null,
                null);

        return new CrimeCursorWrapper(cursor);
    }
}
