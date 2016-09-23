/**
 * Copyright (c) 2010 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.beans;

import java.io.Serializable;

/** Class for holding Content details.  Used for Lenses, collections and modules. */
public class Content implements Serializable, Comparable<Content>
{

    /** URL  to retrieve content */
    private String url;
    /** Title of content */
    private String title;
    /** icon of content.  Only used for Lenses */
    private String icon;
    private String bookTitle;
    /**
     * String to hold lens description and keywords
     */
    private String contentString;
    /**
     * database id
     */
    private int id;

    private String bookUrl;

    public String getBookUrl()
    {
        return bookUrl;
    }

    public void setBookUrl(String bookUrl)
    {
        this.bookUrl = bookUrl;
    }

    public String getBookTitle()
    {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle)
    {
        this.bookTitle = bookTitle;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
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

    public void setContentString(String chars)
    {
        contentString = chars;

    }

    public String getContentString()
    {
        return contentString;
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
            return url.compareTo(another.url);
        }

    }
}
