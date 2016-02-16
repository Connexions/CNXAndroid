/**
 * Copyright (c) 2010 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details. 
 */
package org.cnx.android.activity;



/**
 * Activity to view list of endorsed Lenses.  Reads RSS feed from cnx.org and displays lenses.
 * 
 * @author Ed Woodward
 *
 */
public class EndorsedLensesActivity extends BaseLensesActivity 
{
    {
      //override fields in BaseLensesActivity
        currentContext = EndorsedLensesActivity.this;
        
        title = "Endorsement List";
        
        storedKey = "endorsedlenses";
        
        atomFeedURL = "http://legacy.cnx.org/endorsements/atom";
        
    }
    
}
