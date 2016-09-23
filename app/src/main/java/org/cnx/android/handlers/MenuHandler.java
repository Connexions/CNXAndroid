/**
 * Copyright (c) 2010 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.handlers;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;
import org.cnx.android.R;
import org.cnx.android.activity.FileBrowserActivity;
import org.cnx.android.activity.LandingActivity;
import org.cnx.android.activity.NoteEditorActivity;
import org.cnx.android.activity.ViewFavsActivity;
import org.cnx.android.activity.WebViewActivity;
import org.cnx.android.beans.Content;
import org.cnx.android.providers.Favs;
import org.cnx.android.utils.Constants;
import org.cnx.android.utils.MenuUtil;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.net.URL;

/**
 * Handler for context and other menus
 * 
 * @author Ed Woodward
 *
 */
public class MenuHandler
{
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

                //Log.d("MenuHandler","title - " + currentContent.getTitle())  ;
                cv.put(Favs.TITLE, currentContent.getTitle());
                //Log.d("MnHndlr.handleCont...()","URL: " + currentContent.getUrl().toString());
                String url = currentContent.getUrl().toString();
                if(isSearch(url, context))
                {
                    return false;
                }
                cv.put(Favs.URL, url.replaceAll("@\\d+(\\.\\d+)?","")+ "?bookmark=1");
                cv.put(Favs.ICON, currentContent.getIcon());
                context.getContentResolver().insert(Favs.CONTENT_URI, cv);
                Toast.makeText(context, "Bookmark added for " + currentContent.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.go_to_favs:
                Intent intent = new Intent(context, ViewFavsActivity.class);
                context.startActivity(intent);
                return true;
            case R.id.search:
//
                handleSearch(context);
                return true;

//            case R.id.viewFile:
//                Intent viewIntent = new Intent(context, FileBrowserActivity.class);
//                context.startActivity(viewIntent);
//                return true;
            case R.id.home:
                Intent homeIntent = new Intent(context, LandingActivity.class);
                context.startActivity(homeIntent);
                return true;
            case R.id.notes:
                if(isSearch(currentContent.getUrl().toString(), context))
                {
                    return false;
                }
                //ContentCache.setObject("content", currentContent);
                Intent noteIntent = new Intent(context, NoteEditorActivity.class);
                noteIntent.putExtra(context.getString(R.string.content), currentContent);
                context.startActivity(noteIntent);
                return true;
            case R.id.viewLicense:
                displayLicensesAlert(context);
                return true;
            case R.id.share:
                Intent shareintent = new Intent(Intent.ACTION_SEND);
                shareintent.setType(context.getString(R.string.mimetype_text));

                if(currentContent != null)
                {
                    shareintent.putExtra(Intent.EXTRA_SUBJECT, currentContent.getBookTitle() + " : " + currentContent.getTitle());
                    shareintent.putExtra(Intent.EXTRA_TEXT, currentContent.getUrl().toString() + "\n\n " + context.getString(R.string.shared_via));

                    Intent chooser = Intent.createChooser(shareintent, context.getString(R.string.tell_friend) + " "+ currentContent.getBookTitle());
                    context.startActivity(chooser);
                }
                else
                {
                    Toast.makeText(context, context.getString(R.string.no_data_msg),  Toast.LENGTH_LONG).show();
                }
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
            message = "PDF files are saved in an OpenStaxCNX folder on the SDCard or on the device's internal memory.  Press OK to continue.";
        }
        else
        {
            message = "EPUB files are saved in an OpenStaxCNX folder on the SDCard or on the device's internal memory.  Press OK to continue.";
        }
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Download");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok), new DialogInterface.OnClickListener()
        {
              public void onClick(DialogInterface dialog, int which) 
              {
                  String url = currentContent.getUrl().toString();

            	  DownloadManager dm = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
            	  if(type.equals(Constants.PDF_TYPE))
            	  {
            		  Uri uri = Uri.parse(MenuUtil.fixPdfURL(currentContent.getUrl().toString(), MenuUtil.getContentType(url)));
            		  DownloadManager.Request request = new Request(uri);
            		  request.setDestinationInExternalPublicDir("/" + context.getString(R.string.folder_name), MenuUtil.getTitle(currentContent.getTitle()) + Constants.PDF_EXTENSION);
            		  request.setTitle(currentContent.getTitle() + Constants.PDF_EXTENSION);
            		  dm.enqueue(request);
            	  }
            	  else
            	  {
            		  Uri uri = Uri.parse(MenuUtil.fixEpubURL(currentContent.getUrl().toString(), MenuUtil.getContentType(url)));
            		  DownloadManager.Request request = new Request(uri);
            		  request.setDestinationInExternalPublicDir("/" + context.getString(R.string.folder_name), MenuUtil.getTitle(currentContent.getTitle()) + Constants.EPUB_EXTENSION);
            		  request.setTitle(currentContent.getTitle() + Constants.EPUB_EXTENSION);
            		  dm.enqueue(request);
            	  }
                	  

            } }); 
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.cancel), new DialogInterface.OnClickListener()
        {
              public void onClick(DialogInterface dialog, int which) 
              {
                  //do nothing
         
            } }); 
        alertDialog.show();
    }

    public void handleSearch(Context context)
    {
        try
        {
            Intent iweb = new Intent(context, WebViewActivity.class);
            Content currentContent = new Content();
            currentContent.setBookUrl("https://cnx.org/search?minimal=true");
            currentContent.setUrl("https://cnx.org/search?minimal=true");
            currentContent.setBookTitle("Search");
            currentContent.setIcon("");
            iweb.putExtra(context.getString(R.string.webcontent), currentContent);
            context.startActivity(iweb);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void displayLicensesAlert(Context context)
    {
        WebView view = (WebView) LayoutInflater.from(context).inflate(R.layout.license_dialog, null);
        view.loadUrl("file:///android_asset/licenses.html");
        AlertDialog alertDialog = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog)
                .setTitle(context.getString(R.string.license_title))
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private boolean isSearch(String url, Context context)
    {
        if(url.contains("/search"))
        {
            Toast.makeText(context, "This feature is not available for searches",  Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

}
