/**
 * Copyright (c) 2010 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.providers;

import android.net.Uri;
import android.provider.BaseColumns;

/**
*  Constants class for notes database table 
* 
* @author Ed Woodward
*
*/
public class Notes implements BaseColumns
{
    /** Private constructor.  Cannot instanciate this class */
    private Notes()
    {
        
    }
    
    public static final Uri CONTENT_URI = Uri.parse("content://org.cnx.android.providers.NotesProvider");
    /** title column name*/
    public static final String TITLE = "notes_title";
    /** url column name*/
    public static final String URL = "notes_url";
    /** id column name*/
    public static final String ID = "_id";
    /** other column name*/
    public static final String NOTE = "notes_note";


}
