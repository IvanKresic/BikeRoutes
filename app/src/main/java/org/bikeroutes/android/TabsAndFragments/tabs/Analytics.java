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
    private ProgressBar numberOfKilometersDay;
    private ProgressBar numberOfKilometersDa;
    private ProgressBar numberOfKilometersWeek;
    private ProgressBar numberOfKilometersWee;
    private ProgressBar numberOfKilometersMonth;
    private ProgressBar numberOfKilometersMont;
    private TextView currentUsersValue;
    private TextView calculatedRoutesValue;
    private TextView currentKilometerValue;

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

    public void recreateAllData()
    {
        ObjectAnimator animationNumberOfKilometersDay;
        ObjectAnimator animationNumberOfKilometersDa;
        ObjectAnimator animationNumberOfKilometersWeek;
        ObjectAnimator animationNumberOfKilometersWee;
        ObjectAnimator animationNumberOfKilometersMonth;
        ObjectAnimator animationNumberOfKilometersMont;


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

    private void animateTextView(int initialValue, int finalValue, final TextView textview) {
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
