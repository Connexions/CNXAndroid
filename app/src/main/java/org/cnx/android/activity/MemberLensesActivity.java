/**
 * Copyright (c) 2010 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details. 
 */
package org.cnx.android.activity;

import android.view.MenuItem;
import org.cnx.android.R;


/**
 * Activity to view list of Member Lenses.  Reads RSS feed from cnx.org and displays lenses.
 * 
 * @author Ed Woodward
 *
 */
public class MemberLensesActivity extends BaseLensesActivity 
{
    
    {
        //override fields in BaseLensesActivity
        currentContext = MemberLensesActivity.this;
        
        title = "Member Lists";
        
        storedKey = "memberlenses";
        
        atomFeedURL = "http://cnx.org/memberlists/atom";
        
    }
    
}
