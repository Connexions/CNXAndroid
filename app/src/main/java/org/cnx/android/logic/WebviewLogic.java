package org.cnx.android.logic;

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
            return url.substring(0,cIndex);
        }
        else
        {
            return url.replace("?bookmark=1","");
        }
    }
}
