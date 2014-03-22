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
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar;
import android.app.ListActivity;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.widget.SimpleAdapter;
import org.cnx.android.R;
import org.cnx.android.adapters.FileListAdapter;
import org.cnx.android.beans.DownloadedFile;
import org.cnx.android.handlers.MenuHandler;
import org.cnx.android.utils.Constants;

import android.app.AlertDialog;
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
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
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
    private File currentDirectory = new File(Environment.getExternalStorageDirectory(), "Connexions/");
    /**
     * List Adapter for display
     */
    FileListAdapter fileListAdapter;

    private List<HashMap<String,String>> navTitles;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    String[] from = { "nav_icon","nav_item" };
    int[] to = { R.id.nav_icon , R.id.nav_item};
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        registerForContextMenu(getListView());
        
        ActionBar aBar = getActionBar();

        aBar.setTitle(getString(R.string.file_browser_title));
        readFileList();

        String[] items = getResources().getStringArray(R.array.nav_list);
        setDrawer(items);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerList = (ListView)findViewById(R.id.left_drawer);
        SimpleAdapter sAdapter = new SimpleAdapter(this,navTitles, R.layout.nav_drawer,from,to);

        // Set the list's click listener
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                //getActionBar().setTitle(getString(R.string.app_name));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                //getActionBar().setTitle(getString(R.string.app_name));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);
        aBar.setDisplayHomeAsUpEnabled(true);
        aBar.setHomeButtonEnabled(true);
        drawerList.setAdapter(sAdapter);
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

    /* (non-Javadoc)
      * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
      */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (drawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        MenuHandler mh = new MenuHandler();
        boolean returnVal = mh.handleContextMenu(item, this, null);

        return returnVal;

    }

    /**
     * checks if Connexions directory exists and then passes control to handleFile()
     */
    public void readFileList()
    {
        currentDirectory = new File(Environment.getExternalStorageDirectory(), "Connexions/");
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
        if (dirOrFile.isDirectory() && !dirOrFile.getPath().endsWith("Connexions/"))
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
        Collections.sort(directoryEntries);
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
        if(file.getAbsolutePath().contains(Constants.PDF_EXTENSION))
        {
            intent.setDataAndType(path, "application/pdf");
            ext = Constants.PDF_EXTENSION;
        }
        else if(file.getAbsolutePath().contains(Constants.EPUB_EXTENSION))
        {
            intent.setDataAndType(path, "application/epub+zip");
            ext = Constants.EPUB_EXTENSION;
        }
        else if(file.getAbsolutePath().contains(Constants.TXT_EXTENSION))
        {
            intent.setDataAndType(path, getString(R.string.mimetype_text));
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

    private void selectItem(int position)
    {
        switch (position)
        {
            case 0:
                Intent landingIntent = new Intent(getApplicationContext(), LandingActivity.class);
                startActivity(landingIntent);

                break;
            case 1:
                Intent lensesIntent = new Intent(getApplicationContext(), ViewLensesActivity.class);
                startActivity(lensesIntent);
                break;

            case 2:
                Intent favsIntent = new Intent(getApplicationContext(), ViewFavsActivity.class);
                startActivity(favsIntent);
                break;

            case 3:
                drawerLayout.closeDrawers();
                break;
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id)
        {
            selectItem(position);
        }
    }

    private void setDrawer(String[] items)
    {
        HashMap<String,String> hm1 = new HashMap<String,String>();
        hm1.put("nav_icon",Integer.toString(R.drawable.home));
        hm1.put("nav_item",items[0]);

        HashMap<String,String> hm2 = new HashMap<String,String>();
        hm2.put("nav_icon",Integer.toString(R.drawable.ic_action_device_access_storage_1));
        hm2.put("nav_item",items[1]);

        HashMap<String,String> hm3 = new HashMap<String,String>();
        hm3.put("nav_icon",Integer.toString(R.drawable.ic_action_star));
        hm3.put("nav_item",items[2]);

        HashMap<String,String> hm4 = new HashMap<String,String>();
        hm4.put("nav_icon",Integer.toString(R.drawable.ic_action_download));
        hm4.put("nav_item",items[3]);

        navTitles = new ArrayList<HashMap<String,String>>();

        navTitles.add(hm1);
        navTitles.add(hm2);
        navTitles.add(hm3);
        navTitles.add(hm4);
    }
}
