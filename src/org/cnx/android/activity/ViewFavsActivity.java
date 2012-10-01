/**
 * Copyright (c) 2010 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.cnx.android.R;
import org.cnx.android.adapters.LensListAdapter;
import org.cnx.android.beans.Content;
import org.cnx.android.handlers.MenuHandler;
import org.cnx.android.providers.Favs;
import org.cnx.android.providers.utils.DBUtils;
import org.cnx.android.utils.ContentCache;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
 * @author Ed Woodward
 *
 */
public class ViewFavsActivity extends SherlockListActivity
{
    /** Adaptor for Lens list display */ 
    LensListAdapter adapter;
    /** list of lenses as Content objects */ 
    ArrayList<Content> content;
    
    /** progress window displayed while feed is loading*/
    protected ProgressDialog progressDialog;
    /**handler */
    final private Handler handler = new Handler();
    
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
          //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.view_lens_title);
          //TextView aTextView=(TextView)findViewById(R.id.lensNameInTitle);
          
          //aTextView.setText("Connexions - Favorites");
          ActionBar aBar = getSupportActionBar();
          aBar.setTitle("Connexions - Favorites");
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
             
          }
      }
      
      /* (non-Javadoc)
       * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
       * Creates context menu from lenses_context_menu.xml
       */
//      @Override
//      public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) 
//      {
//          //Log.d("ViewLenses.onCreateContextMenu()", "Called");
//          AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
//          Content content = (Content)getListView().getItemAtPosition(info.position);
//          menu.setHeaderTitle(content.getTitle());
//          super.onCreateContextMenu(menu, v, menuInfo);
//          getMenuInflater().inflate(R.menu.favs_context_menu, menu);
//      }
      
      /* (non-Javadoc)
       * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
       * Passes menu selection to MenuHandler
       */
//      @Override
//      public boolean onContextItemSelected(MenuItem item) 
//      {
//          AdapterContextMenuInfo info= (AdapterContextMenuInfo) item.getMenuInfo();
//          Content content = (Content)getListView().getItemAtPosition(info.position);
//          MenuHandler mh = new MenuHandler();
//          boolean returnVal = mh.handleContextMenu(item, this, content);
//          if(item.getItemId() == R.id.delete_from__favs)
//          {
//              //readDB();
//              adapter.remove(content);
//          }
//          if(returnVal)
//          {
//              return returnVal;
//          }
//          else
//          {
//              return super.onContextItemSelected(item);
//          }
//      }
      
      /* (non-Javadoc)
       * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
       */
      @Override
      public boolean onCreateOptionsMenu(Menu menu) 
      {
          
          getSupportMenuInflater().inflate(R.menu.lenses_options_menu, menu);
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
          progressDialog.dismiss();
      }
      
      /** reads feed in a separate thread.  Starts progress dialog*/
      private void readDB()
      {
          progressDialog = ProgressDialog.show(
              ViewFavsActivity.this,
              null,
              getResources().getString(R.string.loading_favs_description)
            );
          Thread loadFavsThread = new Thread() 
          {
            public void run() 
            {
                
                content = DBUtils.readCursorIntoList(getContentResolver().query(Favs.CONTENT_URI, null, null, null, null));
                
               Collections.sort((List<Content>)content);
                
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

}
