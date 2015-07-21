/**
 * Copyright (c) 2011 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details. 
 */
package org.cnx.android.beans;

import java.io.Serializable;

/**
 * Holds downloaded file info
 * @author Ed Woodward
 *
 */
public class DownloadedFile implements Serializable, Comparable<DownloadedFile>
{
    /** id for serialization */
    public static final long serialVersionUID = 1L;
    /**
     * Path used in display
     */
    private String displayPath;
    /**
     * Full path to file on SD Card
     */
    private String fullPath;
    
    
    public String getDisplayPath()
    {
        return displayPath;
    }
    public void setDisplayPath(String displayPath)
    {
        this.displayPath = displayPath;
    }
    public String getFullPath()
    {
        return fullPath;
    }
    public void setFullPath(String fullPath)
    {
        this.fullPath = fullPath;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(DownloadedFile another)
    {
        int pathCompare = displayPath.toUpperCase().trim().compareTo(another.getDisplayPath().toUpperCase().trim());
        
        if(pathCompare != 0)
        {
            return pathCompare;
        }
        else
        {
            return fullPath.compareTo(another.getFullPath());
        }
        
    }
    

}
