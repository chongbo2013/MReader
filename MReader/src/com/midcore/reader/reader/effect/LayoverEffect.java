package com.midcore.reader.reader.effect;

import android.content.Context;
import android.graphics.Color;

import com.glview.graphics.RectF;
import com.glview.graphics.shader.LinearGradient;
import com.glview.hwui.GLCanvas;
import com.glview.hwui.GLPaint;
import com.glview.view.View;
import com.midcore.reader.reader.BitmapPage;

public class LayoverEffect extends PageSwitchEffect {
	
	RectF mTmpRect = new RectF();
	
	final static int SHADOW_SIZE = 50;
	
	LinearGradient mShader = new LinearGradient(0, 0, SHADOW_SIZE, 0, 0x88000000, Color.TRANSPARENT);
	
	GLPaint mPaint = new GLPaint();
	
	public LayoverEffect(Context context, View view) {
		super(context, view);
		mPaint.setShader(mShader);
	}
	
	@Override
	protected int getStartPosition() {
		return - SHADOW_SIZE;
	}
	
	@Override
	void innerDraw(GLCanvas canvas, BitmapPage page1, BitmapPage page2, boolean forward) {
		if (forward) {
			mTmpRect.set(mRect);
			canvas.drawBitmap(page2.getBitmap(), null, mTmpRect, null);
			mTmpRect.offset(mCurrentDistance - mWidth, 0);
			canvas.drawBitmap(page1.getBitmap(), null, mTmpRect, null);
		} else {
			mTmpRect.set(mRect);
			canvas.drawBitmap(page1.getBitmap(), null, mTmpRect, null);
			mTmpRect.offset(mCurrentDistance - mWidth, 0);
			canvas.drawBitmap(page2.getBitmap(), null, mTmpRect, null);
		}
		canvas.translate(mCurrentDistance, 0);
		canvas.drawRect(0, 0, SHADOW_SIZE, mHeight, mPaint);
	}

}
