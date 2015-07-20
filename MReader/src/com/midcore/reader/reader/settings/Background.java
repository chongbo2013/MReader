package com.midcore.reader.reader.settings;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class Background {
	
	Theme mTheme;
	
	Bitmap mBackgroundImage;
	boolean mTile;
	int mBackgroundColor;
	
	Paint mPaint;

	private Background(Context context, Theme theme) {
		mPaint = new Paint();
		mTile = theme.tile;
		if (theme.background > 0) {
			Drawable d = context.getResources().getDrawable(theme.background);
			if (d instanceof BitmapDrawable) {
				mBackgroundImage = ((BitmapDrawable) d).getBitmap();
				if (mTile) {
					mPaint.setShader(new BitmapShader(mBackgroundImage, TileMode.REPEAT, TileMode.REPEAT));
				}
			}
		}
		
		if (mBackgroundImage == null) {
			mBackgroundColor = theme.backgroundColor;
			mPaint.setColor(mBackgroundColor);
		}
	}
	
	public static Background create(Context context, Theme theme) {
		return new Background(context, theme);
	}
	
	public void draw(Canvas canvas, Rect rect) {
		if (mBackgroundImage != null && !mTile) {
			canvas.drawBitmap(mBackgroundImage, null, rect, mPaint);
		} else {
			canvas.drawRect(rect, mPaint);
		}
	}
}
