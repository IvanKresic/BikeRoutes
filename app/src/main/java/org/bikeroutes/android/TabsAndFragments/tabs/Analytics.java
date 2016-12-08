package org.bikeroutes.android.TabsAndFragments.tabs;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;

/**
 * Created by Ivan on 12/6/2016.
 */

public class Analytics {

    private View view;
    private static ProgressBar numberOfKilometersDay;
    private static ProgressBar numberOfKilometersDa;
    private static ProgressBar numberOfKilometersWeek;
    private static ProgressBar numberOfKilometersWee;
    private static ProgressBar numberOfKilometersMonth;
    private static ProgressBar numberOfKilometersMont;
    private static ObjectAnimator animationNumberOfKilometersDay;
    private static ObjectAnimator animationNumberOfKilometersDa;
    private static ObjectAnimator animationNumberOfKilometersWeek;
    private static ObjectAnimator animationNumberOfKilometersWee;
    private static ObjectAnimator animationNumberOfKilometersMonth;
    private static ObjectAnimator animationNumberOfKilometersMont;

    public Analytics(View view)
    {
        this.view = view;
        numberOfKilometersDay = (ProgressBar) view.findViewById(org.bikeroutes.android.R.id.numberOfKilometersDay);
        numberOfKilometersDa = (ProgressBar) view.findViewById(org.bikeroutes.android.R.id.numberOfKilometersDa);
        numberOfKilometersWeek = (ProgressBar) view.findViewById(org.bikeroutes.android.R.id.numberOfKilometersWeek);
        numberOfKilometersWee = (ProgressBar) view.findViewById(org.bikeroutes.android.R.id.numberOfKilometersWee);
        numberOfKilometersMonth = (ProgressBar) view.findViewById(org.bikeroutes.android.R.id.numberOfKilometersMonth);
        numberOfKilometersMont = (ProgressBar) view.findViewById(org.bikeroutes.android.R.id.numberOfKilometersMont);
    }

    public void setView()
    {
        animationNumberOfKilometersDay = ObjectAnimator.ofInt (numberOfKilometersDay, "progress", 0, 161); // see this max value coming back here, we animale towards that value
        animationNumberOfKilometersDa = ObjectAnimator.ofInt (numberOfKilometersDa, "progress", 0, 161); // see this max value coming back here, we animale towards that value
        animationNumberOfKilometersWeek = ObjectAnimator.ofInt (numberOfKilometersWeek, "progress", 0, 461); // see this max value coming back here, we animale towards that value
        animationNumberOfKilometersWee = ObjectAnimator.ofInt (numberOfKilometersWee, "progress", 0, 461); // see this max value coming back here, we animale towards that value
        animationNumberOfKilometersMonth = ObjectAnimator.ofInt (numberOfKilometersMonth, "progress", 0, 311); // see this max value coming back here, we animale towards that value
        animationNumberOfKilometersMont = ObjectAnimator.ofInt (numberOfKilometersMont, "progress", 0, 311); // see this max value coming back here, we animale towards that value
        animationNumberOfKilometersDay.setDuration (3000);
        animationNumberOfKilometersDa.setDuration (3000);
        animationNumberOfKilometersWeek.setDuration (3000);
        animationNumberOfKilometersWee.setDuration (3000);
        animationNumberOfKilometersMonth.setDuration (3000);
        animationNumberOfKilometersMont.setDuration (3000);
        animationNumberOfKilometersDay.setInterpolator (new DecelerateInterpolator());
        animationNumberOfKilometersDa.setInterpolator (new DecelerateInterpolator());
        animationNumberOfKilometersWeek.setInterpolator (new DecelerateInterpolator());
        animationNumberOfKilometersWee.setInterpolator (new DecelerateInterpolator());
        animationNumberOfKilometersMonth.setInterpolator (new DecelerateInterpolator());
        animationNumberOfKilometersMont.setInterpolator (new DecelerateInterpolator());
        //animation.setStartDelay(3000);
        animationNumberOfKilometersDay.start();
        animationNumberOfKilometersDa.start();
        animationNumberOfKilometersWeek.start();
        animationNumberOfKilometersWee.start();
        animationNumberOfKilometersMonth.start();
        animationNumberOfKilometersMont.start();
    }

    public static void recreateAllData()
    {
        animationNumberOfKilometersDay = ObjectAnimator.ofInt (numberOfKilometersDay, "progress", 0, 161); // see this max value coming back here, we animale towards that value
        animationNumberOfKilometersDa = ObjectAnimator.ofInt (numberOfKilometersDa, "progress", 0, 161); // see this max value coming back here, we animale towards that value
        animationNumberOfKilometersWeek = ObjectAnimator.ofInt (numberOfKilometersWeek, "progress", 0, 461); // see this max value coming back here, we animale towards that value
        animationNumberOfKilometersWee = ObjectAnimator.ofInt (numberOfKilometersWee, "progress", 0, 461); // see this max value coming back here, we animale towards that value
        animationNumberOfKilometersMonth = ObjectAnimator.ofInt (numberOfKilometersMonth, "progress", 0, 311); // see this max value coming back here, we animale towards that value
        animationNumberOfKilometersMont = ObjectAnimator.ofInt (numberOfKilometersMont, "progress", 0, 311); // see this max value coming back here, we animale towards that value
        animationNumberOfKilometersDay.setDuration (3000);
        animationNumberOfKilometersDa.setDuration (3000);
        animationNumberOfKilometersWeek.setDuration (3000);
        animationNumberOfKilometersWee.setDuration (3000);
        animationNumberOfKilometersMonth.setDuration (3000);
        animationNumberOfKilometersMont.setDuration (3000);
        animationNumberOfKilometersDay.setInterpolator (new DecelerateInterpolator());
        animationNumberOfKilometersDa.setInterpolator (new DecelerateInterpolator());
        animationNumberOfKilometersWeek.setInterpolator (new DecelerateInterpolator());
        animationNumberOfKilometersWee.setInterpolator (new DecelerateInterpolator());
        animationNumberOfKilometersMonth.setInterpolator (new DecelerateInterpolator());
        animationNumberOfKilometersMont.setInterpolator (new DecelerateInterpolator());
        animationNumberOfKilometersDay.start();
        animationNumberOfKilometersDa.start();
        animationNumberOfKilometersWeek.start();
        animationNumberOfKilometersWee.start();
        animationNumberOfKilometersMonth.start();
        animationNumberOfKilometersMont.start();
    }
}
