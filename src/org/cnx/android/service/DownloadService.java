/**
 * Copyright (c) 2011 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.service;

import org.cnx.android.handlers.DownloadHandler;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * @author Ed Woodward
 *
 */
public class DownloadService extends IntentService
{
    /**
     * Constant for Download URL added to intent
     */
    public final static String DOWNLOAD_URL = "downloadURL";
    /**
     * Constant for download file name added to intent
     */
    public final static String DOWNLOAD_FILE_NAME = "downloadFileName";
    
    /**
     * Constructor
     */
    public DownloadService()
    {
        super("DownloadService");
    }

    /* (non-Javadoc)
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent)
    {
        //Log.d("DownloadService", "called");
        String url = intent.getStringExtra(DOWNLOAD_URL);
        String fileName = intent.getStringExtra(DOWNLOAD_FILE_NAME);
        //Log.d("DownloadHandler.download", "url = " + url + " filename = " + fileName);
        DownloadHandler dh = new DownloadHandler();
        dh.downloadFile(getApplicationContext(), url, fileName);

    }

}
