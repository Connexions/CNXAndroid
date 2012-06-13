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
import org.cnx.android.utils.CNXUtil;
import org.cnx.android.utils.ContentCache;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
 * Base Activity to view list of Lenses.  Reads RSS feeds from cnx.org and displays lenses.
 * 
 * @author Ed Woodward
 *
 */
public class BaseLensesActivity extends ListActivity 
{
    
   /** Adaptor for Lens list display */ 
    LensesAdapter adapter;
    /** list of lenses as Content objects */ 
    ArrayList<Content> content;
    
    /** progress window displayed while feed is loading*/
    protected ProgressDialog progressDialog;
    /**handler */
    final private Handler handler = new Handler();
    /**
     * Current Context - should be overridden
     */
    public Context currentContext = BaseLensesActivity.this;
    /**
     * Title to display in title bar - should be overridden
     */
    public String title = "Base Lenses";
    /**
     * Key used to store list of lenses - should be overridden
     */
    public String storedKey = "baselenses";
    /**
     * URL for atom feed - should be overridden
     */
    public String atomFeedURL = "http://cnx.org/lenses/atom";
    
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
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.list_view);
        registerForContextMenu(getListView());
        //get already retrieved feed and reuse if it is there
        content = (ArrayList<Content>)ContentCache.getObject(storedKey);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.view_favs_title);
        TextView aTextView=(TextView)findViewById(R.id.lensNameInTitle);
        
        aTextView.setText(title);
        if(content==null && savedInstanceState != null)
        {
            //Log.d("ViewLenses.onCreate()", "getting saved data");
            content = (ArrayList<Content>)savedInstanceState.getSerializable(storedKey);
        }
        if(content == null)
        {
            //no previous data, so RSS feed must be read
            readFeed();
        }
        else
        {
            //reuse existing feed data
            adapter = new LensesAdapter(currentContext, content);
            setListAdapter(adapter);
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
        
        //
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
        if(content == null || content.size() < 1)
        {
            getMenuInflater().inflate(R.menu.empty_lenses_menu, menu);
        }
        else
        {
            getMenuInflater().inflate(R.menu.lenses_options_menu, menu);
        }
        return true;
        
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        MenuHandler mh = new MenuHandler();
        boolean returnVal = mh.handleContextMenu(item, this, null);
        if(returnVal)
        {
            return returnVal;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    //@Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        //Log.d("ViewLenses.onSaveInstanceState()", "saving data");
        ContentCache.setObject(storedKey, content);
        
    }
    
    /* (non-Javadoc)
     * Handles selection of an item in the Lenses list
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) 
    {
        Content content = (Content)getListView().getItemAtPosition(position);
        Intent intent = new Intent(currentContext, ViewLensActivity.class);
        ContentCache.setObject(getString(R.string.cache_sentcontent), content);
        startActivity(intent);
    }
    
    /** Actions after list is loaded in View*/
    protected void finishedLoadingList() 
    {
        setListAdapter(adapter);
        getListView().setSelection(0);
        getListView().setSaveEnabled(true);
        getListView().setClickable(true);
        progressDialog.dismiss();
    }
    
    /** reads feed in a separate thread.  Starts progress dialog*/
    protected void readFeed()
    {
        if(CNXUtil.isConnected(currentContext))
        {
            progressDialog = ProgressDialog.show(
                    currentContext,
                null,
                getResources().getString(R.string.loading_lenses_description)
              );
            Thread loadFeedThread = new Thread() 
            {
              public void run() {
                  Feed feed = new Feed();
                  try
                  {
                      feed.url = new URL(atomFeedURL);
                      feed.id = 1;
                      //Log.d("LensViewer", "Feed id and url set");
                  }
                  catch (MalformedURLException mue)
                  {
                      Log.e("BaseActivity", mue.toString());
                  }
                  
                  //Read RSS feed
                  
                  AtomHandler rh = new AtomHandler();
                  content = rh.parseFeed(getApplicationContext(), feed);
                  
                 Collections.sort((List<Content>)content);
                  
                  
                  fillData(content);
                  handler.post(finishedLoadingListTask);
              }
            };
            loadFeedThread.start();
        }
        else
        {
            CNXUtil.makeNoDataToast(currentContext);
        }
        
        
    }
    
//    private void displayToast()
//    {
//        Toast.makeText(BaseLensesActivity.this, "No data connection",  Toast.LENGTH_LONG).show();
//    }
    
    /**
     * Loads feed data into adapter on initial reading of feed
     * @param contentList ArrayList<Content>
     */
    private void fillData(ArrayList<Content> contentList) 
    {
        //Log.d("LensViewer", "fillData() called");
        adapter = new LensesAdapter(currentContext, contentList);
    }
    
}
