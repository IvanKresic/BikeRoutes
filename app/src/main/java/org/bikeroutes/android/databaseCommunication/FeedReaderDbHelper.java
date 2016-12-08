package org.bikeroutes.android.databaseCommunication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ivan on 24.05.16..
 */
public class FeedReaderDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "BikeRoutes.db";

    private static final String TEXT_TYPE = " TEXT";
    private  static final String DOUBLE_TYPE = " DECIMAL";
    private  static final String INT_TYPE = " INT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_BIKEROUTES_ENTRIES =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_NAME + " ("+
                    FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_UID + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_LATITUDE + DOUBLE_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_LONGITUDE + DOUBLE_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAPM + INT_TYPE + " );";

    private static final String SQL_CREATE_ARDUINO_ENTRIES =
            "CREATE TABLE " + FeedReaderContract.ArduinoEntry.TABLE_NAME + " ("+
                    FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.ArduinoEntry.COLUMN_NAME_SENSOR + TEXT_TYPE + COMMA_SEP+
                    FeedReaderContract.ArduinoEntry.COLUMN_NAME_VALUE + DOUBLE_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_LATITUDE + DOUBLE_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_LONGITUDE + DOUBLE_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAPM + INT_TYPE + " );";

    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_BIKEROUTES_ENTRIES);
        sqLiteDatabase.execSQL(SQL_CREATE_ARDUINO_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
