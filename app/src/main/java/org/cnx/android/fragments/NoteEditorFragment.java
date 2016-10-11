/**
 * Copyright (c) 2015 Rice University
 *
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.fragments;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import org.cnx.android.R;
import org.cnx.android.activity.LandingActivity;
import org.cnx.android.activity.NoteEditorActivity;
import org.cnx.android.beans.Content;
import org.cnx.android.handlers.MenuHandler;
import org.cnx.android.providers.Notes;
import org.cnx.android.utils.MenuUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Fragment for displaying note editor
 * @author Ed Woodward
 */
public class NoteEditorFragment extends Fragment
{
    // This is our state data that is stored when freezing.
    private static final String ORIGINAL_CONTENT = "origContent";
    private static final String[] STORAGE_PERMS={
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private int REQUEST = 1337;

    // The different distinct states the activity can be run in.
    private static final int STATE_EDIT = 0;
    private static final int STATE_UPDATE = 1;

    private int state;
    private Cursor cursor;
    private EditText editText;
    private String originalContent;
    private Content content;
    AppCompatActivity activity;

    /**
     * A custom EditText that draws lines between each line of text that is displayed.
     */
    public static class LinedEditText extends EditText
    {
        private Rect rect;
        private Paint paint;

        public LinedEditText(Context context, AttributeSet attrs)
        {
            super(context, attrs);

            rect = new Rect();
            paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(0x80FFFFFF);
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            int count = getLineCount();
            Rect r = rect;
            Paint newpaint = paint;

            for (int i = 0; i < count; i++)
            {
                int baseline = getLineBounds(i, r);

                canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, newpaint);
            }

            super.onDraw(canvas);
        }
    }

    public static NoteEditorFragment newInstance(Content c)
    {
        NoteEditorFragment nef = new NoteEditorFragment();
        Bundle args = new Bundle();
        args.putSerializable("content", c);
        nef.setArguments(args);
        return nef;

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        activity = (AppCompatActivity)getActivity();
        content = (Content)getArguments().get("content");
        View v = inflater.inflate(R.layout.note_editor, container, false);

        state = STATE_EDIT;

        editText = (EditText) v.findViewById(R.id.note);
        checkDBForNote();

        if (savedInstanceState != null)
        {
            originalContent = savedInstanceState.getString(ORIGINAL_CONTENT);
        }
        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            Intent mainIntent = new Intent(getContext(), LandingActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(mainIntent);
            return true;
        }
        else if(item.getItemId() == R.id.delete_note)
        {
            deleteNote();
            getActivity().finish();
            return true;

        }
        else if(item.getItemId() == R.id.export_note)
        {
            exportNote();
            return true;

        }
        else
        {

            MenuHandler mh = new MenuHandler();
            return mh.handleContextMenu(item, getContext(), content);
        }

    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (originalContent == null && editText != null)
        {
            originalContent = editText.getText().toString();
        }

    }

    /* (non-Javadoc)
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        // Save away the original text, so we still have it if the activity
        // needs to be killed while paused.
        outState.putString(ORIGINAL_CONTENT, originalContent);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    public void onPause()
    {
        super.onPause();

        if(editText == null)
        {
            activity.setResult(NoteEditorActivity.RESULT_CANCELED);
            return;
        }

        String text = editText.getText().toString();
        int length = text.length();

        if (activity.isFinishing() && (length == 0) && cursor != null)
        {
            activity.setResult(NoteEditorActivity.RESULT_CANCELED);
        }
        else
        {
            saveNote();
        }
    }

    /**
     * IF the note is empty, it displays a message to the user
     * If the note has text, the title is set and the note placed in the database.
     * Handles updating or inserting a new note based on the flag set in checkDBForNote()
     */
    public final void saveNote()
    {

        ContentValues values = new ContentValues();

        String text = editText.getText().toString();
        //Log.d("NoteEditorActivity", "note: " + text);
        int length = text.length();

        if (length == 0)
        {
            Toast.makeText(activity, getString(R.string.nothing_to_save), Toast.LENGTH_SHORT).show();
            return;
        }
        String title = content.getBookTitle();
        if (length > 30)
        {
            int lastSpace = title.lastIndexOf(' ');
            if (lastSpace > 0)
            {
                title = title.substring(0, lastSpace);
            }
        }
        values.put(Notes.TITLE, title);

        values.put(Notes.NOTE, text);
        values.put(Notes.URL, content.getBookUrl());

        try
        {
            if (state == STATE_UPDATE)
            {
                //Log.d("NoteEditorActivity", "updating note");
                activity.getContentResolver().update(Notes.CONTENT_URI, values, "notes_url=?", new String[]{content.getBookUrl()});
            }
            else
            {
                activity.getContentResolver().insert(Notes.CONTENT_URI, values);
            }
        }
        catch (NullPointerException e)
        {
            Log.e("NoteEditorActivity", e.getMessage());
        }
    }

    /**
     * Deletes the note from the database.
     */
    private final void deleteNote()
    {
        activity.getContentResolver().delete(Notes.CONTENT_URI, "notes_url=?", new String[]{content.getBookUrl()});
        editText.setText("");
        activity.finish();
    }

    /**
     * Checks database for an existing note for the URL of the current content
     * If the note exists, it is retrieved and the cursor placed at the end of the text
     */
    private void checkDBForNote()
    {
        if(content != null)
        {
            cursor = activity.getContentResolver().query(Notes.CONTENT_URI, null, "notes_url='" + content.getBookUrl() + "'", null, null);
            if(cursor.getCount()>0)
            {
                cursor.moveToNext();
                //Log.d("NoteEditorActivity.checkDBForNote()", "cursor.count(): " + cursor.getCount());
                int notesColumn = cursor.getColumnIndex(Notes.NOTE);
                //Log.d("NoteEditorActivity.checkDBForNote()", "urlColumn: " + urlColumn);
                //Log.d("NoteEditorActivity.checkDBForNote()", "notesColumn: " + notesColumn);
                editText.append(cursor.getString(notesColumn));
                editText.setSelection(editText.getText().length());
                Linkify.addLinks(editText, Linkify.ALL);
                state = STATE_UPDATE;
            }
            else
            {
                state = STATE_EDIT;
                editText.setText("");
            }
        }
        else
        {
            state = STATE_EDIT;
            editText.setText("Please the note and try again.  It was not created correctly.");
        }
    }

    /**
     * Saves the note as a text file in the Connexions file.
     */
    private void exportNote()
    {
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE )== PackageManager.PERMISSION_GRANTED )
        {
            File cnxDir = new File(Environment.getExternalStorageDirectory(), "OpenStax/");
            if(!cnxDir.exists())
            {
                cnxDir.mkdir();
            }
            String fileName = MenuUtil.getTitle(content.getBookTitle()) + ".txt";
            File file = new File(cnxDir, fileName);
            String text = editText.getText().toString();
            PrintWriter pw = null;

            try
            {
                pw = new PrintWriter(file);
                pw.write(text);
                pw.flush();
                Toast.makeText(activity, fileName + " saved to OpenStax folder.", Toast.LENGTH_LONG).show();
            }
            catch(FileNotFoundException e)
            {
                Log.d("NoteEditorActivity", "Error exporting note: " + e.toString(), e);
            }
            finally
            {
                if(pw != null)
                {
                    pw.close();
                }
            }
        }
        else if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            Snackbar.make(getView(), getString(R.string.external_storage_request),Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.ok_button), new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            requestPermissions(STORAGE_PERMS,REQUEST);
                        }
                    })
                    .show();
        }
        else
        {
            requestPermissions(STORAGE_PERMS, REQUEST);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {

            exportNote();
        }
    }
}
