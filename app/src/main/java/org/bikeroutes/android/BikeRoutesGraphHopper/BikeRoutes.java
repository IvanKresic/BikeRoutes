package org.bikeroutes.android.BikeRoutesGraphHopper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.bikeroutes.android.AndroidDownloader;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import org.bikeroutes.android.AndroidHelper;
import org.bikeroutes.android.GHAsyncTask;
import org.bikeroutes.android.databaseCommunication.SQLiteQueries;
import org.bikeroutes.android.util.Const;
import org.bikeroutes.android.util.Cupus;
import com.graphhopper.util.Constants;
import com.graphhopper.util.Helper;
import com.graphhopper.util.PointList;
import com.graphhopper.util.ProgressListener;
import com.graphhopper.util.StopWatch;

import org.oscim.android.MapView;
import org.oscim.android.canvas.AndroidGraphics;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.GeoPoint;
import org.oscim.core.Tile;
import org.oscim.event.Gesture;
import org.oscim.event.GestureListener;
import org.oscim.event.MotionEvent;
import org.oscim.layers.Layer;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.layers.vector.PathLayer;
import org.oscim.layers.vector.geometries.Style;
import org.oscim.theme.VtmThemes;
import org.oscim.theme.XmlRenderThemeStyleLayer;
import org.oscim.theme.XmlRenderThemeStyleMenu;
import org.oscim.tiling.source.mapfile.MapFileTileSource;
import org.w3c.dom.Attr;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;


/**
 * Created by ivan on 02.06.16..
 */
public class BikeRoutes{

    private static MapView mapView;
    private GraphHopper hopper;
    private GeoPoint start;
    private GeoPoint end;
    private volatile boolean prepareInProgress = false;
    private volatile boolean shortestPathRunning = false;
    private String currentArea = "croatia";
    //private String fileListURL = "http://download2.graphhopper.com/public/maps/0.6/";
    //private String prefixURL = fileListURL;
    private String downloadURL;
    private File mapsFolder;
    private LocationManager lm;
    private LocationListener locationListener;
    private static TelephonyManager tm;
    private static String DeviceId;
    private boolean zoom = false;
    private static  GeoPoint position ;
    private ItemizedLayer<MarkerItem> itemizedLayer;
    private PathLayer pathLayer;
    private SQLiteQueries sql = new SQLiteQueries();
    private MarkerItem mark;
    private Context context;
    private View fragmentView;
    private View routingData;
    private View apiCall;
    private String provider;
    private Activity activity;
    private RelativeLayout mapLayout;

    public BikeRoutes(Context cont, View view)
    {
        context = cont;
        fragmentView = view;
        Const.savedFragment = view;
    }

    public void setLocationListener()
    {
        /**
         * retrieve a reference to provide access to the system location
         * services
         */
        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        /**
         * explicitly select the GPS provider, create a set of Criteria and let
         * android choose the best provider available
         */

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        provider = lm.getBestProvider(criteria, true);
        locationListener = new MyLocationListener();
    }

    public void setMapView(Activity activity) {
        this.activity = activity;
        Tile.SIZE = Tile.calculateTileSize(activity.getResources().getDisplayMetrics().scaledDensity);

        try {
            mapLayout.removeView(mapView);
        }
        catch (Exception e)
        {
            Log.d("DEBUG", "Map not added yet!");
            e.getMessage();
        }

        mapView = new MapView(activity.getApplicationContext());


        mapView.setClickable(true);
        itemizedLayer = new ItemizedLayer<>(mapView.map(), (MarkerSymbol) null);

        boolean greaterOrEqKitkat = Build.VERSION.SDK_INT >= 19;
        if (greaterOrEqKitkat)
        {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            {
                logUser("GraphHopper is not usable without an external storage!");
                return;
            }
            mapsFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "/graphhopper/maps/");
        } else
            mapsFolder = new File(Environment.getExternalStorageDirectory(), "/graphhopper/maps/");

        if (!mapsFolder.exists())
        { mapsFolder.mkdirs();}

        lm.requestLocationUpdates(provider, Const.getGpsDelay(), 0.5f, locationListener);
        Location lastKnownLocation = lm.getLastKnownLocation(provider);
        if(lastKnownLocation != null) {
            position = new GeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        }
        else
            position = new GeoPoint(0.0,0.0);
        mark = createMarkerItem(position, org.bikeroutes.android.R.drawable.location);
        itemizedLayer.addItem(mark);
    }

    public void setUserDeviceId()
    {
        tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        //Ovdje dohvaćam podatke o samom uređaju, kao jedinstveni ID
        String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" +Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        DeviceId = deviceUuid.toString();
        Const.DeviceId = deviceUuid.toString();
    }

    public void createAndInitializeAllUIObjects(final Activity activity)
    {
        ImageButton enterStartAndEndPoint;
        Button calculateRouteButton;
        ImageButton callApiButton;
        Button eventsButton;
        Button temperatureButton;
        Button humidityButton;
        Button coButton;
        Button noiseButton;
        Button popularRoutesButton;
        Button myRoutesButton;
        //final EditText locationTo;

        chooseAreaFromLocal();
        this.activity = activity;
        enterStartAndEndPoint = (ImageButton) fragmentView.findViewById(org.bikeroutes.android.R.id.direction_button);
        calculateRouteButton = (Button) fragmentView.findViewById(org.bikeroutes.android.R.id.calculateRouteButton);
        callApiButton = (ImageButton) fragmentView.findViewById(org.bikeroutes.android.R.id.apiCallButton);
        routingData = fragmentView.findViewById(org.bikeroutes.android.R.id.dataForRouting);
        apiCall = fragmentView.findViewById(org.bikeroutes.android.R.id.apiCallLayout);
        eventsButton = (Button) fragmentView.findViewById(org.bikeroutes.android.R.id.events_button);
        temperatureButton = (Button) fragmentView.findViewById(org.bikeroutes.android.R.id.temperature);
        humidityButton = (Button) fragmentView.findViewById(org.bikeroutes.android.R.id.humidity);
        coButton = (Button) fragmentView.findViewById(org.bikeroutes.android.R.id.co);
        noiseButton = (Button) fragmentView.findViewById(org.bikeroutes.android.R.id.noise);
        popularRoutesButton = (Button) fragmentView.findViewById(org.bikeroutes.android.R.id.popular_routes_button);
        myRoutesButton = (Button) fragmentView.findViewById(org.bikeroutes.android.R.id.my_routes_button);
        //locationTo = (EditText) fragmentView.findViewById(R.id.locationTo);
        enterStartAndEndPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(routingData.getVisibility() == View.VISIBLE)
                {
                    routingData.setVisibility(View.GONE);
                }
                else if(apiCall.getVisibility() == View.VISIBLE)
                {
                    apiCall.setVisibility(View.GONE);
                    routingData.setVisibility(View.VISIBLE);
                }
                else
                {
                    routingData.setVisibility(View.VISIBLE);
                }
            }
        });

        calculateRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(position != null) {
                    start = position;
                    itemizedLayer.addItem(createMarkerItem(start, org.bikeroutes.android.R.drawable.flag_red));
                    itemizedLayer.addItem(createMarkerItem(end, org.bikeroutes.android.R.drawable.flag_green));
                    mapView.map().updateMap(true);
                }
                calcPath(start.getLatitude(), start.getLongitude(), end.getLatitude(),
                        end.getLongitude());
            }
        });

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                activity.getFragmentManager().findFragmentById(org.bikeroutes.android.R.id.place_autocomplete_fragment);


        LatLngBounds latLngBounds = new LatLngBounds(
            new LatLng(48.103967, 16.249577),//S-E
                new LatLng(48.290364, 16.464802)//N-W
         );
        autocompleteFragment.setBoundsBias(latLngBounds);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                end = new GeoPoint(place.getLatLng().latitude, place.getLatLng().longitude);
                Log.i("TAG", "Place:" + place.getName());
            }
            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });

        callApiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(apiCall.getVisibility() == View.VISIBLE)
                {
                    apiCall.setVisibility(View.GONE);
                }
                else if(routingData.getVisibility() == View.VISIBLE)
                {
                        routingData.setVisibility(View.GONE);
                        apiCall.setVisibility(View.VISIBLE);
                }
                else
                {
                    apiCall.setVisibility(View.VISIBLE);
                }
            }
        });
        calculateRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                routingData.setVisibility(View.GONE);
            }
        });
        eventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cupus.initializeTriplet(0);
                apiCall.setVisibility(View.GONE);
            }
        });

        //TIPKA ZA PRETPLAĆIVANJE NA TEMPERATURU
        temperatureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cupus.initializeTriplet(1);
                apiCall.setVisibility(View.GONE);
            }
        });

        //TIPKA ZA PRETPLAĆIVANJE NA VLAŽNOST
        humidityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cupus.initializeTriplet(2);
                apiCall.setVisibility(View.GONE);
            }
        });

        //TIPKA ZA PRETPLAĆIVANJE NA CO
        coButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cupus.initializeTriplet(3);
                apiCall.setVisibility(View.GONE);
            }
        });

        //TIPKA ZA PRETPLAĆIVANJE NA BUKU
        noiseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cupus.initializeTriplet(4);
                apiCall.setVisibility(View.GONE);
            }
        });

        myRoutesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cupus.initializeTriplet(5);
                apiCall.setVisibility(View.GONE);
            }
        });

        popularRoutesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cupus.initializeTriplet(6);
                apiCall.setVisibility(View.GONE);
            }
        });

        mapView.map().animator().animateTo(position);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private void chooseAreaFromLocal()
    {
        List<String> nameList = new ArrayList<>();
        String[] files = mapsFolder.list(new FilenameFilter()
        {
            @Override
            public boolean accept( File dir, String filename )
            {
                return filename != null
                        && (filename.endsWith(".ghz") || filename
                        .endsWith("-gh"));
            }
        });
        for (String file : files)
        {
            nameList.add(file);
        }

        if (nameList.isEmpty())
            return;

        chooseArea(nameList);
    }

    private void chooseArea(List<String> nameList)
    {
        final Map<String, String> nameToFullName = new TreeMap<>();
        for (String fullName : nameList)
        {
            String tmp = Helper.pruneFileEnd(fullName);
            if (tmp.endsWith("-gh"))
                tmp = tmp.substring(0, tmp.length() - 3);

            tmp = AndroidHelper.getFileName(tmp);
            nameToFullName.put(tmp, fullName);
        }
        nameList.clear();
        nameList.addAll(nameToFullName.keySet());
        String myArea = nameList.get(0);
        Const.setCurrentMap(nameList.get(0));
        String fullPathToFile = nameToFullName.get(myArea);
        Const.fullPathToFile = fullPathToFile;
        initFiles(fullPathToFile);
        }

    private void initFiles( String area )
    {
        prepareInProgress = true;
        currentArea = area;
        downloadingFiles();
    }

    private void downloadingFiles()
    {
        final File areaFolder = new File(mapsFolder, currentArea);
        if (downloadURL == null || areaFolder.exists())
        {
            loadMap(areaFolder);
            return;
        }

        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Downloading and uncompressing " + downloadURL);
        dialog.setIndeterminate(false);
        dialog.setMax(100);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.show();

        new GHAsyncTask<Void, Integer, Object>()
        {
            protected Object saveDoInBackground( Void... _ignore )
                    throws Exception
            {
                String localFolder = Helper.pruneFileEnd(AndroidHelper.getFileName(downloadURL));
                localFolder = new File(mapsFolder, localFolder + "-gh").getAbsolutePath();
                log("downloading & unzipping " + downloadURL + " to " + localFolder);
                AndroidDownloader downloader = new AndroidDownloader();
                downloader.setTimeout(30000);
                downloader.downloadAndUnzip(downloadURL, localFolder,
                        new ProgressListener()
                        {
                            @Override
                            public void update( long val )
                            {
                                publishProgress((int) val);
                            }
                        });
                return null;
            }

            protected void onProgressUpdate( Integer... values )
            {
                super.onProgressUpdate(values);
                dialog.setProgress(values[0]);
            }

            protected void onPostExecute( Object _ignore )
            {
                dialog.hide();
                if (hasError())
                {
                    String str = "An error happend while retrieving maps:" + getErrorMessage();
                    log(str, getError());
                    logUser(str);
                } else
                {
                    loadMap(areaFolder);
                }
            }
        }.execute();
    }

    private void loadMap( File areaFolder )
    {
        // Long press receiver

        mapView.map().layers().add(new LongPressLayer(mapView.map()));


        // Map file source
        MapFileTileSource tileSource = new MapFileTileSource();
        tileSource.setMapFile(new File(areaFolder, currentArea + ".map").getAbsolutePath());
        VectorTileLayer l = mapView.map().setBaseMap(tileSource);
        mapView.map().setTheme(VtmThemes.DEFAULT);
        mapView.map().layers().add(new BuildingLayer(mapView.map(), l));
        mapView.map().layers().add(new LabelLayer(mapView.map(), l));

        // Markers layer
        mapView.map().layers().add(itemizedLayer);

        // Map position
        GeoPoint mapCenter = null;
        try
        {
            mapCenter  = tileSource.getMapInfo().boundingBox.getCenterPoint();
        }
        catch(Exception e)
        {
            e.getMessage();
        }
        mapView.map().setMapPosition(mapCenter.getLatitude(), mapCenter.getLongitude(), 1 << 15);
        mapLayout = (RelativeLayout) Const.savedFragment.findViewById(org.bikeroutes.android.R.id.map_layout);
        mapLayout.addView(mapView, 0);
        loadGraphStorage();
    }

    private void loadGraphStorage()
    {
        logUser("loading graph (" + Constants.VERSION + ") ... ");
        new GHAsyncTask<Void, Void, Path>()
        {
            protected Path saveDoInBackground( Void... v ) throws Exception
            {
                GraphHopper tmpHopp = new GraphHopper().forMobile();
                tmpHopp.load(new File(mapsFolder, currentArea).getAbsolutePath());
                log("found graph " + tmpHopp.getGraphHopperStorage().toString() + ", nodes:" + tmpHopp.getGraphHopperStorage().getNodes());
                hopper = tmpHopp;
                return null;
            }

            protected void onPostExecute( Path o )
            {
                if (hasError())
                {
                    logUser("An error happend while creating graph:"
                            + getErrorMessage());
                } else
                {
                    logUser("Finished loading graph. Press long to define where to start and end the route.");
                }

                finishPrepare();
            }
        }.execute();
    }

    private void finishPrepare()
    {
        prepareInProgress = false;
        zoom = false;
        Const.setContext(context);
        //testThis();
    }

    private void testThis()
    {
        XmlRenderThemeStyleMenu xmlRenderThemeStyleMenu = new XmlRenderThemeStyleMenu("","","");
        Map<String, XmlRenderThemeStyleLayer> layerMap = xmlRenderThemeStyleMenu.getLayers();
        int i=0;
        while(layerMap.values().iterator().hasNext())
        {
            Log.d("Ovo je iz mape",layerMap.get(i).getId());
        }
    }

    private MarkerItem createMarkerItem(GeoPoint p, int resource) {
        //Fix this
        Drawable drawable = activity.getApplication().getApplicationContext().getResources().getDrawable(resource);
        Bitmap bitmap = AndroidGraphics.drawableToBitmap(drawable);
        MarkerSymbol markerSymbol = new MarkerSymbol(bitmap, 0.5f, 1);
        MarkerItem markerItem = new MarkerItem("", "", p);
        markerItem.setMarker(markerSymbol);
        return markerItem;
    }

    private void calcPath( final double fromLat, final double fromLon,
                          final double toLat, final double toLon )
    {

        log("calculating path ...");
        new AsyncTask<Void, Void, PathWrapper>()
        {
            float time;

            protected PathWrapper doInBackground( Void... v )
            {
                StopWatch sw = new StopWatch().start();
                GHRequest req = new GHRequest(fromLat, fromLon, toLat, toLon).setAlgorithm("dijkstrabi");
                req.getHints().
                        put("instructions", "true");
                GHResponse resp = hopper.route(req);
                time = sw.stop().getSeconds();
                return resp.getBest();
            }

            protected void onPostExecute( PathWrapper resp )
            {
                if (!resp.hasErrors())
                {
                    log("from:" + fromLat + "," + fromLon + " to:" + toLat + ","
                            + toLon + " found path with distance:" + resp.getDistance()
                            / 1000f + ", nodes:" + resp.getPoints().getSize() + ", time:"
                            + time + " " + resp.getDebugInfo());
                    logUser("the route is " + (int) (resp.getDistance() / 100) / 10f
                            + "km long, time:" + resp.getTime() / 60000f + "min, debug:" + time);

                    pathLayer = createPathLayer(resp);
                    mapView.map().layers().add(pathLayer);
                    mapView.map().updateMap(true);
                } else
                {
                    logUser("Error:" + resp.getErrors());
                }
                shortestPathRunning = false;
            }
        }.execute();
    }

    private boolean onMapTap( GeoPoint p )
    {
        if (!isReady())
            return false;

        if (shortestPathRunning)
        {
            logUser("Calculation still in progress");
            return false;
        }

        if (start != null && end == null) {
            end = p;
            shortestPathRunning = true;
            itemizedLayer.addItem(createMarkerItem(p, org.bikeroutes.android.R.drawable.flag_red));
            mapView.map().updateMap(true);

            calcPath(start.getLatitude(), start.getLongitude(), end.getLatitude(),
                    end.getLongitude());
        } else
        {
            start = p;
            end = null;

            // remove all layers but the first one, which is the map
           int temp_layers_size = itemizedLayer.size()-1;
            Log.d("MAPA", "Sa mape prije brisanja: " +temp_layers_size);
            if(temp_layers_size > 1)
            {
                for (int i = temp_layers_size; i>=1;i--) {
                    itemizedLayer.removeItem(i);
                }
                Log.d("MAPA", "Sa mape nakon brisanja: " +itemizedLayer.size());
            }

            mapView.map().layers().remove(pathLayer);
            //itemizedLayer.removeAllItems();

            itemizedLayer.addItem(createMarkerItem(start, org.bikeroutes.android.R.drawable.flag_green));
            mapView.map().updateMap(true);
        }
        return true;
    }

    private PathLayer createPathLayer(PathWrapper response) {
        Style style = Style.builder()
                .generalization(Style.GENERALIZATION_SMALL)
                .strokeColor(0x9900cc33)
                .strokeWidth(4 * Const.getContext().getResources().getDisplayMetrics().density)
                .build();
        PathLayer pathLayer = new PathLayer(mapView.map(), style);
        List<GeoPoint> geoPoints = new ArrayList<>();
        PointList pointList = response.getPoints();
        for (int i = 0; i < pointList.getSize(); i++)
            geoPoints.add(new GeoPoint(pointList.getLatitude(i), pointList.getLongitude(i)));
        pathLayer.setPoints(geoPoints);
        return pathLayer;
    }

    /**
     * Deprecated
     */
   /* private Polyline createPolyline( PathWrapper response )
    {
        Paint paintStroke = AndroidGraphicFactory.INSTANCE.createPaint();
        paintStroke.setStyle(Style.STROKE);
        paintStroke.setColor(Color.argb(170, 0, 3, 230));
        /*paintStroke.setDashPathEffect(new float[] {25, 15});
        paintStroke.setStrokeWidth(8);

        line = new Polyline(paintStroke, AndroidGraphicFactory.INSTANCE);
        List<LatLong> geoPoints = line.getLatLongs();
        PointList tmp = response.getPoints();
        for (int i = 0; i < response.getPoints().getSize(); i++)
        {
            geoPoints.add(new LatLong(tmp.getLatitude(i), tmp.getLongitude(i)));
        }

        return line;
    }*/

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            double longi = loc.getLongitude();
            double lati = loc.getLatitude();
            long timestamp = loc.getTime();

            position = new GeoPoint(lati, longi);
            if(!zoom) {
                zoom = true;
            }
            itemizedLayer.removeItem(mark);
            mark.geoPoint = position;
            itemizedLayer.addItem(mark);
            itemizedLayer.map().updateMap(true);
            Const.setLatitude(lati);
            Const.setLongitude(longi);
            Const.setTimestamp(timestamp);

            //***********************************
            // Slanje podataka u bazu
            //***********************************
            try {
                sql.insertIntoTableBikeRoutes(context, DeviceId,lati,longi,timestamp);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    }




    private boolean isReady()
    {
        // only return true if already loaded
        if (hopper != null)
            return true;

        if (prepareInProgress)
        {
            logUser("Preparation still in progress");
            return false;
        }
        logUser("Prepare finished but hopper not ready. This happens when there was an error while loading the files");
        return false;
    }

    private void log( String str )
    {
        Log.i("GH", str);
    }

    private void log( String str, Throwable t )
    {
        Log.i("GH", str, t);
    }

    private void logUser( String str )
    {
        log(str);
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }


    private class LongPressLayer extends Layer implements GestureListener {

        LongPressLayer(org.oscim.map.Map map) {
            super(map);
        }

        @Override
        public boolean onGesture(Gesture g, MotionEvent e) {
            if (g instanceof Gesture.LongPress) {
                GeoPoint p = mMap.viewport().fromScreenPoint(e.getX(), e.getY());
                return onMapTap(p);
            }
            return false;
        }
    }

    public static void pauseMap()
    {
        Log.e("DEBUG", "onPause of Map Home Fragment");
        mapView.onPause();
    }

    public static void resumeMap()
    {
        Log.e("DEBUG", "onResume of Map Home Fragment");

        mapView.onResume();
        mapView.map().clearMap();
        mapView.map().updateMap(true);
    }
}