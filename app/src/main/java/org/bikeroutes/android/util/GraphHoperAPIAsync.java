package org.bikeroutes.android.util;

/**
 * Created by Ivan on 12/21/2015.
 */

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;


import com.google.gson.Gson;

public class GraphHoperAPIAsync extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... params) {

        Log.i("google api async", "uso sam u doInBackgroud..");
        String[] coo;
        String latitude, longitude, timestamp;
        String tmp = params[0].toString();
        coo = tmp.split("#");
        latitude = coo[0];
        longitude = coo[1];
        timestamp = coo[2];

        Log.i("google api async", "parametri su-> latitude: " + latitude
                + ", longitude: " + longitude + ", time: " + timestamp);
        try {

            // long pa lat
            String jsonresp = readUrl(URLs.googleAPI + longitude + ","
                    + latitude);

            // Parsiranje json-a
            Gson gson = new Gson();
            GraphHoperStreetModel model;
            model = gson.fromJson(jsonresp, GraphHoperStreetModel.class);

            String fullAddress = model.getResults()[0].getFormatted_address();
            String address = fullAddress.split(",")[0];
            Log.i("google api async", "adresa je : " + address);
            return address;

        } catch (Exception e) {

            e.printStackTrace();
        }
        Log.i("google api async",  "vracam prazni string....");
        return "";
    }

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

}

