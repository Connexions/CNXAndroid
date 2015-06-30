/**
 * Copyright (c) 2010 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details. 
 */
package org.cnx.android.adapters;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import org.cnx.android.R;
import org.cnx.android.beans.Content;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

/** Adapter to properly display the Lens list 
 * @author Ed Woodward
 * */
public class LensListAdapter extends ArrayAdapter<Content> implements SectionIndexer
{
    /** Current context */
    private Context context;
    /** List of Content objects to display*/
    private ArrayList<Content> contentList;
    /**
     * used to create array of alpha characters for section indexer
     */
    private HashMap<String, Integer> alphaIndexer;
    /**
     * list of alpha characters for section indexer
     */
    private String[] sections;
    
    /**
     * Viewholder for better performance
     */
    ViewHolder holder;

    
    /**
     * Constructor
     * @param context - Current Context
     * @param contentList - ArrayList of Content objects
     */
    public LensListAdapter(Context context, ArrayList <Content> contentList)
    {
        super(context, android.R.layout.simple_list_item_1, contentList);
        this.context = context;
        this.contentList = contentList;
        
        alphaIndexer = new HashMap<>();
        int size = contentList.size();

        for (int x = 0; x < size; x++) 
        {
            Content s = contentList.get(x);

            String ch =  s.title.substring(0, 1);
            if(!ch.equals(" "))
            {
                 // convert to uppercase otherwise lowercase a -z will be sorted after upper A-Z
                ch = ch.toUpperCase();

                // HashMap will prevent duplicates
                alphaIndexer.put(ch, x);
            }
        }

        Set<String> sectionLetters = alphaIndexer.keySet();

        // create a list from the set to sort
        ArrayList<String> sectionList = new ArrayList<String>(sectionLetters); 

        Collections.sort(sectionList);
        sections = new String[sectionList.size()];
        sectionList.toArray(sections);

        //Log.d("LensListAdapter constructor", "content list size: " + contentList.size());
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
            //Log.d("LensListAdapter.getView()", "view is null ");
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.cnx_list, null);
            holder = new ViewHolder(v);
           
            v.setTag(holder);
        }
        else
        {
            //Log.d("LensListAdapter.getView()", "view is NOT null ");
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
            //Log.d("LensListAdapter.getView()", "content is not null ");
            TextView text = holder.textView;
            TextView other = holder.otherView;
            holder.imageView.setTag(position);
            if (holder.imageView != null) 
            {
                if(c.icon != null)
                {
                    Builder uriBuilder = new Builder();
                    uriBuilder.scheme("http");
                    uriBuilder.path(c.icon.substring(5));
                    Uri uri = uriBuilder.build();
                    //Log.d("LensListAdapter" ,"uri:" + uri.toString());

                    new FetchImageTask(holder.imageView).execute(uri.toString());
                    
                }
                else
                {
                    //set correct icon based on URL
                    if(c.url.toString().contains("lenses"))
                    {
                        holder.imageView.setImageResource(R.drawable.lenses);
                    }
                    else if(c.url.toString().contains("content/m"))
                    {
                        holder.imageView.setImageResource(R.drawable.modules);
                    }
                    else if(c.url.toString().contains("content/col"))
                    {
                        holder.imageView.setImageResource(R.drawable.collections);
                    }
                    else if(c.url.toString().contains("google.com") || c.url.toString().contains("cnx.org/content/search") )
                    {
                        holder.imageView.setImageResource(R.drawable.search_selected);
                    }
                    else
                    {
                        holder.imageView.setImageResource(R.drawable.lenses);
                    }
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
    
    /**
     * inner class to download image for favorites
     *
     */
    private class FetchImageTask extends AsyncTask<String, Integer, Drawable>
    {
        private Drawable content = null;
        private ImageView imv;
        private int position;

        
        public FetchImageTask(ImageView imv) 
        {
            this.imv = imv;
            
            position = Integer.parseInt(imv.getTag().toString());
       }

        @Override
        protected Drawable doInBackground(String... strings )
        {
            //Object content = null;
            try
            {
                URL url = new URL(strings[0]);
                InputStream is = (InputStream)url.getContent();
                content = Drawable.createFromStream(is, "src");
            }
            catch (MalformedURLException me)
            {
                Log.d("LensListAdapter" ,"Exception: " + me.toString());
            }
            catch (IOException e)
            {
                Log.d("LensListAdapter" ,"Exception: " + e.toString());
            }
            return content;
        }
        
        @Override
        protected void onPostExecute(Drawable result)
        {
            if (!(Integer.parseInt(imv.getTag().toString()) == position)) 
            {
                /* The path is not same. This means that this
                   image view is handled by some other async task. 
                   We don't do anything and return. */
                return;
            }

            imv.setImageDrawable(result);
        }
        
        
    }
    
    /* (non-Javadoc)
     * @see android.widget.SectionIndexer#getPositionForSection(int)
     */
    public int getPositionForSection(int section) 
    {
    	String letter;
        
    	if(section < sections.length)
    	{
    		letter = sections[section];
    	}
    	else
    	{
    		return 0;
    	}
        //Log.d("LensListAdapter ", "letter: " + letter);
        return alphaIndexer.get(letter);
    }

    /* (non-Javadoc)
     * @see android.widget.SectionIndexer#getSectionForPosition(int)
     */
    public int getSectionForPosition(int position) 
    {
        return 1;
    }

    /* (non-Javadoc)
     * @see android.widget.SectionIndexer#getSections()
     */
    public Object[] getSections() 
    {
         return sections;
    }
    
    




}
