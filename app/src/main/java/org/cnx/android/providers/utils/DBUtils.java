/**
 * Copyright (c) 2010 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.providers.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.cnx.android.beans.Content;
import org.cnx.android.providers.Favs;

import android.database.Cursor;

/**
 * Utility class for Content Provider
 * @author Ed Woodward
 *
 */
public class DBUtils
{
    /**
     * Private constructor since all methods are static
     */
    private DBUtils()
    {
        
    }
    
    /**
     * Read Cursor into ArrayList of Content objects.  Closes the cursor when read is finished.
     * @param c - the Cursor to read
     * @return ArrayList<Content>
     */
    public static ArrayList<Content> readCursorIntoList(Cursor c)
    {
        ArrayList<Content> contentList = new ArrayList<>();
        
        int titleColumn = c.getColumnIndex(Favs.TITLE); 
        int urlColumn = c.getColumnIndex(Favs.URL);
        int idColumn = c.getColumnIndex(Favs.ID);
        int iconColumn = c.getColumnIndex(Favs.ICON);
        int otherColumn = c.getColumnIndex(Favs.OTHER);
        if(c.getCount() > 0)
        {
            c.moveToNext();
            do
            {
                try
                {
                    Content con = new Content();
                    con.setTitle(c.getString(titleColumn));
                    con.setUrl(new URL(c.getString(urlColumn)));
                    con.setId(c.getInt(idColumn));
                    con.setIcon(c.getString(iconColumn));
                    con.setContentString(c.getString(otherColumn));
                    contentList.add(con);
                }
                catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }
            }while(c.moveToNext());
        }
        c.close();
        
        return contentList;
        
    }

}
