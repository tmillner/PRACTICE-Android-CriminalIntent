package com.trevor.showcase.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.trevor.showcase.criminalintent.Crime;
import com.trevor.showcase.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.util.Date;
import java.util.UUID;

/**
 * Created by trevormillner on 12/17/17.
 */

public class CrimeCursorWrapper extends CursorWrapper {

    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuid = getString(getColumnIndex(CrimeTable.Columns.UUID));
        String title = getString(getColumnIndex(CrimeTable.Columns.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Columns.DATE));
        int solved = getInt(getColumnIndex(CrimeTable.Columns.SOLVED));

        Crime crime = new Crime(UUID.fromString(uuid));
        crime.setCrimeDate(new Date(date));
        crime.setTitle(title);
        crime.setCrimeSolved(solved != 0);

        return crime;
    }
}
