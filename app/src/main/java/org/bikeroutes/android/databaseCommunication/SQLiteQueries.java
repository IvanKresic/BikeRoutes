package org.bikeroutes.android.databaseCommunication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.bikeroutes.android.util.Const;
import com.vividsolutions.jts.geom.Coordinate;

import org.openiot.cupus.artefact.HashtablePublication;
import org.openiot.cupus.entity.publisher.Publisher;

/**
 * Created by ivan on 20.05.16..
 */
public class SQLiteQueries  {


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
    }

    public void readFromDatabaseBikeRoutes(Context conn)
    {
        Publisher publisherUserData = new Publisher("UserData", Const.getBrokerIpAddress(), 10000);
        publisherUserData.connect();

        HashtablePublication UserPublication  = new HashtablePublication(-1, System.currentTimeMillis());
        UserPublication.setProperty("DataType","User");
        UserPublication.setProperty("UUID", Const.DeviceId);



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

        int rowsIndDatabase = c.getCount();

        Coordinate[] coordinates = new Coordinate[rowsIndDatabase];
        String timestamp = "";

        Log.d("BAZA", "Prije slanja korisnika: "+rowsIndDatabase);
        if(rowsIndDatabase != 0) {
            int i = 0;
            c.moveToFirst();
            while (!c.isAfterLast()) {
                double lat = c.getDouble(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LATITUDE));
                double longi = c.getDouble(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LONGITUDE));
                long time = c.getLong(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAPM));
                coordinates[i] = new Coordinate(lat,longi);
                timestamp += time + ",";
                i++;
                c.moveToNext();
            }
            String subs = timestamp.substring(0, timestamp.length()-1);
            UserPublication.setProperty("Timestamp", subs);
            UserPublication.setGPSCoordinates(coordinates);
        }
        publisherUserData.publish(UserPublication);
        c.close();
        db.close();
        Const.readyToResetUserDatabase = true;
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
        Publisher publisherArduinoData = new Publisher("ArduinoData", Const.getBrokerIpAddress(), 10000);
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
                //******** TEST *****

                Coordinate[] coordinates = new Coordinate[1];
                coordinates[0] = new Coordinate(lat, longi);
                ArduinoPublication.setProperty("DataType","Arduino");
                ArduinoPublication.setProperty("SensorType", sensorType);
                ArduinoPublication.setProperty("Value", sensorValue);
                ArduinoPublication.setProperty("Timestamp", time);
                ArduinoPublication.setGPSCoordinates(coordinates);
                publisherArduinoData.publish(ArduinoPublication);
                //*******************

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
