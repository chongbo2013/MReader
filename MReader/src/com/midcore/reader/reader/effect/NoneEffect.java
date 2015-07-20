package com.midcore.reader.reader.effect;

import android.content.Context;
import android.view.MotionEvent;

import com.glview.hwui.GLCanvas;
import com.glview.view.View;
import com.midcore.reader.reader.BitmapPage;

public class NoneEffect extends PageSwitchEffect {

	public NoneEffect(Context context, View view) {
		super(context, view);
	}
	
	@Override
	public void backward() {
	}
	
	@Override
	protected boolean onTouchUp(MotionEvent event) {
		if (isInSwitching()) {
			stopSwitching();
		}
		return true;
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (isInSwitching()) {
			stopSwitching();
		}
		return true;
	}
	
	@Override
	void innerDraw(GLCanvas canvas, BitmapPage page1, BitmapPage page2, boolean forward) {
		canvas.drawBitmap(page1.getBitmap(), null, mRect, null);
	}

}
