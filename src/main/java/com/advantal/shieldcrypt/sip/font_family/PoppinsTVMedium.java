package com.advantal.shieldcrypt.sip.font_family;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * Created by SHRIG on 5/23/2016.
 */
public class PoppinsTVMedium extends AppCompatTextView {


    public PoppinsTVMedium(Context context) {
        super(context);
        init();
    }

    public PoppinsTVMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PoppinsTVMedium(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /*public SamsungTVRegular(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }*/

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-Medium.otf");
            setTypeface(tf);
//            setAlpha(0.75F);
        }
    }
}
