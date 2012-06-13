/**
 * Copyright (c) 2011 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details. 
 */
package org.cnx.android.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.cnx.android.R;
import org.cnx.android.adapters.FileListAdapter;
import org.cnx.android.beans.DownloadedFile;
import org.cnx.android.utils.Constants;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
 * File browser to select downloaded file and open it.
 * Had help from http://www.anddev.org/viewtopic.php?t=67
 * @author Ed Woodward
 *
 */
public class FileBrowserActivity extends ListActivity
{
    /**
     * List of DownloadedFile objects that represent files in /Connexions directory
     */
    private List<DownloadedFile> directoryEntries = new ArrayList<DownloadedFile>();
    /**
     * The /Connexions directory as a file object
     */
    private File currentDirectory = new File(Environment.getExternalStorageDirectory(), getString(R.string.cnx_folder));
    /**
     * List Adapter for display
     */
    FileListAdapter fileListAdapter;
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.list_view);
        registerForContextMenu(getListView());
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.view_favs_title);
        TextView aTextView=(TextView)findViewById(R.id.lensNameInTitle);
        
        aTextView.setText(getString(R.string.file_browser_title));
        readFileList();
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) 
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        DownloadedFile df = (DownloadedFile)getListView().getItemAtPosition(info.position);
        menu.setHeaderTitle(df.getDisplayPath());
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.file_context_menu, menu);
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) 
    {
        AdapterContextMenuInfo info= (AdapterContextMenuInfo) item.getMenuInfo();
        DownloadedFile content = (DownloadedFile)getListView().getItemAtPosition(info.position);
        boolean returnVal = handleDeleteFile(content);
        return returnVal;
    }

    /**
     * checks if Connexions directory exists and then passes control to handleFile()
     */
    public void readFileList()
    {
        currentDirectory = new File(Environment.getExternalStorageDirectory(), getString(R.string.cnx_folder));
        if(currentDirectory.exists())
        {
            handleFile(currentDirectory);
        }
        
    }
    
    /**
     * if directory is passed, then read files in directory.  If a file is selected, then display alert to user
     * @param File dirOrFile - the directory or file to handle
     */
    private void handleFile(final File dirOrFile)
    {
        //Log.d("FileBrowserActivity.browseTo()", "Called");
        if (dirOrFile.isDirectory() && !dirOrFile.getPath().endsWith(getString(R.string.cnx_folder)))
        {
            this.currentDirectory = dirOrFile;
            loadList(dirOrFile.listFiles());
        }
        else
        {
                
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(getString(R.string.file_dialog_title));
            alertDialog.setMessage("Open file " + dirOrFile.getName() + "?");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() 
            {
                  public void onClick(DialogInterface dialog, int which) 
                  {
                      openFile(dirOrFile);
             
                } }); 
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() 
            {
                  public void onClick(DialogInterface dialog, int which) 
                  {
                      //do nothing
             
                } }); 
            alertDialog.show();
               
        }
    }
    
    /**
     * Loops through Array of Files and creates DownloadedFile objects for each and adds them to a list
     * Adds list to ArrayAdapter and sets the list adapter
     * @param files - Array of File objects to process
     */
    private void loadList(File[] files) 
    {
        //Log.d("FileBrowserActivity.fill()", "Called");
        directoryEntries.clear();
        
        int pathLength = currentDirectory.getAbsolutePath().length();
        for (File file : files)
        {
            DownloadedFile df = new DownloadedFile();
            df.setDisplayPath(file.getAbsolutePath().substring(pathLength+ 1));
            df.setFullPath(file.getAbsolutePath());
            directoryEntries.add(df);
        }
        Collections.sort((List<DownloadedFile>)directoryEntries);
        fileListAdapter = new FileListAdapter(this, directoryEntries);
        
        setListAdapter(fileListAdapter);
    }
    
    
    
    /* (non-Javadoc)
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) 
    {
        int selectedItem = position;
        DownloadedFile df = this.directoryEntries.get(position);
        String selectedFileString = df.getDisplayPath();
        if (selectedFileString.equals(".")) 
        {
            // Refresh
            handleFile(this.currentDirectory);
        } 
        else 
        {
            //Log.d("FileBrowserActivity.onListItemClick()", "in else stmt");
            File clickedFile = new File(this.directoryEntries.get(selectedItem).getFullPath());
            if(clickedFile != null)
            {
                handleFile(clickedFile);
            }
        }
    }
    
    /**
     * Opens the selected file in a different app if there is an app for the file type
     * If no app for the file type, displays toast message
     * @param File - The File to open
     */
    private void openFile(File file)
    {
       
        File newFile = new File(Environment.getExternalStorageDirectory() + "/Connexions/" + file.getName());
        Uri path = Uri.fromFile(newFile);
        //Log.d("FileBrowserActivity", "path: " + path.toString());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final String ext;
        if(file.getAbsolutePath().indexOf(Constants.PDF_EXTENSION) > -1)
        {
            intent.setDataAndType(path, "application/pdf");
            ext = Constants.PDF_EXTENSION;
        }
        else if(file.getAbsolutePath().indexOf(Constants.EPUB_EXTENSION) > -1)
        {
            intent.setDataAndType(path, "application/epub+zip");
            ext = Constants.EPUB_EXTENSION;
        }
        else if(file.getAbsolutePath().indexOf(Constants.TXT_EXTENSION) > -1)
        {
            intent.setDataAndType(path, "text/plain");
            ext = Constants.TXT_EXTENSION;
        }
        else
        {
            ext = "";
        }

        try 
        {
            startActivity(intent);
        } 
        catch (ActivityNotFoundException e) 
        {
            if(ext.equals(""))
            {
                Toast.makeText(FileBrowserActivity.this, getString(R.string.file_browser_toast),  Toast.LENGTH_SHORT).show();
            }
            else
            {
            
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("No Application Found");
                alertDialog.setMessage("No application found to open " + ext + " files.  Select Open Google Play button to install app to open selected file type.");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Open Google Play", new DialogInterface.OnClickListener() 
                {
                    
                    public void onClick(DialogInterface dialog, int which) 
                    {
                        Uri marketUri = Uri.parse("");
                          if(ext.equals(Constants.PDF_EXTENSION))
                          {
                              marketUri = Uri.parse("market://search?q=pdf&c=apps");
                          }
                          else if(ext.equals(Constants.EPUB_EXTENSION))
                          {
                              marketUri = Uri.parse("market://search?q=epub&c=apps");
                          }
                          else if(ext.equals(Constants.TXT_EXTENSION))
                          {
                              marketUri = Uri.parse("market://search?q=text+editor&c=apps");
                          }
                          Intent marketIntent = new Intent(Intent.ACTION_VIEW).setData(marketUri);
                          PackageManager pm = getPackageManager();
                          if(marketIntent.resolveActivity(pm) != null)
                          {
                              startActivity(marketIntent);
                          }
                          else
                          {
                              Toast.makeText(FileBrowserActivity.this, "Google Play is not available.",  Toast.LENGTH_SHORT).show();
                          }
                 
                    } }); 
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No Thanks", new DialogInterface.OnClickListener() 
                {
                      public void onClick(DialogInterface dialog, int which) 
                      {
                          //do nothing
                 
                    } }); 
                alertDialog.show();
               
                }
        }
    }
    
    /**
     * Handles deleting a file is delete file is selected from the menu
     * @param downloadedFile - the File to delete
     * @return boolean - always true
     */
    public boolean handleDeleteFile(final DownloadedFile downloadedFile)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Delete Files");
        alertDialog.setMessage("Delete " + downloadedFile.getDisplayPath() + "?  Press Cancel to abort.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete", new DialogInterface.OnClickListener() 
        {
              public void onClick(DialogInterface dialog, int which) 
              {
                  new File(downloadedFile.getFullPath()).delete();
                  Toast toast = Toast.makeText(FileBrowserActivity.this, "File deleted.", Toast.LENGTH_SHORT);
                  toast.show();
                  fileListAdapter.remove(downloadedFile);
         
            } }); 
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, Constants.CANCEL, new DialogInterface.OnClickListener() 
        {
              public void onClick(DialogInterface dialog, int which) 
              {
                  //do nothing
         
            } }); 
        alertDialog.show();
        return true;
    }
}
