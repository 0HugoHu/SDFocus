package com.sdzx.news.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * 
 * @date 2013-11-23 下午11:49:26
 * @author JohnWatson
 * @version 1.0
 
 */
public class MyViewPager extends ViewPager {

	private int mTouchSlop;
	private int aaa;
	private float mLastMotionX = 0;
	private float mLastMotionY = 0;
	
	private int mMoveCount = 0;
	
	/**
	 * @param pContext
	 */
	public MyViewPager(Context pContext) {
		this(pContext, null);
	}

	/**
	 * @param pContext
	 * @param pAttrs
	 */
	public MyViewPager(Context pContext, AttributeSet pAttrs) {
		super(pContext, pAttrs);
		final ViewConfiguration _ViewConfiguration = ViewConfiguration.get(pContext);
		mTouchSlop = _ViewConfiguration.getScaledTouchSlop();
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent pEv) {
		return super.dispatchTouchEvent(pEv);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent pEv) {
		
		switch (pEv.getAction()) {

		case MotionEvent.ACTION_DOWN:

			mLastMotionX = pEv.getX(0);
			mLastMotionY = pEv.getY(0);
			
			break;

		case MotionEvent.ACTION_MOVE:

			mMoveCount += 1;
			
			if(mMoveCount <= 5){
				break;
			}
			
			int _XDiff = (int) Math.abs(pEv.getX(0) - mLastMotionX);
			int _YDiff = (int) Math.abs(pEv.getY(0) - mLastMotionY);

			int _XY_Diff = _XDiff * _XDiff + _YDiff * _YDiff;

			if (_XY_Diff <= mTouchSlop * mTouchSlop) {
				break;
			}
			
			if (_XDiff > _YDiff) {
				return true;
			}else{
				break;
			}
		case MotionEvent.ACTION_UP:
			mMoveCount = 0;
			break;

		default:
			break;
		}

		return super.onInterceptTouchEvent(pEv);
	}

}
