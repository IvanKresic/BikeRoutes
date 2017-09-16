package org.bikeroutes.android.util;

import android.util.Log;
import android.widget.Toast;

import org.bikeroutes.android.R;
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
import org.oscim.map.Layers;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;


/**
 * Created by ivan on 17.05.16..
 */
public class Cupus {

    private static Publisher publisherUserData = new Publisher("UserData", Const.getBrokerIpAddress(), 10000);
    private static SQLiteQueries myQueries = new SQLiteQueries();
    private static Subscriber subscriber = new Subscriber("clientSubscriber", Const.getBrokerIpAddress(), 10000);
    private static TripletSubscription lastSubscription = null;
    public static MapView mapView;
    private static long category;

    private static void unsubscribeFromLastSubscription()
    {
        if(lastSubscription != null){
            subscriber.unsubscribe(lastSubscription);
        }
    }

    public static void resetMap()
    {

        mapView = Const.getMapView();
        Layers layers = mapView.map().layers();

        if(layers.size() > 2)
        {
            int temp_layers_size = layers.size()-1;
            System.out.println("Sa mape: " + layers.size());
            if(temp_layers_size > 2)
            {
                for (int i = temp_layers_size; i>2;i--) {
                    layers.remove(i);
                }
                System.out.println("Sa mape nakon brisanja: " + layers.size());
            }
        }
    }

    public static void initializeTriplet(int i)
    {
        unsubscribeFromLastSubscription();
        Const.isRouteCalculated=false;
        resetMap();
        switch(i)
        {
            case 0: // EVENTS
                lastSubscription = new TripletSubscription(-1, System.currentTimeMillis());
                lastSubscription.addPredicate(new Triplet("DataType","Event", Operator.EQUAL));
                initializeSubscriber(lastSubscription, 0, false);
                break;

            case 1://TEMPERATURE
                lastSubscription = new TripletSubscription(-1, System.currentTimeMillis());
                lastSubscription.addPredicate(new Triplet("SensorType","Temperature", Operator.EQUAL));
                initializeSubscriber(lastSubscription, 1, false);
                break;

            case 2://HUMIDITY
                lastSubscription = new TripletSubscription(-1, System.currentTimeMillis());
                lastSubscription.addPredicate(new Triplet("SensorType","Humidity", Operator.EQUAL));
                initializeSubscriber(lastSubscription, 2, false);
                break;

            case 3://CO
                lastSubscription = new TripletSubscription(-1, System.currentTimeMillis());
                lastSubscription.addPredicate(new Triplet("SensorType","CO", Operator.EQUAL));
                initializeSubscriber(lastSubscription, 3, false);
                break;

            case 4://NOISE
                lastSubscription = new TripletSubscription(-1, System.currentTimeMillis());
                lastSubscription.addPredicate(new Triplet("SensorType","Noise", Operator.EQUAL));
                initializeSubscriber(lastSubscription, 4, false);
                break;

            case 5://MY ROUTES
                lastSubscription = new TripletSubscription(-1, System.currentTimeMillis());
                lastSubscription.addPredicate(new Triplet("DataType","UserRoutes", Operator.EQUAL));
                lastSubscription.addPredicate(new Triplet("UUID",Const.DeviceId, Operator.EQUAL));
                initializeSubscriber(lastSubscription, 5, false);
                break;

            case 6://POPULAR ROUTES
                lastSubscription = new TripletSubscription(-1, System.currentTimeMillis());
                lastSubscription.addPredicate(new Triplet("DataType","MostPopularRoutes", Operator.EQUAL));
                initializeSubscriber(lastSubscription, 6, true);
                break;
        }

    }

    private static void initializeSubscriber(TripletSubscription ts1, final int shapeType, final boolean popularRoutes){

        /**
         * TODO - hardcoded boundries - needs to be implemented
         */
        //Wien boundries
        /*Coordinate[] coordinates = new Coordinate[5];
        coordinates[0] = new Coordinate(48.154821, 16.245798);//Bottom-left
        coordinates[1] = new Coordinate(48.154821, 16.553674);//Bottom-right
        coordinates[2] = new Coordinate(48.279491, 16.513273);//Top-right
        coordinates[3] = new Coordinate(48.279491, 16.226498);//Top-left
        coordinates[4] = new Coordinate(48.154821, 16.245798);*///First and last same to close polygon


        //Zagreb boundries
/*        Coordinate[] coordinates = new Coordinate[5];
        coordinates[0] = new Coordinate(45.7788,15.9164);//Bottom-left
        coordinates[1] = new Coordinate(45.7788,16.0236);//Bottom-right
        coordinates[2] = new Coordinate(45.8227,16.0236);//Top-right
        coordinates[3] = new Coordinate(45.8227,15.9164);//Top-left
        coordinates[4] = new Coordinate(45.7788,15.9164);*/


        NotificationListener notificationListener = new NotificationListener() {
            @Override
            public void notify(UUID uuid, String s, Publication publication) {
                HashtablePublication notification = (HashtablePublication) publication;
                final HashMap<String, Object> receivedData = notification.getProperties();
                if(receivedData == null)
                    connectSubscriber();
                System.out.println(publication);
                Coordinate[] coordinates1 = ((HashtablePublication) publication).getGeometry().getCoordinates();
                Const.setCoordinates1(coordinates1);
                SubscriptionClassUI drawSubscriptions = new SubscriptionClassUI();
                if (popularRoutes) {
                    category = receivedData.get("Category") == null ? 0 : (long) receivedData.get("Category");
                }
                drawSubscriptions.drawSubscriptionOnMap(shapeType, category);
            }

            @Override
            public void notify(UUID uuid, String s, Subscription subscription, boolean b) {

            }
        };

        subscriber.setNotificationListener(notificationListener);
        connectSubscriber();
        subscriber.subscribe(ts1);
    }

    private static void connectSubscriber()
    {
        subscriber.connect();
       /* try {
            //if(Const.isConnectedToInternet()){
                subscriber.connect();
            //}
            else{
//                Toast toast = new Toast(Const.getContext());
//                toast.setDuration(Toast.LENGTH_LONG);
//                toast.setText(Const.getContext().getString(R.string.no_internet_connection_message));
//                toast.show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    private static void connectPublisher() {
        // connect to broker
        if(!publisherUserData.isConnected()) {
            Log.d("PUBLISHER", "Is publisher connected: " + publisherUserData.isConnected());
            publisherUserData.connect();
        }
    }


    public static void disconnectPublisher() {
        if(publisherUserData.isConnected())
        {
            publisherUserData.disconnectFromBroker();
        }
    }

    public static void sendPublicationCustom() {
        //publisher.isConnected()
        if(true) {
            connectPublisher();
            myQueries.setAndConnectUserDataPublisher(publisherUserData);
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