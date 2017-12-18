package com.trevor.showcase.criminalintent;

import java.util.Date;
import java.util.UUID;

/**
 * Created by trevormillner on 12/14/17.
 */

public class Crime {

    private UUID mUUID;
    private String mTitle;
    private Date mCrimeDate;
    private boolean mCrimeSolved;

    public Crime() {
        this(UUID.randomUUID());
    }

    public Crime(UUID uuid) {
        mUUID = uuid;
        mCrimeDate = new Date();
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getCrimeDate() {
        return mCrimeDate;
    }

    public void setCrimeDate(Date crimeDate) {
        mCrimeDate = crimeDate;
    }

    public boolean isCrimeSolved() {
        return mCrimeSolved;
    }

    public void setCrimeSolved(boolean crimeSolved) {
        mCrimeSolved = crimeSolved;
    }

    public UUID getUUID() {
        return mUUID;
    }

    public void setUUID(UUID UUID) {
        mUUID = UUID;
    }
}
