package org.bikeroutes.android.util.mapsCore;

import org.bikeroutes.android.util.Const;
import org.oscim.android.MapView;
import org.oscim.core.GeoPoint;

/**
 * Created by Ivan on 6/6/2017.
 */
public class MapEngine {

    private static void calculateAngleBetweenGeolocations(GeoPoint currentPosition, GeoPoint endPosition)
    {
        MapView mapView = Const.getMapView();
        mapView.animate().rotationXBy((float) (currentPosition.getLatitude() - endPosition.getLatitude()));
        mapView.animate().rotationYBy((float) (currentPosition.getLongitude() - endPosition.getLongitude()));
    }
}
