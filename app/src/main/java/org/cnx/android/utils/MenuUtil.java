/**
 * Copyright (c) 2010 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Utility class for MenuHandler
 * @author Ed Woodward
 *
 */
public class MenuUtil
{
    /**
     * Replaces spaces in title with underscores and truncates to 20 chars if needed
     * @param title - The title to modify
     * @return String - the given title with spaces replaced by underscores and truncated to 20 chars if longer that 20 chars.
     */
    public static String getTitle(String title)
    {
    	if(title == null)
    	{
    		return "";
    	}
        return title.replaceAll("\\p{Punct}", "");

    }
    
    /**
     * Based on URL, determine if URL is for collection or module
     * @param url - the URL to parse
     * @return int One of 2 constants: Constants.COLLECTION_TYPE or Constants.MODULE_TYPE
     */
    public static int getContentType(String url)
    {
        if(url.contains("/col"))
        {
            return Constants.COLLECTION_TYPE;
        }
        else
        {
            return Constants.MODULE_TYPE;
        }
        
    }
    
    /**
     * Adds parameters to download pdf  to url based on content type
     * @param url - String the URL to modify
     * @param type - The content type.  Should be one of 2 constants: Constants.COLLECTION_TYPE or Constants.MODULE_TYPE
     * @return String - url with added parameters
     */
    public static String fixPdfURL(String url, int type)
    {
        
        if(type == Constants.COLLECTION_TYPE)
        {
            return url + Constants.PDF_TYPE;
        }
        else
        {
            return url + "?format=pdf";
        }
        //Log.d("MenuHandler.fixPdfURL()", "newURL: " + newURL);
    }
    
    /**
     * Adds parameters to download epub  to url based on content type
     * @param url - String the URL to modify
     * @param type - The content type.  Should be one of 2 constants: Constants.COLLECTION_TYPE or Constants.MODULE_TYPE
     * @return String - url with added parameters
     */
    public static String fixEpubURL(String url, int type)
    {
        
        if(type == Constants.COLLECTION_TYPE)
        {
            return url + Constants.EPUB_TYPE;
        }
        else
        {
            return url + "?format=epub";
        }
        //Log.d("MenuHandler.fixEpubURL()", "newURL: " + newURL);
    }
    
    /**
     * Used to display dialog when SD Card is not in slot
     * @param context - the current Context
     */
    public static void showMissingMediaDialog(Context context)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Download");
        alertDialog.setMessage("The requested file cannot be downloaded because an SD Card is not installed.  Please install an SD Card and try again.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, Constants.OK, new DialogInterface.OnClickListener() 
        {
              public void onClick(DialogInterface dialog, int which) 
              {
                  //do nothing
         
            } }); 
        alertDialog.show();
        
        
    }
    
    /**
     * Fixes encoded spaces in connexions title saved in favs database
     * @param title - the part of the URL that is being used for the title
     * @return String - Connexions search with the search terms added
     */
    public static String getSearchTitle(String title)
    {
        StringBuilder sb = new StringBuilder();
        String newTitle = "";
        int wordsIndex = title.indexOf("words=");
        sb.append("OpenStaxCNX search: ");
        int ampIndex = title.indexOf("&", wordsIndex);
        if(wordsIndex != -1 && ampIndex != -1)
        {
            newTitle = title.substring(wordsIndex+6, ampIndex);
        }
        else
        {
            newTitle = "Unknown Title";
        }
        try
        {
            newTitle = URLDecoder.decode(newTitle, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        sb.append(newTitle);
        return sb.toString();
    }
   

}
