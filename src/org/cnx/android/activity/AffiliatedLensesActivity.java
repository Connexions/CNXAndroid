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
 * Activity to view list of Affiliated Lenses.  Reads RSS feed from cnx.org and displays lenses.
 * 
 * @author Ed Woodward
 *
 */
public class AffiliatedLensesActivity extends BaseLensesActivity 
{
    {
        //override fields in BaseLensesActivity
        currentContext = AffiliatedLensesActivity.this;
        
        title = "Affiliation List";
        
        storedKey = "affiliatedlenses";
        
        atomFeedURL = "http://cnx.org/affiliations/atom";
    
   
    }
    

}
