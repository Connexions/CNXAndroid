/**
 * Copyright (c) 2011 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.handlers;

import java.net.MalformedURLException;
import java.net.URL;

import org.cnx.android.R;
import org.cnx.android.activity.WebViewActivity;
import org.cnx.android.beans.Content;
import org.cnx.android.utils.Constants;
import org.cnx.android.utils.ContentCache;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;

/**
 * Opens popup window for entering search criteria.
 * @author Ed Woodward
 *
 */
public class SearchHandler
{
    PopupWindow popUp;
    
    /**
     * Displays Popup window for search
     * @param context Context - the current Context
     */
    public void displayPopup(final Context context)
    {
        popUp = new PopupWindow(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
        View layout = inflater.inflate(R.layout.search_popup, null, true);
        popUp = new PopupWindow(layout,  300,  125,    true); 
        popUp.setBackgroundDrawable(new BitmapDrawable());
        popUp.setOutsideTouchable(true);
        popUp.setAnimationStyle(R.style.Animations_GrowFromBottom);
        popUp.setTouchInterceptor(new OnTouchListener() {
            
            public boolean onTouch(View v, MotionEvent event) 
            {
                if(event.getAction() == MotionEvent.ACTION_OUTSIDE) 
                {
                    popUp.dismiss();
                    return true;
                }
                return false;
            }
        });
        final EditText searchCriteria = (EditText)layout.findViewById(R.id.searchCriteria);
        searchCriteria.setOnKeyListener(new View.OnKeyListener()
        {
            
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if(event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER)
                    {
                        //EditText searchFor = (EditText)findViewById(R.id.searchText);
                        performSearch(searchCriteria.getText().toString(),Constants.CNX_SEARCH, context);
                    }
                }
                return false;
            }
        });
        ImageButton searchButton = (ImageButton)layout.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new OnClickListener() 
        {
                  
                  public void onClick(View v) 
                  {
                      String searchFor = searchCriteria.getText().toString();
                      performSearch(searchFor, Constants.CNX_SEARCH, context);
                  }
              });
        popUp.showAtLocation(layout, Gravity.TOP, 0, 30); 
    }
    
    /**
     * Opens web view with correct URL
     * @param searchFor- String the criteria to search for
     * @param searchType - one of 2 constants: CNX_SEARCH or GOOGLE_SEARCH
     */
    public void performSearch(String searchFor, int searchType, Context context)
    {
        try
        {
            Content content = new Content();
            content.setUrl(new URL(createQueryString(searchFor, searchType))); 
            content.setTitle(context.getString(R.string.search_title) + searchFor);
            Intent webintent = new Intent(context, WebViewActivity.class);
            ContentCache.setObject(context.getString(R.string.webcontent), content);
            context.startActivity(webintent);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        
    }
    
    /**
     * creates search string based on search type
     * @param searchFor - the criteria to search for
     * @param searchType - one of 2 constants: CNX_SEARCH or GOOGLE_SEARCH
     * @return String - the URL for the Google search
     */
    private String createQueryString(String searchFor, int searchType)
    {
        StringBuilder sb = new StringBuilder();
        searchFor.replaceAll(" ", "+");
        if(searchType == Constants.GOOGLE_SEARCH)
        {
            sb.append("http://www.google.com/search?hl=en&q=site%3Acnx.org+");
            sb.append(searchFor);
        }
        else if(searchType == Constants.CNX_SEARCH)
        {
            sb.append("http://m.cnx.org/content/search?words=");
            sb.append(searchFor);
            sb.append("&allterms=weakAND&search=Search&subject=");
        }
        return sb.toString();
    }

}
