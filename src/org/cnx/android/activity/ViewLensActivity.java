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
import java.util.List;

import org.cnx.android.R;
import org.cnx.android.adapters.LensesAdapter;
import org.cnx.android.beans.Content;
import org.cnx.android.beans.Feed;
import org.cnx.android.handlers.MenuHandler;
import org.cnx.android.handlers.AtomHandler;
import org.cnx.android.handlers.RssHandler;
import org.cnx.android.utils.CNXUtil;
import org.cnx.android.utils.ContentCache;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.View;
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
public class ViewLensActivity extends SherlockListActivity
{
    /** Constant for serialized object passed to Activity */
    public static final String CONTENT = "content";
    /** Variable for serialized Content object */
    private Content content;
    /** Adaptor for Lens list display*/
    private LensesAdapter adapter;
    /** progress window displayed while feed is loading  */
    protected ProgressDialog progressDialog;
    /** handler */
    final private Handler handler = new Handler();
    /** list of lenses as Content objects */ 
    ArrayList<Content> contentList;
    
    private ActionBar aBar;
    
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
        content = (Content)ContentCache.getObject(getString(R.string.cache_sentcontent));
        if(content==null)
        {
            content = (Content)ContentCache.getObject(getString(R.string.cache_savedcontent));
            if(content==null)
            {
                return;
            }
        }
        contentList = (ArrayList<Content>)ContentCache.getObject(getString(R.string.cache_contentlist));
        aBar = getSupportActionBar();
        aBar.setDisplayHomeAsUpEnabled(true);
        aBar.setTitle(content.getTitle());
        setSupportProgressBarIndeterminateVisibility(true);
        //get stored data if there is any
        if(contentList == null)
        {
            contentList = (ArrayList<Content>)getLastNonConfigurationInstance();
            if(contentList == null)
            {
                //no stored data, so read RSS feed
                readFeed();
            }
        }
        else
        {
            //reuse stored data
            adapter = new LensesAdapter(ViewLensActivity.this, contentList);
            setListAdapter(adapter);
            setSupportProgressBarIndeterminateVisibility(false);
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
    public boolean onContextItemSelected(android.view.MenuItem item) 
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
        
        getSupportMenuInflater().inflate(R.menu.lens_options_menu, menu);
        return true;
        
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
    	if(item.getItemId() == android.R.id.home)
        {
    		ContentCache.removeObject(getString(R.string.cache_contentlist));
            Intent mainIntent = new Intent(getApplicationContext(), ViewLensesActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            return true;
        }
    	else
    	{
	        MenuHandler mh = new MenuHandler();
	        boolean returnVal = true;
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
            ContentCache.removeObject(getString(R.string.cache_contentlist));
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
//            progressDialog = ProgressDialog.show(
//                ViewLensActivity.this,
//                null,
//                getResources().getString(R.string.loading_lens_description)
//              );
            Thread loadFeedThread = new Thread() 
            {
              public void run() {
                  Feed feed = new Feed();
                  if(content.url == null)
                  {
                	  try {
    					feed.url = new URL("");
    				} catch (MalformedURLException e) {
    					e.printStackTrace();
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
                  
                  Collections.sort((List<Content>)contentList);
                  
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
        ContentCache.setObject(getString(R.string.webcontent), content);
        startActivity(new Intent(this, WebViewActivity.class));
    }
    
    /** Actions after list is loaded in View */
    protected void finishedLoadingList() 
    {
        setListAdapter(adapter);
        getListView().setSelection(0);
        getListView().setClickable(true);
        //progressDialog.dismiss();
        setSupportProgressBarIndeterminateVisibility(false);
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
