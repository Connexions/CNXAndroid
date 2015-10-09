/**
 * Copyright (c) 2015 Rice University
 *
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.fragments;

/**
 * Fragment for note editor
 * @author Ed Woodward
 */

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.cnx.android.R;
import org.cnx.android.beans.Content;
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

    // The different distinct states the activity can be run in.
    private static final int STATE_EDIT = 0;
    private static final int STATE_UPDATE = 1;

    private int state;
    private Cursor cursor;
    private EditText editText;
    private String originalContent;
    private Content content;
    Activity activity;

    /**
     * A custom EditText that draws lines between each line of text that is displayed.
     */
    public static class LinedEditText extends EditText
    {
        private Rect rect;
        private Paint paint;

        // we need this constructor for LayoutInflater
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        activity = getActivity();
        content = (Content)getArguments().get("content");
        View v = inflater.inflate(R.layout.note_editor, container, false);

        state = STATE_EDIT;

        //        if(content == null)
        //        {
        //            Toast.makeText(activity, "Cannot create note.  Please try again.", Toast.LENGTH_SHORT).show();
        //            return;
        //        }

        //        setContentView(R.layout.note_editor);
        //
        //        ActionBar aBar = getActionBar();
        //
        //        if(content == null)
        //        {
        //            aBar.setTitle("Note not created correctly.");
        //        }
        //        else
        //        {
        //            aBar.setTitle("Note for " + content.getTitle());
        //        }

        editText = (EditText) v.findViewById(R.id.note);
        checkDBForNote();

        if (savedInstanceState != null)
        {
            originalContent = savedInstanceState.getString(ORIGINAL_CONTENT);
        }
        setActionBar(v);
        return v;
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
            activity.setResult(activity.RESULT_CANCELED);
            return;
        }

        String text = editText.getText().toString();
        int length = text.length();

        if (activity.isFinishing() && (length == 0) && cursor != null)
        {
            activity.setResult(activity.RESULT_CANCELED);
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
    private final void saveNote()
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
        String title = content.getTitle();//.substring(0, Math.min(30, length));
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
        values.put(Notes.URL, content.getUrl().toString());

        try
        {
            if (state == STATE_UPDATE)
            {
                //Log.d("NoteEditorActivity", "updating note");
                activity.getContentResolver().update(Notes.CONTENT_URI, values, "notes_url=?", new String[]{content.getUrl().toString()});
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
        activity.getContentResolver().delete(Notes.CONTENT_URI, "notes_url=?", new String[]{content.getUrl().toString()});
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
            cursor = activity.getContentResolver().query(Notes.CONTENT_URI, null, "notes_url='" + content.getUrl().toString() + "'", null, null);
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
        File cnxDir = new File(Environment.getExternalStorageDirectory(), "OpenStaxCollege/");
        if(!cnxDir.exists())
        {
            cnxDir.mkdir();
        }
        String fileName = MenuUtil.getTitle(content.getTitle()) + ".txt";
        File file = new File(cnxDir, fileName);
        String text = editText.getText().toString();
        PrintWriter pw = null;

        try
        {
            pw = new PrintWriter(file);
            pw.write(text);
            pw.flush();
            //pw.close();
            Toast.makeText(activity, fileName + " saved to OpenStaxCollege folder.", Toast.LENGTH_LONG).show();
        }
        catch (FileNotFoundException e)
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

    /**
     * Sets up the buttons for the Action Bar
     */
    private void setActionBar(View v)
    {
        ImageButton saveButton = (ImageButton)v.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View v)
            {
                saveNote();
                activity.finish();
            }
        });

        ImageButton exportButton = (ImageButton)v.findViewById(R.id.exportButton);
        exportButton.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View v)
            {
                exportNote();

            }
        });

        ImageButton deleteButton = (ImageButton)v.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View v)
            {
                deleteNote();
                activity.finish();

            }
        });

        ImageButton shareButton = (ImageButton)v.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType(getString(R.string.mimetype_text));

                if(content != null)
                {
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Note for " + content.getTitle());
                    String text = editText.getText().toString();
                    intent.putExtra(Intent.EXTRA_TEXT, text + "\n\n" + getString(R.string.shared_via));

                    Intent chooser = Intent.createChooser(intent, getString(R.string.tell_friend) + " " + content.getTitle());
                    startActivity(chooser);
                }
                else
                {
                    Toast.makeText(activity, getString(R.string.no_data_msg), Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
