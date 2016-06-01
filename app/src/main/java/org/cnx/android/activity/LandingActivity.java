/**
 * Copyright (c) 2013 Rice University
 *
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.activity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import org.cnx.android.R;
import org.cnx.android.beans.Content;
import org.cnx.android.fragments.GridFragment;
import org.cnx.android.handlers.MenuHandler;
import org.cnx.android.handlers.SearchHandler;
import org.cnx.android.listeners.DrawerItemClickListener;
import org.cnx.android.utils.CNXUtil;
import org.cnx.android.utils.Constants;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * Activity for Landing page
 * @author Ed Woodward
 *
 */
public class LandingActivity extends Activity implements GridFragment.OnBookSelectedListener
{
    
    private ActionBarDrawerToggle drawerToggle;
    String[] from = { "nav_icon","nav_item" };
    int[] to = { R.id.nav_icon , R.id.nav_item};
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_landing);
        ActionBar aBar = this.getActionBar();
        aBar.setTitle(Html.fromHtml("&nbsp;&nbsp;" + getString(R.string.app_name_html)));

        //setLayout();

        List<HashMap<String,String>> navTitles = CNXUtil.createNavItems(this);
        DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        ListView drawerList = (ListView)findViewById(R.id.left_drawer);
        SimpleAdapter sAdapter = new SimpleAdapter(this,navTitles, R.layout.nav_drawer,from,to);

        drawerList.setOnItemClickListener(new DrawerItemClickListener(this,drawerLayout));

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close)
        {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);
        aBar.setDisplayHomeAsUpEnabled(true);
        aBar.setHomeButtonEnabled(true);
        drawerList.setAdapter(sAdapter);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        GridFragment fragment = new GridFragment();
        transaction.replace(R.id.contentFragment, fragment);
        transaction.commit();
    }

    public void onBookSelected(Content content)
    {

        Intent i = new Intent(getApplicationContext(), WebViewActivity.class);
        i.putExtra(getString(R.string.webcontent),content);
        startActivity(i);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(drawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        if(item.getItemId() == android.R.id.home)
        {
//            Intent mainIntent = new Intent(getApplicationContext(), LandingActivity.class);
//            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(mainIntent);
            return true;
        }
        else
        {
//            try
//            {
//
//                content.setTitle(webView.getTitle().replace(" - " + content.getBookTitle() + " - OpenStax CNX",""));
//                content.setUrl(new URL(webView.getUrl()));
//
//            }
//            catch(MalformedURLException mue)
//            {
//
//            }
            MenuHandler mh = new MenuHandler();
            return mh.handleContextMenu(item, this, new Content());

        }
        //return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.landing_options_menu, menu);

        return true;
    }
    
    /**
     * Sets the list adapter and adds the listeners to the list and the search button
     */
//    private void setLayout()
//    {
//
//        ImageButton searchButton = (ImageButton)findViewById(R.id.searchButton);
//        searchButton.setOnClickListener(new OnClickListener()
//        {
//
//              public void onClick(View v)
//              {
//                  EditText searchFor = (EditText)findViewById(R.id.searchText);
//                  performSearch(searchFor.getText().toString());
//              }
//          });
//        EditText searchText = (EditText)findViewById(R.id.searchText);
//        searchText.setOnKeyListener(new View.OnKeyListener()
//        {
//
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event)
//            {
//                if(event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER))
//                {
//                    EditText searchFor = (EditText)findViewById(R.id.searchText);
//                    performSearch(searchFor.getText().toString());
//                }
//                return false;
//            }
//        });
//
//    }


    
    /**
     * Calls SearchHandler to perform cnx search
     * @param searchFor String - what to search for
     */
    private void performSearch(String searchFor)
    {
        SearchHandler sh = new SearchHandler();
        sh.performSearch(searchFor, Constants.CNX_SEARCH, this);
    }

}
