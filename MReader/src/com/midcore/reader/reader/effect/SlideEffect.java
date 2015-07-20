package com.midcore.reader.reader.effect;

import android.content.Context;
import android.graphics.Color;

import com.glview.graphics.RectF;
import com.glview.graphics.shader.LinearGradient;
import com.glview.hwui.GLCanvas;
import com.glview.hwui.GLPaint;
import com.glview.view.View;
import com.midcore.reader.reader.BitmapPage;

public class SlideEffect extends PageSwitchEffect {
	
	RectF mTmpRect = new RectF();
	
	LinearGradient mShader = new LinearGradient(0, 0, 30, 0, Color.GRAY, Color.TRANSPARENT);
	
	GLPaint mPaint = new GLPaint();
	public SlideEffect(Context context, View view) {
		super(context, view);
		mPaint.setShader(mShader);
	}
	
	@Override
	void innerDraw(GLCanvas canvas, BitmapPage page1, BitmapPage page2, boolean forward) {
		if (forward) {
			mTmpRect.set(mRect);
			mTmpRect.offset(mCurrentDistance, 0);
			canvas.drawBitmap(page2.getBitmap(), null, mTmpRect, null);
			mTmpRect.set(mRect);
			mTmpRect.offset(mCurrentDistance - mWidth, 0);
			canvas.drawBitmap(page1.getBitmap(), null, mTmpRect, null);
		} else {
			mTmpRect.set(mRect);
			mTmpRect.offset(mCurrentDistance, 0);
			canvas.drawBitmap(page1.getBitmap(), null, mTmpRect, null);
			mTmpRect.set(mRect);
			mTmpRect.offset(mCurrentDistance - mWidth, 0);
			canvas.drawBitmap(page2.getBitmap(), null, mTmpRect, null);
		}
	}

}
