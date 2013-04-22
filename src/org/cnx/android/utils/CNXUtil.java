/**
 * Copyright (c) 2012 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

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

}
