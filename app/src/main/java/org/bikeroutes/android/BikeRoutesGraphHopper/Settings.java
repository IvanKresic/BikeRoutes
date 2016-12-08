package org.bikeroutes.android.BikeRoutesGraphHopper;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.bikeroutes.android.WifiSocketTask;
import org.bikeroutes.android.util.Const;
import org.bikeroutes.android.util.Cupus;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ivan on 03.06.16..
 */
public class Settings {
    EditText ipAddress;
    EditText publicationTime;
    EditText gpsTime;
    TextView connectionStatus;
    TextView currentMap;
    Button cupusButton;
    boolean buttonToggle = false;
    View v;
    Context con;
    TimerTask delayedThreadStartTask;

    public Settings(View view, Context context)
    {
        v=view;
        con = context;
        currentMap = (TextView) view.findViewById(org.bikeroutes.android.R.id.currentMap);
        ipAddress = (EditText) view.findViewById(org.bikeroutes.android.R.id.ipaddress);
        publicationTime = (EditText) view.findViewById(org.bikeroutes.android.R.id.publicationTime);
        gpsTime = (EditText) view.findViewById(org.bikeroutes.android.R.id.collect_gps_tag);
        connectionStatus = (TextView) view.findViewById(org.bikeroutes.android.R.id.statusText_status);
        cupusButton = (Button) view.findViewById(org.bikeroutes.android.R.id.connect_button);
    }

    public void setThisParameters()
    {
        ipAddress.setText(Const.getBrokerIpAddress());
        publicationTime.setText(String.valueOf(Const.getDelay()));
        gpsTime.setText(String.valueOf(Const.getGpsDelay()));
        connectionStatus.setText(org.bikeroutes.android.R.string.not_connected);
        connectionStatus.setTextColor(con.getResources().getColor(org.bikeroutes.android.R.color.red));
        currentMap.setText(Const.getCurrentMap());
        cupusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Timer timer = new Timer();

                if(!buttonToggle)
                {
                    Const.setBrokerIpAddress(ipAddress.getText().toString());
                    Const.setDelay(Integer.parseInt(publicationTime.getText().toString()));
                    connectionStatus.setTextColor(con.getResources().getColor(org.bikeroutes.android.R.color.yellow));
                    connectionStatus.setText("Please wait ...");
                    delayedThreadStartTask = new TimerTask() {
                        @Override
                        public void run() {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Cupus.connectPublisher();
                                    Cupus.sendPublicationCustom();
                                    Const.getMainActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            connectionStatus.setText(org.bikeroutes.android.R.string.connected);
                                            connectionStatus.setTextColor(con.getResources().getColor(org.bikeroutes.android.R.color.green));
                                            cupusButton.setText(org.bikeroutes.android.R.string.disconnect_button_text);
                                        }
                                    });
                                }
                            }).start();

                        }
                    };
                    timer.schedule(delayedThreadStartTask, Const.getDelay() * 1000, Const.getDelay() * 1000);
                    buttonToggle = true;
                }
                else
                {
                    delayedThreadStartTask.cancel();
                    connectionStatus.setText(org.bikeroutes.android.R.string.not_connected);
                    connectionStatus.setTextColor(con.getResources().getColor(org.bikeroutes.android.R.color.red));
                    cupusButton.setText(org.bikeroutes.android.R.string.connect_button_text);
                    buttonToggle = false;
                }
            }
        });
    }

    public static void connectToArduino()
    {
        WifiSocketTask mySocket = new WifiSocketTask();
        mySocket.StartArduinoConnectionThread();
    }



}
