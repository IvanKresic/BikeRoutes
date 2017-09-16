package org.bikeroutes.android;
import android.graphics.drawable.Drawable;
import org.bikeroutes.android.util.Const;
import com.vividsolutions.jts.geom.Coordinate;
import org.oscim.android.MapView;
import org.oscim.android.canvas.AndroidGraphics;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Paint;
import org.oscim.core.GeoPoint;
import org.oscim.layers.vector.PathLayer;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.vector.geometries.Style;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivan on 16.06.16..
 */
public class SubscriptionClassUI {

    private MapView mapView;
    private ItemizedLayer eventMark;
    private List<ItemizedLayer> listOfItems;
    private long category;
    public void drawSubscriptionOnMap(int shapeType, long category) {

        mapView = Const.getMapView();
       //eventMark = new ItemizedLayer<>(mapView.map(), (MarkerSymbol) null);
       //mapView.map().layers().add(eventMark);
        listOfItems = new ArrayList<>();
        this.category = category;
        Coordinate[] coordinates = Const.getCoordinates1();

        /*
        0 - Event
        1 - Temperature
        2 - Humidity
        3 - CO
        4 - Noise
        5 - MyRoutes
        6 - PopularRoutes
         */
        switch(shapeType)
        {
            case 0:
                resetAndInflateTheMap(coordinates, R.drawable.hifi);
                break;
            case 1:
                    resetAndInflateTheMap(coordinates, R.drawable.circle);
                break;
            case 2:

                break;
            case 3:

                break;
            case 4:

                break;
            case 5:
                mapView.map().layers().add(createPathLayer(coordinates, getColor(category)));
                break;
            case 6:
                mapView.map().layers().add(createPathLayer(coordinates, getColor(category)));
                break;
            default:
                resetAndInflateTheMap(coordinates, R.drawable.hifi);
                break;
        }
    }

    private void resetAndInflateTheMap(Coordinate[] coordinates, int resourceId)
    {
        GeoPoint eventPosition;
        eventMark.removeAllItems();
        for (Coordinate coordinate: coordinates) {
            eventPosition = new GeoPoint(coordinate.x, coordinate.y);
            listOfItems.add(createMarker(eventPosition, resourceId));
        }
        eventMark.addItems(listOfItems);
        listOfItems.clear();
    }

    private ItemizedLayer<MarkerItem> createMarker( GeoPoint p, int resource )
    {
        ItemizedLayer<MarkerItem> myMarker = new ItemizedLayer<>(mapView.map(), (MarkerSymbol) null);
        Drawable drawable = Const.getContext().getResources().getDrawable(resource);
        Bitmap bitmap = AndroidGraphics.drawableToBitmap(drawable);
        MarkerSymbol markerSymbol = new MarkerSymbol(bitmap, 0.5f, 1);
        MarkerItem markerItem = new MarkerItem("", "", p);
        markerItem.setMarker(markerSymbol);
        myMarker.addItem(markerItem);
        return myMarker;
    }

    private PathLayer createPolyline(Coordinate[] response, int color)
    {
        PathLayer line;
        Paint paintStroke = AndroidGraphics.newPaint();
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setColor(color);
        paintStroke.setStrokeWidth(8);
        line = new PathLayer(mapView.map(), color);

        List<GeoPoint> geoPoints = line.getPoints();
        for (Coordinate coordinate: response) {
            geoPoints.add(new GeoPoint(coordinate.x, coordinate.y));
        }
        return line;
    }

    private PathLayer createPathLayer(Coordinate[] response, String color) {
        List<GeoPoint> geoPoints = new ArrayList<>();
        Style style = Style.builder()
                .generalization(Style.GENERALIZATION_SMALL)
                .strokeColor(color)
                .strokeWidth(4 * Const.getContext().getResources().getDisplayMetrics().density)
                .build();
        PathLayer line = new PathLayer(mapView.map(), style);

        for (Coordinate coordinate: response) {
            if(!Double.isNaN(coordinate.z)) {
                geoPoints.add(new GeoPoint(coordinate.x, coordinate.y));
                geoPoints.add(new GeoPoint(coordinate.y, coordinate.z));
            }
            else{
                geoPoints.add(new GeoPoint(coordinate.x, coordinate.y));
            }
        }
        line.setPoints(geoPoints);
        return line;
    }

    private String getColor(long category)
    {
        String pathColor = "";

        if(category == 0)
        {
            pathColor = "#0000ff";
        }
        if(category == 1)
        {
            pathColor = "#cc0000";
        }
        else if(category == 2)
        {
            pathColor = "#eabd03";
        }
        else if(category == 3)
        {
            pathColor = "#00d848";
        }
        return pathColor;
    }
}
