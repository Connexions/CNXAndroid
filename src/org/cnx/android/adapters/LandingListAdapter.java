/**
 * Copyright (c) 2011 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details. 
 */
package org.cnx.android.adapters;

import java.util.ArrayList;

import org.cnx.android.R;
import org.cnx.android.beans.Content;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * @author Ed Woodward
 *
 */
public class LandingListAdapter extends ArrayAdapter<Content>
{
    /** Current context */
    private Context context;
    /** List of Content objects to display*/
    private ArrayList<Content> contentList;
    
    LandingHolder holder;
    
    public LandingListAdapter(Context context, ArrayList <Content> contentList)
    {
        super(context, android.R.layout.simple_list_item_1, contentList);
        this.context = context;
        this.contentList = contentList;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) 
    {
        View v = convertView;
        if (v == null) 
        {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.landing_list, null);
            holder = new LandingHolder(v);
            v.setTag(holder);
        }
        else
        {
            holder= (LandingHolder)v.getTag();
            if(holder == null)
            {
                holder = new LandingHolder(v);
                v.setTag(holder);
            }
        }
        
        Content c = contentList.get(position);
        if(c != null)
        {
            //Log.d("LandingListAdapter", "title = " + c.title);
            TextView text = holder.textView;
            if(text != null)
            {
                holder.textView.setText(c.title);
            }
        }
        else
        {
            //Log.d("LandingListAdapter", "Content is null");
        }
        
        return v;
    }

    

}
