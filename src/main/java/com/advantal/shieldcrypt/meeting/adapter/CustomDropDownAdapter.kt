package com.advantal.shieldcrypt.meeting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.meeting.model.DataItemL

/**
 * Created by Prashant Lal on 28-11-2022 19:00.
 */
class CustomDropDownAdapter(val context: Context, var dataSource: List<DataItemL>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View
        val vh: ItemHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.custom_spinner_item, parent, false)
            vh = ItemHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemHolder
        }
        vh.label.text = dataSource.get(position).name
        /*vh.label.setOnClickListener{
            setItem(dataSource.get(position))
        }*/
//        val id = context.resources.getIdentifier(dataSource.get(position).url, "drawable", context.packageName)
//        vh.img.setBackgroundResource(id)

        return view
    }

    override fun getItem(position: Int): Any? {
        return dataSource[position];
    }

    override fun getCount(): Int {
        return dataSource.size;
    }

    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

    private class ItemHolder(row: View?) {
        val label: TextView
//        val img: ImageView

        init {
            label = row?.findViewById(R.id.text) as TextView
//            img = row?.findViewById(R.id.img) as ImageView
        }
    }

    public fun updateStatuslist(dataSource: ArrayList<DataItemL>) {
        this.dataSource = dataSource
        notifyDataSetChanged()
    }

    interface onSelectedItemClicked {
        fun onClick(item: DataItemL)
    }
}