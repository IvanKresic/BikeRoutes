package org.bikeroutes.android.databaseCommunication;

import android.provider.BaseColumns;

/**
 * Created by ivan on 24.05.16..
 */
public final class FeedReaderContract {
    public FeedReaderContract() {}


    /* Inner class that defines the table contents */
    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "BikerLocations";
        public static final String COLUMN_NAME_UID = "uid";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_TIMESTAPM = "timestamp";

    }

    /* Inner class that defines the table contents */
    public static abstract class ArduinoEntry implements BaseColumns {

        public static final String TABLE_NAME = "ArduinoReadings";
        public static final String COLUMN_NAME_SENSOR = "sensor";
        public static final String COLUMN_NAME_VALUE = "value";
    }
}
