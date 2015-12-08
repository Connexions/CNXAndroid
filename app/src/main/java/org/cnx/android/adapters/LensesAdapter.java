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
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/** Adapter to properly display the Lenses list
 * removes the fast scroll code since it is not working well on the list of lenses. 
 * @author Ed Woodward
 * */
public class LensesAdapter extends ArrayAdapter<Content>
{
    /** Current context */
    private Context context;
    /** List of Content objects to display*/
    private ArrayList<Content> contentList;
    /**
     * used to create array of alpha characters for section indexer
     */
    //private HashMap<String, Integer> alphaIndexer;
    /**
     * list of alpha characters for section indexer
     */
   // private String[] sections;
    /**
     * View holder for list performance
     */
    ViewHolder holder;
    
    /**
     * Constructor
     * @param context - Current Context
     * @param contentList - ArrayList of Content objects
     */
    public LensesAdapter(Context context, ArrayList <Content> contentList)
    {
        super(context, android.R.layout.simple_list_item_1, contentList);
        this.context = context;
        this.contentList = contentList;
        
        //Log.d("LensesAdapter constructor", "content list size: " + contentList.size());
    }
    
    /* (non-Javadoc)
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
     * Creates layout
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) 
    {
        View v = convertView;
        
        if (v == null) 
        {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.cnx_list, null);
            holder = new ViewHolder(v);
            v.setTag(holder);
        }
        else
        {
            //Log.d("LensesAdapter.getView()", "view is NOT null ");
            holder= (ViewHolder)v.getTag();
            if(holder == null)
            {
                holder = new ViewHolder(v);
                v.setTag(holder);
            }
        }
        
        Content c = contentList.get(position);
        if(c != null)
        {
            //Log.d("LensesAdapter.getView()", "content is not null ");
            ImageView iv = holder.imageView;
            TextView text = holder.textView;
            TextView other = holder.otherView;
           
            if(iv != null)
            {
                if(c.getIconDrawable() != -1)
                {
                    holder.imageView.setImageResource(c.getIconDrawable());
                }
                else
                {
                    holder.imageView.setImageDrawable(c.getIconImage());
                }
            }
            if(text != null){
                holder.textView.setText(c.title);
            }
            if(other != null)
            {
                holder.otherView.setText(c.getContentString());
            }
        }
        return v;
    }
    
    /**  allows access to list of Content objects.
     *  Used so data can be stored by Activity when orientation is changed.
     *  Prevents data reload.
     *  
     *  @return ArrayList of Content objects
     * */
    public ArrayList<Content> getItems()
    {
        return contentList;
    }
    
}

