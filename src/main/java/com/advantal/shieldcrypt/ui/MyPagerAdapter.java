package com.advantal.shieldcrypt.ui;

/**
 * Created by Sonam on 07-11-2022 11:46.
 */

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class MyPagerAdapter extends FragmentPagerAdapter {

    Context mContext;
    int mTotalTabs=3;

    public MyPagerAdapter(Context context, FragmentManager fragmentManager, int totalTabs) {
        super(fragmentManager);
        mContext = context;
        mTotalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Log.d("asasas", position + "");
        switch (position) {
            case 0:
                new ConversationsOverviewFragment();
            case 1:
                new ConversationsOverviewFragment();
            case 2:
                new ConversationsOverviewFragment();
            default:
                 new ConversationsOverviewFragment();

        }
        return null;
    }

    @Override
    public int getCount() {
        return mTotalTabs;
    }
//
//    @NonNull
//    @Override
//    public Fragment createFragment(int position) {
//      return   new ConversationsOverviewFragment();
//    }
//
//    @Override
//    public int getItemCount() {
//        return 3;
//    }
}