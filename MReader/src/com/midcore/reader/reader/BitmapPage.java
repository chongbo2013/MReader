package com.midcore.reader.reader;

import android.graphics.Bitmap.Config;

import com.glview.graphics.Bitmap;

public class BitmapPage {
	
	private Bitmap mBitmap;
	private boolean mValid = false;
	
	public Bitmap getBitmap() {
		return mBitmap;
	}
	
	public boolean isValid() {
		return mValid;
	}
	
	public void setValid(boolean valid) {
		mValid = valid;
		if (mValid) {
			mBitmap.invalidate();
		}
	}
	
	public void invalidate() {
		mValid = false;
	}

	public BitmapPage(int w, int h, Config config) {
		mBitmap = Bitmap.createBitmap(w, h, config);
	}
	
	public void recycle() {
		if (mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.recycle();
		}
	}
}
