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

import org.cnx.android.R;
import org.cnx.android.activity.WebViewActivity;
import org.cnx.android.beans.Content;
import org.cnx.android.utils.CNXUtil;

import java.util.ArrayList;

/** Adapter to properly display books in RecyclerView
* @author Ed Woodward
* */
public class LandingListRecyclerViewAdapter  extends RecyclerView.Adapter<LandingListRecyclerViewAdapter.ViewHolder>
{

    private int rowLayout;
    Context context;
    private ArrayList<Content> contentList;

    public LandingListRecyclerViewAdapter(ArrayList<Content> content, int rowLayout, Context context)
    {
        contentList = content;
        this.rowLayout = rowLayout;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new ViewHolder(v,contentList);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        Content book = contentList.get(position);
        holder.bookTitle.setText(book.getBookTitle());
        if (holder.logo != null && book.getIcon() != null)
        {
            holder.logo.setImageResource(CNXUtil.getCoverId(book.getIcon(), context));

        }
    }

    @Override
    public int getItemCount()
    {
        return contentList == null ? 0 : contentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView bookTitle;
        private ImageView logo;
        ArrayList<Content> contentList;

        public ViewHolder(View itemView, ArrayList<Content> contentList)
        {
            super(itemView);
            this.contentList = contentList;
            bookTitle = (TextView) itemView.findViewById(R.id.title);
            logo = (ImageView) itemView.findViewById(R.id.logoView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            int position = getAdapterPosition();
            Content book = contentList.get(position);
            Context context = v.getContext();
            Intent wv = new Intent(v.getContext(), WebViewActivity.class);
            wv.putExtra(context.getString(R.string.webcontent), book);

            context.startActivity(wv);
        }
    }

}
