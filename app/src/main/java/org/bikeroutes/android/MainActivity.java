package org.bikeroutes.android;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import org.bikeroutes.android.TabsAndFragments.MainViewPager;
import org.bikeroutes.android.TabsAndFragments.SampleFragmentPagerAdapter;
import org.bikeroutes.android.TabsAndFragments.tabs.Analytics;
import org.bikeroutes.android.util.Const;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tab_layout);
        Const.setMainActivity(this);
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        setFragments();
        pleaseRemoveBarsForMe();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        pleaseRemoveBarsForMe();
    }

    /**
     * Sets Fragments ViewPager and disables swipe between fragments
     */
    public void setFragments()
    {
        final MainViewPager mainViewPager = (MainViewPager) findViewById(R.id.viewpager);

        mainViewPager.setPagingEnabled(false);
        SampleFragmentPagerAdapter pagerAdapter = new SampleFragmentPagerAdapter(getSupportFragmentManager(), MainActivity.this);
        mainViewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mainViewPager);
    }



    /**
     * Removes navigation and action bars
     */
    public void pleaseRemoveBarsForMe()
    {
        //******************* HIDING NAVIGATION BAR **********************
        if(Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        //****************************************************************
    }
}
