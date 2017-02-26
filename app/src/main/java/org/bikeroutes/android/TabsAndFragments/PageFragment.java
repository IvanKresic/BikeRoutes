package org.bikeroutes.android.TabsAndFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.bikeroutes.android.BikeRoutesGraphHopper.BikeRoutes;
import org.bikeroutes.android.BikeRoutesGraphHopper.Settings;
import org.bikeroutes.android.MainActivity;
import org.bikeroutes.android.R;
import org.bikeroutes.android.TabsAndFragments.tabs.Analytics;
import org.bikeroutes.android.util.Const;

import static org.bikeroutes.android.util.Cupus.mapView;


/**
 * Created by ivan on 02.06.16..
 */
public class PageFragment extends Fragment{
    public static final String ARG_PAGE = "ARG_PAGE";
    private boolean isAnalyticsLoaded = false;
    private int mPage;
    private Context context;
    private View view;
    private BikeRoutes bikeMapAndRouting;
    public Analytics analytics;
    private Settings mySettings;
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
        final MainViewPager mainViewPager = (MainViewPager) getActivity().findViewById(R.id.viewpager);
        context = container.getContext();

        mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0)
                {
                    BikeRoutes.resumeMap();
                }

                if(position == 1)
                {
                    if(isAnalyticsLoaded)
                        analytics.recreateAllData();
                    BikeRoutes.pauseMap();
                }
                if(position == 2)
                {
                    BikeRoutes.pauseMap();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if(mPage == 1) //MAP
        {
            if(bikeMapAndRouting == null) {
                view = inflater.inflate(org.bikeroutes.android.R.layout.map, container, false);
                bikeMapAndRouting = new BikeRoutes(context, view);
            }
                bikeMapAndRouting.setLocationListener();
                bikeMapAndRouting.setUserDeviceId();
                bikeMapAndRouting.setMapView(Const.getMainActivity());
                bikeMapAndRouting.createAndInitializeAllUIObjects(Const.getMainActivity());
            return view;
        }
        else if(mPage == 2) //ANALYTICS
        {
            fragment.getFragmentManager().findFragmentByTag("Analytics");
            if(analytics == null)
            {
                view = inflater.inflate(org.bikeroutes.android.R.layout.activity_analytics, container, false);
                analytics = new Analytics(view);
                analytics.recreateAllData();
                isAnalyticsLoaded = true;
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
        this.getFragmentManager().saveFragmentInstanceState(fragment);
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(outState != null && savedState == null) {
            savedState = outState;
        }
       getActivity().getSupportFragmentManager().putFragment(outState, "mContent", fragment);
    }
}
