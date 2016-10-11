/**
 * Copyright (c) 2010 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.activity;

import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import android.content.Intent;
import android.os.Bundle;

import org.cnx.android.R;
import org.cnx.android.fragments.FavsFragment;
import org.cnx.android.handlers.MenuHandler;

/**
 * Activity for displaying Favs Fragment
 * @author Ed Woodward
 *
 */
public class ViewFavsActivity extends BaseActivity
{

      /* (non-Javadoc)
       * @see android.app.Activity#onCreate(android.os.Bundle)
       * Called when the activity is first created.
       */
      @Override
      public void onCreate(Bundle savedInstanceState) 
      {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_favs);
          Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
          setSupportActionBar(toolbar);
          CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
          toolbarLayout.setTitle(getString(R.string.title_favs));
          getSupportActionBar().setDisplayHomeAsUpEnabled(true);

          FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
          FavsFragment fragment = new FavsFragment();
          transaction.add(R.id.container, fragment);
          transaction.commit();

      }
      

    /* (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     * Handles selected options menu item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            Intent mainIntent = new Intent(getApplicationContext(), LandingActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(mainIntent);
            return true;
        }
        else
        {
            MenuHandler mh = new MenuHandler();
            return mh.handleContextMenu(item, this, null);
        }

    }


      
}
