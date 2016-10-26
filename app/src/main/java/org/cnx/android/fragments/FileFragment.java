/**
 * Copyright (c) 2015 Rice University
 *
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.cnx.android.R;
import org.cnx.android.adapters.FileListRecyclerViewAdapter;
import org.cnx.android.beans.DownloadedFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import co.paulburke.android.itemtouchhelperdemo.helper.OnStartDragListener;
import co.paulburke.android.itemtouchhelperdemo.helper.SimpleItemTouchHelperCallback;

/**
 * Fragment for displaying downloaded files
 * @author Ed Woodward
 */
public class FileFragment extends Fragment implements OnStartDragListener
{
    /** Adaptor for card display */
    FileListRecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    /** list of bookmarks as Content objects */
    private ArrayList<DownloadedFile> directoryEntries = new ArrayList<>();

    /**
     * The /OpenStaxCNX directory as a file object
     */
    private File currentDirectory;

    private ItemTouchHelper itemTouchHelper;

    Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        activity = getActivity();
        View v = inflater.inflate(R.layout.card_view, container, false);


        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        recyclerView = (RecyclerView)getView().findViewById(R.id.book_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        currentDirectory = new File(Environment.getExternalStorageDirectory(), getString(R.string.folder_name) + "/");
        readFileList();

        adapter = new FileListRecyclerViewAdapter(directoryEntries, R.layout.card_row, activity);
        recyclerView.setAdapter(adapter);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    /**
     * For OnStartDragListener
     * @param viewHolder The holder of the view to drag.
     */
    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder)
    {
        itemTouchHelper.startDrag(viewHolder);
    }

    public void readFileList()
    {
        currentDirectory = new File(Environment.getExternalStorageDirectory(), getString(R.string.folder_name) + "/");
        if(currentDirectory.exists())
        {
            handleFile(currentDirectory);
        }

    }

    private void handleFile(final File dirOrFile)
    {
        //Log.d("FileBrowserActivity.browseTo()", "Called");
        this.currentDirectory = dirOrFile;
        loadList(dirOrFile.listFiles());

    }

    private void loadList(File[] files)
    {
        //Log.d("FileBrowserActivity.fill()", "Called");
        directoryEntries.clear();

        int pathLength = currentDirectory.getAbsolutePath().length();
        for (File file : files)
        {
            DownloadedFile df = new DownloadedFile();
            df.setDisplayPath(file.getAbsolutePath().substring(pathLength+ 1));
            df.setFullPath(file.getAbsolutePath());
            directoryEntries.add(df);
        }
        Collections.sort(directoryEntries);

    }

}
