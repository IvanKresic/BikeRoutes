package org.bikeroutes.android.util;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by ivan on 20.05.16..
 */
public class CupusThreadClass implements Runnable
{
    Handler h = new Handler();


    public CupusThreadClass(View view)
    {
        final Button connect = (Button)  view.findViewById(org.bikeroutes.android.R.id.connect_button);
        connect.setText("Please wait ...");
        Timer timer = new Timer();
        TimerTask delayedThreadStartTask = new TimerTask() {
            @Override
            public void run() {

                //captureCDRProcess();
                //moved to TimerTask
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        h.postDelayed(new Runnable(){
                            public void run(){
                                Log.d("HELLO", "Hello from Thread!");
                                Cupus.sendPublicationCustom();
                                connect.setText("Connected");
                                h.postDelayed(this, Const.getDelay());
                            }
                        }, Const.getDelay());

                    }
                }).start();
            }
        };

        timer.schedule(delayedThreadStartTask, Const.getDelay() * 1000); //1 minute

        //************************
    }



    @Override
    public void run()
    {
       Cupus.sendPublicationCustom();
    }
}
