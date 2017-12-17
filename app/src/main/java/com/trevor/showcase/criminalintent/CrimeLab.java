package com.trevor.showcase.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by trevormillner on 12/16/17.
 */

public class CrimeLab {

    private List<Crime> mCrimes;
    private static CrimeLab sCrimeLab;

    private CrimeLab(Context context) {
        mCrimes = new ArrayList<>();
        for(int i = 0; i<100; i++) {
            Crime newCrime = new Crime();
            newCrime.setTitle("Crime #" + i);
            newCrime.setCrimeSolved(i % 2 == 0);
            mCrimes.add(newCrime);
        }
    }

    // Not yet sure why context is passed in...
    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    public List<Crime> getCrimes() {
        return mCrimes;
    }

    public Crime getCrime(UUID uuid) {
        for( Crime crime : mCrimes ) {
            if(crime.getUUID().equals(uuid)) {
                return crime;
            }
        }
        return null;
    }
}
