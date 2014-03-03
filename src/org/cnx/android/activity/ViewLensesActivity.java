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

import android.app.ActionBar;
import android.app.ListActivity;
import android.view.Menu;
import android.view.MenuItem;
import org.cnx.android.R;
import org.cnx.android.adapters.LensesAdapter;
import org.cnx.android.beans.Content;
import org.cnx.android.handlers.MenuHandler;
import org.cnx.android.utils.ContentCache;

//import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

/**
 * Activity to view list of available Lens types. 
 * 
 * @author Ed Woodward
 *
 */
public class ViewLensesActivity extends ListActivity
{
    /**
     * Constant for Endorsement label
     */
    private static String ENDORSED = "Endorsement List";
    /**
     * Constant for Affiliation label
     */
    private static String AFFILIATED = "Affiliation List";
    /**
     * Constant for Member List label
     */
    private static String MEMBER = "Member Listed Books";
    
    /**
     * Constant for Featured Content List label
     */
    private static String FEATURED = "Featured Content";
    
    /**
     * Constant for recently published list label
     */
    private static String RECENT = "Recent Content";
    
    private static String OSC = "OpenStax College";
   /** Adaptor for Lens list display */ 
    LensesAdapter adapter;
    /** list of lenses as Content objects */ 
    ArrayList<Content> content;
    
    private ActionBar aBar;
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.list_view);
        registerForContextMenu(getListView());
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.view_lenses_title);
        //TextView aTextView=(TextView)findViewById(R.id.lensNameInTitle);
        //aTextView.setText("Connexions - Book Lists");
        aBar = this.getActionBar();
        aBar.setTitle("Book Lists");
        aBar.setDisplayHomeAsUpEnabled(true);
        //get already retrieved feed and reuse if it is there
        content = (ArrayList<Content>)getLastNonConfigurationInstance();
        if(content==null && savedInstanceState != null)
        {
            //Log.d("ViewLenses.onCreate()", "getting saved data");
            content = (ArrayList<Content>)savedInstanceState.getSerializable(getString(R.string.cache_lenstypes));
        }
        if(content == null)
        {
            createList();
        }
       
        fillData(content);
        finishedLoadingList();
            
        setListAdapter(adapter);
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.lenses_options_menu, menu);
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
            Intent mainIntent = new Intent(getApplicationContext(), LandingActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            return true;
        }
    	else
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
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        //Log.d("ViewLenses.onSaveInstanceState()", "saving data");
        outState.putSerializable(getString(R.string.cache_lenstypes), content);
        
    }
    
    /* (non-Javadoc)
     * Handles selection of an item in the Lenses list
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) 
    {
        Content content = (Content)getListView().getItemAtPosition(position);
        if(content.getTitle().equals(ENDORSED))
        {
            startActivity(new Intent(this, EndorsedLensesActivity.class));
        }
        else if(content.getTitle().equals(AFFILIATED))
        {
            startActivity(new Intent(this, AffiliatedLensesActivity.class));
        }
        else if(content.getTitle().equals(MEMBER))
        {
            startActivity(new Intent(this, MemberLensesActivity.class));
        }
        else if(content.getTitle().equals(FEATURED) || content.getTitle().equals(RECENT))
        {
            Content contentObj = (Content)getListView().getItemAtPosition(position);
            ContentCache.setObject(getString(R.string.cache_sentcontent), contentObj);
            startActivity(new Intent(this, ViewLensActivity.class));
        }
        else if(content.getTitle().equals(OSC) || content.getTitle().equals(OSC))
        {
            Content contentObj = (Content)getListView().getItemAtPosition(position);
            ContentCache.setObject(getString(R.string.cache_sentcontent), contentObj);
            startActivity(new Intent(this, ViewLensActivity.class));
        }
    }
    
    /** Actions after list is loaded in View*/
    protected void finishedLoadingList() 
    {
        setListAdapter(adapter);
        getListView().setSelection(0);
        getListView().setSaveEnabled(true);
        getListView().setClickable(true);
    }
    
    /**
     * Loads data into list view
     * @param contentList ArrayList<Content>
     */
    private void fillData(ArrayList<Content> contentList) 
    {
        //Log.d("LensViewer", "fillData() called");
        adapter = new LensesAdapter(ViewLensesActivity.this, contentList);
    }
    
    /**
     * Create objects for Lens type list
     */
    private void createList()
    {
        String fakeURL = getString(R.string.lenses_fake_url);
        try
        {
        	Content c6 = new Content();
            c6.setTitle(OSC);
            c6.setContentString(getString(R.string.lenses_osc_desc));
            c6.setUrl(new URL("http://cnx.org/lenses/OpenStaxCollege/endorsements/atom"));
            c6.setIconDrawable(R.drawable.lenses);
        	
            
        	Content c = new Content();
            c.setTitle(ENDORSED);
            c.setContentString(getString(R.string.lenses_endorsed_desc));
            c.setUrl(new URL(fakeURL));
            c.setIconDrawable(R.drawable.lenses);
            
            Content c2 = new Content();
            c2.setTitle(AFFILIATED);
            c2.setContentString(getString(R.string.lenses_affiliated_desc));
            c2.setUrl(new URL(fakeURL));
            c2.setIconDrawable(R.drawable.lenses);
            
            Content c3 = new Content();
            c3.setTitle(MEMBER);
            c3.setContentString(getString(R.string.lenses_member_desc));
            c3.setUrl(new URL(fakeURL));
            c3.setIconDrawable(R.drawable.lenses);
            
            Content c4 = new Content();
            c4.setTitle(FEATURED);
            c4.setContentString(getString(R.string.lenses_featured_content_desc));
            c4.setUrl(new URL("http://cnx.org/lenses/cnxorg/featured/atom"));
            c4.setIconDrawable(R.drawable.lenses);
            
            Content c5 = new Content();
            c5.setTitle(RECENT);
            c5.setContentString(getString(R.string.lenses_recent_desc));
            c5.setUrl(new URL("http://cnx.org/content/recent.rss"));
            c5.setIconDrawable(R.drawable.lenses);
            
            if(content == null)
            {
                content = new ArrayList<Content>();
            }
            
            content.add(c6);
            content.add(c4);
            content.add(c);
            content.add(c2);
            content.add(c3);
            content.add(c5);
            
            //Collections.sort((List)content);
        }
        catch (MalformedURLException e)
        {
            Log.d("ViewLenses.createList()", "Error: " + e.toString(),e);
        }
        
    }
    
}
