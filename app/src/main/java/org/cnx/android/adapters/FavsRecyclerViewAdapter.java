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
import org.cnx.android.providers.Favs;

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
        if (viewHolder.logo != null && content.icon != null)
        {

            //set correct icon based on URL
            if(content.getIcon().equals("physics"))
            {
                viewHolder.logo.setImageResource(R.drawable.physics_lg);
            }
            else if(content.getIcon().equals("sociology"))
            {
                viewHolder.logo.setImageResource(R.drawable.sociology_lg);
            }
            else if(content.getIcon().equals("biology"))
            {
                viewHolder.logo.setImageResource(R.drawable.biology_lg);
            }
            else if(content.getIcon().equals("concepts"))
            {
                viewHolder.logo.setImageResource(R.drawable.concepts_biology_lg);
            }
            else if(content.getIcon().equals("anatomy"))
            {
                viewHolder.logo.setImageResource(R.drawable.anatomy_lg);
            }
            else if(content.getIcon().equals("statistics"))
            {
                viewHolder.logo.setImageResource(R.drawable.statistics_lg);
            }
            else if(content.getIcon().equals("econ"))
            {
                viewHolder.logo.setImageResource(R.drawable.econ_lg);
            }
            else if(content.getIcon().equals("macro"))
            {
                viewHolder.logo.setImageResource(R.drawable.macro_econ_lg);
            }
            else if(content.getIcon().equals("micro"))
            {
                viewHolder.logo.setImageResource(R.drawable.micro_econ_lg);
            }
            else if(content.getIcon().equals("precalculus"))
            {
                viewHolder.logo.setImageResource(R.drawable.precalculus_lg);
            }
            else if(content.getIcon().equals("psychology"))
            {
                viewHolder.logo.setImageResource(R.drawable.psychology_lg);
            }
            else if(content.getIcon().equals("history"))
            {
                viewHolder.logo.setImageResource(R.drawable.history_lg);
            }
            else if(content.getIcon().equals("chemistry"))
            {
                viewHolder.logo.setImageResource(R.drawable.chemistry_lg);
            }
            else if(content.getIcon().equals("algebra"))
            {
                viewHolder.logo.setImageResource(R.drawable.algebra_lg);
            }
            else if(content.getIcon().equals("trig"))
            {
                viewHolder.logo.setImageResource(R.drawable.trig_lg);
            }
            else if(content.getIcon().equals("ap-physics"))
            {
                viewHolder.logo.setImageResource(R.drawable.ap_physics_lg);
            }
            else if(content.getIcon().equals("ap-macro"))
            {
                viewHolder.logo.setImageResource(R.drawable.ap_macro);
            }
            else if(content.getIcon().equals("ap-micro"))
            {
                viewHolder.logo.setImageResource(R.drawable.ap_micro);
            }
            else if(content.getIcon().equals("Business Fundamentals"))
            {
                viewHolder.logo.setImageResource(R.drawable.bus_fundamentals);
            }
            else if(content.getIcon().equals("Fundamentals of Electrical Engineering"))
            {
                viewHolder.logo.setImageResource(R.drawable.elec_engineering);
            }
            else if(content.getIcon().equals("Elementary Algebra"))
            {
                viewHolder.logo.setImageResource(R.drawable.elem_algebra);
            }
            else if(content.getIcon().equals("Advanced Algebra II"))
            {
                viewHolder.logo.setImageResource(R.drawable.advanced_algebra);
            }
            else if(content.getIcon().equals("Applied Probability"))
            {
                viewHolder.logo.setImageResource(R.drawable.applied_probability);
            }
            else if(content.getIcon().equals("Fast Fourier Transforms"))
            {
                viewHolder.logo.setImageResource(R.drawable.fast_fourier);
            }
            else if(content.getIcon().equals("First Course in Electrical Engineering"))
            {
                viewHolder.logo.setImageResource(R.drawable.first_course);
            }
            else if(content.getIcon().equals("Flowering Light"))
            {
                viewHolder.logo.setImageResource(R.drawable.flowering_light);
            }
            else if(content.getIcon().equals("Houston Reflections"))
            {
                viewHolder.logo.setImageResource(R.drawable.houston_reflections);
            }
            else if(content.getIcon().equals("Images of Memorable Cases"))
            {
                viewHolder.logo.setImageResource(R.drawable.memorable_cases);
            }
            else if(content.getIcon().equals("Understanding Basic Music Theory"))
            {
                viewHolder.logo.setImageResource(R.drawable.music_theory);
            }
            else if(content.getIcon().equals("Programming Fundamentals"))
            {
                viewHolder.logo.setImageResource(R.drawable.programming_fundamentals);
            }
            else
            {
                viewHolder.logo.setImageResource(R.drawable.ic_book_black_48dp);
            }
        }
        else
        {
//            if(content.url.toString().contains("lenses"))
//            {
//                viewHolder.logo.setImageResource(R.drawable.lenses);
//            }
//            else if(content.url.toString().contains("content/m"))
//            {
//                viewHolder.logo.setImageResource(R.drawable.modules);
//            }
//            else if(content.url.toString().contains("content/col"))
//            {
//                viewHolder.logo.setImageResource(R.drawable.collections);
//            }
//            else if(content.url.toString().contains("google.com") || content.url.toString().contains("legacy.cnx.org/content/search"))
//            {
//                viewHolder.logo.setImageResource(R.drawable.search_selected);
//            }
//            else
//            {
                viewHolder.logo.setImageResource(R.drawable.ic_book_black_48dp);
            //}
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
        public View view;
        ArrayList<Content> contentList;

        public ViewHolder(View itemView, ArrayList<Content> contentList)
        {
            super(itemView);
            view = itemView;
            this.contentList = contentList;

            logo = (ImageView) itemView.findViewById(R.id.logoView);
            title = (TextView)itemView.findViewById(R.id.bookName);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v)
        {
            Content content = contentList.get(getAdapterPosition());
            Context context = v.getContext();
            Intent wv = new Intent(v.getContext(), WebViewActivity.class);
            wv.putExtra(v.getContext().getString(R.string.webcontent), content);

            context.startActivity(wv);
        }


    }
}
