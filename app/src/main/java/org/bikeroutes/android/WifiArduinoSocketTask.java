package org.bikeroutes.android;


import android.util.Log;

import org.bikeroutes.android.util.ArduinoDataMessageModel;
import org.bikeroutes.android.util.Const;
import com.google.gson.Gson;
import org.bikeroutes.android.BikeRoutesGraphHopper.Settings;
import org.bikeroutes.android.databaseCommunication.SQLiteQueries;
import org.bikeroutes.android.util.ArduinoMessageModel;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
/**
 * Created by ivan on 11.06.16..
 */
public class WifiArduinoSocketTask {
    private DatagramSocket serverSocket;
    private byte[] receiveData;
    private byte[] sendData;
    private byte[] buffer;
    private int port = 11001;
    private ArduinoDataMessageModel arduionoDataModel = new ArduinoDataMessageModel();
    private SQLiteQueries sql = new SQLiteQueries();

    ArduinoMessageModel message = new ArduinoMessageModel();

    public void StartArduinoConnectionThread()
    {
        new Thread(new Runnable() {
            public void run() {
                try {
                    serverSocket = new DatagramSocket(port);
                    while(true)
                    {
                        receiveData = new byte[128];
                        sendData = new byte[128];
                        buffer = new byte[128];
                        DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                        serverSocket.receive(receivePacket);
                        byte[] data = receivePacket.getData();
                        String json = new String(data, 0, receivePacket.getLength());
                        System.out.println("RECEIVED: " + json);
                        InetAddress IPAddress = receivePacket.getAddress();
                        int port = receivePacket.getPort();

                        Gson gson = new Gson();
                        arduionoDataModel = gson.fromJson(json, ArduinoDataMessageModel.class);
                        System.out.println("Temperatura:" + arduionoDataModel.temperatureSensor + " Vlažnost:" + arduionoDataModel.humiditySensor + " CO-Level:"+arduionoDataModel.coSensor);
                        sql.insertIntoTableArduinoData(Const.getContext(),"Temperature", arduionoDataModel.temperatureSensor,Const.getLatitude(), Const.getLongitude(), Const.getTimestamp());
                        sql.insertIntoTableArduinoData(Const.getContext(),"Humidity", arduionoDataModel.humiditySensor,Const.getLatitude(), Const.getLongitude(), Const.getTimestamp());
                        sql.insertIntoTableArduinoData(Const.getContext(),"CO", arduionoDataModel.coSensor,Const.getLatitude(), Const.getLongitude(), Const.getTimestamp());
                        //sql.insertIntoTableArduinoData(Const.getContext(),"Noise", arduionoDataModel.noiseSensor,Const.getLatitude(), Const.getLongitude(), Const.getTimestamp());

                        String capitalizedSentence = json.toUpperCase();
                        sendData = capitalizedSentence.getBytes();
                        DatagramPacket sendPacket =
                                new DatagramPacket(sendData, sendData.length, IPAddress, port);
                        serverSocket.send(sendPacket);
                    }
                }
                catch (Exception e) {
                    e.getMessage();
                    Log.d("TAG", "Shit had happened!");
                    Settings.connectToArduino();
                }
            }
        }).start();
    }
}
