package com.trevor.showcase.criminalintent.database;

/**
 * Created by trevormillner on 12/17/17.
 */

public class CrimeDbSchema {
    public static class CrimeTable {
        public static final String NAME = "Crimes";

        public static final class Columns {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
        }
    }
}
