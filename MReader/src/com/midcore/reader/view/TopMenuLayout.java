package com.midcore.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.glview.widget.FrameLayout;

public class TopMenuLayout extends FrameLayout {

	public TopMenuLayout(Context context) {
		super(context);
	}

	public TopMenuLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TopMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public TopMenuLayout(Context context, AttributeSet attrs, int defStyleAttr,
			int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}
	
	public void show() {
		Fade.fadeIn(this, Fade.TOP);
	}
	
	public void hide() {
		Fade.fadeOut(this, Fade.TOP);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event) || true;
	}

}
