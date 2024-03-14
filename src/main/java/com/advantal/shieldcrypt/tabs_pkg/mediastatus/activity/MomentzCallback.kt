package com.advantal.shieldcrypt.tabs_pkg.mediastatus.activity

import android.view.View
import android.view.ViewGroup

/**
 * Created by Prashant Lal on 01-11-2022 20:57.
 */
interface MomentzCallback{
    abstract val container: ViewGroup

    fun done()

    fun onNextCalled(view: View, momentz : Momentz, index: Int)
}