package com.advantal.shieldcrypt.sip.font_family;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;


public class PoppinsEtMedium extends AppCompatEditText {

    public PoppinsEtMedium(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        applyCustomFont(context);
        init();
    }

    public PoppinsEtMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
//        applyCustomFont(context);
        init();
    }

    public PoppinsEtMedium(Context context) {
        super(context);
//        applyCustomFont(context);
        init();
    }


    /*private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("fonts/SamsungOne-400.ttf", context);
        setTypeface(customFont);
    }*/

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-Medium.otf");
            setTypeface(tf);
//            setAlpha(0.75F);
        }
    }
}