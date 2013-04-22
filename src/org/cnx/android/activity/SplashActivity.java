/**
 * Copyright (c) 2010 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.activity;

import org.cnx.android.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Activity to display a splash screen
 * Not currently being used.
 * @author Ed Woodward
 *
 */
public class SplashActivity extends Activity
{
    /**
     * Splash display time in milliseconds
     */
    private int SPLASH_DISPLAY_TIME = 3000;
    /**
     * Boolean for if Splash is currently being displayed
     */
    private boolean active = true;
    
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
     
        // thread for displaying the SplashScreen
        Thread splashTread = new Thread() 
        {
            @Override
            public void run() 
            {
                try {
                    int waited = 0;
                    while(active && (waited < SPLASH_DISPLAY_TIME)) 
                    {
                        sleep(100);
                        if(active) 
                        {
                            waited += 100;
                        }
                    }
                } catch(InterruptedException e) 
                {
                    // do nothing
                } 
                finally 
                {
                    finish();
                    endSplash();
                }
            }
        };
        splashTread.start();
    }
    
    /**
     * Starts next Activity after Splash times out
     */
    private void endSplash()
    {
        startActivity(new Intent(this,ViewLensesActivity.class));
    }

}
