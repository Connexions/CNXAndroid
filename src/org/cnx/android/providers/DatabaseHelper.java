/**
 * Copyright (c) 2011 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.providers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Ed Woodward
 * 
 * database helper class shared by providers
 *
 */
public class DatabaseHelper extends SQLiteOpenHelper 
{
    private static final String DATABASE_NAME = "connexions.db";
    /**  database version */
    private static final int DATABASE_VERSION = 5;
    /**  Constructor*/
    DatabaseHelper(Context context) 
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**  Creates table if it does not exist*/
    @Override
    public void onCreate(SQLiteDatabase db) 
    {
        db.execSQL("CREATE TABLE " + ConnexionsProvider.FAVS_TABLE + " ("
                + Favs.ID + " INTEGER PRIMARY KEY,"
                + Favs.TITLE + " TEXT,"
                + Favs.URL + " TEXT,"
                + Favs.ICON + " TEXT,"
                + Favs.OTHER + " TEXT"
                + ");");
        db.execSQL("CREATE TABLE " + ConnexionsProvider.NOTES_TABLE + " ("
                + Notes._ID + " INTEGER PRIMARY KEY,"
                + Notes.TITLE + " TEXT,"
                + Notes.NOTE + " TEXT,"
                + Notes.URL + " TEXT"
                + ");");
    }

    /** For upgrading database */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
    {
        //db.execSQL("alter table " + FAVS_TABLE + " add column " + Favs.OTHER + " text");
        db.execSQL("CREATE TABLE " + ConnexionsProvider.NOTES_TABLE + " ("
                + Notes._ID + " INTEGER PRIMARY KEY,"
                + Notes.TITLE + " TEXT,"
                + Notes.NOTE + " TEXT,"
                + Notes.URL + " TEXT"
                + ");");
    }
}
