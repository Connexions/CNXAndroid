/**
 * Copyright (c) 2010 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details. 
 */
package org.cnx.android.activity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar;
import android.app.ListActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.*;
import android.widget.SimpleAdapter;
import org.cnx.android.R;
import org.cnx.android.adapters.LensesAdapter;
import org.cnx.android.beans.Content;
import org.cnx.android.beans.Feed;
import org.cnx.android.handlers.MenuHandler;
import org.cnx.android.handlers.AtomHandler;
import org.cnx.android.handlers.RssHandler;
import org.cnx.android.listeners.DrawerItemClickListener;
import org.cnx.android.utils.CNXUtil;
import org.cnx.android.utils.ContentCache;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
 * Activity to view list of content contained in a selected lens.  Reads RSS feed from cnx.org and displays contents lens.
 * 
 * @author Ed Woodward
 *
 */
public class ViewLensActivity extends ListActivity
{
    /** Variable for serialized Content object */
    private Content content;
    /** Adaptor for Lens list display*/
    private LensesAdapter adapter;
    /** handler */
    final private Handler handler = new Handler();
    /** list of lenses as Content objects */ 
    ArrayList<Content> contentList;
    
    private ActionBarDrawerToggle drawerToggle;
    String[] from = {"nav_icon","nav_item"};
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
        Intent intent = getIntent();
        content =  (Content)ContentCache.getObject(getString(R.string.cache_sentcontent));
        //content = (Content)intent.getSerializableExtra(getString(R.string.cache_sentcontent));
        if(content==null)
        {
            content = (Content) ContentCache.getObject(getString(R.string.cache_savedcontent));
            if(content==null)
            {
                return;
            }
        }
        if(savedInstanceState != null)
        {
            contentList = (ArrayList<Content>) savedInstanceState.getSerializable(getString(R.string.cache_contentlist));
            if(contentList == null)
            {
                contentList = (ArrayList<Content>)ContentCache.getObject(getString(R.string.cache_contentlist));
            }
        }
        ActionBar aBar = getActionBar();
        aBar.setTitle("  " + content.getTitle());
        setProgressBarIndeterminateVisibility(true);
        if(contentList == null)
        {
            readFeed();
        }
        else
        {
            //reuse stored data
            adapter = new LensesAdapter(ViewLensActivity.this, contentList);
            setListAdapter(adapter);
            setProgressBarIndeterminateVisibility(false);
        }
        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(25);
        set.addAnimation(animation);

        animation = new TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, -1.0f,Animation.RELATIVE_TO_SELF, 0.0f
        );
        animation.setDuration(300);
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
        ListView listView = getListView();        
        listView.setLayoutAnimation(controller);

        List<HashMap<String,String>> navTitles = CNXUtil.createNavItems(this);
        DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        ListView drawerList = (ListView)findViewById(R.id.left_drawer);
        SimpleAdapter sAdapter = new SimpleAdapter(this,navTitles, R.layout.nav_drawer,from,to);

        drawerList.setOnItemClickListener(new DrawerItemClickListener(this,drawerLayout));

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
        getMenuInflater().inflate(R.menu.lens_context_menu, menu);
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
     * Passes menu selection to MenuHandler
     */
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterContextMenuInfo info= (AdapterContextMenuInfo) item.getMenuInfo();
        Content content = (Content)getListView().getItemAtPosition(info.position);
        MenuHandler mh = new MenuHandler();
        boolean returnVal = mh.handleContextMenu(item, this, content);
        if(returnVal)
        {
            return returnVal;
        }
        else
        {
            return super.onContextItemSelected(item);
        }
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        
        getMenuInflater().inflate(R.menu.lens_options_menu, menu);
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

    	if(item.getItemId() == android.R.id.home)
        {
    		//ContentCache.removeObject(getString(R.string.cache_contentlist));
            Intent mainIntent = new Intent(getApplicationContext(), ViewLensesActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            return true;
        }
    	else
    	{
	        MenuHandler mh = new MenuHandler();
	        boolean returnVal;
	        if(item.getItemId() == R.id.add_to_favs)
	        {
	            returnVal = mh.handleContextMenu(item, this, content);
	        }
	        else
	        {
	            returnVal = mh.handleContextMenu(item, this, null);
	        }
	        if(returnVal)
	        {
	            return returnVal;
	        }
	        else
	        {
	            return super.onOptionsItemSelected(item);
	        }
    	}
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) 
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) 
        {
            //ContentCache.removeObject(getString(R.string.cache_contentlist));
            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        //Log.d("ViewLenses.onSaveInstanceState()", "saving data");
        ContentCache.setObject(getString(R.string.cache_savedcontent), content);
        ContentCache.setObject(getString(R.string.cache_contentlist), contentList);
        
    }
    
    /** reads feed in a separate thread.  Starts progress dialog  */
    private void readFeed()
    {
        if(CNXUtil.isConnected(this))
        {
//
            Thread loadFeedThread = new Thread() 
            {
              public void run() {
                  Feed feed = new Feed();
                  if(content.url == null)
                  {
                	  try {
    					feed.url = new URL("");
    				} catch (MalformedURLException e) {
    					Log.d("readfeed",e.toString());
    				}
                  }
                  else
                  {
                	  feed.url = content.url;
                  }
                  feed.id = 1;
                  //Log.d("LensViewer", "Feed id and url set");
                  
                  //Read RSS feed
                  if(feed.url.toString().contains("/atom"))
                  {
                      AtomHandler rh = new AtomHandler();
                      contentList = rh.parseFeed(ViewLensActivity.this, feed);
                  }
                  else
                  {
                      RssHandler rh = new RssHandler();
                      contentList = rh.parseFeed(ViewLensActivity.this, feed);
                  }
                  
                  Collections.sort(contentList);
                  
                  fillData(contentList);
                  handler.post(finishedLoadingListTask);
              }
            };
            loadFeedThread.start();
        }
        else
        {
            CNXUtil.makeNoDataToast(this);
        }
    }
    
    /* (non-Javadoc)
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     * Handles selection of an item in the Lenses list
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) 
    {
        Content content = (Content)getListView().getItemAtPosition(position);
        Intent i = new Intent(getApplicationContext(), WebViewActivity.class);
        i.putExtra(getString(R.string.webcontent),content);
        startActivity(i);
    }
    
    /** Actions after list is loaded in View */
    protected void finishedLoadingList() 
    {
        setListAdapter(adapter);
        getListView().setSelection(0);
        getListView().setClickable(true);
        setProgressBarIndeterminateVisibility(false);
    }
    
    /**
     * Loads feed data into adapter
     * @param ArrayList<Content> contentList
     */
    private void fillData(ArrayList<Content> contentList) 
    {
        //Log.d("LensViewer", "fillData() called");
        adapter = new LensesAdapter(ViewLensActivity.this, contentList);
    }

}
