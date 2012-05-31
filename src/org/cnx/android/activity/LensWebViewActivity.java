/**
 * Copyright (c) 2010 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.activity;

import java.net.MalformedURLException;
import java.net.URL;

import org.cnx.android.R;
import org.cnx.android.beans.Content;
import org.cnx.android.handlers.MenuHandler;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle; 
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.FrameLayout;

/**
 * Activity to view selected lens content in a web browser.  
 * 
 * @author Ed Woodward
 *
 */
public class LensWebViewActivity extends Activity
{
    /** Web browser view for Activity */
    private WebView webView;
    /** Variable for serialized Content object */
    private Content content;
    /** Constant for serialized object passed to Activity */
    public static final String CONTENT = "content";
    /** Parameter Constant for zoom controls */
    private static final FrameLayout.LayoutParams ZOOM_PARAMS =
        new FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.FILL_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        Gravity.BOTTOM);
    
    /** inner class for WebViewClient*/
    private WebViewClient webViewClient = new WebViewClient() 
    {
        @Override
        public void onLoadResource(WebView view, String url) 
        {
            super.onLoadResource(view, url);
            //Log.d("WebViewClient.onLoadResource()", "Called");
        }
        
        /** loads URL into view */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) 
        {
            view.loadUrl(url);
            return true;
        }
        
        /* (non-Javadoc)
         * @see android.webkit.WebViewClient#onPageFinished(android.webkit.WebView, java.lang.String)
         * Sets title and URL correctly after the page is fully loaded
         */
        @Override
        public void onPageFinished(WebView view, String url)
        {
            //Log.d("WebViewClient.onPageFinished", "title: " + view.getTitle());
            //Log.d("WebViewClient.onPageFinished", "url: " + url);
            
            content.setTitle(view.getTitle());
            try
            {
                content.setUrl(new URL(url));
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
        }

    };
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        //Log.d("LensWebView.onCreate()", "Called");
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        content = (Content)getIntent().getSerializableExtra(CONTENT);
        setContentView(R.layout.web_view);
        setUpViews();
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     * Creates option menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        MenuInflater inflater = getMenuInflater();
        if(content.getUrl().toString().indexOf("Connexions_Android_App_Help.html") == -1)
        {
            inflater.inflate(R.menu.web_options_menu, menu);
        }
        else
        {
            inflater.inflate(R.menu.help_options_menu, menu);
        }
        return true;
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     * Handles selected options menu item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        MenuHandler mh = new MenuHandler();
        boolean returnVal = mh.handleContextMenu(item, this, content);
        if(returnVal)
        {
            return returnVal;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     * Handles use of back button on browser 
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) 
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
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

    
    /** sets properties on WebView and loads selected content into browser. */
    private void setUpViews() 
    {
        //Log.d("LensWebView.setupViews()", "Called");
        webView = (WebView)findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        
        //add zoom controls to top level view
        FrameLayout contentView = (FrameLayout) getWindow().getDecorView().findViewById(android.R.id.content);
        final View zoom = webView.getZoomControls();
        contentView.addView(zoom, ZOOM_PARAMS); 
        
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setDefaultFontSize(20);
        webView.setInitialScale(80);
        webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS); 
        final Activity activity = this;
        webView.setWebChromeClient(new WebChromeClient() 
        {
            public void onProgressChanged(WebView view, int progress) 
            {
              activity.setTitle("Loading...");
              activity.setProgress(progress * 100);
              if(progress == 100)
              {
                  activity.setTitle(R.string.app_name);
              }
            }
            
            public void onReceiveTitle(WebView view, String title)
            {
                super.onReceivedTitle(view, title);
                //Log.d("LensWebView.onCreate()", "Called");
            }
        });
        
        webView.setWebViewClient(webViewClient);
        webView.loadUrl(content.url.toString());
        
     }
    
    
}
