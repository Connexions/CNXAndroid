/**
 * Copyright (c) 2010 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.activity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.view.*;
import android.widget.*;
import org.cnx.android.R;
import org.cnx.android.beans.Content;
import org.cnx.android.handlers.MenuHandler;
import org.cnx.android.listeners.DrawerItemClickListener;
import org.cnx.android.utils.CNXUtil;
import org.cnx.android.utils.Constants;
import org.cnx.android.views.ObservableWebView;
import org.cnx.android.views.ObservableWebView.OnScrollChangedCallback;

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
 * Activity to view selected content in a web browser.
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

    private float yPosition = 0f;
    
    private boolean progressBarRunning;

    private ActionBarDrawerToggle drawerToggle;
    String[] from = { "nav_icon","nav_item"};
    int[] to = { R.id.nav_icon , R.id.nav_item};

    String[] oscBooks = new String[]{"col11406","col11407","col11448","col11487","col11613","col11627","col11626","col11496","col11562","col11667","col11740","col11629","col11758","col11759","col11760","col11844","col11762","col11858","col11864"};
    List<String> bookList = Arrays.asList(oscBooks);
    SharedPreferences sharedPref;
    
    /**
     * Progress bar when page is loading
     */
    //private ProgressDialog progressBar;
    
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
            
            //Log.d("WbVClt.onLoadResource()", "Called");
        }
        
        /** loads URL into view */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) 
        {
        	//Log.d("WV.shouldOverrideUrl()", "Called");
        	if(!progressBarRunning)
            {
            	setProgressBarIndeterminateVisibility(true);
            }

            view.loadUrl(url);
            try
            {
                content.setUrl(new URL(url));
                
            }
            catch (MalformedURLException e)
            {
                Log.d("WV.shouldOverrideUrl()", "Error: " + e.toString(),e);
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
            //Log.d("WebViewC.onPageFinished", "title: " + view.getTitle());
            //Log.d("WebViewC.onPageFinished", "url: " + url);
            content.setTitle(view.getTitle());
            if(!url.contains("?minimal=true"))
            {
                url = url + "?minimal=true";
                view.loadUrl(url);
            }
            try
            {
                //content.setUrl(new URL(url));
                content.setUrl(new URL(view.getUrl()));

            }
            catch (MalformedURLException e)
            {
                Log.d("WebVA.onPageFinished()", "Error: " + e.toString(),e);
            }
            
            setProgressBarIndeterminateVisibility(false);
            progressBarRunning = false;
            Log.d("WebViewC.onPageFinished", "setSupportProgressBarIndeterminateVisibility(false) Called");
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
        ActionBar aBar = this.getActionBar();
        sharedPref = getSharedPreferences("org.cnx.android",MODE_PRIVATE);
        setProgressBarIndeterminateVisibility(true);
        progressBarRunning = true;
        //Log.d("WebView.onCreate()", "Called");
        Intent intent = getIntent();
        content = (Content)intent.getSerializableExtra(getString(R.string.webcontent));
        Log.d("url",content.getUrl().toString());

        try
        {
            if(!content.getUrl().toString().contains("?bookmark=1"))
            {

                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.cnx_package), MODE_PRIVATE);
                String url = sharedPref.getString(content.getIcon(), "");

                if(!url.equals(""))
                {
                    url = convertURL(url);
                    try
                    {
                        content.setUrl(new URL(url));
                    }
                    catch(MalformedURLException mue)
                    {
                        Log.e("WViewActivity.onResume", mue.toString());
                    }
                }
            }
            else
            {
                //remove bookmark parameter
                String newURL = content.getUrl().toString().replace("?bookmark=1","");
                content.setUrl(new URL(newURL));

            }
        }
        catch(MalformedURLException mue)
        {
            Log.e("WViewActivity.onResume", mue.toString());
        }

        aBar.setTitle(Html.fromHtml(getString(R.string.app_name_html)));

        if(CNXUtil.isConnected(this))
        {
            setUpViews();
            
        }
        else
        {
            webView = (ObservableWebView)findViewById(R.id.web_view);
            CNXUtil.makeNoDataToast(this);
        }

        List<HashMap<String,String>> navTitles = CNXUtil.createNavItems(this);
        DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        ListView drawerList = (ListView)findViewById(R.id.left_drawer);
        SimpleAdapter sAdapter = new SimpleAdapter(this,navTitles, R.layout.nav_drawer,from,to);

        drawerList.setOnItemClickListener(new DrawerItemClickListener(this, drawerLayout));

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close)
        {
            public void onDrawerClosed(View view)
            {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView)
            {
                invalidateOptionsMenu();
            }
        };
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);
        aBar.setDisplayHomeAsUpEnabled(true);
        aBar.setHomeButtonEnabled(true);
        drawerList.setAdapter(sAdapter);
        String pref = sharedPref.getString("cacheCleared", "");
        if(pref.equals(""))
        {
            webView.clearCache(true);
            SharedPreferences.Editor ed = sharedPref.edit();
            ed.putString("cacheCleared", "true");
            ed.apply();
        }
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     * Creates option menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
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
     * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) 
    {
        super.onPrepareOptionsMenu(menu);
        //handle changing menu based on URL
        return onCreateOptionsMenu(menu);
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
            Intent mainIntent = new Intent(getApplicationContext(), LandingActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            return true;
        }
    	else
    	{
            try
            {

                content.setTitle(webView.getTitle().replace(" - " + content.getBookTitle() + " - OpenStax CNX",""));
                content.setUrl(new URL(webView.getUrl()));

            }
            catch(MalformedURLException mue)
            {

            }
	        MenuHandler mh = new MenuHandler();
	        return mh.handleContextMenu(item, this, content);

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
        if(content.getIcon() != null)
        {
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.cnx_package), MODE_PRIVATE);
            String url = sharedPref.getString(content.getIcon(), "");
            //Log.d("WebViewActivity.onResume()","URL retrieved: " + url);
            if(!url.equals(""))
            {
                url = convertURL(url);
                try
                {
                    content.setUrl(new URL(url));
                }
                catch(MalformedURLException mue)
                {
                    Log.e("WViewActivity.onResume", mue.toString());
                }
            }
        }

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if(content.getIcon() != null)
        {
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.cnx_package), MODE_PRIVATE);
            SharedPreferences.Editor ed = sharedPref.edit();
            //Log.d("WVA.onPause()","URL saved: " + content.getUrl().toString());
            String url = webView.getUrl().replace("?bookmark=1", "");
            ed.putString(content.getIcon(), url);
            ed.apply();
        }
    }

    
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        //Log.d("ViewLenses.onSaveInstanceState()", "saving data");
        outState.putSerializable(getString(R.string.webcontent),content);
        if(content.getIcon() != null)
        {
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.cnx_package), MODE_PRIVATE);
            SharedPreferences.Editor ed = sharedPref.edit();
            String url = webView.getUrl().replace("?bookmark=1", "");
            ed.putString(content.getIcon(), url);
            ed.apply();
        }
        
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

        webView.setWebChromeClient(new WebChromeClient() 
        {

        });
        
        webView.setWebViewClient(webViewClient);
        webView.loadUrl(content.url.toString());
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
    
//    private void showToolbar()
//    {
//    	RelativeLayout relLayout = (RelativeLayout)findViewById(R.id.relativeLayout1);
//        int visibility = relLayout.getVisibility();
//
//        if(url.contains("/content/"))
//        {
//            return url.replace("//m.","//");
//        }
//        else
//        {
//            return url;
//        }
//    }

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

    private String convertURL(String url)
    {

        if(url.contains("/content/"))
        {
            return url.replace("//m.","//");
        }
        else
        {
            return url;
        }
    }
    
}
