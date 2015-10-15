/**
 * Copyright (c) 2010 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.activity;

import java.util.ArrayList;
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

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Activity for displaying Favs Fragment
 * @author Ed Woodward
 *
 */
public class ViewFavsActivity extends Activity
{

    private List<HashMap<String,String>> navTitles;
    private DrawerLayout drawerLayout;
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

          String[] items = getResources().getStringArray(R.array.nav_list);
          setDrawer(items);
          drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
          ListView drawerList = (ListView)findViewById(R.id.left_drawer);
          SimpleAdapter sAdapter = new SimpleAdapter(this,navTitles, R.layout.nav_drawer,from,to);

          drawerList.setOnItemClickListener(new DrawerItemClickListener());

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
          
          getMenuInflater().inflate(R.menu.lenses_options_menu, menu);
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
      
    private void selectItem(int position)
    {
        switch (position)
        {
            case 0:
                Intent landingIntent = new Intent(getApplicationContext(), LandingActivity.class);
                startActivity(landingIntent);
                break;
            case 1:
                Intent lensesIntent = new Intent(getApplicationContext(), ViewLensesActivity.class);
                startActivity(lensesIntent);
                break;
            case 2:
                drawerLayout.closeDrawers();
                break;
            case 3:
                Intent fileIntent = new Intent(getApplicationContext(), FileBrowserActivity.class);
                startActivity(fileIntent);
                break;
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id)
        {
            selectItem(position);
        }
    }

    private void setDrawer(String[] items)
    {
        HashMap<String,String> hm1 = new HashMap<>();
        hm1.put(getString(R.string.nav_icon),Integer.toString(R.drawable.magnify));
        hm1.put(getString(R.string.nav_item),items[0]);

        HashMap<String,String> hm2 = new HashMap<>();
        hm2.put(getString(R.string.nav_icon),Integer.toString(R.drawable.ic_action_device_access_storage_1));
        hm2.put(getString(R.string.nav_item),items[1]);

        HashMap<String,String> hm3 = new HashMap<>();
        hm3.put(getString(R.string.nav_icon),Integer.toString(R.drawable.ic_action_star));
        hm3.put(getString(R.string.nav_item),items[2]);

        HashMap<String,String> hm4 = new HashMap<>();
        hm4.put(getString(R.string.nav_icon),Integer.toString(R.drawable.ic_action_download));
        hm4.put(getString(R.string.nav_item),items[3]);

        navTitles = new ArrayList<>();

        navTitles.add(hm1);
        navTitles.add(hm2);
        navTitles.add(hm3);
        navTitles.add(hm4);
    }

}
