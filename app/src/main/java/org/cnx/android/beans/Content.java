/**
 * Copyright (c) 2010 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.beans;

import java.io.Serializable;
import java.net.URL;

import android.graphics.drawable.Drawable;

/** Class for holding Content details.  Used for Lenses, collections and modules. */
public class Content implements Serializable, Comparable<Content>
{
    /** id for serialization */
    public static final long serialVersionUID = 1L;
    
    /** URL  to retrieve content */
    public URL url;
    /** Title of content */
    public String title;
    /** icon of content.  Only used for Lenses */
    public String icon;

    private String bookTitle;

    private String bookURL;

    /**
     * String to hold lens description and keywords
     */
    private String contentString = "";
    /**
     * database id
     */
    private int id;
    
    private Drawable iconImage;
    
    private int iconDrawable = -1;

    /** Constructor */
    public Content()
    {

    }

    public String getBookTitle()
    {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle)
    {
        this.bookTitle = bookTitle;
    }

    public String getBookURL()
    {
        return bookURL;
    }

    public void setBookURL(String bookURL)
    {
        this.bookURL = bookURL;
    }

    public int getIconDrawable()
    {
        return iconDrawable;
    }

    public void setIconDrawable(int iconDrawable)
    {
        this.iconDrawable = iconDrawable;
    }

    public Drawable getIconImage()
    {
        return iconImage;
    }

    public void setIconImage(Drawable iconImage)
    {
        this.iconImage = iconImage;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public URL getUrl()
    {
        return url;
    }

    public void setUrl(URL url)
    {
        this.url = url;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getIcon()
    {
        return icon;
    }

    public void setIcon(String icon)
    {
        this.icon = icon;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     * Required method for Comparable interface
     */
    //@Override
    public int compareTo(Content another)
    {
        int titleCompare = title.toUpperCase().trim().compareTo(another.title.toUpperCase().trim());
        if(titleCompare != 0)
        {
            return titleCompare;
        }
        else
        {
            return url.toString().compareTo(another.url.toString());
        }
        
    }

    public void setContentString(String chars)
    {
        contentString = chars;
        
    }
    
    public String getContentString()
    {
        return contentString;
    }

}
