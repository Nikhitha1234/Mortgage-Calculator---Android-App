package com.example.group7.mortgage_calculator_lab2;

import android.provider.BaseColumns;

/**
 * Created by nikhi on 25-03-2018.
 */

public class SaveDataContract {

    public SaveDataContract(){}

    /* Inner class that defines the table contents */
    public static class SaveDataEntry implements BaseColumns {
        public static final String TABLE_NAME = "save_data";
        public static final String COLUMN_NAME_PROPTYPE = "proptype";
        public static final String COLUMN_NAME_STREET = "street";
        public static final String COLUMN_NAME_CITY = "city";
        public static final String COLUMN_NAME_LOANAMT = "loanamt";
        public static final String COLUMN_NAME_APR = "apr";
        public static final String COLUMN_NAME_MPMT = "monthlypmt";

    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + SaveDataContract.SaveDataEntry.TABLE_NAME + " (" +
                    SaveDataContract.SaveDataEntry._ID + " INTEGER PRIMARY KEY," +
                    SaveDataContract.SaveDataEntry.COLUMN_NAME_PROPTYPE + " TEXT," +
                    SaveDataContract.SaveDataEntry.COLUMN_NAME_STREET + " TEXT," +
                    SaveDataContract.SaveDataEntry.COLUMN_NAME_CITY + " TEXT," +
                    SaveDataContract.SaveDataEntry.COLUMN_NAME_LOANAMT + " TEXT," +
                    SaveDataContract.SaveDataEntry.COLUMN_NAME_APR + " TEXT," +
                    SaveDataContract.SaveDataEntry.COLUMN_NAME_MPMT + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + SaveDataContract.SaveDataEntry.TABLE_NAME;

    public String getSqlCreateEntries() {
        return SQL_CREATE_ENTRIES;
    }

    public String getSqlDeleteEntries() {
        return SQL_DELETE_ENTRIES;
    }
}
