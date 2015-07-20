package com.midcore.reader.reader.effect;

import android.content.Context;
import android.graphics.Color;

import com.glview.hwui.GLCanvas;
import com.glview.hwui.GLPaint;
import com.glview.view.View;
import com.midcore.reader.reader.BitmapPage;
import com.midcore.reader.reader.effect.mesh.VerticalBlindsMesh;

public class VerticalBlindsEffect extends PageSwitchEffect {
	
	VerticalBlindsMesh mMesh;
	
	GLPaint mPaint = new GLPaint();

	public VerticalBlindsEffect(Context context, View view) {
		super(context, view);
		mPaint.setColor(Color.BLACK);
	}
	
	@Override
	public void setSize(int w, int h) {
		super.setSize(w, h);
		mMesh = new VerticalBlindsMesh(w, h);
	}

	@Override
	void innerDraw(GLCanvas canvas, BitmapPage page1, BitmapPage page2,
			boolean forward) {
		float p = mCurrentDistance / (getEndPosition() - getStartPosition()) * 2;
		mMesh.setCurrentPosition(p);
		canvas.drawRect(mRect.left, mRect.top, mRect.right, mRect.bottom, mPaint);
		if (forward) {
			if (p < 1) {
				canvas.drawBitmapMesh(page2.getBitmap(), mMesh, null);
			} else {
				canvas.drawBitmapMesh(page1.getBitmap(), mMesh, null);
			}
		} else {
			if (p < 1) {
				canvas.drawBitmapMesh(page1.getBitmap(), mMesh, null);
			} else {
				canvas.drawBitmapMesh(page2.getBitmap(), mMesh, null);
			}
		}
	}

}
