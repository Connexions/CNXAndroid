/**
 * Copyright (c) 2011 Rice University
 * 
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details. 
 */
package org.cnx.android.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import android.app.ActionBar;
import android.app.Activity;
import org.cnx.android.R;
import org.cnx.android.beans.Content;
import org.cnx.android.providers.Notes;
import org.cnx.android.utils.ContentCache;
import org.cnx.android.utils.MenuUtil;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Note editor.  
 * Based on sample Android Notepad app: http://developer.android.com/resources/samples/NotePad/index.html
 * @author Ed Woodward
 *
 */
public class NoteEditorActivity extends Activity
{
    private static final String TAG = "NoteEditor";

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

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        state = STATE_EDIT;
        
        content = (Content)ContentCache.getObject(getString(R.string.content));
        if(content == null)
        {
            content = (Content)ContentCache.getObject(getString(R.string.cache_savednotecontent));
        }
        
        if(content == null)
        {
            Toast.makeText(NoteEditorActivity.this, "Cannot create note.  Please try again.",  Toast.LENGTH_SHORT).show();
            return;
        }

        setContentView(R.layout.note_editor);
        
        ActionBar aBar = getActionBar();
        
        if(content == null)
        {
            aBar.setTitle("Note not created correctly.");
        }
        else
        {
            aBar.setTitle("Note for " + content.getTitle());
        }
        
        editText = (EditText) findViewById(R.id.note);
        checkDBForNote();

        if (savedInstanceState != null) 
        {
            originalContent = savedInstanceState.getString(ORIGINAL_CONTENT);
        }
        setActionBar();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() 
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
    protected void onSaveInstanceState(Bundle outState) 
    {
        // Save away the original text, so we still have it if the activity
        // needs to be killed while paused.
        outState.putString(ORIGINAL_CONTENT, originalContent);
        ContentCache.setObject(getString(R.string.cache_savednotecontent), content);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() 
    {
        super.onPause();
        
        if(editText == null)
        {
            setResult(RESULT_CANCELED);
            return;
        }

        String text = editText.getText().toString();
        int length = text.length();

        if (isFinishing() && (length == 0) && cursor != null) 
        {
            setResult(RESULT_CANCELED);
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
            Toast.makeText(this, getString(R.string.nothing_to_save), Toast.LENGTH_SHORT).show();
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
                getContentResolver().update(Notes.CONTENT_URI, values, "notes_url=?", new String[]{content.getUrl().toString()});
            }
            else
            {
                getContentResolver().insert(Notes.CONTENT_URI, values);
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
        getContentResolver().delete(Notes.CONTENT_URI, "notes_url=?", new String[]{content.getUrl().toString()});
        editText.setText("");
        finish();
    }
    
    /**
     * Checks database for an existing note for the URL of the current content
     * If the note exists, it is retrieved and the cursor placed at the end of the text
     */
    private void checkDBForNote()
    { 
        if(content != null)
        {
            cursor = getContentResolver().query(Notes.CONTENT_URI, null, "notes_url='" + content.getUrl().toString() + "'", null, null);
            if(cursor.getCount()>0)
            {
                cursor.moveToNext();
                //Log.d("NoteEditorActivity.checkDBForNote()", "cursor.count(): " + cursor.getCount());
                //int urlColumn = cursor.getColumnIndex(Notes.URL);
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
        File cnxDir = new File(Environment.getExternalStorageDirectory(), "OpenStaxCNX/");
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
            Toast.makeText(this, fileName + " saved to OpenStaxCNX folder.", Toast.LENGTH_LONG).show();
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
    private void setActionBar()
    {
        ImageButton saveButton = (ImageButton)findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new OnClickListener() 
        {
                  
              public void onClick(View v) 
              {
                  saveNote();
                  finish();
              }
          });
        
        ImageButton exportButton = (ImageButton)findViewById(R.id.exportButton);
        exportButton.setOnClickListener(new OnClickListener() 
        {
                  
              public void onClick(View v) 
              {
                  exportNote();

              }
          });
        
        ImageButton deleteButton = (ImageButton)findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new OnClickListener() 
        {
                  
              public void onClick(View v) 
              {
                  deleteNote();
                  finish();

              }
          });
        
        ImageButton shareButton = (ImageButton)findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new OnClickListener() 
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

                      Intent chooser = Intent.createChooser(intent, getString(R.string.tell_friend) + " "+ content.getTitle());
                      startActivity(chooser);
                  }
                  else
                  {
                      Toast.makeText(NoteEditorActivity.this, getString(R.string.no_data_msg),  Toast.LENGTH_LONG).show();
                  }

              }
          });
    }
}


