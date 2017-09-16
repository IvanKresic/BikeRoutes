package org.bikeroutes.android.databaseCommunication;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.bikeroutes.android.R;
import org.bikeroutes.android.util.Const;
import com.vividsolutions.jts.geom.Coordinate;

import org.bikeroutes.android.util.Cupus;
import org.openiot.cupus.artefact.HashtablePublication;
import org.openiot.cupus.entity.publisher.Publisher;

/**
 * Created by ivan on 20.05.16..
 */
public class SQLiteQueries  {

    private Publisher publisherUserData;
    static int publication_duration = 1800000;//30 minutes
    private static Boolean isReadyToSendPublication = false;
    private String subs;

    public void insertIntoTableBikeRoutes(Context conn, String UID, double lat, double longi, long time)
    {
        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(conn);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_UID, UID);
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LONGITUDE, longi);
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LATITUDE, lat);
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAPM, time);

        db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);
        db.close();
        mDbHelper.close();
    }

    public void setAndConnectUserDataPublisher(Publisher publisherUserData)
    {
        this.publisherUserData = publisherUserData;
    }

    public void readFromDatabaseBikeRoutes(Context conn)
    {
        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(conn);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                FeedReaderContract.FeedEntry.COLUMN_NAME_UID,
                FeedReaderContract.FeedEntry.COLUMN_NAME_LONGITUDE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_LATITUDE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAPM
        };

        Cursor c = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME,
                projection, null, null, null, null, null
        );

        int rowsInDatabase = c.getCount();

        Coordinate[] coordinates = new Coordinate[rowsInDatabase];
        String timestamp = "";

        Log.d("BAZA", "Prije slanja korisnika: "+rowsInDatabase);
        if(rowsInDatabase > 3 && rowsInDatabase != 0) {
            int i = 0;
            c.moveToFirst();
            while (!c.isAfterLast()) {
                double lat = c.getDouble(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LATITUDE));
                double longi = c.getDouble(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LONGITUDE));
                long time = c.getLong(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAPM));
                coordinates[i] = new Coordinate(lat, longi);
                timestamp += time + ",";
                i++;
                c.moveToNext();
            }

            try {
                subs = timestamp.substring(0, timestamp.length() - 1);
            } catch (Exception e) {
                e.getMessage();
            }
            HashtablePublication userPublication = new HashtablePublication(System.currentTimeMillis() + publication_duration, System.currentTimeMillis());
            userPublication.setProperty("DataType", "RoutesFromUser");
            userPublication.setProperty("UUID", Const.DeviceId);
            userPublication.setProperty("Timestamp", subs);
            userPublication.setGPSCoordinates(coordinates);
            publisherUserData.publish(userPublication);
            Log.d("PUBLICATION: ", " " + userPublication.getProperties().get("DataType"));
            c.close();
            db.close();
            mDbHelper.close();
            Const.readyToResetUserDatabase = true;
        }
    }

    public void deleteRecordsFromDatabaseBikeRoutes(Context conn)
    {
        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(conn);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        db.execSQL("DELETE FROM " + FeedReaderContract.FeedEntry.TABLE_NAME);
        db.close();
        Const.readyToResetUserDatabase = false;
    }

    /*****************************************
     *********** FOR ARDUINO DATABASE ********
     ****************************************/
    public void insertIntoTableArduinoData(Context conn, String SensorType, double value, double lat, double longi, long time)
    {
        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(conn);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.ArduinoEntry.COLUMN_NAME_SENSOR, SensorType);
        values.put(FeedReaderContract.ArduinoEntry.COLUMN_NAME_VALUE, value);
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LATITUDE, lat);
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LONGITUDE, longi);
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAPM, time);

        db.insert(FeedReaderContract.ArduinoEntry.TABLE_NAME, null, values);
        db.close();
    }

    public void readFromDatabaseArduinoData(Context conn)
    {
        Publisher publisherArduinoData = new Publisher(Const.getContext().getString(R.string.arduino_publisher_name),
                Const.getBrokerIpAddress(), 10000);
        publisherArduinoData.connect();
        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(conn);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                FeedReaderContract.ArduinoEntry.COLUMN_NAME_SENSOR,
                FeedReaderContract.ArduinoEntry.COLUMN_NAME_VALUE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_LATITUDE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_LONGITUDE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAPM
        };

        Cursor c = db.query(
                FeedReaderContract.ArduinoEntry.TABLE_NAME,
                projection, null, null, null, null, null
        );

        Log.d("BAZA", "Prije slanja senzora: "+c.getCount());
        if(c.getCount() != 0) {
            c.moveToFirst();
            while (!c.isAfterLast()) {
                HashtablePublication ArduinoPublication  = new HashtablePublication(-1, System.currentTimeMillis());
                String sensorType = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.ArduinoEntry.COLUMN_NAME_SENSOR));
                double sensorValue = c.getDouble(c.getColumnIndexOrThrow(FeedReaderContract.ArduinoEntry.COLUMN_NAME_VALUE));
                double lat = c.getDouble(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LATITUDE));
                double longi = c.getDouble(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LONGITUDE));
                long time = c.getLong(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAPM));

                Coordinate[] coordinates = new Coordinate[1];
                coordinates[0] = new Coordinate(lat, longi);
                ArduinoPublication.setProperty(Const.getContext().getString(R.string.data_type), Const.getContext().getString(R.string.arduino_camel_case));
                ArduinoPublication.setProperty(Const.getContext().getString(R.string.sensor_type), sensorType);
                ArduinoPublication.setProperty(Const.getContext().getString(R.string.sensor_value), sensorValue);
                ArduinoPublication.setProperty(Const.getContext().getString(R.string.timestamp), time);
                ArduinoPublication.setGPSCoordinates(coordinates);
                publisherArduinoData.publish(ArduinoPublication);
                c.moveToNext();
            }
        }
        c.close();
        db.close();
        Const.readyToResetArduinoDatabase = true;
    }

    public void deleteRecordsFromDatabaseArduinoData(Context conn)
    {
        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(conn);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        db.execSQL("DELETE FROM " + FeedReaderContract.ArduinoEntry.TABLE_NAME);
        db.close();
        Const.readyToResetArduinoDatabase = false;
    }
}