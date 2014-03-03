/**
 * Copyright (c) 2010 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.handlers;

import java.net.MalformedURLException;
import java.net.URL;

import android.view.MenuItem;
import org.cnx.android.R;
import org.cnx.android.activity.FileBrowserActivity;
import org.cnx.android.activity.LandingActivity;
import org.cnx.android.activity.NoteEditorActivity;
import org.cnx.android.activity.ViewFavsActivity;
import org.cnx.android.activity.WebViewActivity;
import org.cnx.android.beans.Content;
import org.cnx.android.providers.Favs;
import org.cnx.android.service.DownloadService;
import org.cnx.android.utils.Constants;
import org.cnx.android.utils.ContentCache;
import org.cnx.android.utils.MenuUtil;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

/**
 * Handler for context and other menus
 * 
 * @author Ed Woodward
 *
 */
public class MenuHandler
{
//    public boolean handleContextMenu(android.view.MenuItem item, Context context, Content currentContent)
//    {
//        return handleContextMenu(item.getItemId(), context, currentContent);
//    }
    
    public boolean handleContextMenu(MenuItem item, Context context, Content currentContent)
    {
        return handleContextMenu(item.getItemId(), context, currentContent);
    }
    /**
     * Handles selected menu item actions
     * @param item MenuItem - the selected menu item
     * @param context - Context the current context
     * @param currentContent Content current content object
     * @return true if menu item handled otherwise false
     */
    public boolean handleContextMenu(int item, Context context, Content currentContent)
    {
        switch (item) 
        {
            case R.id.add_to_favs:
                ContentValues cv = new ContentValues();
                if(currentContent.getUrl().toString().indexOf("http://mobile.cnx.org/content/search") > -1 || currentContent.getUrl().toString().indexOf("http://m.cnx.org/content/search") > -1)
                {
                    String title = MenuUtil.getSearchTitle(currentContent.getUrl().toString());
                    cv.put(Favs.TITLE, title);
                }
                else
                {
                    cv.put(Favs.TITLE, currentContent.getTitle());
                }
                cv.put(Favs.URL, currentContent.getUrl().toString());
                cv.put(Favs.ICON, currentContent.getIcon());
                cv.put(Favs.OTHER, currentContent.getContentString());
                context.getContentResolver().insert(Favs.CONTENT_URI, cv);
                return true;
            case R.id.go_to_favs:
                Intent intent = new Intent(context, ViewFavsActivity.class);
                context.startActivity(intent);
                return true;
            case R.id.help:
                try
                {
                    Content content = new Content();
                    content.setUrl(new URL(Constants.HELP_FILE_URL)); 
                    ContentCache.setObject(context.getString(R.string.webcontent), content);
                    context.startActivity(new Intent(context, WebViewActivity.class));
                    
                }
                catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }
                return true;
            case R.id.delete_from__favs:
                context.getContentResolver().delete(Favs.CONTENT_URI, "_id="+ currentContent.getId(), null);
                return true;
            case R.id.search:
                SearchHandler sh = new SearchHandler();
                sh.displayPopup(context);
                return true;
            case R.id.refresh:
                return true;
            case R.id.viewFile:
                Intent viewIntent = new Intent(context, FileBrowserActivity.class);
                context.startActivity(viewIntent);
                return true;
            case R.id.home:
                Intent homeIntent = new Intent(context, LandingActivity.class);
                context.startActivity(homeIntent);
                return true;
            case R.id.note:
                ContentCache.setObject("content", currentContent);
                Intent noteIntent = new Intent(context, NoteEditorActivity.class);
                context.startActivity(noteIntent);
                return true;
            case R.id.menu_save:
                return true;
            case R.id.rate:
            	context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=org.cnx.android")));
                return true;
            default:
                return false;
        }
    }
    
    
    /**
     * Displays alert telling user where the downloaded files are located, the size of the files to download and confirms download.
     * If download is confirmed, DownloadHandler is called.
     * @param context - Context - the current context
     * @param currentContent - Content - current content object
     * @param type - the type of download: pdf or epub
     */
    public void displayAlert(final Context context, final Content currentContent, final String type)
    {
        if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
        {
            MenuUtil.showMissingMediaDialog(context);
            return;
        }

        
        String message = "";
        if(type.equals(Constants.PDF_TYPE))
        {
            message = "PDF files are saved in a Connexions folder on the SDCard or on the device's internal memory.  Press OK to continue.";
        }
        else
        {
            message = "EPUB files are saved in a Connexions folder on the SDCard or on the device's internal memory.  Press OK to continue.";
        }
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Download");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, Constants.OK, new DialogInterface.OnClickListener() 
        {
              public void onClick(DialogInterface dialog, int which) 
              {
                  String url = currentContent.getUrl().toString();
                  //Intent intent = new Intent(context, DownloadService.class);
                  
            	  DownloadManager dm = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
            	  if(type.equals(Constants.PDF_TYPE))
            	  {
            		  Uri uri = Uri.parse(MenuUtil.fixPdfURL(currentContent.getUrl().toString(), MenuUtil.getContentType(url)));
            		  DownloadManager.Request request = new Request(uri);
            		  request.setDestinationInExternalPublicDir("/Connexions", MenuUtil.getTitle(currentContent.getTitle()) + Constants.PDF_EXTENSION);
            		  request.setTitle(currentContent.getTitle() + Constants.PDF_EXTENSION);
            		  dm.enqueue(request);
            	  }
            	  else
            	  {
            		  Uri uri = Uri.parse(MenuUtil.fixEpubURL(currentContent.getUrl().toString(), MenuUtil.getContentType(url)));
            		  DownloadManager.Request request = new Request(uri);
            		  request.setDestinationInExternalPublicDir("/Connexions", MenuUtil.getTitle(currentContent.getTitle()) + Constants.EPUB_EXTENSION);
            		  request.setTitle(currentContent.getTitle() + Constants.EPUB_EXTENSION);
            		  dm.enqueue(request);
            	  }
                	  

            } }); 
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, Constants.CANCEL, new DialogInterface.OnClickListener() 
        {
              public void onClick(DialogInterface dialog, int which) 
              {
                  //do nothing
         
            } }); 
        alertDialog.show();
    }
}
