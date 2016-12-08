package org.bikeroutes.android.util;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ivan on 11.06.16..
 */
public class ArduinoDataMessageModel {
    @SerializedName("1")
    public double temperatureSensor;
    @SerializedName("2")
    public double humiditySensor;
    @SerializedName("3")
    public double coSensor;
    //@SerializedName("4")
    //public double noiseSensor;
}
