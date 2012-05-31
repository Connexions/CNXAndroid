/**
 * Copyright (c) 2010 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.handlers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.cnx.android.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

/**
 * Handles downloading of files
 * @author Ed Woodward
 *
 */
public class DownloadHandler
{
    /**
     * Downloads files in a separate thread.  Adds notification of download to status bar.
     * @param context - The current context
     * @param url - the URL to download
     * @param fileName - the name to save the file as
     */
    public void downloadFile(final Context context, final String url,final String fileName)
    {
        /**
         * The directory to store the files in
         */
        final String STORAGE_PATH = "Connexions/";
        /**
         * Download buffer size 
         */
        final int BUFFER_SIZE = 1024 * 23;
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context,context.getClass()), 0);

        // configure the notification
        final Notification notification = new Notification(R.drawable.download_icon, "Downloading " + fileName, System.currentTimeMillis());
        notification.flags = notification.flags |=Notification.FLAG_AUTO_CANCEL;
        notification.contentIntent = pendingIntent;
        notification.setLatestEventInfo(context, "Downloading file", "Downloading " + fileName, pendingIntent);

        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(42, notification);
        
        Thread downloadThread = new Thread() 
        {
            public void run() 
            {
       
                try
                {
                    File cnxDir = new File(Environment.getExternalStorageDirectory(), STORAGE_PATH);
                    if(!cnxDir.exists())
                    {
                        cnxDir.mkdir();
                    }
                    File file = new File(cnxDir, fileName);
                    URL urlObj = new URL(url);
                    URLConnection con = urlObj.openConnection();
                    //Log.d("MenuHandler.download", "length = " + length);
                    BufferedInputStream bis = new BufferedInputStream(con.getInputStream(), BUFFER_SIZE);

                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] bArray = new byte[BUFFER_SIZE];
                    int current = 0;
                    int read = 0;
                    while(current != -1)
                    {
                        fos.write(bArray,0,current);
                        current = bis.read(bArray, 0, BUFFER_SIZE);
                        read = read + current;
                        //Log.d("DownloadHandler.download", "read = " + read);
                        //Log.d("DownloadHandler.download", "percent complete = " + complete);
                    }
                    //fos.flush();
                    fos.close();
                    bis.close();
                    notificationManager.cancel(42);
                    
                }
                catch(Exception ioe)
                {
                    Log.d("DownloadHandler.download", "Error: " + ioe.toString(), ioe);
                }
            }
        };
        downloadThread.start();
    }

}
