package com.advantal.shieldcrypt.meeting.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.meeting.model.DataItemL

class AttenderSearchAdapter (
    private val mContext: Context,
    private val mLayoutResourceId: Int,
    cities: List<DataItemL>
) :
    ArrayAdapter<DataItemL>(mContext, mLayoutResourceId, cities) {
    private val attender: MutableList<DataItemL> = ArrayList(cities)

    override fun getCount(): Int {
        return attender.size
    }
    override fun getItem(position: Int): DataItemL {
        return attender[position]
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            val inflater = (mContext as Activity).layoutInflater
            //convertView = inflater.inflate(R.layout.custom_spinner_item, parent, false)
            convertView = inflater.inflate(mLayoutResourceId, parent, false)
        }
        try {
            val city: DataItemL = getItem(position)
            val cityAutoCompleteView = convertView!!.findViewById<View>(R.id.text) as TextView
            cityAutoCompleteView.text = city.name
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return convertView!!
    }
}