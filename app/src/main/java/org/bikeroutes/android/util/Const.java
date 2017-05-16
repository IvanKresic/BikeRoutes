package org.bikeroutes.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;

import com.vividsolutions.jts.geom.Coordinate;

import org.bikeroutes.android.R;
import org.bikeroutes.android.TabsAndFragments.PageFragment;
import org.oscim.android.MapView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivan on 17.05.16..
 */
public class Const {

    public static List<UserLocationModel> userModel = new ArrayList<>();
    public static List<ArduinoDataMessageModel> arduinoModel = new ArrayList<>();
    public static List<SensorReadingsModel> sensorReadingsModelList = new ArrayList<>();
    private static String BrokerIpAddress = "161.53.19.88";
    private static String TAG;
    private static int CupusSendDelay = 30;
    private static int GPS_DELAY = 5;
    private static Context context;
    private static Activity mainActivity;
    private static String currentMap;
    private static double Latitude;
    private static double Longitude;
    private static long Timestamp;
    private static Coordinate[] coordinates1;
    private static MapView mapView;
    public static String DeviceId;
    public static boolean readyToResetUserDatabase = false;
    public static boolean readyToResetArduinoDatabase = false;
    public static String fullPathToFile;
    public static View savedFragment;

    public static MapView getMapView() {
        return mapView;
    }

    public static void setMapView(MapView mapView) {
        Const.mapView = mapView;
    }

    public static Coordinate[] getCoordinates1() {
        return coordinates1;
    }

    public static void setCoordinates1(Coordinate[] coordinates1) {
        Const.coordinates1 = coordinates1;
    }

    public static long getTimestamp() {
        return Timestamp;
    }

    public static void setTimestamp(long timestamp) {
        Timestamp = timestamp;
    }

    public static double getLatitude() {
        return Latitude;
    }

    public static void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public static void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public static double getLongitude() {
        return Longitude;
    }

    public static void setCurrentMap(String s)
    {
        currentMap = s;
    }
    public static String getCurrentMap()
    {
        return currentMap;
    }

    public static void setGpsDelay(int i)
    {
        GPS_DELAY = i;
    }

    public static int getGpsDelay() {
        return GPS_DELAY;
    }

    public static void setMainActivity(Activity activity)
    {
        mainActivity = activity;
    }

    public static Activity getMainActivity()
    {
        return mainActivity;
    }

    public static void setContext(Context c)
    {
        context = c;
    }

    public static Context getContext() {
        return context;
    }

    public static void setBrokerIpAddress(String address)
    {
            BrokerIpAddress = address;
    }

    public static String getBrokerIpAddress()
    {
        return BrokerIpAddress;
    }

    public void setTAG(String tag)
    {
        if(tag != null)
        {
            TAG = tag;
        }
    }

    public String getTAG()
    {
        return TAG;
    }

    public void addToUserList(UserLocationModel user)
    {
        userModel.add(user);
    }

    public static void  resetUserModelList()
    {
        userModel.clear();
    }

    public static void resetSensorReadingsModelList()
    {
        sensorReadingsModelList.clear();
    }

    public static List<SensorReadingsModel> getSensorReadingsModelList() {
        return sensorReadingsModelList;
    }



    public static List<UserLocationModel> getUserList()
    {
        return userModel;
    }

    public static void setDelay(int del)
    {
        if(del != 0)
        {
            CupusSendDelay = del;
        }
        else
            CupusSendDelay = 30;
    }

    public static int getDelay(){ return CupusSendDelay; }

    public static void setArduinoModel(List<ArduinoDataMessageModel> arduinoModel) {
        Const.arduinoModel = arduinoModel;
    }

    public static List<ArduinoDataMessageModel> getArduinoModel() {
        return arduinoModel;
    }
}
