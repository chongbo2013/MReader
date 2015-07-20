package com.midcore.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.glview.hwui.GLCanvas;
import com.glview.view.GestureDetector.OnGestureListener;
import com.glview.view.View;
import com.glview.widget.Toast;
import com.midcore.reader.Constant;
import com.midcore.reader.R;
import com.midcore.reader.reader.BitmapPage;
import com.midcore.reader.reader.BookPageManager;
import com.midcore.reader.reader.effect.LayoverEffect;
import com.midcore.reader.reader.effect.PageSwitchEffect;
import com.midcore.reader.reader.effect.PageSwitchEffect.FlingState;
import com.midcore.reader.reader.effect.PageSwitchEffect.PageSwitchListener;
import com.midcore.reader.reader.effect.PageSwitchEffect.SwitchState;
import com.midcore.reader.reader.settings.PageStyle;
import com.midcore.reader.reader.settings.ReadSettings;
import com.midcore.reader.reader.settings.SettingsObserver;

public class BookReadView extends View implements PageSwitchListener, OnGestureListener, SettingsObserver {
	
	private final static String TAG = Constant.TAG;
	
	private BookPageManager mBookPageManager;
	
	private PageSwitchEffect mPageSwitchEffect;
	
	private PageStyle mPageStyle;
	
	boolean mTouched = false;
	
	public BookReadView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BookReadView(Context context, AttributeSet attrs, int defStyleAttr) {
		this(context, attrs, defStyleAttr, 0);
	}
	
	public BookReadView(Context context, AttributeSet attrs, int defStyleAttr,
			int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		
		resetPageStyle();
	}
	
	public void setBootPageManager(BookPageManager m) {
		mBookPageManager = m;
	}
	
	public BookPageManager getBookPageManager() {
		return mBookPageManager;
	}
	
	private void resetPageStyle() {
		mPageStyle = ReadSettings.getPageStyle(getContext());
		try {
			mPageSwitchEffect = mPageStyle.target.getConstructor(Context.class, View.class).newInstance(getContext(), this);
		} catch (Exception e) {
			Log.w(TAG, "make instance of pageStyle[" + mPageStyle.key + "] failed!", e);
			mPageSwitchEffect = new LayoverEffect(getContext(), this);
		}
		mPageSwitchEffect.setPageSwitchListener(this);
		if (getWidth() > 0 && getHeight() > 0) {
			mPageSwitchEffect.setSize(getWidth(), getHeight());
		}
	}
	
	public boolean isInSwitching() {
		return mPageSwitchEffect.isInSwitching();
	}
	
	@Override
	protected void onLayout(boolean changeSize, int left, int top, int right,
			int bottom) {
		super.onLayout(changeSize, left, top, right, bottom);
		mBookPageManager.setSize(getWidth(), getHeight());
		mPageSwitchEffect.setSize(getWidth(), getHeight());
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		ReadSettings.addSettingsObserver(this);
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mBookPageManager.destroy();
		ReadSettings.removeSettingsObserver(this);
	}
	
	private void switchToPreviousPage() {
		if (mBookPageManager.hasPreviousPage()) {
			mPageSwitchEffect.startSwitch(false);
			invalidate();
		} else {
			Toast.showShortToast(getContext(), R.string.already_first_page);
		}
	}
	
	private void switchToNextPage() {
		if (mBookPageManager.hasNextPage()) {
			mPageSwitchEffect.startSwitch(true);
			invalidate();
		} else {
			Toast.showShortToast(getContext(), R.string.already_last_page);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mTouched = false;
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			break;
		default:
			break;
		}
		mPageSwitchEffect.onTouchEvent(event);
		invalidate();
		return false;
	}
	
	@Override
	protected void onDraw(GLCanvas canvas) {
		super.onDraw(canvas);
		BitmapPage page = mBookPageManager.getCurrentPage();
		if (!mPageSwitchEffect.isInSwitching()) {
			if (page != null && page.isValid()) {
				canvas.drawBitmap(page.getBitmap(), 0, 0, null);
			}
		} else {
			boolean forward = mPageSwitchEffect.getSwitchState() == SwitchState.TO_NEXT;
			BitmapPage next = forward ? mBookPageManager.getNextPage() : mBookPageManager.getPreviousPage();
			if (mPageSwitchEffect.draw(canvas, page, next, forward)) {
				invalidate();
			}
		}
	}
	
	private void shift(boolean forward) {
		mBookPageManager.shift(forward);
		invalidate();
	}

	@Override
	public void onSwitchStarted(SwitchState state) {
		invalidate();
	}

	@Override
	public void onSwitchFinished(SwitchState state, FlingState flingState) {
		if (flingState == FlingState.BACKWARD) {
			mBookPageManager.invalidate();
			invalidate();
			return;
		}
		if (state == SwitchState.TO_NEXT) {
			shift(true);
		} else if (state == SwitchState.TO_PREVIOUS) {
			shift(false);
		}
	}

	@Override
	public void onSwitching(SwitchState state) {
		invalidate();
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return mPageSwitchEffect.onDown(e);
	}

	@Override
	public void onShowPress(MotionEvent e) {
		mPageSwitchEffect.onShowPress(e);
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		float x = e.getX();
		if (x < getWidth() / 2) {
			switchToPreviousPage();
			mPageSwitchEffect.onFling(e, e, 1, 0);
			return true;
		} else if (x > getWidth() / 2) {
			switchToNextPage();
			mPageSwitchEffect.onFling(e, e, - 1, 0);
			return true;
		}
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		if (!mPageSwitchEffect.isInSwitching() && !mTouched) {
			if (distanceX > 0) {
				switchToNextPage();
			} else if (distanceX < 0) {
				switchToPreviousPage();
			}
		}
		mTouched = true;
		if (mPageSwitchEffect.isInSwitching()) {
			invalidate();
			return mPageSwitchEffect.onScroll(e1, e2, distanceX, distanceY);
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		mPageSwitchEffect.onLongPress(e);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return mPageSwitchEffect.onFling(e1, e2, velocityX, velocityY);
	}

	@Override
	public void onChange(String key, Object oldValue, Object newValue) {
		if (ReadSettings.PAGE_STYLE.equals(key)) {
			resetPageStyle();
		}
	}

}
