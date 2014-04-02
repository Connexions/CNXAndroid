/**
 * Copyright (c) 2010 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.activity;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.*;
import android.widget.*;
import org.cnx.android.R;
import org.cnx.android.beans.Content;
import org.cnx.android.handlers.MenuHandler;
import org.cnx.android.utils.CNXUtil;
import org.cnx.android.utils.Constants;
import org.cnx.android.utils.ContentCache;
import org.cnx.android.views.ObservableWebView;
import org.cnx.android.views.ObservableWebView.OnScrollChangedCallback;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle; 
import android.util.Log;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;

/**
 * Activity to view selected lens content in a web browser.  
 * 
 * @author Ed Woodward
 *
 */
public class WebViewActivity extends Activity
{
    /** Web browser view for Activity */
    private ObservableWebView webView;
    /** Variable for serialized Content object */
    private Content content;
    /** Constant for serialized object passed to Activity */
    public static final String WEB_MENU = "web";
    public static final String HELP_MENU = "help";
    
    private ActionBar aBar;
    
    private float yPosition = 0f;
    
    private boolean progressBarRunning;

    private List<HashMap<String,String>> navTitles;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    String[] from = { "nav_icon","nav_item" };
    int[] to = { R.id.nav_icon , R.id.nav_item};

    String[] oscBooks = new String[]{"col11406","col11407","col11448","col11487","col11613","col11627","col11626","col11496","col11562"};
    List<String> bookList = Arrays.asList(oscBooks);
    
    /**
     * Progress bar when page is loading
     */
    private ProgressDialog progressBar;
    
    /**
     * keeps track of the previous menu for when the back button is used.
     */
    private String previousMenu =  "";
    
    /** inner class for WebViewClient*/
    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onLoadResource(WebView view, String url) 
        {
            super.onLoadResource(view, url);
            
            Log.d("WebViewClient.onLoadResource()", "Called");
        }
        
        /** loads URL into view */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) 
        {
        	Log.d("WebViewClient.shouldOverrideUrlLo()", "Called");
        	if(!progressBarRunning)
            {
            	setProgressBarIndeterminateVisibility(true);
            }
            view.loadUrl(fixURL(url));
            try
            {
                content.setUrl(new URL(url));
                setUpViews();
                
            }
            catch (MalformedURLException e)
            {
                Log.d("WebViewActivity.shouldOverrideUrlLoading()", "Error: " + e.toString(),e);
            }
            return true;
        }
        
        /* (non-Javadoc)
         * @see android.webkit.WebViewClient#onPageFinished(android.webkit.WebView, java.lang.String)
         * Sets title and URL correctly after the page is fully loaded
         */
        @Override
        public void onPageFinished(WebView view, String url)
        {
            Log.d("WebViewClient.onPageFinished", "title: " + view.getTitle());
            Log.d("WebViewClient.onPageFinished", "url: " + url);
            content.setTitle(view.getTitle());
            try
            {
                content.setUrl(new URL(url));
                
            }
            catch (MalformedURLException e)
            {
                Log.d("WebViewActivity.onPageFinished()", "Error: " + e.toString(),e);
            }
            
            setLayout(url);
            setProgressBarIndeterminateVisibility(false);
            progressBarRunning = false;
            Log.d("WebViewClient.onPageFinished", "setSupportProgressBarIndeterminateVisibility(false) Called");
            yPosition = 0f;

        }

    };
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        //Log.d("LensWebView.onCreate()", "Called");
        
        setContentView(R.layout.new_web_view);
        aBar = this.getActionBar();
        setProgressBarIndeterminateVisibility(true);
        progressBarRunning = true;
        Log.d("WebView.onCreate()", "Called");
        content = (Content)ContentCache.getObject(getString(R.string.webcontent));
        aBar.setTitle(getString(R.string.app_name));
        if(content != null && content.getUrl() != null)
        {
            setLayout(content.getUrl().toString());
        }
        else
        {
            setLayout(getString(R.string.mobile_url));
        }
        
        if(CNXUtil.isConnected(this))
        {
            setUpViews();
            
        }
        else
        {
            webView = (ObservableWebView)findViewById(R.id.web_view);
            CNXUtil.makeNoDataToast(this);
        }

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
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     * Creates option menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
//        super.onCreateOptionsMenu(menu);
//        MenuInflater inflater = getMenuInflater();
//
//        return true;
        MenuInflater inflater = getMenuInflater();
        if(content == null)
        {
            return false;
        }

        menu.clear();
        inflater.inflate(R.menu.web_options_menu, menu);
        previousMenu = WEB_MENU;

        return true;
    }
    

    /* (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     * Handles selected options menu item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        if (drawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

    	if(item.getItemId() == android.R.id.home)
        {
    		ContentCache.removeObject(getString(R.string.cache_contentlist));
            Intent mainIntent = new Intent(getApplicationContext(), ViewLensesActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            return true;
        }
    	else
    	{
	        MenuHandler mh = new MenuHandler();
	        boolean returnVal = mh.handleContextMenu(item, this, content);

            return returnVal;

    	}
        
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     * Handles use of back button on browser 
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) 
    {
        if (webView != null && ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()))
        {
            webView.goBack();
            return true;
            
        }
        return super.onKeyDown(keyCode, event);
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
     * added to handle orientation change.  Not sure why this is needed, but it is.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {        
        super.onConfigurationChanged(newConfig);
    }
    
    
    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() 
    {
        super.onResume();

    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        //Log.d("ViewLenses.onSaveInstanceState()", "saving data");
        ContentCache.setObject(getString(R.string.webcontent), content);
        
    }
    
    /** sets properties on WebView and loads selected content into browser. */
    private void setUpViews() 
    {
        if(content == null || content.url == null)
        {
            return;
        }
        
        //Log.d("WebViewView.setupViews()", "Called");
        webView = (ObservableWebView)findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDefaultFontSize(17);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS); 
        webView.setOnScrollChangedCallback(new OnScrollChangedCallback(){
            public void onScroll(int l, int t)
            {
            	
            	String url = content.getUrl().toString();
            	float newY = webView.getScrollY();
                //Log.d("WebViewActivity", "newY: " +newY);
                //Log.d("WebViewActivity", "yPosition: " +yPosition);
            	if(url.contains(getString(R.string.search)) || url.contains(getString(R.string.html_ext)))
                {
            		hideToolbar();
                }
                else if(newY >= yPosition)
               {
              	 //hide layout
              	 hideToolbar();
               }
               else
               {
              	 //show toolbar
              	 showToolbar();
               }
               yPosition = newY;
            }
         });
        
        //showProgressDialog();
        webView.setWebChromeClient(new WebChromeClient() 
        {

        });
        
        webView.setWebViewClient(webViewClient);
        webView.loadUrl(fixURL(content.url.toString()));
    }        
    
    private void emulateShiftHeld(WebView view)
    {
        try
        {
            KeyEvent shiftPressEvent = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SHIFT_LEFT, 0, 0);
            shiftPressEvent.dispatch(view);
            if(Build.VERSION.SDK_INT == 10) 
            {
                Toast.makeText(this, getString(R.string.gingerbread_copy_msg), Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(this, getString(R.string.froyo_copy_msg), Toast.LENGTH_LONG).show();
            }

        }
        catch (Exception e)
        {
            Log.e("dd", "Exception in emulateShiftHeld()", e);
        }

    }
    
    
    
    /**
     * Replace cnx.org with mobile.cnx.org
     * @param url String - the URL to fix
     * @return String - either the original URL or the modified URL 
     */
    protected String fixURL(String url)
    {
        //Log.d("WebView.fixURL()", "url: " + url);
        StringBuilder newURL = new StringBuilder();
        int googIndex = url.indexOf(getString(R.string.google));
        int helpIndex = url.indexOf(getString(R.string.help_page));
        if(googIndex > -1 || helpIndex > -1)
        {
            return url;
        }
        int index = url.indexOf(getString(R.string.lenses_fake_url));
        int startIndex = 14;
        if(index == -1)
        {
            index = url.indexOf(getString(R.string.mobile_url));
            startIndex = 16;
        }
        if(index > -1)
        {
            newURL.append(Constants.MOBILE_CNX_URL);
            newURL.append(url.substring(startIndex));
            Log.d("WebViewActivity","URL = " + newURL.toString());
            return newURL.toString();
        }
        else
        {

            return url;
            //return "http://m.qa.cnx.org/content/m11932/latest/";
        }
    }
    
    /**
     * Displays dialog to start file download
     * @param type one of 2 types PDF or EPUB
     */
    private void download(String type)
    {
        if(CNXUtil.isConnected(this))
        { 
            MenuHandler mh = new MenuHandler();
            mh.displayAlert(this, content, type);
        }
        else
        {
            CNXUtil.makeNoDataToast(this);
        }
    }
    
    private void hideToolbar()
    {
    	RelativeLayout relLayout = (RelativeLayout)findViewById(R.id.relativeLayout1);
        int visibility = relLayout.getVisibility();
        if(visibility == View.VISIBLE)
        {
            relLayout.setVisibility(View.GONE);
        }
    }
    
    private void showToolbar()
    {
    	RelativeLayout relLayout = (RelativeLayout)findViewById(R.id.relativeLayout1);
        int visibility = relLayout.getVisibility();

        if(visibility == View.GONE)
        {
            relLayout.setVisibility(View.VISIBLE);
        }
    }
    
    /**
     * Hides or displays the action bar based on URL.
     * Should be hidden is search or help is displayed.
     * @param url - URL used to determine if action bar should be displayed.
     */
    private void setLayout(String url)
    {
        RelativeLayout relLayout = (RelativeLayout)findViewById(R.id.relativeLayout1);
        int visibility = relLayout.getVisibility();
        if(url.contains(getString(R.string.search)) || url.contains(getString(R.string.html_ext)))
        {
            if(visibility == View.VISIBLE)
            {
                relLayout.setVisibility(View.GONE);
            }
        }
        else
        {
            if(visibility == View.GONE)
            {
                relLayout.setVisibility(View.VISIBLE);
            }
            
                
                ImageButton noteButton = (ImageButton)findViewById(R.id.noteButton);
                noteButton.setOnClickListener(new OnClickListener() 
                {
                          
                      public void onClick(View v) 
                      {
                          Intent noteintent = new Intent(getApplicationContext(), NoteEditorActivity.class);
                          ContentCache.setObject(getString(R.string.content), content);
                          startActivity(noteintent);
                      }
                  });
                
                ImageButton shareButton = (ImageButton)findViewById(R.id.shareButton);
                shareButton.setOnClickListener(new OnClickListener() 
                {
                          
                      public void onClick(View v) 
                      {
                          Intent intent = new Intent(Intent.ACTION_SEND);
                          intent.setType(getString(R.string.mimetype_text));

                          if(content != null)
                          {
                              intent.putExtra(Intent.EXTRA_SUBJECT, content.getTitle());
                              intent.putExtra(Intent.EXTRA_TEXT, content.getUrl().toString() + " " + getString(R.string.shared_via));
    
                              Intent chooser = Intent.createChooser(intent, getString(R.string.tell_friend) + " "+ content.getTitle());
                              startActivity(chooser);
                          }
                          else
                          {
                              Toast.makeText(WebViewActivity.this, getString(R.string.no_data_msg),  Toast.LENGTH_LONG).show();
                          }

                      }
                  });
                
                ImageButton epubButton = (ImageButton)findViewById(R.id.epubButton);
                
                ImageButton pdfButton = (ImageButton)findViewById(R.id.pdfButton);

                if(isOSCBook(content.getUrl().toString()))
                {
                    epubButton.setVisibility(View.GONE);
                    pdfButton.setVisibility(View.GONE);
                }
                else
                {
                    epubButton.setVisibility(View.VISIBLE);
                    pdfButton.setVisibility(View.VISIBLE);
                    epubButton.setOnClickListener(new OnClickListener()
                    {

                        public void onClick(View v)
                        {
                            download(Constants.EPUB_TYPE);

                        }
                    });

                    pdfButton.setOnClickListener(new OnClickListener()
                    {

                        public void onClick(View v)
                        {
                            download(Constants.PDF_TYPE);

                        }
                    });

                }
                
                ImageButton copyButton = (ImageButton)findViewById(R.id.copyButton);
                if(Build.VERSION.SDK_INT < 11) 
                {
                    copyButton.setOnClickListener(new OnClickListener() 
                    {
                              
                          public void onClick(View v) 
                          {
                              emulateShiftHeld(webView);

                          }
                      });
                }
                else
                {
                    copyButton.setVisibility(View.GONE);
                }

            
        }
    }

    private void selectItem(int position)
    {
        switch (position)
        {
            case 0:
                Intent landingIntent = new Intent(getApplicationContext(), LandingActivity.class);
                startActivity(landingIntent);

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
        hm1.put("nav_icon",Integer.toString(R.drawable.home));
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

    private boolean isOSCBook(String url)
    {
        boolean isOSC = false;
        if(url.contains("content/m"))
        {
            return isOSC;
        }

        for(int i = 0; i < bookList.size(); i++)
        {
            if(url.contains("content/" + bookList.get(i)))
            {
                isOSC = true;
                break;
            }
        }

        return isOSC;
    }
    
}
