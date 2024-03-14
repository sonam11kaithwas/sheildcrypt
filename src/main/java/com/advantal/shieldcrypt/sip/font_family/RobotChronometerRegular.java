package com.advantal.shieldcrypt.sip.font_family;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Chronometer;

/**
 * Created by SHRIG on 5/23/2016.
 */
public class RobotChronometerRegular extends Chronometer {


    public RobotChronometerRegular(Context context) {
        super(context);
        init();
    }

    public RobotChronometerRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RobotChronometerRegular(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /*public SamsungTVRegular(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }*/

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Light.ttf");
            setTypeface(tf);
//            setAlpha(0.75F);
        }
    }
}
