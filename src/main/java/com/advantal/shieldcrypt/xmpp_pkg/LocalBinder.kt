package com.advantal.shieldcrypt.xmpp_pkg

import android.os.Binder
import java.lang.ref.WeakReference

/**
 * Created by Sonam on 17-08-2022 17:19.
 */

class LocalBinder<S>(val service: S) : Binder() {

    private var mService: WeakReference<S>? = null

    init {
        mService = WeakReference<S>(service)

    }

    public fun getServices(): S {
      return mService!!.get()!!
    }

}

//My Application
//N/W
//Drawe ACtiivty -PPT
//Util
//Roaster
//XAMpppMain
//Incoming
//ChatActivity
//ChatAdpter
//login
//firebase
