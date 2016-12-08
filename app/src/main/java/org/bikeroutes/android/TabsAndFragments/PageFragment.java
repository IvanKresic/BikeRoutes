package org.bikeroutes.android.TabsAndFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.bikeroutes.android.BikeRoutesGraphHopper.BikeRoutes;
import org.bikeroutes.android.BikeRoutesGraphHopper.Settings;
import org.bikeroutes.android.TabsAndFragments.tabs.Analytics;
import org.bikeroutes.android.util.Const;


/**
 * Created by ivan on 02.06.16..
 */
public class PageFragment extends Fragment{
    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    Context context;
    View view;
    BikeRoutes bikeMapAndRouting;
    public Analytics analytics;
    Settings mySettings;
    static PageFragment fragment;
    private Bundle savedState = null;

    public static PageFragment newInstance(int page)
    {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        if(savedInstanceState != null && savedState == null) {
            savedState = savedInstanceState;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        context = container.getContext();
        if(mPage == 1) //MAP
        {
            if(bikeMapAndRouting == null) {
                view = inflater.inflate(org.bikeroutes.android.R.layout.map, container, false);
                bikeMapAndRouting = new BikeRoutes(context, view);
                bikeMapAndRouting.setLocationListener();
                bikeMapAndRouting.setMapView(Const.getMainActivity());
                bikeMapAndRouting.setUserDeviceId();
                bikeMapAndRouting.createAndInitializeAllUIObjects(Const.getMainActivity());
            }
            return view;
        }
        else if(mPage == 2) //ANALYTICS
        {
            fragment.getFragmentManager().findFragmentByTag("Analytics");
            if(analytics == null)
            {
                view = inflater.inflate(org.bikeroutes.android.R.layout.activity_analytics, container, false);
                analytics = new Analytics(view);
                analytics.setView();
            }
            return view;
        }
        else if(mPage == 3) //SETTINGS
        {
            if(mySettings == null)
            {
                view = inflater.inflate(org.bikeroutes.android.R.layout.main, container, false);
                mySettings = new Settings(view, container.getContext());
                mySettings.setThisParameters();
                Settings.connectToArduino();
            }
            return view;
        }
        return null;
    }

    @Override
    public void onResume() {
        Log.e("DEBUG", "onResume of HomeFragment");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.e("DEBUG", "onResume of HomeFragment");
        super.onResume();
        this.getFragmentManager().saveFragmentInstanceState(fragment);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the fragment's instance
       getActivity().getSupportFragmentManager().putFragment(outState, "mContent", fragment);
    }

}
