package org.cnx.android.activity;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.cnx.android.R;
import org.cnx.android.listeners.DrawerItemClickListener;
import org.cnx.android.utils.CNXUtil;

import java.util.HashMap;
import java.util.List;

/**
 * Created by ew2 on 9/20/16.
 */
public class BaseActivity extends AppCompatActivity
{
    protected ActionBarDrawerToggle drawerToggle;
    String[] from = {"nav_icon","nav_item"};
    int[] to = { R.id.nav_icon , R.id.nav_item};

    protected void setNavDrawer()
    {
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
        drawerLayout.addDrawerListener(drawerToggle);
        drawerList.setAdapter(sAdapter);
    }
}
