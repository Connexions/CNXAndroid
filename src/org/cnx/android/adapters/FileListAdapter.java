/**
 * Copyright (c) 2011 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details. 
 */
package org.cnx.android.adapters;

import java.util.ArrayList;
import java.util.List;

import org.cnx.android.R;
import org.cnx.android.beans.DownloadedFile;
import org.cnx.android.utils.Constants;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/** Adapter to properly display the  list of downloaded files 
 * @author Ed Woodward
 * */
public class FileListAdapter extends ArrayAdapter<DownloadedFile> 
{
    /** Current context */
    private Context context;
    /** List of DownloadedFile objects to display*/
    private List<DownloadedFile> directoryEntries = new ArrayList<DownloadedFile>();
    
    /**
     * Constructor
     * @param context - Current Context
     * @param contentList - ArrayList of Content objects
     */
    public FileListAdapter(Context context, List <DownloadedFile> fileList)
    {
        super(context, android.R.layout.simple_list_item_1, fileList);
        this.context = context;
        this.directoryEntries = fileList;
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
        FileViewHolder holder;
        
        if (v == null) 
        {
            //Log.d("LensListAdapter.getView()", "view is null ");
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.file_row, null);
            holder = new FileViewHolder(v);
           
            v.setTag(holder);
        }
        else
        {
            //Log.d("LensListAdapter.getView()", "view is NOT null ");
            holder= (FileViewHolder)v.getTag();
            if(holder == null)
            {
                holder = new FileViewHolder(v);
                v.setTag(holder);
            }
        }
        
        DownloadedFile c = directoryEntries.get(position);
        ImageView iv = holder.imageView;
        if(c != null)
        {
            //Log.d("LensListAdapter.getView()", "content is not null ");
            if(c.getDisplayPath().indexOf(Constants.PDF_EXTENSION) > -1)
            {
                holder.imageView.setImageResource(R.drawable.pdf_icon);
            }
            else
            {
                holder.imageView.setImageResource(R.drawable.epub_icon);
            }
            
            TextView text = holder.textView;
            if(text != null)
            {
                holder.textView.setText(c.getDisplayPath());
            }
            
        }
        return v;
    }
    
    /**  allows access to list of DownloadedFile objects.
     *  Used so data can be stored by Activity when orientation is changed.
     *  Prevents data reload.
     *  
     *  @return ArrayList of DownloadedFile objects
     * */
    public List<DownloadedFile> getItems()
    {
        return directoryEntries;
    }
    
    
    
    




}
