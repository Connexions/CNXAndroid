/**
 * Copyright (c) 2015 Rice University
 *
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.listeners;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.cnx.android.activity.FileBrowserActivity;
import org.cnx.android.activity.LandingActivity;
import org.cnx.android.activity.ViewFavsActivity;
import org.cnx.android.handlers.MenuHandler;

/**
 * Created by ew2 on 11/18/15.
 */
public class DrawerItemClickListener implements ListView.OnItemClickListener
{
    Context context;
    DrawerLayout drawerLayout;

    public DrawerItemClickListener(Context c, DrawerLayout dLayout)
    {
        context = c;
        drawerLayout = dLayout;
    }

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id)
    {
        selectItem(position);
    }

    private void selectItem(int position)
    {
        switch (position)
        {
            case 0:
                Intent landingIntent = new Intent(context, LandingActivity.class);
                landingIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                context.startActivity(landingIntent);
                drawerLayout.closeDrawers();
                break;
            case 1:
                Intent favsIntent = new Intent(context, ViewFavsActivity.class);
                //favsIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                context.startActivity(favsIntent);
                drawerLayout.closeDrawers();
                break;

            case 2:
                new MenuHandler().handleSearch(context);
                drawerLayout.closeDrawers();
                break;
        }
    }
}
