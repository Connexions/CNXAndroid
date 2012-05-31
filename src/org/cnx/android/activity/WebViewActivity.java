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
import org.cnx.android.utils.Constants;
import org.cnx.android.utils.ContentCache;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle; 
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

/**
 * Activity to view selected lens content in a web browser.  
 * 
 * @author Ed Woodward
 *
 */
public class WebViewActivity extends Activity
{
    /** Web browser view for Activity */
    private WebView webView;
    /** Variable for serialized Content object */
    private Content content;
    /** Constant for serialized object passed to Activity */
    public static final String WEB_MENU = "web";
    public static final String HELP_MENU = "help";
    
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
            //showProgressDialog();
            //Log.d("WebViewClient.onLoadResource()", "Called");
        }
        
        /** loads URL into view */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) 
        {
            view.loadUrl(fixURL(url));
            showProgressDialog();
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
                setLayout(url);
                if (progressBar.isShowing()) 
                {
                    progressBar.dismiss();
                }
            }
            catch (MalformedURLException e)
            {
                Log.d("WebViewActivity.onPageFinished()", "Error: " + e.toString(),e);
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
        content = (Content)ContentCache.getObject(getString(R.string.webcontent));
        setContentView(R.layout.new_web_view);
        if(content != null && content.getUrl() != null)
        {
            setLayout(content.getUrl().toString());
        }
        else
        {
            setLayout("http://m.cnx.org");
        }
            
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
        if(content.getUrl().toString().indexOf("Connexions_Android_App_Help.html") == -1 && content.getUrl().toString().indexOf("/search") == -1 && content.getUrl().toString().indexOf("google.com") == -1)
        {
            //if the web menu is already being used, don't recreate it
            if(!previousMenu.equals(WEB_MENU))
            {
                menu.clear();
                inflater.inflate(R.menu.web_options_menu, menu);
                previousMenu = WEB_MENU;
            }
        }
        else 
        {
            //no need to check for help menu since there is only one path to it.
            menu.clear();
            inflater.inflate(R.menu.help_options_menu, menu);
            previousMenu = HELP_MENU;
        }
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
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) 
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
        Log.d("WebViewActivity.onResume()", "Called");
        Content c = (Content)ContentCache.getObject(getString(R.string.webcontent));
        if(c != null)
        {
            content = c;
            ContentCache.setObject("content", content);
        }
        //setButton();

    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
//    @Override
//    protected void onPause()
//    {
//        super.onPause();
//        Log.d("WebViewActivity.onPause()", "Called");
//        ContentCache.setObject(getString(R.string.webcontent), content);
//    }
    
    /** sets properties on WebView and loads selected content into browser. */
    private void setUpViews() 
    {
        if(content == null || content.url == null)
        {
            return;
        }
        Log.d("WebViewView.setupViews()", "Called");
        webView = (WebView)findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setDefaultFontSize(17);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        //webView.setInitialScale(80);
        webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS); 
        //final Activity activity = this;
        //progressBar = new ProgressDialog(this);
        //progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //progressBar.setIndeterminate(true);
        //progressBar = ProgressDialog.show(this, null, "Loading...", true);
        showProgressDialog();
        webView.setWebChromeClient(new WebChromeClient() 
        {
//            public void onProgressChanged(WebView view, int progress) 
//            {
//              activity.setTitle(getString(R.string.loading_web_title));
//              activity.setProgress(progress * 100);
//              if(progress == 100)
//              {
//                  activity.setTitle(R.string.app_name);
//              }
//            }
            
            public void onReceiveTitle(WebView view, String title)
            {
                super.onReceivedTitle(view, title);
                //Log.d("LensWebView.onCreate()", "Called");
            }
        });
        
        webView.setWebViewClient(webViewClient);
        webView.loadUrl(fixURL(content.url.toString()));
        
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
        int googIndex = url.indexOf("http://www.google.com/");
        int helpIndex = url.indexOf("Connexions_Android_App_Help.html");
        if(googIndex > -1 || helpIndex > -1)
        {
            return url;
        }
        int index = url.indexOf("http://cnx.org/");
        int startIndex = 14;
        if(index == -1)
        {
            index = url.indexOf("http://m.cnx.org/");
            startIndex = 16;
        }
        if(index > -1)
        {
            newURL.append(Constants.MOBILE_CNX_URL);
            newURL.append(url.substring(startIndex));
            return newURL.toString();
        }
        else
        {
            return url;
        }
    }
    
//    private void handleSearch()
//    {
//        SearchHandler sh = new SearchHandler();
//        sh.displayPopup(this);
//    }
    
    /**
     * Displays dialog to start file download
     * @param type one of 2 types PDF or EPUB
     */
    private void download(String type)
    {
        MenuHandler mh = new MenuHandler();
        mh.displayAlert(this, content, type);
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
        if(url.contains("/search") || url.contains(".html"))
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
                          intent.setType("text/plain");

                          intent.putExtra(Intent.EXTRA_SUBJECT, content.getTitle());
                          intent.putExtra(Intent.EXTRA_TEXT, content.getUrl().toString() + " " + getString(R.string.shared_via));

                          Intent chooser = Intent.createChooser(intent, getString(R.string.tell_friend) + " "+ content.getTitle());
                          startActivity(chooser);

                      }
                  });
                
                ImageButton epubButton = (ImageButton)findViewById(R.id.epubButton);
                epubButton.setOnClickListener(new OnClickListener() 
                {
                          
                      public void onClick(View v) 
                      {
                          download(Constants.EPUB_TYPE);

                      }
                  });
                
                ImageButton pdfButton = (ImageButton)findViewById(R.id.pdfButton);
                pdfButton.setOnClickListener(new OnClickListener() 
                {
                          
                      public void onClick(View v) 
                      {
                          download(Constants.PDF_TYPE);

                      }
                  });
            
        }
    }
    
    /**
     * Displays Loading... and spinning wheel while page loads
     */
    private void showProgressDialog()
    {
        progressBar = ProgressDialog.show(this, null, getString(R.string.loading_web_title), true);
    }
    
//    private boolean checkForNote()
//    {
//        Cursor cursor = getContentResolver().query(Notes.CONTENT_URI, null, "notes_url='" + content.getUrl().toString() + "'", null, null);
//        if(cursor.getCount()>0)
//        {
//            return true;
//        }
//        else
//        {
//            return false;
//        }
//    }
    
    
}
