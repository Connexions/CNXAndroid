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
import org.cnx.android.utils.CNXUtil;
import org.cnx.android.utils.Constants;
import org.cnx.android.utils.ContentCache;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle; 
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Activity to view selected lens content in a web browser.  
 * 
 * @author Ed Woodward
 *
 */
public class WebViewActivity extends SherlockActivity
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
        ActionBar aBar = this.getSupportActionBar();
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
            webView = (WebView)findViewById(R.id.web_view);
            CNXUtil.makeNoDataToast(this);
        }
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     * Creates option menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        MenuInflater inflater = getSupportMenuInflater();
        if(content == null)
        {
            return false;
        }
        if(content.getUrl().toString().indexOf(getString(R.string.help_page)) == -1 && content.getUrl().toString().indexOf(getString(R.string.search)) == -1 && content.getUrl().toString().indexOf(getString(R.string.google)) == -1)
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
            MenuItem menuItem = menu.findItem(R.id.add_to_favs);
            if(content.getUrl().toString().indexOf(getString(R.string.help_page)) != -1)
            {
                
                menuItem.setVisible(false);
            }
            else
            {
                menuItem.setVisible(true);
            }
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
        if(webView != null)
        {
            if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) 
            {
                webView.goBack();
                return true;
            }
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
        //Log.d("WebViewActivity.onResume()", "Called");
        Content c = (Content)ContentCache.getObject(getString(R.string.webcontent));
        if(c != null)
        {
            content = c;
            ContentCache.setObject("content", content);
        }

    }
    
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
        webView = (WebView)findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        
        //webView.getSettings().setUseWideViewPort(true);
        //webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setDefaultFontSize(17);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS); 
        
        showProgressDialog();
        webView.setWebChromeClient(new WebChromeClient() 
        {
            
//            public void onReceiveTitle(WebView view, String title)
//            {
//                super.onReceivedTitle(view, title);
//                //Log.d("LensWebView.onCreate()", "Called");
//            }
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
            return newURL.toString();
        }
        else
        {
            return url;
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
    
    /**
     * Displays Loading... and spinning wheel while page loads
     */
    private void showProgressDialog()
    {
        progressBar = ProgressDialog.show(this, null, getString(R.string.loading_web_title), true);
    }
    
}
