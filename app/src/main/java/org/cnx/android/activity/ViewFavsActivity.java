/**
 * Copyright (c) 2010 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.activity;

import java.util.HashMap;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.*;
import android.widget.SimpleAdapter;
import org.cnx.android.R;
import org.cnx.android.fragments.FavsFragment;
import org.cnx.android.handlers.MenuHandler;
import org.cnx.android.listeners.DrawerItemClickListener;
import org.cnx.android.utils.CNXUtil;

import android.os.Bundle;
import android.widget.ListView;

/**
 * Activity for displaying Favs Fragment
 * @author Ed Woodward
 *
 */
public class ViewFavsActivity extends Activity
{

    private ActionBarDrawerToggle drawerToggle;
    String[] from = {"nav_icon","nav_item"};
    int[] to = { R.id.nav_icon , R.id.nav_item};
    
      /* (non-Javadoc)
       * @see android.app.Activity#onCreate(android.os.Bundle)
       * Called when the activity is first created.
       */
      @Override
      public void onCreate(Bundle savedInstanceState) 
      {
          super.onCreate(savedInstanceState);
          requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
          setContentView(R.layout.favs_activity);
          
          ActionBar aBar = getActionBar();
          aBar.setTitle("  " + getString(R.string.title_favs));

          FragmentTransaction transaction = getFragmentManager().beginTransaction();
          FavsFragment fragment = new FavsFragment();
          transaction.add(R.id.favsFragment, fragment);
          transaction.commit();

          List<HashMap<String,String>> navTitles = CNXUtil.createNavItems(this);
          DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
          ListView drawerList = (ListView)findViewById(R.id.left_drawer);
          SimpleAdapter sAdapter = new SimpleAdapter(this,navTitles, R.layout.nav_drawer,from,to);

          drawerList.setOnItemClickListener(new DrawerItemClickListener(this, drawerLayout));

          drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close)
          {
              public void onDrawerClosed(View view)
              {
                  invalidateOptionsMenu();
              }

              public void onDrawerOpened(View drawerView)
              {
                  invalidateOptionsMenu();
              }
          };
          drawerToggle.setDrawerIndicatorEnabled(true);
          drawerToggle.syncState();
          drawerLayout.setDrawerListener(drawerToggle);
          aBar.setDisplayHomeAsUpEnabled(true);
          aBar.setHomeButtonEnabled(true);
          drawerList.setAdapter(sAdapter);
      }
      

      
      /* (non-Javadoc)
       * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
       */
      @Override
      public boolean onCreateOptionsMenu(Menu menu)
      {
          
          getMenuInflater().inflate(R.menu.favs_options_menu, menu);
          return true;
          
      }

      /* (non-Javadoc)
       * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
       */
      @Override
      public boolean onOptionsItemSelected(MenuItem item)
      {
          if (drawerToggle.onOptionsItemSelected(item))
          {
              return true;
          }

          MenuHandler mh = new MenuHandler();
          boolean returnVal = mh.handleContextMenu(item, this, null);

          return returnVal;

      }
      
}
