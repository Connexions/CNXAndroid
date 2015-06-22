/**
 * Copyright (c) 2010 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details. 
 */
package org.cnx.android.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.cnx.android.R;
import org.cnx.android.beans.Content;
import org.cnx.android.beans.Feed;
import org.cnx.android.utils.Constants;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri.Builder;
import android.util.Log;

/** 
 * Reads RSS feed and parses the XML.
 * Places Text from XML into Content objects 
 * Based on code from NewsDroid (http://www.helloandroid.com/tutorials/newsdroid-rss-reader)
 */
public class AtomHandler extends DefaultHandler
{
    public static List<String> BAD_STRINGS = Arrays.asList("", "\n",null);
    /** set to true when in an item element */
   //private boolean inItem = false;
    /** set to true when in the title element */
    private boolean inTitle = false;
    
    private boolean inListItem = false;
    
    private boolean inTags = false;
    /**
     * Cons tant for class name
     */
    private static String HANDLER = "AtomHandler";

    // Feed and Article objects to use for temporary storage
    /** Current Content object */
    private Content currentContent;
    /** Feed object */

    // A flag to know if looking for Articles or Feed name
    //private int targetFlag;

    /** List for storing Content objects from RSS feed */
    private ArrayList<Content> contentList = new ArrayList<Content>();

    /** Required method for Sax parser */
    public void startElement(String uri, String name, String qName, Attributes atts) 
    {
        //Log.d("LensViewer","AtomHandler.startElement() called. name = " + name);
        if(name.trim().equals("entry"))
        {
            currentContent = new Content();
        }
        if (name.trim().equals("title"))
        {
            inTitle = true;
        }
        else if (name.trim().equals("link"))
        {
            try
            {
                String href = atts.getValue("href");
                if(href.endsWith("latest/"))
                {
                    currentContent.url = new URL(Constants.MOBILE_CNX_URL + href);
                }
                else if(href.endsWith("/"))
                {
                    currentContent.url = new URL(href + "atom");
                }
                else
                {
                    currentContent.url = new URL(href + "/atom");
                }
                //Log.d("LensViewer","AtomHandler.startElement() added link");
            }
            catch(MalformedURLException mue)
            {
                Log.e("AtomHandler", mue.toString());
            }
        }
        else if (name.trim().equals("img"))
        {
            currentContent.icon = atts.getValue("src");
            if(currentContent.icon != null || !currentContent.icon.equals(""))
            {
                
                Builder uriBuilder = new Builder();
                uriBuilder.scheme("http");
                uriBuilder.path(currentContent.icon.substring(5));
                //Log.d("LensListAdapter" ,"uri:" + uri.toString());
                InputStream is;
                try
                {
                    is = (InputStream) this.fetch(uriBuilder.build().toString());
                    //if InputStream is null, just return
                    if(is == null)
                    {
                        return;
                    }
                    Drawable d = Drawable.createFromStream(is, "src");
                    is.close();
                    currentContent.setIconImage(d); 
                    //Log.d("LensesAdapter.getView()", "calling async task");
                }
                catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            //Log.d("LensViewer","AtomHandler.startElement() added icon src");
        }
        else if (name.trim().equals("id"))
        {
            removeTrailingComma();
            inTags = false;
        }
    }

    /** Required method for Sax parser */
    public void endElement(String uri, String name, String qName) throws SAXException 
    {
        inTitle = false;
        
        if(currentContent != null && currentContent.url != null && currentContent.title != null && !contentList.contains(currentContent))
        {
            if(currentContent.getIconImage() == null)
            {
                setIcon();
            }
            //Log.d("LensViewer","AtomHandler.endElement() called. content added to list ");
            contentList.add(currentContent);
        }

    }

    /** Required method for Sax parser */
    public void characters(char ch[], int start, int length) 
    {

        String chars = (new String(ch).substring(start, start + length));
        //Log.d("LensViewer","AtomHandler.characters() called. chars = " + chars);
        
        if (inTitle && currentContent != null)
        {
            //Log.d("LensViewer","AtomHandler.characters() called. add title = " + chars);
            if(currentContent.title == null || currentContent.title.equals("")  )
            {
                currentContent.title = chars;
            }
            else
            {
                currentContent.title = currentContent.title + chars;
            }
        }
        else if(inListItem && currentContent != null && !chars.equals("\n") )
        {
            currentContent.setContentString(chars.trim() + " pages and/or books");
            //Log.d("AtomHandler.characters()","contentString = " + chars);
            inListItem = false;
        }
        else if(inTags && currentContent != null && !BAD_STRINGS.contains(chars.trim()) )
        {
            currentContent.setContentString(currentContent.getContentString() + chars + ", ");
            //Log.d("AtomHandler.characters()","contentString = " + chars);
        }
        else if(chars.equals("Content:"))
        {
            inListItem = true;
        }
        else if(chars.equals("Tags:"))
        {
            inTags = true;
        }

    }

    /** Parses RSS feed and returns List of Content objects 
     * @return ArrayList of Content objects
     */
    public ArrayList<Content> parseFeed(Context ctx, Feed feed) 
    {
        try 
        {
           // Log.d("LensViewer","AtomHandler.parseFeed() called");

            if(feed != null && feed.url != null)
            {
                SAXParserFactory spf = SAXParserFactory.newInstance();
                SAXParser sp = spf.newSAXParser();
                XMLReader xr = sp.getXMLReader();
                xr.setContentHandler(this);
                xr.parse(new InputSource(feed.url.openStream()));
            }
            
        } 
        catch (IOException e) 
        {
            Log.e(HANDLER, e.toString(),e);
        } 
        catch (SAXException e) 
        {
            Log.e(HANDLER, e.toString(),e);
        } 
        catch (ParserConfigurationException e) 
        {
            Log.e(HANDLER, e.toString(),e);
        } 
        return contentList;
    }
    
    /* (non-Javadoc)
     * @see org.cnx.android.tasks.LoadImageAsyncTask.LoadImageAsyncTaskResponder#imageLoading()
     */
    public void imageLoading() 
    {
        //do nothing
    }

      /* (non-Javadoc)
     * @see org.cnx.android.tasks.LoadImageAsyncTask.LoadImageAsyncTaskResponder#imageLoadCancelled()
     */
    public void imageLoadCancelled() 
      {
        // do nothing
      }

      /* (non-Javadoc)
     * @see org.cnx.android.tasks.LoadImageAsyncTask.LoadImageAsyncTaskResponder#imageLoaded(android.graphics.drawable.Drawable)
     */
    public Drawable imageLoaded(Drawable drawable) 
   {
        //Log.d("LensListAdapter.imageLoaded()", "setting image in view");
        currentContent.setIconImage(drawable);
        return drawable;
   }
    
    /**
     * removes trailing comma from the list of keywords
     */
    private void removeTrailingComma()
    {
        if(currentContent != null)
        {
            String other = currentContent.getContentString().trim();
            int length = other.length();
            if(length > 1)
            {
                currentContent.setContentString(other.substring(0, length - 1));
            }
        }
    }
    
    /**
     * Sets icon based on URL
     */
    private void setIcon()
    {
        //Log.d("AtomHandler.setIcon()","called");
        String url = currentContent.url.toString();
        if(url.indexOf("lenses") > -1)
        {
            currentContent.setIconDrawable(R.drawable.lenses);
        }
        else if(url.indexOf("content/m") > -1)
        {
            currentContent.setIconDrawable(R.drawable.modules);
        }
        else if(url.indexOf("content/col") > -1)
        {
            currentContent.setIconDrawable(R.drawable.collections);
        }
        else if(url.indexOf("google.com") > -1 || url.indexOf("http://cnx.org/content/search") > -1)
        {
            currentContent.setIconDrawable(R.drawable.search_selected);
        }
        else
        {
            currentContent.setIconDrawable(R.drawable.lenses);
        }
    }
    
    /** Fetches contents of URL 
     * 
     * @returns Object.  If there is an error, null is returned.
     * 
     */
    private Object fetch(String address)
    {
        Object content = null;
    
        try
        {
            URL url = new URL(address);
            content = url.getContent();
        }
        catch(Exception e)
        {
            Log.d("AtomHandler", "Error: " + e.toString(), e);
        }
        return content;
    }

}
