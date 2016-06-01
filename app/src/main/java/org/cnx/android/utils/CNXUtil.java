/**
 * Copyright (c) 2012 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.utils;


import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import org.cnx.android.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * General Utility Class
 * @author Ed Woodward
 *
 */
public class CNXUtil
{
    /**
     * Checks to see if there is a mobile or wifi connection
     * @param context - the current Context
     * @return true if there is a connection, otherwise false.
     */
    public static boolean isConnected(Context context)
    {
        boolean isConnected = false;
        
        String conService = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(conService);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if(ni != null)
        {
            isConnected = ni.isConnectedOrConnecting();
        }
        
        return isConnected;
    }
    
    public static void makeNoDataToast(Context context)
    {
        Toast.makeText(context, "No data connection",  Toast.LENGTH_LONG).show();
    }

    public static boolean isXLarge(Context activityContext)
    {
        return ((activityContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);

    }

    public static boolean isTabletDevice(Context activityContext)
    {

        boolean xlarge = false;

        if((activityContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE || (activityContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 3)
        {
            xlarge = true;
        }
        //Log.d("CNXUtil.isTabletDevice()", "screenLayout = " + test);
        //Log.d("CNXUtil.isTabletDevice()","screenlayout size mask = " + Configuration.SCREENLAYOUT_SIZE_MASK);

        if (xlarge)
        {
            DisplayMetrics metrics = new DisplayMetrics();
            Activity activity = (Activity) activityContext;
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            if (metrics.densityDpi == DisplayMetrics.DENSITY_DEFAULT
                    || metrics.densityDpi == DisplayMetrics.DENSITY_HIGH
                    || metrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM
                    || metrics.densityDpi == DisplayMetrics.DENSITY_TV
                    || metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH)
            {

                return true;
            }
        }

        return false;
    }

    public static List<HashMap<String,String>> createNavItems(Context c)
    {
        String[] items = c.getResources().getStringArray(R.array.nav_list);
        List<HashMap<String,String>> navTitles;
        String[] from = { "nav_icon","nav_item" };
        int[] to = { R.id.nav_icon , R.id.nav_item};
        HashMap<String,String> hm1 = new HashMap<>();
        hm1.put(c.getString(R.string.nav_icon), Integer.toString(R.drawable.ic_search_black_24dp));
        hm1.put(c.getString(R.string.nav_item),items[0]);

//        HashMap<String,String> hm2 = new HashMap<>();
//        hm2.put(c.getString(R.string.nav_icon),Integer.toString(R.drawable.ic_view_list_black_24dp));
//        hm2.put(c.getString(R.string.nav_item),items[1]);

        HashMap<String,String> hm3 = new HashMap<>();
        hm3.put(c.getString(R.string.nav_icon),Integer.toString(R.drawable.ic_star_black_24dp));
        hm3.put(c.getString(R.string.nav_item),items[2]);

        HashMap<String,String> hm4 = new HashMap<>();
        hm4.put(c.getString(R.string.nav_icon),Integer.toString(R.drawable.ic_file_download_black_24dp));
        hm4.put(c.getString(R.string.nav_item),items[3]);

        navTitles = new ArrayList<>();

        navTitles.add(hm1);
        //navTitles.add(hm2);
        navTitles.add(hm3);
        navTitles.add(hm4);

        return navTitles;
    }

}
