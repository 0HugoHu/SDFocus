package com.sdzx.news.views;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 16-1-29.
 */
public class BmpDataClass
{
    public Bitmap bitmap;
    public String path;
    public BmpDataClass(Bitmap bmp, String pth)
    {
        bitmap = bmp;
        path = pth;
    }
    public void delete()
    {
        bitmap = null;
        path = null;
    }
}