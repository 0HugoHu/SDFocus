package it.sephiroth.android.library.imagezoom.graphics;

import java.io.InputStream;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * Fast bitmap drawable. Does not support states. it only
 * support alpha and colormatrix
 * @author alessandro
 *
 */
public class FastBitmapDrawable extends Drawable implements IBitmapDrawable {

	protected Bitmap mBitmap;
	protected Paint mPaint;

	public FastBitmapDrawable( Bitmap b ) {
		mBitmap = b;
		mPaint = new Paint();
		mPaint.setDither( true );
		mPaint.setFilterBitmap( true );
	}
	
	public FastBitmapDrawable( Resources res, InputStream is ){
		this(BitmapFactory.decodeStream(is));
	}

	 
	public void draw( Canvas canvas ) {
		canvas.drawBitmap( mBitmap, 0.0f, 0.0f, mPaint );
	}

	 
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	 
	public void setAlpha( int alpha ) {
		mPaint.setAlpha( alpha );
	}

	 
	public void setColorFilter( ColorFilter cf ) {
		mPaint.setColorFilter( cf );
	}

	 
	public int getIntrinsicWidth() {
		return mBitmap.getWidth();
	}

	 
	public int getIntrinsicHeight() {
		return mBitmap.getHeight();
	}

	 
	public int getMinimumWidth() {
		return mBitmap.getWidth();
	}

	 
	public int getMinimumHeight() {
		return mBitmap.getHeight();
	}
	
	public void setAntiAlias( boolean value ){
		mPaint.setAntiAlias( value );
		invalidateSelf();
	}

	 
	public Bitmap getBitmap() {
		return mBitmap;
	}
}