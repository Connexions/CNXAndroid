package org.cnx.android.logic;

import android.util.Log;

import org.cnx.android.beans.Content;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ew2 on 6/15/16.
 */
public class WebviewLogic
{
    public String getBookURL(String url)
    {
        int cIndex = url.lastIndexOf(":");
        if(cIndex > 5)
        {
            String bookWithVersion = url.substring(0,cIndex);
            int atIndex = bookWithVersion.lastIndexOf("@");
            if(atIndex > -1)
            {
                return bookWithVersion.substring(0, atIndex);
            }
            else
            {
                return bookWithVersion;
            }
        }
        else
        {
            return url.replace("?bookmark=1","");
        }
    }

    public String convertURL(String url)
    {
        String temp;

        if(url.contains("/content/"))
        {
            if(url.contains("mobile."))
            {
                temp = url.replace("//mobile.","//");
            }
            else
            {
                temp = url.replace("//m.", "//");
            }
        }
        else
        {
            temp = url;
        }
        return temp;
    }

    public String getBookTitle(String title)
    {
        //String title = webView.getTitle();
        int index1 = title.indexOf(" - ");
        if(index1 > -1)
        {
            int index2 = title.indexOf(" - ", index1 + 3);
            //Log.d("WebViewActivity","1: " + index1 + " 2: " + index2);
            if(index2 == -1)
            {
                return title.substring(0, index1);
            }
            else
            {

                return title.substring(index1 + 3, index2);
            }
        }
        else
        {
            return "";
        }
    }

    public void setContentURLs(String currentURL, Content content)
    {
        try
        {
            //String currentURL = webView.getUrl();
            content.setUrl(new URL(currentURL));
            WebviewLogic wl = new WebviewLogic();
            String bookURL = wl.getBookURL(currentURL);
            content.setBookURL(bookURL);

        }
        catch (MalformedURLException e)
        {
            Log.d("WebVA.setContentURLs()", "Error: " + e.toString(),e);
        }

    }
}
