/**
 * Copyright (c) 2015 Rice University
 *
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.adapters;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.cnx.android.R;
import org.cnx.android.beans.DownloadedFile;
import org.cnx.android.utils.Constants;

import java.io.File;
import java.util.ArrayList;

import co.paulburke.android.itemtouchhelperdemo.helper.ItemTouchHelperAdapter;

/**
 * Created by ew2 on 10/15/15.
 */
public class FileListRecyclerViewAdapter extends RecyclerView.Adapter<FileListRecyclerViewAdapter.ViewHolder> implements ItemTouchHelperAdapter
{
    /** Current context */
    private static  Context context;
    /** List of DownloadedFile objects to display*/
    private static ArrayList<DownloadedFile> directoryEntries = new ArrayList<>();

    private int rowLayout;

    static File currentDirectory;

    public FileListRecyclerViewAdapter(ArrayList<DownloadedFile> fileList, int rowLayout, Context context)
    {
        this.context = context;
        directoryEntries = fileList;
        this.rowLayout = rowLayout;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        currentDirectory = new File(Environment.getExternalStorageDirectory(), context.getString(R.string.folder_name) + "/");
        return new ViewHolder(v,directoryEntries);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i)
    {
        DownloadedFile c = directoryEntries.get(i);
        if(c != null)
        {
            //Log.d("FileList.onBind()", "content is not null ");
            if(c.getDisplayPath().contains(Constants.PDF_EXTENSION))
            {
                viewHolder.logo.setImageResource(R.drawable.pdf_icon);
            }
            else
            {
                viewHolder.logo.setImageResource(R.drawable.epub_icon);
            }

            viewHolder.title.setText(c.getDisplayPath());

        }

    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition)
    {
        return true;
    }

    @Override
    public int getItemCount()
    {
        return directoryEntries == null ? 0 : directoryEntries.size();
    }

    @Override
    public void onItemDismiss(final int position)
    {
        final DownloadedFile downloadedFile = directoryEntries.get(position);

        new File(downloadedFile.getFullPath()).delete();
        Toast toast = Toast.makeText(context, "File deleted.", Toast.LENGTH_SHORT);
        toast.show();
        directoryEntries.remove(position);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public ImageView logo;
        public TextView title;
        public View view;
        ArrayList<DownloadedFile> contentList;

        public ViewHolder(View itemView, ArrayList<DownloadedFile> contentList)
        {
            super(itemView);
            view = itemView;
            this.contentList = contentList;

            logo = (ImageView) itemView.findViewById(R.id.logoView);
            title = (TextView)itemView.findViewById(R.id.bookName);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v)
        {
            DownloadedFile df = contentList.get(getAdapterPosition());
            String selectedFileString = df.getDisplayPath();
            if (selectedFileString.equals("."))
            {
                // Refresh
                handleFile(currentDirectory);
            }
            else
            {
                //Log.d("FileBrowserActivity.onListItemClick()", "in else stmt");
                File clickedFile = new File(df.getFullPath());
                if(clickedFile != null)
                {
                    handleFile(clickedFile);
                }
            }
        }

        private void handleFile(final File dirOrFile)
        {
            //Log.d("FileBrowserActivity.browseTo()", "Called");

            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle(context.getString(R.string.file_dialog_title));
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

        private void openFile(File file)
        {

            File newFile = new File(Environment.getExternalStorageDirectory() + "/" + context.getString(R.string.folder_name) + "/" + file.getName());
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
                intent.setDataAndType(path, context.getString(R.string.mimetype_text));
                ext = Constants.TXT_EXTENSION;
            }
            else
            {
                ext = "";
            }

            try
            {
                context.startActivity(intent);
            }
            catch (ActivityNotFoundException e)
            {
                if(ext.equals(""))
                {
                    Toast.makeText(context, context.getString(R.string.file_browser_toast), Toast.LENGTH_SHORT).show();
                }
                else
                {

                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
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
                            PackageManager pm = context.getPackageManager();
                            if(marketIntent.resolveActivity(pm) != null)
                            {
                                context.startActivity(marketIntent);
                            }
                            else
                            {
                                Toast.makeText(context, "Google Play is not available.",  Toast.LENGTH_SHORT).show();
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


    }

}
