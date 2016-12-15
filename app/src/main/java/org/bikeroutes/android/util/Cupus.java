package org.bikeroutes.android.util;

import android.util.Log;

import org.bikeroutes.android.SubscriptionClassUI;
import org.bikeroutes.android.databaseCommunication.SQLiteQueries;
import com.vividsolutions.jts.geom.Coordinate;

import org.openiot.cupus.artefact.HashtablePublication;
import org.openiot.cupus.artefact.Publication;
import org.openiot.cupus.artefact.Subscription;
import org.openiot.cupus.artefact.TripletSubscription;
import org.openiot.cupus.common.Triplet;
import org.openiot.cupus.common.enums.Operator;
import org.openiot.cupus.entity.publisher.Publisher;
import org.openiot.cupus.entity.subscriber.NotificationListener;
import org.openiot.cupus.entity.subscriber.Subscriber;
import org.oscim.android.MapView;

import java.util.HashMap;
import java.util.UUID;


/**
 * Created by ivan on 17.05.16..
 */
public class Cupus {

    public static Publisher publisherUserData = new Publisher("UserData", Const.getBrokerIpAddress(), 10000);
    static SQLiteQueries myQueries = new SQLiteQueries();
    public static Subscriber subscriber;
    public static TripletSubscription lastSubscription = null;
    public static MapView mapView;
    static int publication_duration = 1800000;

    public static void unsubscribeFromLastSubscription()
    {
        if(lastSubscription != null)
        {
            subscriber.unsubscribe(lastSubscription);
        }
    }

    public static void resetMap()
    {
        /*
        mapView = Const.getMapView();
        Layers layers = mapView.getLayerManager().getLayers();

        //*****************
        if(mapView.getLayerManager().getLayers().size() <= 2)
        {
            System.out.println("Za manje od 2: "+ mapView.getLayerManager().getLayers().size());
            //layers.remove(2);
        }
        else if(mapView.getLayerManager().getLayers().size() > 2)
        {
            int temp_layers_size = layers.size()-1;
            System.out.println("Sa mape: " + mapView.getLayerManager().getLayers().size());
            if(temp_layers_size > 2)
            {
                for (int i = temp_layers_size; i>=2;i--) {
                    layers.remove(i);
                }
                System.out.println("Sa mape nakon brisanja: " +mapView.getLayerManager().getLayers().size());
            }
        }
        */

    }

    public static void initializeTriplet(int i)
    {
        unsubscribeFromLastSubscription();
        resetMap();
        switch(i)
        {
            case 0: // EVENTS
                lastSubscription = new TripletSubscription(-1, System.currentTimeMillis());
                lastSubscription.addPredicate(new Triplet("DataType","Event", Operator.EQUAL));
                initializeSubscriber(lastSubscription, 0);
                break;

            case 1://TEMPERATURE
                lastSubscription = new TripletSubscription(-1, System.currentTimeMillis());
                lastSubscription.addPredicate(new Triplet("SensorType","Temperature", Operator.EQUAL));
                initializeSubscriber(lastSubscription, 1);
                break;

            case 2://HUMIDITY
                lastSubscription = new TripletSubscription(-1, System.currentTimeMillis());
                lastSubscription.addPredicate(new Triplet("SensorType","Humidity", Operator.EQUAL));
                initializeSubscriber(lastSubscription, 2);
                break;

            case 3://CO
                lastSubscription = new TripletSubscription(-1, System.currentTimeMillis());
                lastSubscription.addPredicate(new Triplet("SensorType","CO", Operator.EQUAL));
                initializeSubscriber(lastSubscription, 3);
                break;

            case 4://NOISE
                lastSubscription = new TripletSubscription(-1, System.currentTimeMillis());
                lastSubscription.addPredicate(new Triplet("SensorType","Noise", Operator.EQUAL));
                initializeSubscriber(lastSubscription, 4);
                break;

            case 5://MY ROUTES
                lastSubscription = new TripletSubscription(-1, System.currentTimeMillis());
                lastSubscription.addPredicate(new Triplet("DataType","MyRoutes", Operator.EQUAL));
                lastSubscription.addPredicate(new Triplet("UUID",Const.DeviceId, Operator.EQUAL));
                initializeSubscriber(lastSubscription, 5);
                break;

            case 6://POPULAR ROUTES
                lastSubscription = new TripletSubscription(-1, System.currentTimeMillis());
                lastSubscription.addPredicate(new Triplet("DataType","PopularRoutes", Operator.EQUAL));
                initializeSubscriber(lastSubscription, 6);
                break;
        }

    }

    public static void initializeSubscriber(TripletSubscription ts1, final int shapeType){

        Coordinate[] coordinates = new Coordinate[5];
        coordinates[0] = new Coordinate(45.7788,15.9164);
        coordinates[1] = new Coordinate(45.7788,16.0236);
        coordinates[2] = new Coordinate(45.8227,16.0236);
        coordinates[3] = new Coordinate(45.8227,15.9164);
        coordinates[4] = new Coordinate(45.7788,15.9164);

        subscriber = new Subscriber("clientSubscriber", Const.getBrokerIpAddress(), 10000);
        subscriber.setNotificationListener(new NotificationListener() {
            @Override
            public void notify(UUID subscriberId, String subscriberName, Publication publication) {
                HashtablePublication notification = (HashtablePublication) publication;
                final HashMap<String, Object> receivedData = notification.getProperties();
                System.out.println(publication);
                Coordinate[] coordinates1 = ((HashtablePublication) publication).getGeometry().getCoordinates();
                Const.setCoordinates1(coordinates1);
                SubscriptionClassUI drawSubs = new SubscriptionClassUI();
                drawSubs.drawSubscriptionOnMap(shapeType);
            }



            @Override
            public void notify(UUID uuid, String s, Subscription subscription, boolean b) {

            }
        });

        // connect to the broker
        subscriber.connect();
        ts1.setGPSCoordinates(coordinates);
        subscriber.subscribe(ts1);
    }



    public static boolean isConnected()
    {
        boolean returnType = false;
        try {
            publisherUserData.isConnected();
            returnType = true;
        }
        catch (Exception e)
        {
            returnType =  false;
        }
        return returnType;
    }

    public static void connectPublisher()
    {
        if(!publisherUserData.isConnected()) {
            Log.d("PUBLISHER", Const.getBrokerIpAddress());
            // connect to broker
            publisherUserData.connect();
        }
                if(Cupus.isConnected())
                {
                    Log.d("PUBLISHER", "Spojio sam se");
                }
    }


    public static void disconnectPublisher()
    {
        if(publisherUserData.isConnected())
        {
            publisherUserData.disconnectFromBroker();
        }
    }

    public static void sendPublicationCustom() {
        //publisher.isConnected()
        if(true) {
            myQueries.readFromDatabaseBikeRoutes(Const.getContext());
            if (Const.readyToResetUserDatabase) {
                myQueries.deleteRecordsFromDatabaseBikeRoutes(Const.getContext());
            }
//            myQueries.readFromDatabaseArduinoData(Const.getContext());
//            if(Const.readyToResetArduinoDatabase)
//            {
//                myQueries.deleteRecordsFromDatabaseArduinoData(Const.getContext());
//            }
        }
    }


}


