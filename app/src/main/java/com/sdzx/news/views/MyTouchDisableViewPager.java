package com.sdzx.news.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * Created by ww on 2016/1/1.
 */
public class MyTouchDisableViewPager extends ViewPager {



    private int mTouchSlop;
    private int aaa;
    private float mLastMotionX = 0;
    private float mLastMotionY = 0;

    private int mMoveCount = 0;

    /**
     * @param pContext
     */
    public MyTouchDisableViewPager(Context pContext) {
        this(pContext, null);
    }

    /**
     * @param pContext
     * @param pAttrs
     */
    public MyTouchDisableViewPager(Context pContext, AttributeSet pAttrs) {
        super(pContext, pAttrs);
        final ViewConfiguration _ViewConfiguration = ViewConfiguration.get(pContext);
        mTouchSlop = _ViewConfiguration.getScaledTouchSlop();
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent pEv) {

        return false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
            return false;
    }


}
