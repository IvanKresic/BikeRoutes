package org.bikeroutes.android.TabsAndFragments.tabs;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.bikeroutes.android.*;


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
    private static TextView currentUsersValue;
    private static TextView calculatedRoutesValue;
    private static TextView currentKilometerValue;

    public Analytics(View view)
    {
        this.view = view;
        numberOfKilometersDay = (ProgressBar) view.findViewById(R.id.numberOfKilometersDay);
        numberOfKilometersDa = (ProgressBar) view.findViewById(R.id.numberOfKilometersDa);
        numberOfKilometersWeek = (ProgressBar) view.findViewById(R.id.numberOfKilometersWeek);
        numberOfKilometersWee = (ProgressBar) view.findViewById(R.id.numberOfKilometersWee);
        numberOfKilometersMonth = (ProgressBar) view.findViewById(R.id.numberOfKilometersMonth);
        numberOfKilometersMont = (ProgressBar) view.findViewById(R.id.numberOfKilometersMont);
        currentUsersValue = (TextView) view.findViewById(R.id.currentUsersValue);
        calculatedRoutesValue = (TextView) view.findViewById(R.id.calculatedRoutesValue);
        currentKilometerValue = (TextView) view.findViewById(R.id.currentKilometerValue);
    }

    public void setView()
    {
        animationNumberOfKilometersDay = ObjectAnimator.ofInt (numberOfKilometersDay, "progress", 0, 387); // see this max value coming back here, we animale towards that value
        animationNumberOfKilometersDa = ObjectAnimator.ofInt (numberOfKilometersDa, "progress", 0, 361); // see this max value coming back here, we animale towards that value
        animationNumberOfKilometersWeek = ObjectAnimator.ofInt (numberOfKilometersWeek, "progress", 0, 461); // see this max value coming back here, we animale towards that value
        animationNumberOfKilometersWee = ObjectAnimator.ofInt (numberOfKilometersWee, "progress", 0, 411); // see this max value coming back here, we animale towards that value
        animationNumberOfKilometersMonth = ObjectAnimator.ofInt (numberOfKilometersMonth, "progress", 0, 311); // see this max value coming back here, we animale towards that value
        animationNumberOfKilometersMont = ObjectAnimator.ofInt (numberOfKilometersMont, "progress", 0, 321); // see this max value coming back here, we animale towards that value
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

    public static void recreateAllData()
    {
        animationNumberOfKilometersDay = ObjectAnimator.ofInt (numberOfKilometersDay, "progress", 0, 387); // see this max value coming back here, we animale towards that value
        animationNumberOfKilometersDa = ObjectAnimator.ofInt (numberOfKilometersDa, "progress", 0, 361); // see this max value coming back here, we animale towards that value
        animationNumberOfKilometersWeek = ObjectAnimator.ofInt (numberOfKilometersWeek, "progress", 0, 461); // see this max value coming back here, we animale towards that value
        animationNumberOfKilometersWee = ObjectAnimator.ofInt (numberOfKilometersWee, "progress", 0, 411); // see this max value coming back here, we animale towards that value
        animationNumberOfKilometersMonth = ObjectAnimator.ofInt (numberOfKilometersMonth, "progress", 0, 311); // see this max value coming back here, we animale towards that value
        animationNumberOfKilometersMont = ObjectAnimator.ofInt (numberOfKilometersMont, "progress", 0, 321); // see this max value coming back here, we animale towards that value
        animationNumberOfKilometersDay.setDuration (1500);
        animationNumberOfKilometersDa.setDuration (1500);
        animationNumberOfKilometersWeek.setDuration (1500);
        animationNumberOfKilometersWee.setDuration (1500);
        animationNumberOfKilometersMonth.setDuration (1500);
        animationNumberOfKilometersMont.setDuration (1500);
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
        animateTextView(0, 465, currentUsersValue);
        animateTextView(0, 162, calculatedRoutesValue);
        animateTextView(0, 12320, currentKilometerValue);
    }

    public static void animateTextView(int initialValue, int finalValue, final TextView textview) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(initialValue, finalValue);
        valueAnimator.setDuration(1500);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                textview.setText(valueAnimator.getAnimatedValue().toString());
            }
        });
        valueAnimator.start();
    }
}
