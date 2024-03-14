package com.advantal.shieldcrypt.utils_pkg

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.advantal.shieldcrypt.R

/**
 * Created by Sonam on 16-06-2022 16:14.
 */
class GenericTextWatcher : TextWatcher {
    private var view: View? = null
    var nextView: View? = null
    var context: Context? = null

    constructor(view: View?, nextView: View?, context: Context) {
        this.view = view
        this.nextView = nextView
        this.context = context
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        var str = s.toString()
        when (view?.id) {
            R.id.editText1,
            R.id.editText2,
            R.id.editText3,
            R.id.editText4
//            comment by PRASHANT LAL on 26 sept 2022 as assigned task on ASANA by sonam ma'am
//            R.id.editText5,
//            R.id.editText6
            ->
                if (str.length == 1) {
                    if (nextView != null)
                        nextView?.requestFocus()
                    /**///                        mListener.otpVerify(true);

                } else {
                    Log.e("Text", "" + str)
                }

        }
    }

}