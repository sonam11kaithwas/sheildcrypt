package com.advantal.shieldcrypt.meeting.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.advantal.shieldcrypt.R;
import com.advantal.shieldcrypt.meeting.model.DataItemL;

import java.util.ArrayList;
import java.util.List;

public class AutocompleteCustomArrayAdapter extends ArrayAdapter<DataItemL> {

    final String TAG = "AutocompleteCustomArrayAdapter.java";

    Context mContext;
    int layoutResourceId;
    private List<DataItemL> dataItemLS;

    public AutocompleteCustomArrayAdapter(Context mContext, int layoutResourceId, List<DataItemL> dataItemLS) {

        super(mContext, layoutResourceId, dataItemLS);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.dataItemLS = dataItemLS;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try{
            if(convertView==null){
                // inflate the layout
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                convertView = inflater.inflate(layoutResourceId, parent, false);
            }

            // object item based on the position
            DataItemL objectItem = dataItemLS.get(position);
            Log.e("getView", " adapter--> " + objectItem.getFirstName());
            // get the TextView and then set the text (item name) and tag (item ID) values
            TextView textViewItem = (TextView) convertView.findViewById(R.id.text);
            textViewItem.setText(objectItem.getFirstName() + " "+objectItem.getLastName());
            // in case you want to add some style, you can do something like:
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;

    }
}
