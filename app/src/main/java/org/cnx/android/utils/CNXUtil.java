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
import org.cnx.android.beans.Content;

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
        hm1.put(c.getString(R.string.nav_icon), Integer.toString(R.drawable.ic_book_white_24dp));
        hm1.put(c.getString(R.string.nav_item),items[0]);

        HashMap<String,String> hm3 = new HashMap<>();
        hm3.put(c.getString(R.string.nav_icon),Integer.toString(R.drawable.ic_star_white_24dp));
        hm3.put(c.getString(R.string.nav_item),items[1]);

        HashMap<String,String> hm4 = new HashMap<>();
        hm4.put(c.getString(R.string.nav_icon),Integer.toString(R.drawable.ic_search_white_24dp));
        hm4.put(c.getString(R.string.nav_item),items[2]);

        navTitles = new ArrayList<>();

        navTitles.add(hm1);
        navTitles.add(hm3);
        navTitles.add(hm4);

        return navTitles;
    }

    public static int getCoverId(Content content, Context context)
    {
        int coverId = 0;
        String icon = content.getIcon();
        String contentString = content.getContentString();

        if(icon == null)
        {
            icon = "";
        }

        if(contentString == null)
        {
            contentString = "";
        }


        if(icon.equals(context.getString(R.string.physics_icon)) || contentString.equals(context.getString(R.string.physics)))
        {
            coverId = R.drawable.physics_lg;
        }
        else if(icon.equals(context.getString(R.string.sociology_icon)) || contentString.equals(context.getString(R.string.sociology)))
        {
            coverId = R.drawable.sociology_lg;
        }
        else if(icon.equals(context.getString(R.string.biology_icon)) || contentString.equals(context.getString(R.string.biology)))
        {
            coverId = R.drawable.biology_lg;
        }
        else if(icon.equals(context.getString(R.string.concepts_icon)) || contentString.equals(context.getString(R.string.concepts_biology)))
        {
            coverId = R.drawable.concepts_biology_lg;
        }
        else if(icon.equals(context.getString(R.string.anatomy_icon)) || contentString.equals(context.getString(R.string.anatomy)))
        {
            coverId = R.drawable.anatomy_lg;
        }
        else if(icon.equals(context.getString(R.string.statistics_icon)) || contentString.equals(context.getString(R.string.statistics)))
        {
            coverId = R.drawable.statistics_lg;
        }
        else if(icon.equals(context.getString(R.string.econ_icon)) || contentString.equals(context.getString(R.string.econ)))
        {
            coverId = R.drawable.econ_lg;
        }
        else if(icon.equals(context.getString(R.string.macro_icon)) || contentString.equals(context.getString(R.string.macro_econ)))
        {
            coverId = R.drawable.macro_econ_lg;
        }
        else if(icon.equals(context.getString(R.string.micro_icon)) || contentString.equals(context.getString(R.string.micro_econ)))
        {
            coverId = R.drawable.micro_econ_lg;
        }
        else if(icon.equals(context.getString(R.string.precalculus_icon)) || contentString.equals(context.getString(R.string.precalculus)))
        {
            coverId = R.drawable.precalculus_lg;
        }
        else if(icon.equals(context.getString(R.string.psychology_icon)) || contentString.equals(context.getString(R.string.psychology)))
        {
            coverId = R.drawable.psychology_lg;
        }
        else if(icon.equals(context.getString(R.string.history_icon)) || contentString.equals(context.getString(R.string.history)))
        {
            coverId = R.drawable.history_lg;
        }
        else if(icon.equals(context.getString(R.string.chemistry_icon)) || contentString.equals(context.getString(R.string.chemistry)))
        {
            coverId = R.drawable.chemistry_lg;
        }
        else if(icon.equals(context.getString(R.string.algebra_icon)) || contentString.equals(context.getString(R.string.algebra)))
        {
            coverId = R.drawable.algebra_lg;
        }
        else if(icon.equals(context.getString(R.string.trig_icon)) || contentString.equals(context.getString(R.string.trig)))
        {
            coverId = R.drawable.trig_lg;
        }
        else if(icon.equals(context.getString(R.string.ap_physics_icon)) || contentString.equals(context.getString(R.string.ap_physics)))
        {
            coverId = R.drawable.ap_physics_lg;
        }
        else if(icon.equals(context.getString(R.string.ap_macro_icon)) || contentString.equals(context.getString(R.string.ap_macro)))
        {
            coverId = R.drawable.ap_macro;
        }
        else if(icon.equals(context.getString(R.string.ap_micro_icon)) || contentString.equals(context.getString(R.string.ap_micro)))
        {
            coverId = R.drawable.ap_micro;
        }
        else if(icon.equals(context.getString(R.string.american_gov_icon)) || contentString.equals(context.getString(R.string.american_gov)))
        {
            coverId = R.drawable.american_gov;
        }
        else if(icon.equals(context.getString(R.string.calculus1_icon)) || contentString.equals(context.getString(R.string.calculus1)))
        {
            coverId = R.drawable.calculus1;
        }
        else if(icon.equals(context.getString(R.string.calculus2_icon)) || contentString.equals(context.getString(R.string.calculus2)))
        {
            coverId = R.drawable.calculus2;
        }
        else if(icon.equals(context.getString(R.string.calculus3_icon)) || contentString.equals(context.getString(R.string.calculus3)))
        {
            coverId = R.drawable.calculus3;
        }
        else if(icon.equals(context.getString(R.string.chemistry_atoms_icon)) || contentString.equals(context.getString(R.string.chemistry_atoms)))
        {
            coverId = R.drawable.chemistry_atoms;
        }
        else if(icon.equals(context.getString(R.string.prealgebra_icon)) || contentString.equals(context.getString(R.string.prealgebra)))
        {
            coverId = R.drawable.prealgebra;
        }
        else if(icon.equals(context.getString(R.string.bus_fundamentals)) || contentString.equals(context.getString(R.string.bus_fundamentals)))
        {
            coverId = R.drawable.bus_fundamentals;
        }
        else if(icon.equals(context.getString(R.string.elec_engineering)) || contentString.equals(context.getString(R.string.elec_engineering)))
        {
            coverId = R.drawable.elec_engineering;
        }
        else if(icon.equals(context.getString(R.string.elem_algebra)) || contentString.equals(context.getString(R.string.elem_algebra)))
        {
            coverId = R.drawable.elem_algebra;
        }
        else if(icon.equals(context.getString(R.string.advanced_algebra)) || contentString.equals(context.getString(R.string.advanced_algebra)))
        {
            coverId = R.drawable.advanced_algebra;
        }
        else if(icon.equals(context.getString(R.string.applied_probability)) || contentString.equals(context.getString(R.string.applied_probability)))
        {
            coverId = R.drawable.applied_probability;
        }
        else if(icon.equals(context.getString(R.string.fast_fourier)) || contentString.equals(context.getString(R.string.fast_fourier)))
        {
            coverId = R.drawable.fast_fourier;
        }
        else if(icon.equals(context.getString(R.string.first_course)) || contentString.equals(context.getString(R.string.first_course)))
        {
            coverId = R.drawable.first_course;
        }
        else if(icon.equals(context.getString(R.string.flowering_light)) || contentString.equals(context.getString(R.string.flowering_light)))
        {
            coverId = R.drawable.flowering_light;
        }
        else if(icon.equals(context.getString(R.string.houston_reflections)) || contentString.equals(context.getString(R.string.houston_reflections)))
        {
            coverId = R.drawable.houston_reflections;
        }
        else if(icon.equals(context.getString(R.string.memorable_cases)) || contentString.equals(context.getString(R.string.memorable_cases)))
        {
            coverId = R.drawable.memorable_cases;
        }
        else if(icon.equals(context.getString(R.string.music_theory)) || contentString.equals(context.getString(R.string.music_theory)))
        {
            coverId = R.drawable.music_theory;
        }
        else if(icon.equals(context.getString(R.string.programming_fundamentals)) || contentString.equals(context.getString(R.string.programming_fundamentals)))
        {
            coverId = R.drawable.programming_fundamentals;
        }
        else if(icon.equals(context.getString(R.string.univ_physics1_icon)) || contentString.equals(context.getString(R.string.univ_physics1_icon)))
        {
            coverId = R.drawable.university_physics_vol1;
        }
        else if(icon.equals(context.getString(R.string.univ_physics2_icon)) || contentString.equals(context.getString(R.string.univ_physics2_icon)))
        {
            coverId = R.drawable.university_physics_vol2;
        }
        else if(icon.equals(context.getString(R.string.univ_physics3_icon)) || contentString.equals(context.getString(R.string.univ_physics3_icon)))
        {
            coverId = R.drawable.university_physics_vol3;
        }
        else if(icon.equals(context.getString(R.string.astronomy_icon)) || contentString.equals(context.getString(R.string.astronomy_icon)))
        {
            coverId = R.drawable.astronomy;
        }
        else if(icon.equals(context.getString(R.string.elem_algebra_icon)))
        {
            coverId = R.drawable.elementary_algebra;
        }
        else if(icon.equals(context.getString(R.string.intermediate_algebra_icon)))
        {
            coverId = R.drawable.intermediate_algebra;
        }
        else
        {
            coverId = R.drawable.ic_book_black_48dp;
        }

        return coverId;
    }


}
