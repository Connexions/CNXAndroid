/**
 * Copyright (c) 2015 Rice University
 *
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.cnx.android.R;
import org.cnx.android.activity.WebViewActivity;
import org.cnx.android.beans.Content;
import org.cnx.android.logic.WebviewLogic;
import org.cnx.android.providers.Favs;
import org.cnx.android.utils.CNXUtil;

import java.util.ArrayList;

import co.paulburke.android.itemtouchhelperdemo.helper.ItemTouchHelperAdapter;

/** Adapter to properly display favorites in RecyclerView
 * @author Ed Woodward
 * */
public class FavsRecyclerViewAdapter extends RecyclerView.Adapter<FavsRecyclerViewAdapter.ViewHolder> implements ItemTouchHelperAdapter
{
    /** List of Content objects to display*/
    private ArrayList<Content> contentList;
    Content content;
    Context context;

    private int rowLayout;

    public FavsRecyclerViewAdapter(ArrayList<Content> content, int rowLayout, Context context)
    {
        contentList = content;
        this.rowLayout = rowLayout;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v,contentList);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i)
    {
        content = contentList.get(i);
        viewHolder.title.setText(content.getTitle());
        viewHolder.other.setText(content.getContentString());
        if (viewHolder.logo != null)
        {
            viewHolder.logo.setImageResource(CNXUtil.getCoverId(content,context));

        }

    }

    @Override
    public int getItemCount()
    {
        return contentList == null ? 0 : contentList.size();
    }

    @Override
    public void onItemDismiss(int position)
    {
        Content currentContent = contentList.get(position);
        context.getContentResolver().delete(Favs.CONTENT_URI, "_id="+ currentContent.getId(), null);
        contentList.remove(position);
        notifyItemRemoved(position);
        Toast.makeText(context, "Bookmark deleted for " + currentContent.getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition)
    {
        return true;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public ImageView logo;
        public TextView title;
        public TextView other;
        public View view;
        ArrayList<Content> contentList;

        public ViewHolder(View itemView, ArrayList<Content> contentList)
        {
            super(itemView);
            view = itemView;
            this.contentList = contentList;

            logo = (ImageView) itemView.findViewById(R.id.logoView);
            title = (TextView)itemView.findViewById(R.id.bookName);
            other = (TextView)itemView.findViewById(R.id.other);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v)
        {
            Content content = contentList.get(getAdapterPosition());
            WebviewLogic wl = new WebviewLogic();
            wl.setContentURLs(content.getUrl(), content);
            Context context = v.getContext();
            Intent wv = new Intent(v.getContext(), WebViewActivity.class);
            wv.putExtra(v.getContext().getString(R.string.webcontent), content);

            context.startActivity(wv);
        }


    }
}
