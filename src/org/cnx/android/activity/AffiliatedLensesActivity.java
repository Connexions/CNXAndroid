/**
 * Copyright (c) 2010 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details. 
 */
package org.cnx.android.activity;

import org.cnx.android.R;

import android.view.MenuItem;

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
    
    /* (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     * If feed read fails, handle refresh menu item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
       if(item.getItemId() == R.id.refresh)
       {
           //refresh selected so reread the rss feed
           super.readFeed();
           return true;
       }
       else
       {
           super.onOptionsItemSelected(item);
           return true;
       }
    }
}
