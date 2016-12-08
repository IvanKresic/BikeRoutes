package org.bikeroutes.android.util;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ivan on 1/29/2016.
 */
public class UserLocationModel {

    @SerializedName("DeviceID")
    private String deviceId;
    @SerializedName("Longitude")
    private double longitude;
    @SerializedName("Latitude")
    private double latitude;
    @SerializedName("Timestamp")
    private double timestamp;

    public void setUID(String uid)
    {
        this.deviceId = uid;
    }

    public String getUID()
    {
        return deviceId;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }

}
