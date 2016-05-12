/**
 * Copyright (c) 2015 Rice University
 *
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import org.cnx.android.R;
import org.cnx.android.beans.Content;
import org.cnx.android.utils.CNXUtil;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Fragment for displaying grid of Featured books
 * @author Ed Woodward
 */
public class GridFragment extends Fragment
{
    /** list of lenses as Content objects */
    ArrayList<Content> content;

    OnBookSelectedListener bookListener;

    // Container Activity must implement this interface
    public interface OnBookSelectedListener
    {
        void onBookSelected(Content content);
    }


    public GridFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Activity activity = getActivity();
        createList();
        View v = inflater.inflate(R.layout.grid_fragment, container, false);
        GridView gridView = (GridView) v.findViewById(R.id.gridView);
        int orient = getResources().getConfiguration().orientation;
        boolean isTablet = CNXUtil.isTabletDevice(activity);
        if(orient == Configuration.ORIENTATION_LANDSCAPE && isTablet)
        {
            if(CNXUtil.isXLarge(activity))
            {
                gridView.setNumColumns(5);
            }
            else
            {
                gridView.setNumColumns(4);
            }
        }
        else if(orient == Configuration.ORIENTATION_LANDSCAPE)
        {
            gridView.setNumColumns(3);
        }
        else if(orient == Configuration.ORIENTATION_PORTRAIT && isTablet)
        {

            if(CNXUtil.isXLarge(activity))
            {
                gridView.setNumColumns(4);
            }
            else
            {
                gridView.setNumColumns(3);
            }
        }

        gridView.setAdapter(new ImageAdapter(activity));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                //Log.d("LandingActivity", "position: " + position);

                Content c = content.get(position);
                bookListener.onBookSelected(c);
            }
        });
        return v;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            bookListener = (OnBookSelectedListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement OnBookSelectedListener");
        }
    }


    /**
     * Create objects for featured book list
     */

    private void createList()
    {
        try
        {
            Content c = new Content();
            c.setTitle(getString(R.string.physics));
            c.setBookTitle(getString(R.string.physics));
            c.setContentString(getString(R.string.physics_desc));
            c.setUrl(new URL("http://cnx.org/contents/Ax2o07Ul:HR_VN3f7?minimal=true"));
            c.setIconDrawable(R.drawable.physics_lg);
            c.setIcon("physics");
            c.setBookURL("http://cnx.org/contents/Ax2o07Ul:HR_VN3f7?minimal=true");

            Content c2 = new Content();
            c2.setTitle(getString(R.string.sociology));
            c2.setBookTitle(getString(R.string.sociology));
            c2.setContentString(getString(R.string.sociology_desc));
            c2.setUrl(new URL("http://cnx.org/contents/AgQDEnLI:TrIRM88K?minimal=true"));
            c2.setIconDrawable(R.drawable.sociology_lg);
            c2.setIcon("sociology");
            c2.setBookURL("http://cnx.org/contents/AgQDEnLI:TrIRM88K?minimal=true");

            Content c3 = new Content();
            c3.setTitle(getString(R.string.biology));
            c3.setBookTitle(getString(R.string.biology));
            c3.setContentString(getString(R.string.biology_desc));
            c3.setUrl(new URL("http://cnx.org/contents/GFy_h8cu:rZudN6XP?minimal=true"));
            c3.setIconDrawable(R.drawable.biology_lg);
            c3.setIcon("biology");
            c3.setBookURL("http://cnx.org/contents/GFy_h8cu:rZudN6XP?minimal=true");

            Content c4 = new Content();
            c4.setTitle(getString(R.string.concepts_biology));
            c4.setBookTitle(getString(R.string.concepts_biology));
            c4.setContentString(getString(R.string.concepts_biology_desc));
            c4.setUrl(new URL("http://cnx.org/contents/s8Hh0oOc:Pj8cW7X1?minimal=true"));
            c4.setIconDrawable(R.drawable.concepts_biology_lg);
            c4.setIcon("concepts");
            c4.setBookURL("http://cnx.org/contents/s8Hh0oOc:Pj8cW7X1?minimal=true");

            Content c5 = new Content();
            c5.setTitle(getString(R.string.anatomy));
            c5.setBookTitle(getString(R.string.anatomy));
            c5.setContentString(getString(R.string.anatomy_desc));
            c5.setUrl(new URL("http://cnx.org/contents/FPtK1zmh:zMTtFGyH?minimal=true"));
            c5.setIconDrawable(R.drawable.anatomy_lg);
            c5.setIcon("anatomy");
            c5.setBookURL("http://cnx.org/contents/FPtK1zmh:zMTtFGyH?minimal=true");

            Content c6 = new Content();
            c6.setTitle(getString(R.string.statistics));
            c6.setBookTitle(getString(R.string.statistics));
            c6.setContentString(getString(R.string.statistics_desc));
            c6.setUrl(new URL("http://cnx.org/contents/MBiUQmmY:2T34_25K?minimal=true"));
            c6.setIconDrawable(R.drawable.statistics_lg);
            c6.setIcon("statistics");
            c6.setBookURL("http://cnx.org/contents/MBiUQmmY:2T34_25K?minimal=true");

            Content c7 = new Content();
            c7.setTitle(getString(R.string.econ));
            c7.setBookTitle(getString(R.string.econ));
            c7.setContentString(getString(R.string.economics_desc));
            c7.setUrl(new URL("http://cnx.org/contents/aWGdK2jw:JgDXaOLP?minimal=true"));
            c7.setIconDrawable(R.drawable.econ_lg);
            c7.setIcon("econ");
            c7.setBookURL("http://cnx.org/contents/aWGdK2jw:JgDXaOLP?minimal=true");

            Content c11 = new Content();
            c11.setTitle(getString(R.string.macro_econ));
            c11.setBookTitle(getString(R.string.macro_econ));
            c11.setContentString(getString(R.string.macro_desc));
            c11.setUrl(new URL("http://cnx.org/contents/QGHIMgmO:JgDXaOLP?minimal=true"));
            c11.setIconDrawable(R.drawable.macro_econ_lg);
            c11.setIcon("macro");
            c11.setBookURL("http://cnx.org/contents/QGHIMgmO:JgDXaOLP?minimal=true");

            Content c12 = new Content();
            c12.setTitle(getString(R.string.micro_econ));
            c12.setBookTitle(getString(R.string.micro_econ));
            c12.setContentString(getString(R.string.micro_desc));
            c12.setUrl(new URL("http://cnx.org/contents/QGHIMgmO:JgDXaOLP?minimal=true"));
            c12.setIconDrawable(R.drawable.micro_econ_lg);
            c12.setIcon("micro");
            c12.setBookURL("http://cnx.org/contents/QGHIMgmO:JgDXaOLP?minimal=true");

            Content c8 = new Content();
            c8.setTitle(getString(R.string.precalculus));
            c8.setBookTitle(getString(R.string.precalculus));
            c8.setContentString(getString(R.string.precalculus_desc));
            c8.setUrl(new URL("http://cnx.org/contents/_VPq4foj:vEOnJry_?minimal=true"));
            c8.setIconDrawable(R.drawable.precalculus_lg);
            c8.setIcon("precalculus");
            c8.setBookURL("http://cnx.org/contents/_VPq4foj:vEOnJry_?minimal=true");

            Content c9 = new Content();
            c9.setTitle(getString(R.string.chemistry));
            c9.setBookTitle(getString(R.string.chemistry));
            c9.setContentString(getString(R.string.chemistry_desc));
            c9.setUrl(new URL("http://cnx.org/contents/havxkyvS:uXg0kUa-?minimal=true"));
            c9.setIconDrawable(R.drawable.chemistry_lg);
            c9.setIcon("chemistry");
            c9.setBookURL("http://cnx.org/contents/havxkyvS:uXg0kUa-?minimal=true");

            Content c10 = new Content();
            c10.setTitle(getString(R.string.history));
            c10.setBookTitle(getString(R.string.history));
            c10.setContentString(getString(R.string.history_desc));
            c10.setUrl(new URL("http://cnx.org/contents/p7ovuIkl:gMXC1GEM?minimal=true"));
            c10.setIconDrawable(R.drawable.history_lg);
            c10.setIcon("history");
            c10.setBookURL("http://cnx.org/contents/p7ovuIkl:gMXC1GEM?minimal=true");

            Content c13 = new Content();
            c13.setTitle(getString(R.string.psychology));
            c13.setBookTitle(getString(R.string.psychology));
            c13.setContentString(getString(R.string.psychology_desc));
            c13.setUrl(new URL("http://cnx.org/contents/Sr8Ev5Og:6HoLG-TA?minimal=true"));
            c13.setIconDrawable(R.drawable.psychology_lg);
            c13.setIcon("psychology");
            c13.setBookURL("http://cnx.org/contents/Sr8Ev5Og:6HoLG-TA?minimal=true");

            Content c14 = new Content();
            c14.setTitle(getString(R.string.bus_fundamentals));
            c14.setBookTitle(getString(R.string.bus_fundamentals));
            c14.setContentString(getString(R.string.bus_fundamentals));
            c14.setUrl(new URL("http://cnx.org/contents/1ttgPM0x:KfFg0EJq?minimal=true"));
            c14.setIconDrawable(R.drawable.bus_fundamentals);
            c14.setBookURL("http://cnx.org/contents/1ttgPM0x:KfFg0EJq?minimal=true");

            Content c15 = new Content();
            c15.setTitle(getString(R.string.elec_engineering));
            c15.setBookTitle(getString(R.string.elec_engineering));
            c15.setContentString(getString(R.string.elec_engineering));
            c15.setUrl(new URL("http://cnx.org/contents/d442r0wh:g9deOnx5?minimal=true"));
            c15.setIconDrawable(R.drawable.elec_engineering);
            c15.setBookURL("http://cnx.org/contents/d442r0wh:g9deOnx5?minimal=true");

            Content c16 = new Content();
            c16.setTitle(getString(R.string.elem_algebra));
            c16.setBookTitle(getString(R.string.elem_algebra));
            c16.setContentString(getString(R.string.elem_algebra));
            c16.setUrl(new URL("http://cnx.org/contents/e9XCtyLF:uUfJZx98?minimal=true"));
            c16.setIconDrawable(R.drawable.elem_algebra);
            c16.setBookURL("http://cnx.org/contents/e9XCtyLF:uUfJZx98?minimal=true");

            Content c17 = new Content();
            c17.setTitle(getString(R.string.advanced_algebra));
            c17.setBookTitle(getString(R.string.advanced_algebra));
            c17.setContentString(getString(R.string.advanced_algebra));
            c17.setUrl(new URL("http://cnx.org/contents/MnoU7pX2:FEyXudZI?minimal=true"));
            c17.setIconDrawable(R.drawable.advanced_algebra);
            c17.setBookURL("http://cnx.org/contents/MnoU7pX2:FEyXudZI?minimal=true");

            Content c18 = new Content();
            c18.setTitle(getString(R.string.applied_probability));
            c18.setBookTitle(getString(R.string.applied_probability));
            c18.setContentString(getString(R.string.applied_probability));
            c18.setUrl(new URL("http://cnx.org/contents/HLT_qvJK:wsOQ6HtH?minimal=true"));
            c18.setIconDrawable(R.drawable.applied_probability);
            c18.setBookURL("http://cnx.org/contents/HLT_qvJK:wsOQ6HtH?minimal=true");

            Content c19 = new Content();
            c19.setTitle(getString(R.string.fast_fourier));
            c19.setBookTitle(getString(R.string.fast_fourier));
            c19.setContentString(getString(R.string.fast_fourier));
            c19.setUrl(new URL("http://cnx.org/contents/gua6b7go:-1XsePkH?minimal=true"));
            c19.setIconDrawable(R.drawable.fast_fourier);
            c19.setBookURL("http://cnx.org/contents/gua6b7go:-1XsePkH?minimal=true");

            Content c20 = new Content();
            c20.setTitle(getString(R.string.first_course));
            c20.setBookTitle(getString(R.string.first_course));
            c20.setContentString(getString(R.string.first_course));
            c20.setUrl(new URL("http://cnx.org/contents/fpkWedRh:tQa_RWkY?minimal=true"));
            c20.setIconDrawable(R.drawable.first_course);
            c20.setBookURL("http://cnx.org/contents/fpkWedRh:tQa_RWkY?minimal=true");

            Content c21 = new Content();
            c21.setTitle(getString(R.string.flowering_light));
            c21.setBookTitle(getString(R.string.flowering_light));
            c21.setContentString(getString(R.string.flowering_light));
            c21.setUrl(new URL("http://cnx.org/contents/OPolDtlN:KvFTO2EG?minimal=true"));
            c21.setIconDrawable(R.drawable.flowering_light);
            c21.setBookURL("http://cnx.org/contents/OPolDtlN:KvFTO2EG?minimal=true");

            //            Content c22 = new Content();
            //            c22.setTitle(getString(R.string.hearing_harmony));
            //            c22.setContentString(getString(R.string.hearing_harmony));
            //            c22.setUrl(new URL(fakeURL));
            //            c22.setIconDrawable(R.drawable.hearing_harmony);

            Content c23 = new Content();
            c23.setTitle(getString(R.string.houston_reflections));
            c23.setBookTitle(getString(R.string.houston_reflections));
            c23.setContentString(getString(R.string.houston_reflections));
            c23.setUrl(new URL("http://cnx.org/contents/eFQIsJEO:AIRHelyE?minimal=true"));
            c23.setIconDrawable(R.drawable.houston_reflections);
            c23.setBookURL("http://cnx.org/contents/eFQIsJEO:AIRHelyE?minimal=true");

            Content c24 = new Content();
            c24.setTitle(getString(R.string.memorable_cases));
            c24.setBookTitle(getString(R.string.memorable_cases));
            c24.setContentString(getString(R.string.memorable_cases));
            c24.setUrl(new URL("http://cnx.org/contents/57cfLKUe:SwTmBcjN?minimal=true"));
            c24.setIconDrawable(R.drawable.memorable_cases);
            c24.setBookURL("http://cnx.org/contents/57cfLKUe:SwTmBcjN?minimal=true");

            Content c25 = new Content();
            c25.setTitle(getString(R.string.music_theory));
            c25.setBookTitle(getString(R.string.music_theory));
            c25.setContentString(getString(R.string.music_theory));
            c25.setUrl(new URL("http://cnx.org/contents/KtdLe6cv:_GmJ4ENa?minimal=true"));
            c25.setIconDrawable(R.drawable.music_theory);
            c25.setBookURL("http://cnx.org/contents/KtdLe6cv:_GmJ4ENa?minimal=true");

            Content c26 = new Content();
            c26.setTitle(getString(R.string.programming_fundamentals));
            c26.setBookTitle(getString(R.string.programming_fundamentals));
            c26.setContentString(getString(R.string.programming_fundamentals));
            c26.setUrl(new URL("http://cnx.org/contents/MDgA8wfz:YzfkjC2r?minimal=true"));
            c26.setIconDrawable(R.drawable.programming_fundamentals);
            c26.setBookURL("http://cnx.org/contents/MDgA8wfz:YzfkjC2r?minimal=true");

            Content c27 = new Content();
            c27.setTitle(getString(R.string.algebra));
            c27.setBookTitle(getString(R.string.algebra));
            c27.setContentString(getString(R.string.algebra_desc));
            c27.setUrl(new URL("http://cnx.org/contents/mwjClAV_:nU8Qkzwo?minimal=true"));
            c27.setIconDrawable(R.drawable.algebra_lg);
            c27.setIcon("algebra");
            c27.setBookURL("http://cnx.org/contents/mwjClAV_:nU8Qkzwo?minimal=true");

            Content c28 = new Content();
            c28.setTitle(getString(R.string.trig));
            c28.setBookTitle(getString(R.string.trig));
            c28.setContentString(getString(R.string.trig_desc));
            c28.setUrl(new URL("http://cnx.org/contents/E6wQevFf@:nU8Qkzwo?minimal=true"));
            c28.setIconDrawable(R.drawable.trig_lg);
            c28.setIcon("trig");
            c28.setBookURL("http://cnx.org/contents/E6wQevFf@:nU8Qkzwo?minimal=true");

            Content c29 = new Content();
            c29.setTitle(getString(R.string.ap_physics));
            c29.setBookTitle(getString(R.string.ap_physics));
            c29.setContentString(getString(R.string.ap_physics_desc));
            c29.setUrl(new URL("http://cnx.org/contents/jQSmhtXo:6pB5TgBD?minimal=true"));
            c29.setIconDrawable(R.drawable.ap_physics_lg);
            c29.setIcon("ap-physics");
            c29.setBookURL("http://cnx.org/contents/jQSmhtXo:6pB5TgBD?minimal=true");

            Content c30 = new Content();
            c30.setTitle(getString(R.string.ap_macro));
            c30.setBookTitle(getString(R.string.ap_macro));
            c30.setContentString(getString(R.string.coming_soon));
            c30.setUrl(new URL("http://cnx.org/contents/MwdgVOwd:CSCWn8lc?minimal=true"));
            c30.setIconDrawable(R.drawable.ap_macro);
            c30.setIcon("ap-macro");
            c30.setBookURL("http://cnx.org/contents/MwdgVOwd:CSCWn8lc?minimal=true");

            Content c31 = new Content();
            c31.setTitle(getString(R.string.ap_micro));
            c31.setBookTitle(getString(R.string.ap_micro));
            c31.setContentString(getString(R.string.coming_soon));
            c31.setUrl(new URL("http://cnx.org/contents/yjROLWcx:CSCWn8lc?minimal=true"));
            c31.setIconDrawable(R.drawable.ap_micro);
            c31.setIcon("ap-micro");
            c31.setBookURL("http://cnx.org/contents/yjROLWcx:CSCWn8lc?minimal=true");

            if(content == null)
            {
                content = new ArrayList<>();
            }

            content.add(c);
            content.add(c2);
            content.add(c3);
            content.add(c4);
            content.add(c5);
            content.add(c6);
            content.add(c7);
            content.add(c11);
            content.add(c12);
            content.add(c8);
            content.add(c9);
            content.add(c10);
            content.add(c13);
            content.add(c14);
            content.add(c15);
            content.add(c16);
            content.add(c17);
            content.add(c18);
            content.add(c19);
            content.add(c20);
            content.add(c21);
            //content.add(c22);
            content.add(c23);
            content.add(c24);
            content.add(c25);
            content.add(c26);
            content.add(c27);
            content.add(c28);
            content.add(c29);
            content.add(c30);
            content.add(c31);
            Collections.sort(content);

        }
        catch (MalformedURLException e)
        {
            Log.d("Landing.createList()", "Error: " + e.toString(), e);
        }

    }

    class ImageAdapter extends BaseAdapter
    {
        private Context context;

        List<Bookcover> bookcovers = new ArrayList<>();

        public ImageAdapter(Context c)
        {
            context = c;

            bookcovers.add(new Bookcover(context.getString(R.string.physics),R.drawable.physics_lg));
            bookcovers.add(new Bookcover(context.getString(R.string.sociology),R.drawable.sociology_lg));
            bookcovers.add(new Bookcover(context.getString(R.string.biology), R.drawable.biology_lg));
            bookcovers.add(new Bookcover(context.getString(R.string.concepts_biology),R.drawable.concepts_biology_lg));
            bookcovers.add(new Bookcover(context.getString(R.string.anatomy),R.drawable.anatomy_lg));
            bookcovers.add(new Bookcover(context.getString(R.string.statistics),R.drawable.statistics_lg));
            bookcovers.add(new Bookcover(context.getString(R.string.econ),R.drawable.econ_lg));
            bookcovers.add(new Bookcover(context.getString(R.string.macro_econ),R.drawable.macro_econ_lg));
            bookcovers.add(new Bookcover(context.getString(R.string.micro_econ),R.drawable.micro_econ_lg));
            bookcovers.add(new Bookcover(context.getString(R.string.precalculus),R.drawable.precalculus_lg));
            bookcovers.add(new Bookcover(context.getString(R.string.psychology),R.drawable.psychology_lg));
            bookcovers.add(new Bookcover(context.getString(R.string.chemistry),R.drawable.chemistry_lg));
            bookcovers.add(new Bookcover(context.getString(R.string.bus_fundamentals),R.drawable.bus_fundamentals));
            bookcovers.add(new Bookcover(context.getString(R.string.history),R.drawable.history_lg));
            bookcovers.add(new Bookcover(context.getString(R.string.elec_engineering),R.drawable.elec_engineering));
            bookcovers.add(new Bookcover(context.getString(R.string.elem_algebra),R.drawable.elem_algebra));
            bookcovers.add(new Bookcover(context.getString(R.string.advanced_algebra),R.drawable.advanced_algebra));
            bookcovers.add(new Bookcover(context.getString(R.string.applied_probability),R.drawable.applied_probability));
            bookcovers.add(new Bookcover(context.getString(R.string.fast_fourier),R.drawable.fast_fourier));
            bookcovers.add(new Bookcover(context.getString(R.string.first_course),R.drawable.first_course));
            bookcovers.add(new Bookcover(context.getString(R.string.flowering_light),R.drawable.flowering_light));
            //bookcovers.add(new Bookcover("Hearing Harmony",R.drawable.hearing_harmony));
            bookcovers.add(new Bookcover(context.getString(R.string.houston_reflections),R.drawable.houston_reflections));
            bookcovers.add(new Bookcover(context.getString(R.string.memorable_cases),R.drawable.memorable_cases));
            bookcovers.add(new Bookcover(context.getString(R.string.music_theory),R.drawable.music_theory));
            bookcovers.add(new Bookcover(context.getString(R.string.programming_fundamentals),R.drawable.programming_fundamentals));
            bookcovers.add(new Bookcover(context.getString(R.string.algebra),R.drawable.algebra_lg));
            bookcovers.add(new Bookcover(context.getString(R.string.trig),R.drawable.trig_lg));
            bookcovers.add(new Bookcover(context.getString(R.string.ap_physics),R.drawable.ap_physics_lg));
            bookcovers.add(new Bookcover(context.getString(R.string.ap_macro), R.drawable.ap_macro));
            bookcovers.add(new Bookcover(context.getString(R.string.ap_micro), R.drawable.ap_micro));
            Collections.sort(bookcovers);




        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount()
        {
            return bookcovers.size();
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        public Object getItem(int position)
        {
            return bookcovers.get(position);
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getItemId(int)
         */
        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {

            View v = convertView;
            ImageView picture;

            if(v == null) {

                v = LayoutInflater.from(context).inflate(R.layout.gridcell, parent, false);
                v.setTag(R.id.grid_item_image, v.findViewById(R.id.grid_item_image));
            }

            picture = (ImageView)v.getTag(R.id.grid_item_image);

            Bookcover item = (Bookcover)getItem(position);

            picture.setImageResource(item.drawableId);

            return v;
        }

    }

    private class Bookcover implements Comparable<Bookcover>
        {

            final String name;
            final int drawableId;

            Bookcover(String name, int drawableId)
            {
                this.name = name;
                this.drawableId = drawableId;
            }

            public int compareTo(Bookcover another)
            {
                int titleCompare = name.toUpperCase().trim().compareTo(another.name.toUpperCase().trim());

                return titleCompare;
            }


        }
}
