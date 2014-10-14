/**
 * 
 */
package org.cnx.android.activity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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

    
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_landing);
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
            c7.setContentString(getString(R.string.coming_soon));
            c7.setUrl(new URL("http://m.cnx.org/content/col11613/latest/"));
            c7.setIconDrawable(R.drawable.econ_lg);
            c7.setIcon("econ");

            Content c11 = new Content();
            c11.setTitle(getString(R.string.macro_econ));
            c11.setContentString(getString(R.string.coming_soon));
            c11.setUrl(new URL("http://m.cnx.org/content/col11626/latest/"));
            c11.setIconDrawable(R.drawable.macro_econ_lg);
            c11.setIcon("macro");

            Content c12 = new Content();
            c12.setTitle(getString(R.string.micro_econ));
            c12.setContentString(getString(R.string.coming_soon));
            c12.setUrl(new URL("http://m.cnx.org/content/col11627/latest/"));
            c12.setIconDrawable(R.drawable.micro_econ_lg);
            c12.setIcon("micro");

            Content c8 = new Content();
            c8.setTitle(getString(R.string.precalculus));
            c8.setContentString(getString(R.string.coming_soon));
            c8.setUrl(new URL(fakeURL));
            c8.setIconDrawable(R.drawable.precalculus_lg);

            Content c9 = new Content();
            c9.setTitle(getString(R.string.chemistry));
            c9.setContentString(getString(R.string.coming_soon));
            c9.setUrl(new URL(fakeURL));
            c9.setIconDrawable(R.drawable.chemistry_lg);

            Content c10 = new Content();
            c10.setTitle(getString(R.string.history));
            c10.setContentString(getString(R.string.coming_soon));
            c10.setUrl(new URL(fakeURL));
            c10.setIconDrawable(R.drawable.history_lg);

            Content c13 = new Content();
            c13.setTitle(getString(R.string.psychology));
            c13.setContentString(getString(R.string.coming_soon));
            c13.setUrl(new URL(fakeURL));
            c13.setIconDrawable(R.drawable.psychology_lg);

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
            content.add(c9);
            content.add(c10);
            content.add(c13);

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

    class ImageAdapter extends BaseAdapter
    {
        private Context context;

        List<Bookcover> bookcovers = new ArrayList<Bookcover>();

        public ImageAdapter(Context c)
        {
            context = c;

            bookcovers.add(new Bookcover("",R.drawable.physics_lg));
            bookcovers.add(new Bookcover("",R.drawable.sociology_lg));
            bookcovers.add(new Bookcover("", R.drawable.biology_lg));
            bookcovers.add(new Bookcover("",R.drawable.concepts_biology_lg));
            bookcovers.add(new Bookcover("",R.drawable.anatomy_lg));
            bookcovers.add(new Bookcover("",R.drawable.statistics_lg));
            bookcovers.add(new Bookcover("",R.drawable.econ_lg));
            bookcovers.add(new Bookcover("",R.drawable.macro_econ_lg));
            bookcovers.add(new Bookcover("",R.drawable.micro_econ_lg));
            bookcovers.add(new Bookcover("",R.drawable.precalculus_lg));
            bookcovers.add(new Bookcover("",R.drawable.psychology_lg));
            bookcovers.add(new Bookcover("",R.drawable.chemistry_lg));
            bookcovers.add(new Bookcover("",R.drawable.history_lg));

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

    private class Bookcover
    {

        final String name;
        final int drawableId;

        Bookcover(String name, int drawableId)
        {
            this.name = name;
            this.drawableId = drawableId;
        }


    }
    


}
