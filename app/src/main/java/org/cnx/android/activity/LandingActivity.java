/**
 * 
 */
package org.cnx.android.activity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import org.cnx.android.R;
import org.cnx.android.adapters.LandingListAdapter;
import org.cnx.android.beans.Content;
import org.cnx.android.handlers.SearchHandler;
import org.cnx.android.utils.CNXUtil;
import org.cnx.android.utils.Constants;
import org.cnx.android.utils.ContentCache;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/**
 * Activity for Landing page
 * @author Ed Woodward
 *
 */
public class LandingActivity extends Activity
{
    
    private ListView listView;

    private ArrayList<Content> content;
    private ActionBar aBar;

    private List<HashMap<String,String>> navTitles;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    String[] from = { "nav_icon","nav_item" };
    int[] to = { R.id.nav_icon , R.id.nav_item};
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_landing);
        aBar = this.getActionBar();
        aBar.setTitle(Html.fromHtml("open<b>stax</b> cnx"));
        createList();
        GridView gridView = (GridView) findViewById(R.id.gridView);
        int orient = getResources().getConfiguration().orientation;
        Display d = getWindowManager().getDefaultDisplay();
        boolean isTablet = CNXUtil.isTabletDevice(this);
        if(orient == Configuration.ORIENTATION_LANDSCAPE && isTablet)
        {
            if(CNXUtil.isXLarge(this))
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
            //gridView.setNumColumns(3);

            if(CNXUtil.isXLarge(this))
            {
                gridView.setNumColumns(4);
            }
            else
            {
                gridView.setNumColumns(3);
            }
        }

        gridView.setAdapter(new ImageAdapter(this));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,int position, long id) {

                Content c = content.get(position);
                Intent i = new Intent(getApplicationContext(), WebViewActivity.class);
                //i.putExtra("webcontent",c);
                ContentCache.setObject(getString(R.string.webcontent), c);
                startActivity(i);

            }
        });

        //listView = (ListView)findViewById(R.id.landingList);
        setLayout();

        String[] items = getResources().getStringArray(R.array.nav_list);
        setDrawer(items);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerList = (ListView)findViewById(R.id.left_drawer);
        SimpleAdapter sAdapter = new SimpleAdapter(this,navTitles, R.layout.nav_drawer,from,to);

        // Set the adapter for the list view
        //drawerList.setAdapter(new ArrayAdapter<String>(this,R.layout.drawer_list_item, navTitles));
        // Set the list's click listener
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                //getActionBar().setTitle(getString(R.string.app_name));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                //getActionBar().setTitle(getString(R.string.app_name));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);
        aBar.setDisplayHomeAsUpEnabled(true);
        aBar.setHomeButtonEnabled(true);
        drawerList.setAdapter(sAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(drawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return false;
    }
    
    /**
     * Sets the list adapter and adds the listeners to the list and the search button
     */
    private void setLayout()
    {
//        listView.setAdapter(new LandingListAdapter(this,setContentList()));
//
//        listView.setOnItemClickListener(new ListView.OnItemClickListener()
//        {
//            public void onItemClick(AdapterView<?> a, View v, int i, long l)
//            {
//                Content c = (Content)listView.getItemAtPosition(i);
//                performAction(c.getTitle());
//
//
//            }
//        });
//
//        listView.setOnItemSelectedListener(new ListView.OnItemSelectedListener()
//        {
//            public void onItemSelected(AdapterView<?> a, View v, int i, long l)
//            {
//                Content c = (Content)listView.getItemAtPosition(i);
//                performAction(c.getTitle());
//            }
//
//            public void onNothingSelected(AdapterView<?> arg0)
//            {
//                //do nothing
//            }
//
//        });
        
        ImageButton searchButton = (ImageButton)findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new OnClickListener() 
        {
                  
              public void onClick(View v) 
              {
                  EditText searchFor = (EditText)findViewById(R.id.searchText);
                  performSearch(searchFor.getText().toString());
              }
          });
        EditText searchText = (EditText)findViewById(R.id.searchText);
        searchText.setOnKeyListener(new View.OnKeyListener()
        {
            
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if(event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER)
                    {
                        EditText searchFor = (EditText)findViewById(R.id.searchText);
                        performSearch(searchFor.getText().toString());
                    }
                }
                return false;
            }
        });
        //listView.setAdapter(new LandingListAdapter(this,setContentList()));
        
    }

    private void createList()
    {
        String fakeURL = getString(R.string.lenses_fake_url);
        try
        {
            Content c = new Content();
            c.setTitle(getString(R.string.physics));
            c.setContentString(getString(R.string.physics_desc));
            //c.setUrl(new URL("http://archive.alpha.cnx.org:6543/contents/031da8d3-b525-429c-80cf-6c8ed997733a@7.31.html"));
            c.setUrl(new URL("http://m.cnx.org/content/col11406/latest"));
            c.setIconDrawable(R.drawable.physics_lg);
            c.setIcon("physics");

            Content c2 = new Content();
            c2.setTitle(getString(R.string.sociology));
            c2.setContentString(getString(R.string.sociology_desc));
            c2.setUrl(new URL("http://m.cnx.org/content/col11407/latest/"));
            c2.setIconDrawable(R.drawable.sociology_lg);
            c2.setIcon("sociology");

            Content c3 = new Content();
            c3.setTitle(getString(R.string.biology));
            c3.setContentString(getString(R.string.biology_desc));
            c3.setUrl(new URL("http://m.cnx.org/content/col11448/latest/"));
            c3.setIconDrawable(R.drawable.biology_lg);
            c3.setIcon("biology");

            Content c4 = new Content();
            c4.setTitle(getString(R.string.concepts_biology));
            c4.setContentString(getString(R.string.concepts_biology_desc));
            c4.setUrl(new URL("http://m.cnx.org/content/col11487/latest/"));
            c4.setIconDrawable(R.drawable.concepts_biology_lg);
            c4.setIcon("concepts");

            Content c5 = new Content();
            c5.setTitle(getString(R.string.anatomy));
            c5.setContentString(getString(R.string.anatomy_desc));
            c5.setUrl(new URL("http://m.cnx.org/content/col11496/latest/"));
            c5.setIconDrawable(R.drawable.anatomy_lg);
            c5.setIcon("anatomy");

            Content c6 = new Content();
            c6.setTitle(getString(R.string.statistics));
            c6.setContentString(getString(R.string.statistics_desc));
            //c6.setUrl(new URL("http://cnx.org/contents/30189442-6998-4686-ac05-ed152b91b9de@16.5"));
            c6.setUrl(new URL("http://m.cnx.org/content/col11562/latest/"));
            c6.setIconDrawable(R.drawable.statistics_lg);
            c6.setIcon("statistics");

            Content c7 = new Content();
            c7.setTitle(getString(R.string.econ));
            c7.setContentString(getString(R.string.economics_desc));
            c7.setUrl(new URL("http://m.cnx.org/content/col11613/latest/"));
            c7.setIconDrawable(R.drawable.econ_lg);
            c7.setIcon("econ");

            Content c11 = new Content();
            c11.setTitle(getString(R.string.macro_econ));
            c11.setContentString(getString(R.string.macro_desc));
            c11.setUrl(new URL("http://m.cnx.org/content/col11626/latest/"));
            c11.setIconDrawable(R.drawable.macro_econ_lg);
            c11.setIcon("macro");

            Content c12 = new Content();
            c12.setTitle(getString(R.string.micro_econ));
            c12.setContentString(getString(R.string.micro_desc));
            c12.setUrl(new URL("http://m.cnx.org/content/col11627/latest/"));
            c12.setIconDrawable(R.drawable.micro_econ_lg);
            c12.setIcon("micro");

            Content c8 = new Content();
            c8.setTitle(getString(R.string.precalculus));
            c8.setContentString(getString(R.string.precalculus_desc));
            c8.setUrl(new URL("http://m.cnx.org/content/col11667/latest/"));
            c8.setIconDrawable(R.drawable.precalculus_lg);
//
//            Content c9 = new Content();
//            c9.setTitle(getString(R.string.chemistry));
//            c9.setContentString(getString(R.string.coming_soon));
//            c9.setUrl(new URL(fakeURL));
//            c9.setIconDrawable(R.drawable.chemistry_lg);

            Content c10 = new Content();
            c10.setTitle(getString(R.string.history));
            c10.setContentString(getString(R.string.history_desc));
            c10.setUrl(new URL("http://m.cnx.org/content/col11740/latest/"));
            c10.setIconDrawable(R.drawable.history_lg);

            Content c13 = new Content();
            c13.setTitle(getString(R.string.psychology));
            c13.setContentString(getString(R.string.psychology_desc));
            c13.setUrl(new URL("http://m.cnx.org/content/col11629/latest/"));
            c13.setIconDrawable(R.drawable.psychology_lg);

            Content c14 = new Content();
            c14.setTitle(getString(R.string.bus_fundamentals));
            c14.setContentString(getString(R.string.bus_fundamentals));
            c14.setUrl(new URL("http://m.cnx.org/content/col11227/latest/"));
            c14.setIconDrawable(R.drawable.bus_fundamentals);

            Content c15 = new Content();
            c15.setTitle(getString(R.string.elec_engineering));
            c15.setContentString(getString(R.string.elec_engineering));
            c15.setUrl(new URL("http://m.cnx.org/content/col10040/latest/"));
            c15.setIconDrawable(R.drawable.elec_engineering);

            Content c16 = new Content();
            c16.setTitle(getString(R.string.elem_algebra));
            c16.setContentString(getString(R.string.elem_algebra));
            c16.setUrl(new URL("http://m.cnx.org/content/col10614/latest/"));
            c16.setIconDrawable(R.drawable.elem_algebra);

            Content c17 = new Content();
            c17.setTitle(getString(R.string.advanced_algebra));
            c17.setContentString(getString(R.string.advanced_algebra));
            c17.setUrl(new URL("http://m.cnx.org/content/col10624/latest/"));
            c17.setIconDrawable(R.drawable.advanced_algebra);

            Content c18 = new Content();
            c18.setTitle(getString(R.string.applied_probability));
            c18.setContentString(getString(R.string.applied_probability));
            c18.setUrl(new URL("http://m.cnx.org/content/col10708/latest/"));
            c18.setIconDrawable(R.drawable.applied_probability);

            Content c19 = new Content();
            c19.setTitle(getString(R.string.fast_fourier));
            c19.setContentString(getString(R.string.fast_fourier));
            c19.setUrl(new URL("http://m.cnx.org/content/col10550/latest/"));
            c19.setIconDrawable(R.drawable.fast_fourier);

            Content c20 = new Content();
            c20.setTitle(getString(R.string.first_course));
            c20.setContentString(getString(R.string.first_course));
            c20.setUrl(new URL("http://m.cnx.org/content/col10685/latest/"));
            c20.setIconDrawable(R.drawable.first_course);

            Content c21 = new Content();
            c21.setTitle(getString(R.string.flowering_light));
            c21.setContentString(getString(R.string.flowering_light));
            c21.setUrl(new URL("http://m.cnx.org/content/col10611/latest/"));
            c21.setIconDrawable(R.drawable.flowering_light);

//            Content c22 = new Content();
//            c22.setTitle(getString(R.string.hearing_harmony));
//            c22.setContentString(getString(R.string.hearing_harmony));
//            c22.setUrl(new URL(fakeURL));
//            c22.setIconDrawable(R.drawable.hearing_harmony);

            Content c23 = new Content();
            c23.setTitle(getString(R.string.houston_reflections));
            c23.setContentString(getString(R.string.houston_reflections));
            c23.setUrl(new URL("http://m.cnx.org/content/col10526/latest/"));
            c23.setIconDrawable(R.drawable.houston_reflections);

            Content c24 = new Content();
            c24.setTitle(getString(R.string.memorable_cases));
            c24.setContentString(getString(R.string.memorable_cases));
            c24.setUrl(new URL("http://m.cnx.org/content/col10449/latest/"));
            c24.setIconDrawable(R.drawable.memorable_cases);

            Content c25 = new Content();
            c25.setTitle(getString(R.string.music_theory));
            c25.setContentString(getString(R.string.music_theory));
            c25.setUrl(new URL("http://m.cnx.org/content/col10363/latest/"));
            c25.setIconDrawable(R.drawable.music_theory);

            Content c26 = new Content();
            c26.setTitle(getString(R.string.programming_fundamentals));
            c26.setContentString(getString(R.string.programming_fundamentals));
            c26.setUrl(new URL("http://m.cnx.org/content/col10621/latest/"));
            c26.setIconDrawable(R.drawable.programming_fundamentals);

            if(content == null)
            {
                content = new ArrayList<Content>();
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
            //content.add(c9);
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
            Collections.sort(content);



        }
        catch (MalformedURLException e)
        {
            Log.d("LandingActivity.createList()", "Error: " + e.toString(), e);
        }

    }
    
    /**
     * Calls SearchHandler to perform cnx search
     * @param searchFor String - what to search for
     */
    private void performSearch(String searchFor)
    {
        SearchHandler sh = new SearchHandler();
        sh.performSearch(searchFor, Constants.CNX_SEARCH, this);
    }

    private void selectItem(int position)
    {
        switch (position)
        {
            case 0:
                drawerLayout.closeDrawers();
                break;

            case 1:
                Intent lensesIntent = new Intent(getApplicationContext(), ViewLensesActivity.class);
                startActivity(lensesIntent);
                break;

            case 2:
                Intent favsIntent = new Intent(getApplicationContext(), ViewFavsActivity.class);
                startActivity(favsIntent);
                break;

            case 3:
                Intent fileIntent = new Intent(getApplicationContext(), FileBrowserActivity.class);
                startActivity(fileIntent);
                break;
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id)
        {
            selectItem(position);
        }
    }

    private void setDrawer(String[] items)
    {
        HashMap<String,String> hm1 = new HashMap<String,String>();
        hm1.put("nav_icon",Integer.toString(R.drawable.magnify));
        hm1.put("nav_item",items[0]);

        HashMap<String,String> hm2 = new HashMap<String,String>();
        hm2.put("nav_icon",Integer.toString(R.drawable.ic_action_device_access_storage_1));
        hm2.put("nav_item",items[1]);

        HashMap<String,String> hm3 = new HashMap<String,String>();
        hm3.put("nav_icon",Integer.toString(R.drawable.ic_action_star));
        hm3.put("nav_item",items[2]);

        HashMap<String,String> hm4 = new HashMap<String,String>();
        hm4.put("nav_icon",Integer.toString(R.drawable.ic_action_download));
        hm4.put("nav_item",items[3]);

        navTitles = new ArrayList<HashMap<String,String>>();

        navTitles.add(hm1);
        navTitles.add(hm2);
        navTitles.add(hm3);
        navTitles.add(hm4);
    }

    class ImageAdapter extends BaseAdapter
    {
        private Context context;

        List<Bookcover> bookcovers = new ArrayList<Bookcover>();

        public ImageAdapter(Context c)
        {
            context = c;

            bookcovers.add(new Bookcover("College Physics",R.drawable.physics_lg));
            bookcovers.add(new Bookcover("Introduction To Sociology",R.drawable.sociology_lg));
            bookcovers.add(new Bookcover("Biology", R.drawable.biology_lg));
            bookcovers.add(new Bookcover("Concepts of Biology",R.drawable.concepts_biology_lg));
            bookcovers.add(new Bookcover("Anatomy and Physiology",R.drawable.anatomy_lg));
            bookcovers.add(new Bookcover("Introductory Statistics",R.drawable.statistics_lg));
            bookcovers.add(new Bookcover("Principles of Economics",R.drawable.econ_lg));
            bookcovers.add(new Bookcover("Principles of Macroeconomics",R.drawable.macro_econ_lg));
            bookcovers.add(new Bookcover("Principles of Microeconomics",R.drawable.micro_econ_lg));
            bookcovers.add(new Bookcover("Precalculus",R.drawable.precalculus_lg));
            bookcovers.add(new Bookcover("Psychology",R.drawable.psychology_lg));
            //bookcovers.add(new Bookcover("Chemistry",R.drawable.chemistry_lg));
            bookcovers.add(new Bookcover("Business Fundamentals",R.drawable.bus_fundamentals));
            bookcovers.add(new Bookcover("US History",R.drawable.history_lg));
            bookcovers.add(new Bookcover("Fundamentals of Electrical Engineering",R.drawable.elec_engineering));
            bookcovers.add(new Bookcover("Elementary Algebra",R.drawable.elem_algebra));
            bookcovers.add(new Bookcover("Advanced Algebra II",R.drawable.advanced_algebra));
            bookcovers.add(new Bookcover("Applied Probability",R.drawable.applied_probability));
            bookcovers.add(new Bookcover("Fast Fourier Transforms",R.drawable.fast_fourier));
            bookcovers.add(new Bookcover("First Course in Electrical Engineering",R.drawable.first_course));
            bookcovers.add(new Bookcover("Flowering Light",R.drawable.flowering_light));
            //bookcovers.add(new Bookcover("Hearing Harmony",R.drawable.hearing_harmony));
            bookcovers.add(new Bookcover("Houston Reflections",R.drawable.houston_reflections));
            bookcovers.add(new Bookcover("Images of Memorable Cases",R.drawable.memorable_cases));
            bookcovers.add(new Bookcover("Understanding Basic Music Theory",R.drawable.music_theory));
            bookcovers.add(new Bookcover("Programming Fundamentals",R.drawable.programming_fundamentals));
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
            //TextView name;

            if(v == null) {

                v = LayoutInflater.from(context).inflate(R.layout.gridcell, parent, false);
                v.setTag(R.id.grid_item_image, v.findViewById(R.id.grid_item_image));
                //v.setTag(R.id.text, v.findViewById(R.id.text));
            }

            picture = (ImageView)v.getTag(R.id.grid_item_image);
            //name = (TextView)v.getTag(R.id.text);

            Bookcover item = (Bookcover)getItem(position);

            picture.setImageResource(item.drawableId);
            //name.setText(item.name);

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
