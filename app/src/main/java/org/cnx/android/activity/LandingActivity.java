/**
 * Copyright (c) 2013 Rice University
 *
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.activity;

import org.cnx.android.R;
import org.cnx.android.beans.Content;
import org.cnx.android.fragments.LandingListFragment;
import org.cnx.android.handlers.MenuHandler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

/**
 * Activity for Landing page
 * @author Ed Woodward
 *
 */
public class LandingActivity extends BaseActivity
{
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        getSupportActionBar().setTitle(Html.fromHtml(getString(R.string.app_name_html)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        List<HashMap<String,String>> navTitles = CNXUtil.createNavItems(this);
//        DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
//        ListView drawerList = (ListView)findViewById(R.id.left_drawer);
//        SimpleAdapter sAdapter = new SimpleAdapter(this,navTitles, R.layout.nav_drawer,from,to);
//
//        drawerList.setOnItemClickListener(new DrawerItemClickListener(this,drawerLayout));
//
//        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close)
//        {
//            public void onDrawerClosed(View view) {
//                invalidateOptionsMenu();
//            }
//
//            public void onDrawerOpened(View drawerView) {
//                invalidateOptionsMenu();
//            }
//        };
//        drawerToggle.setDrawerIndicatorEnabled(true);
//        drawerToggle.syncState();
//        drawerLayout.addDrawerListener(drawerToggle);
//        //aBar.setDisplayHomeAsUpEnabled(true);
//        //aBar.setHomeButtonEnabled(true);
//        drawerList.setAdapter(sAdapter);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        LandingListFragment fragment = new LandingListFragment();
        transaction.replace(R.id.sample_content_fragment, fragment);
        transaction.commit();

        final Context context = this;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(context, ViewFavsActivity.class);
                context.startActivity(intent);
                //MenuHandler mh = new MenuHandler();
                //mh.handleSearch(context);
            }
        });
        setNavDrawer();

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

            return true;
        }
        else
        {

            MenuHandler mh = new MenuHandler();
            return mh.handleContextMenu(item, this, new Content());

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.landing_options_menu, menu);

        return true;
    }




}
