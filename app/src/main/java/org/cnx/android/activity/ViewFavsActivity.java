/**
 * Copyright (c) 2010 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar;
import android.app.ListActivity;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.*;
import android.widget.SimpleAdapter;
import org.cnx.android.R;
import org.cnx.android.adapters.LensListAdapter;
import org.cnx.android.beans.Content;
import org.cnx.android.handlers.MenuHandler;
import org.cnx.android.providers.Favs;
import org.cnx.android.providers.utils.DBUtils;
import org.cnx.android.utils.ContentCache;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
 * @author Ed Woodward
 *
 */
public class ViewFavsActivity extends ListActivity
{
    /** Adaptor for Lens list display */ 
    LensListAdapter adapter;
    /** list of lenses as Content objects */ 
    ArrayList<Content> content;
    
    /** progress window displayed while feed is loading*/
    protected ProgressDialog progressDialog;
    /**handler */
    final private Handler handler = new Handler();

    private List<HashMap<String,String>> navTitles;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    String[] from = { "nav_icon","nav_item" };
    int[] to = { R.id.nav_icon , R.id.nav_item};
    
    /** Inner class for completing load work */
    private Runnable finishedLoadingListTask = new Runnable() 
    {
        public void run() 
        {
          finishedLoadingList();
        }
      };
      
      /* (non-Javadoc)
       * @see android.app.Activity#onCreate(android.os.Bundle)
       * Called when the activity is first created.
       */
      @Override
      public void onCreate(Bundle savedInstanceState) 
      {
          super.onCreate(savedInstanceState);
          requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
          setContentView(R.layout.list_view);
          registerForContextMenu(getListView());
          
          ActionBar aBar = getActionBar();
          aBar.setTitle(getString(R.string.title_favs));
          setProgressBarIndeterminateVisibility(true);
          //get already retrieved feed and reuse if it is there
          content = (ArrayList<Content>)getLastNonConfigurationInstance();
          if(content == null)
          {
              //no previous data, so database must be read
              readDB();
          }
          else
          {
                  //reuse existing feed data
                  adapter = new LensListAdapter(ViewFavsActivity.this, content);
                  setListAdapter(adapter);
                  setProgressBarIndeterminateVisibility(false);
             
          }

          String[] items = getResources().getStringArray(R.array.nav_list);
          setDrawer(items);
          drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
          drawerList = (ListView)findViewById(R.id.left_drawer);
          SimpleAdapter sAdapter = new SimpleAdapter(this,navTitles, R.layout.nav_drawer,from,to);

          // Set the adapter for the list view
          //drawerList.setAdapter(new ArrayAdapter<String>(this,R.layout.drawer_list_item, navTitles));
          // Set the list's click listener
          drawerList.setOnItemClickListener(new DrawerItemClickListener());

          drawerToggle = new ActionBarDrawerToggle(
                  this,                  /* host Activity */
                  drawerLayout,         /* DrawerLayout object */
                  R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                  R.string.drawer_open,  /* "open drawer" description for accessibility */
                  R.string.drawer_close  /* "close drawer" description for accessibility */
          ) {
              public void onDrawerClosed(View view) {
                  //getActionBar().setTitle(getString(R.string.app_name));
                  invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
              }

              public void onDrawerOpened(View drawerView) {
                  //getActionBar().setTitle(getString(R.string.app_name));
                  invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
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
       * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
       * Creates context menu from lenses_context_menu.xml
       */
      @Override
      public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) 
      {
          //Log.d("ViewLenses.onCreateContextMenu()", "Called");
          AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
          Content content = (Content)getListView().getItemAtPosition(info.position);
          menu.setHeaderTitle(content.getTitle());
          super.onCreateContextMenu(menu, v, menuInfo);
          getMenuInflater().inflate(R.menu.favs_context_menu, menu);
      }
      
      /* (non-Javadoc)
       * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
       * Passes menu selection to MenuHandler
       */
      @Override
      public boolean onContextItemSelected(android.view.MenuItem item) 
      {
          AdapterContextMenuInfo info= (AdapterContextMenuInfo) item.getMenuInfo();
          Content content = (Content)getListView().getItemAtPosition(info.position);
          MenuHandler mh = new MenuHandler();
          boolean returnVal = mh.handleContextMenu(item, this, content);
          if(item.getItemId() == R.id.delete_from__favs)
          {
              //readDB();
              adapter.remove(content);
          }

          return returnVal;

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
      
      /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
      protected void onResume()
      {
          super.onResume();
          //if database state has changed, reload the display
          if(content != null)
          {
              int dbCount = getDBCount();
              
              if(dbCount >  content.size())
              {
                  readDB();
              }
          }
      }
      
      /* (non-Javadoc)
       * Handles selection of an item in the Lenses list
       * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
       */
      @Override
      protected void onListItemClick(ListView l, View v, int position, long id) 
      {
          Content content = (Content)getListView().getItemAtPosition(position);
          //ContentCache.setObject("content", content);
          int index = content.getUrl().toString().indexOf("lenses");
          if(index > -1)
          {
              ContentCache.setObject(getString(R.string.cache_sentcontent), content);
              startActivity(new Intent(this, ViewLensActivity.class));
          }
          else
          {
              ContentCache.setObject(getString(R.string.webcontent), content);
              startActivity(new Intent(this, WebViewActivity.class));
          }
      }
      
      /** Actions after list is loaded in View*/
      protected void finishedLoadingList() 
      {
          setListAdapter(adapter);
          getListView().setSelection(0);
          setProgressBarIndeterminateVisibility(false);
      }
      
      /** reads feed in a separate thread.  Starts progress dialog*/
      private void readDB()
      {

          Thread loadFavsThread = new Thread() 
          {
            public void run() 
            {
                
                content = DBUtils.readCursorIntoList(getContentResolver().query(Favs.CONTENT_URI, null, null, null, null));
                
               Collections.sort(content);
                
                fillData(content);
                handler.post(finishedLoadingListTask);
            }
          };
          loadFavsThread.start();
          
      }
      /**
       * Loads feed data into adapter on initial reading of feed
       * @param contentList ArrayList<Content>
       */
      private void fillData(ArrayList<Content> contentList) 
      {
          //Log.d("LensViewer", "fillData() called");
          adapter = new LensListAdapter(ViewFavsActivity.this, contentList);
      }
      
      /**
       * Queries the database to get the number of favorites stored
     * @return int - the number of favorites items in the database
     */
    private int getDBCount()
    {
          Cursor c = getContentResolver().query(Favs.CONTENT_URI, null, null, null, null);
          int count = c.getCount();
          c.close();
          return count;
          
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
        HashMap<String,String> hm1 = new HashMap<String,String>();
        hm1.put("nav_icon",Integer.toString(R.drawable.magnify));
        hm1.put("nav_item",items[0]);

        HashMap<String,String> hm2 = new HashMap<String,String>();
        hm2.put("nav_icon",Integer.toString(R.drawable.ic_action_device_access_storage_1));
        hm2.put("nav_item",items[1]);

        HashMap<String,String> hm3 = new HashMap<String,String>();
        hm3.put("nav_icon",Integer.toString(R.drawable.ic_action_star));
        hm3.put("nav_item",items[2]);

        HashMap<String,String> hm4 = new HashMap<String,String>();
        hm4.put("nav_icon",Integer.toString(R.drawable.ic_action_download));
        hm4.put("nav_item",items[3]);

        navTitles = new ArrayList<HashMap<String,String>>();

        navTitles.add(hm1);
        navTitles.add(hm2);
        navTitles.add(hm3);
        navTitles.add(hm4);
    }

}
