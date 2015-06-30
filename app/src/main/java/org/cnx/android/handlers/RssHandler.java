/**
 * Copyright (c) 2011 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details. 
 */
package org.cnx.android.handlers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

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
import android.util.Log;

/** 
 * Reads RSS feed and parses the XML.
 * Places Text from XML into Content objects 
 * Based on code from NewsDroid (http://www.helloandroid.com/tutorials/newsdroid-rss-reader)
 */
public class RssHandler extends DefaultHandler
{
    /** set to true when in the title element */
    private boolean inTitle = false;
    
    private boolean inLink = false;

    // Feed and Article objects to use for temporary storage
    /** Current Content object */
    private Content currentContent;

    /** List for storing Content objects from RSS feed */
    private ArrayList<Content> contentList = new ArrayList<Content>();
    
    private String link;

    /** Required method for Sax parser */
    public void startElement(String uri, String name, String qName, Attributes atts) 
    {
        //Log.d("RssHandler.start()","name: " + name + " qName: " + qName);
        if(name.trim().equals("item"))
        {
            currentContent = new Content();
        }
        if (name.trim().equals("title"))
        {
            inTitle = true;
        }
        else if (name.trim().equals("link"))
        {
            inLink = true;
        }
    }

    /** Required method for Sax parser */
    public void endElement(String uri, String name, String qName) throws SAXException 
    {
        if(inLink && currentContent != null)
        {
            try
            {
                currentContent.setUrl(new URL(Constants.MOBILE_CNX_URL + link.substring(14)));
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
        }
        inTitle = false;
        inLink = false;
        link = "";
        
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
        //Log.d("RssHandler.characters()","chars: " + chars);
        
        if (inTitle && currentContent != null)
        {
            if(currentContent.title == null || currentContent.title.equals("")  )
            {
                currentContent.title = chars;
            }
            else
            {
                currentContent.title = currentContent.title + chars;
            }
            //Log.d("LensViewer","AtomHandler.characters() called. add title = " + chars);
        }
        else if(inLink && currentContent != null)
        {
            if(link == null || link.equals(""))
            {
                link = chars;
            }
            else
            {
                link = link + chars;
            }
            
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

            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            xr.setContentHandler(this);
            xr.parse(new InputSource(feed.url.openStream()));
            
        } 
        catch (IOException e) 
        {
            Log.e("RssHandler", e.toString(),e);
        } 
        catch (SAXException e) 
        {
            Log.e("RssHandler", e.toString(),e);
        } 
        catch (ParserConfigurationException e) 
        {
            Log.e("RssHandler", e.toString(),e);
        } 
        return contentList;
    }
    
    /**
     * Sets icon based on URL
     */
    private void setIcon()
    {
        //Log.d("AtomHandler.setIcon()","called");
        String url = currentContent.url.toString();
        if(url.contains("lenses"))
        {
            currentContent.setIconDrawable(R.drawable.lenses);
        }
        else if(url.contains("content/m"))
        {
            currentContent.setIconDrawable(R.drawable.modules);
        }
        else if(url.contains("content/col"))
        {
            currentContent.setIconDrawable(R.drawable.collections);
        }
        else if(url.contains("google.com") || url.contains("http://cnx.org/content/search"))
        {
            currentContent.setIconDrawable(R.drawable.search_selected);
        }
        else
        {
            currentContent.setIconDrawable(R.drawable.lenses);
        }
    }
    
}
