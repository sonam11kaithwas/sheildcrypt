package com.advantal.shieldcrypt.tabs_pkg.mediastatus.activity

import android.content.Context
import android.util.TypedValue

/**
 * Created by Prashant Lal on 01-11-2022 21:25.
 */
fun Float.toPixel(mContext: Context): Int {
    val r = mContext.resources
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        r.displayMetrics
    ).toInt()
}