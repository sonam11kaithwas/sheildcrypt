package com.advantal.shieldcrypt.utils_pkg;

import android.app.Activity;
import android.graphics.Point;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

public class FloatingView {
    private static PopupWindow popWindow;

    private FloatingView() {
    }


    public static void onShowPopup(Activity activity, View inflatedView) {

        // get device size
        Display display = activity.getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        int widht = ((int) activity.getResources().getDimension(com.intuit.sdp.R.dimen._280sdp));
        int height = ((int) activity.getResources().getDimension(com.intuit.sdp.R.dimen._200sdp));
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                widht,
//                height);
//
//        params.setMargins( ((int) activity.getResources().getDimension(com.intuit.sdp.R.dimen._70sdp)),
//                20,20,((int) activity.getResources().getDimension(com.intuit.sdp.R.dimen._10sdp)));
//        inflatedView.setLayoutParams(params);


        // fill the data to the list items
        // set height depends on the device size
//        popWindow = new PopupWindow(inflatedView, size.x, ViewGroup.LayoutParams.WRAP_CONTENT,
        popWindow = new PopupWindow(inflatedView, widht, height,
                true);
        // set a background drawable with rounders corners
//        popWindow.setBackgroundDrawable(activity.getResources().getDrawable(
//                R.drawable.comment_popup_bg));
        // make it focusable to show the keyboard to enter in `EditText`
        popWindow.setFocusable(true);
        // make it outside touchable to dismiss the popup window
        popWindow.setOutsideTouchable(true);

        popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        // show the popup at bottom of the screen and set some margin at
        // bottom ie,


        popWindow.showAtLocation(inflatedView,
                Gravity.BOTTOM, 0,
//0
                ((int) activity.getResources().getDimension(com.intuit.sdp.R.dimen._60sdp))
        );


    }

    public static void dismissWindow() {
        if (popWindow != null && popWindow.isShowing())
            popWindow.dismiss();
    }
}
