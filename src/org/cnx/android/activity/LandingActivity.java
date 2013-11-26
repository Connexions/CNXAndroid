/**
 * 
 */
package org.cnx.android.activity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.cnx.android.R;
import org.cnx.android.adapters.LandingListAdapter;
import org.cnx.android.beans.Content;
import org.cnx.android.handlers.SearchHandler;
import org.cnx.android.utils.Constants;
import org.cnx.android.utils.ContentCache;

import com.actionbarsherlock.app.SherlockActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity for Landing page
 * @author Ed Woodward
 *
 */
public class LandingActivity extends SherlockActivity
{
    private static String PREVIOUS_VERSION = "3.2";
    
    private static String VERSION_PROPERTY = "cnxVersion";
    
    private Toast toast;
    
    private ListView listView;
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing);
        listView = (ListView)findViewById(R.id.landingList);
        setLayout();
        displayToast();
    }
    
    /**
     * Sets the list adapter and adds the listeners to the list and the search button
     */
    private void setLayout()
    {
        listView.setAdapter(new LandingListAdapter(this,setContentList()));
        
        listView.setOnItemClickListener(new ListView.OnItemClickListener() 
        {
            public void onItemClick(AdapterView<?> a, View v, int i, long l) 
            {
                Content c = (Content)listView.getItemAtPosition(i);
                performAction(c.getTitle());
                
                
            }
        });
        
        listView.setOnItemSelectedListener(new ListView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> a, View v, int i, long l) 
            {
                Content c = (Content)listView.getItemAtPosition(i);
                performAction(c.getTitle());
            }
            
            public void onNothingSelected(AdapterView<?> arg0) 
            {
                //do nothing
            }

        });
        
        ImageButton searchButton = (ImageButton)findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new OnClickListener() 
        {
                  
              public void onClick(View v) 
              {
                  EditText searchFor = (EditText)findViewById(R.id.searchText);
                  performSearch(searchFor.getText().toString());
              }
          });
        EditText searchText = (EditText)findViewById(R.id.searchText);
        searchText.setOnKeyListener(new View.OnKeyListener()
        {
            
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if(event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER)
                    {
                        EditText searchFor = (EditText)findViewById(R.id.searchText);
                        performSearch(searchFor.getText().toString());
                    }
                }
                return false;
            }
        });
        //listView.setAdapter(new LandingListAdapter(this,setContentList()));
        
    }
    
    /**
     * Creates a List of Content objects to display the options
     * 
     * @return ArrayList of Content objects
     */
    private ArrayList<Content> setContentList()
    {
        ArrayList<Content> list = new ArrayList<Content>();
        
        Content c = new Content();
        c.setTitle(getString(R.string.title_browse));
        
        Content c2 = new Content();
        c2.setTitle(getString(R.string.title_favs));
        
        Content c3 = new Content();
        c3.setTitle(getString(R.string.title_download));
        
        //Content c4 = new Content();
        //c4.setTitle(getString(R.string.title_help));
        
        list.add(c);
        list.add(c2);
        list.add(c3);
        //list.add(c4);
        
        return list;
    }
    
    /**
     * Calls SearchHandler to perform cnx search
     * @param searchFor String - what to search for
     */
    private void performSearch(String searchFor)
    {
        SearchHandler sh = new SearchHandler();
        sh.performSearch(searchFor, Constants.CNX_SEARCH, this);
    }
    
    /**
     * Redirects to another avtivity based on what the user selected from the ListView
     * @param item String - the selected item
     */
    private void performAction(String item)
    {
        if(item.contains(getString(R.string.title_browse)))
        {
            Intent lensesIntent = new Intent(getApplicationContext(), ViewLensesActivity.class);
            //ContentCache.setObject(getString(R.string.content), content);
            startActivity(lensesIntent);
        }
        else if(item.contains(getString(R.string.title_favs)))
        {
            Intent intent = new Intent(getApplicationContext(), ViewFavsActivity.class);
            startActivity(intent);
        }
        else if(item.contains(getString(R.string.title_download)))
        {
            Intent viewIntent = new Intent(getApplicationContext(), FileBrowserActivity.class);
            startActivity(viewIntent);
        }
        else if(item.contains(getString(R.string.title_help)))
        {
            try
            {
                Content content = new Content();
                content.setUrl(new URL(Constants.HELP_FILE_URL)); 
                ContentCache.setObject(getString(R.string.webcontent), content);
                startActivity(new Intent(getApplicationContext(), WebViewActivity.class));
                
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            
        }
    }
    
    /**
     * displays toast message when user first opens the app.
     */
    private void displayToast()
    {
        //SharedPreference
        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
        String propVersion = myPrefs.getString(VERSION_PROPERTY, "");
        
        if(propVersion == null || propVersion.equals("") || propVersion.equals(PREVIOUS_VERSION))
        {
            SharedPreferences.Editor prefsEditor = myPrefs.edit();
            prefsEditor.putString(VERSION_PROPERTY,"3.3");
            prefsEditor.commit();
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast, (ViewGroup) findViewById(R.id.toast_layout_root));

            ImageView image = (ImageView) layout.findViewById(R.id.image);
            image.setImageResource(R.drawable.logo);
            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText(getString(R.string.intro));

            toast = new Toast(this);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            
            keepToast();
        }
    }
    
    /**
     * keeps toast around for longer message
     */
    private void keepToast() 
    {
          Thread t = new Thread() 
          {
              public void run() 
              {
                  int count = 0;
                  try 
                  {
                      while (true && count < 6) 
                      {
                          toast.show();
                          sleep(1850);
                          count++;
   
                      }
                  } 
                  catch (Exception e) 
                  {
                      //Log.e("LongToast", "", e);
                  }
              }
          };
          t.start();
      }

}
