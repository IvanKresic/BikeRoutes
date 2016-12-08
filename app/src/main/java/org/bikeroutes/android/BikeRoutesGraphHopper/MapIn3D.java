package org.bikeroutes.android.BikeRoutesGraphHopper;

import android.content.Context;
import android.view.View;

import java.io.File;

/**
 * Created by Ivan on 9/24/2016.
 */
public class MapIn3D {

    private View myView;
    private File mapsFolder;
    Context context;
    View view;

    public MapIn3D(Context context, View view)
    {
        this.context = context;
        this.view = view;
    }

    public void initialize3DMap()
    {
        /*
        // 1. The initial step: register your license. This must be done before using MapView!
        com.nutiteq.ui.MapView.registerLicense("a5c90f5cbd51b3b86709f6a82a378ebb", context);

        // Create map view
        mapView = (com.nutiteq.ui.MapView) view.findViewById(R.id.mapView);

        // Create base layer. Use vector style from assets
        VectorTileLayer baseLayer = new NutiteqOnlineVectorTileLayer("nutibright-v2a.zip");



        // Add layer to map
        mapView.getLayers().add(baseLayer);*/
    }



}
