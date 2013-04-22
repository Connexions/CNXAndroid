/**
 * Copyright (c) 2011 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.providers;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder; 
import android.net.Uri;
import android.util.Log;

/**
*  Database provider for user favorites 
* 
* @author Ed Woodward
*
*/
public class NotesProvider extends ContentProvider
{
    public static final String AUTHORITY = "org.cnx.android.providers.NotesProvider";
    /** notes table name */
    private static final String NOTES_TABLE = "notes";
    /** Map of Notes table columns */
    private static HashMap<String, String> NotesProjectionMap;
    
    /** static section to initialize notes table map */
    static
    {
        NotesProjectionMap = new HashMap<String,String>();
        NotesProjectionMap.put(Notes.ID, Notes.ID);
        NotesProjectionMap.put(Notes.TITLE, Notes.TITLE);
        NotesProjectionMap.put(Notes.URL, Notes.URL);
        NotesProjectionMap.put(Notes.NOTE, Notes.NOTE);
    }
    
    /** Variable for database helper */
    private DatabaseHelper dbHelper;
    
    /**  Called when class created. initializes database helper*/
    @Override
    public boolean onCreate() 
    {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    /** used to delete content from notes table */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = db.delete(NOTES_TABLE, selection, selectionArgs);
        db.close();
        return count;
    }

    /**  needed for interface.  Not sure why.*/
    @Override
    public String getType(Uri uri)
    {
        return null;
    }

    /** Used to insert a Note in the notes table */
    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(NOTES_TABLE, null, new ContentValues(values));
        db.close();
        if (rowId > 0) 
        {
            Uri favUri = ContentUris.withAppendedId(Notes.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(favUri, null);
            return favUri;
        }
            
        throw new SQLException("Failed to insert row into " + uri);
        

    }

    /**  Used to retrieve all items in the notes table*/
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(NOTES_TABLE);
        qb.setProjectionMap(NotesProjectionMap);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }

    /** Update the Notes table. */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.update(NOTES_TABLE, values, selection, selectionArgs);
    }
    
}
