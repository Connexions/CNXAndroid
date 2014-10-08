/**
 * Copyright (c) 2014 Rice University
 *
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */
package org.cnx.android.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @author Ed Woodward
 */
public class CNXImageView extends ImageView
{
    public CNXImageView(Context context)
    {
        super(context);
    }

    public CNXImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CNXImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }


}
