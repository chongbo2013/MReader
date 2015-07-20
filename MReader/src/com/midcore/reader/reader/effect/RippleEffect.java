package com.midcore.reader.reader.effect;

import android.content.Context;
import android.view.MotionEvent;

import com.glview.graphics.RectF;
import com.glview.graphics.shader.RippleShader;
import com.glview.hwui.GLCanvas;
import com.glview.hwui.GLPaint;
import com.glview.view.View;
import com.midcore.reader.reader.BitmapPage;

public class RippleEffect extends PageSwitchEffect {
	
	RectF mTmpRect = new RectF();
	
	RippleShader mShader = new RippleShader();
	
	GLPaint mPaint = new GLPaint();
	
	public RippleEffect(Context context, View view) {
		super(context, view);
		mPaint.setShader(mShader);
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		return super.onDown(e);
	}
	
	@Override
	void innerDraw(GLCanvas canvas, BitmapPage page1, BitmapPage page2, boolean forward) {
		float progress = mCurrentDistance / mWidth;
		
		if (forward) {
			mShader.setProgress(1 - progress);
			mTmpRect.set(mRect);
			mPaint.setAlpha(255);
			canvas.drawBitmap(page2.getBitmap(), null, mTmpRect, mPaint);
			mPaint.setAlpha((int) (progress * 255));
			canvas.drawBitmap(page1.getBitmap(), null, mTmpRect, mPaint);
		} else {
			mShader.setProgress(progress);
			mTmpRect.set(mRect);
			mPaint.setAlpha(255);
			canvas.drawBitmap(page1.getBitmap(), null, mTmpRect, mPaint);
			mPaint.setAlpha((int) (progress * 255));
			canvas.drawBitmap(page2.getBitmap(), null, mTmpRect, mPaint);
		}
	}

}
