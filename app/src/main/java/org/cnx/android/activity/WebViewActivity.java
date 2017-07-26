/**
 * Copyright (c) 2010 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.activity;

import android.content.SharedPreferences;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import org.cnx.android.R;
import org.cnx.android.beans.Content;
import org.cnx.android.handlers.MenuHandler;
import org.cnx.android.logic.WebviewLogic;
import org.cnx.android.utils.CNXUtil;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;

/**
 * Activity to view selected content in a web browser.
 * 
 * @author Ed Woodward
 *
 */
public class WebViewActivity extends BaseActivity
{
    /** Web browser view for Activity */
    private WebView webView;
    /** Variable for serialized Content object */
    private Content content;

    private WebviewLogic webviewLogic = new WebviewLogic();

    private boolean progressBarRunning;

    SharedPreferences sharedPref;
    
    /** inner class for WebViewClient*/
    private WebViewClient webViewClient = new WebViewClient()
    {
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
            content.setUrl(url);

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
            if(content.getBookTitle() == null || (!content.getTitle().contains(content.getBookTitle())))
            {
                content.setBookTitle(webviewLogic.getBookTitle(content.getTitle()));
            }
            if(!url.contains("?minimal=true"))
            {
                url = url + "?minimal=true";
                view.loadUrl(url);
            }

            webviewLogic.setContentURLs(view.getUrl(), content);

            setProgressBarIndeterminateVisibility(false);
            progressBarRunning = false;
            //Log.d("WebViewC.onPageFinished", "setSupportProgressBarIndeterminateVisibility(false) Called");

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
        setContentView(R.layout.web_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Html.fromHtml(getString(R.string.app_name_html)));;

        
        sharedPref = getSharedPreferences("org.cnx.android",MODE_PRIVATE);
        setProgressBarIndeterminateVisibility(true);
        progressBarRunning = true;
        //Log.d("WebView.onCreate()", "Called");
        Intent intent = getIntent();
        content = (Content)intent.getSerializableExtra(getString(R.string.webcontent));
        Log.d("url",content.getUrl());

        if(!content.getUrl().contains("?bookmark=1") && !content.getUrl().contains("/search"))
        {

            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.cnx_package), MODE_PRIVATE);
            String bookURL = webviewLogic.getBookURL(content.getUrl());
            String url = sharedPref.getString(bookURL, "");

            if(url.equals(""))
            {
                url = content.getUrl();

            }
            content.setUrl(webviewLogic.convertURL(url));

        }
        else
        {
            //remove bookmark parameter
            String newURL = content.getUrl().replace("?bookmark=1","");
            content.setUrl(webviewLogic.convertURL(newURL));

        }

        if(CNXUtil.isConnected(this))
        {
            setUpViews();
            
        }
        else
        {
            webView = (WebView)findViewById(R.id.web_view);
            CNXUtil.makeNoDataToast(this);
        }

        String pref = sharedPref.getString("cacheCleared", "");
        if(pref.equals(""))
        {
            webView.clearCache(true);
            SharedPreferences.Editor ed = sharedPref.edit();
            ed.putString("cacheCleared", "true");
            ed.apply();
        }
        setNavDrawer();
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
        //Log.d("WebViewActivity","url: " + content.getUrl().toString());

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
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(mainIntent);
            return true;
        }
    	else
    	{
            content.setBookTitle(webviewLogic.getBookTitle(webView.getTitle()));
            int first = webView.getTitle().indexOf(" - ");
            int second = webView.getTitle().indexOf(" - ", first + 3);
            if(second > -1)
            {
                content.setTitle(webView.getTitle().replace(" - " + content.getBookTitle() + " - OpenStax CNX", ""));
            }
            else
            {
                content.setTitle(webView.getTitle().replace(" - OpenStax CNX", ""));
            }
            webviewLogic.setContentURLs(webView.getUrl(), content);
            //Log.d("optionSelected","url - " + content.getBookURL());


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
        if(!content.getUrl().toString().contains("/search"))
        {
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.cnx_package), MODE_PRIVATE);
            WebviewLogic wl = new WebviewLogic();
            String bookURL = wl.getBookURL(content.getUrl().toString());
            //Log.d("onResume", "BookURL - " + bookURL);
            String url = sharedPref.getString(bookURL, "");
            //Log.d("WebViewActivity.onResume()","URL retrieved: " + url);
            if(!url.equals(""))
            {
                url = webviewLogic.convertURL(url);
                content.setUrl(url);

            }
        }

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if(!(content.getIcon().equals("")))
        {
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.cnx_package), MODE_PRIVATE);
            SharedPreferences.Editor ed = sharedPref.edit();
            WebviewLogic wl = new WebviewLogic();
            String bookURL = wl.getBookURL(content.getUrl());
            //Log.d("onPause", "BookURL - " + bookURL);
            //Log.d("WVA.onPause()","URL saved: " + content.getUrl().toString());
            String url;
            if(webView.getUrl() != null)
            {
                url = webView.getUrl().replace("?bookmark=1", "");
            }
            else
            {
                url = content.getUrl();
            }
            ed.putString(bookURL, url);
            ed.apply();
        }
    }

    
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        //Log.d("ViewLenses.onSaveInstanceState()", "saving data");
        outState.putSerializable(getString(R.string.webcontent),content);
        if(!content.getIcon().equals(""))
        {
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.cnx_package), MODE_PRIVATE);
            SharedPreferences.Editor ed = sharedPref.edit();
            WebviewLogic wl = new WebviewLogic();
            String bookURL = wl.getBookURL(content.getUrl());
            //Log.d("SIS", "BookURL - " + bookURL);
            if(webView != null && webView.getUrl() != null)
            {
                String url = webView.getUrl().replace("?bookmark=1", "");
                ed.putString(bookURL, url);
                ed.apply();
            }
        }
        
    }
    
    /** sets properties on WebView and loads selected content into browser. */
    private void setUpViews() 
    {
        if(content == null || content.getUrl() == null)
        {
            return;
        }
        
        //Log.d("WebViewView.setupViews()", "Called");
        webView = (WebView)findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDefaultFontSize(17);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        webView.setWebChromeClient(new WebChromeClient() 
        {

        });
        
        webView.setWebViewClient(webViewClient);
        webView.loadUrl(content.getUrl());
    }
}
