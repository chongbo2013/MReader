package com.midcore.reader.view;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.glview.thread.Handler;
import com.glview.thread.Looper;
import com.glview.view.GestureDetector.OnGestureListener;
import com.glview.view.View;
import com.glview.widget.FrameLayout;
import com.midcore.reader.R;

public class ReaderLayout extends FrameLayout implements OnGestureListener {
	
	private BookReadView mBookReadView;
	
	private TopMenuLayout mTopMenu;
	private BottomMenuLayout mBottomMenu;
	private BrightMenuLayout mBrightMenu;
	private PageStyleLayout mPageStyleLayout;
	private FontMenuLayout mFontMenu;
	
	GestureDetector mGestureDetector;
	
	boolean mIsMenuShown = false;
	
	public ReaderLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mGestureDetector = new GestureDetector(context, this, new Handler(Looper.getMainLooper()));
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!mIsMenuShown) {
			mBookReadView.onTouchEvent(event);
		}
		mGestureDetector.onTouchEvent(event);
		return true;
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LOW_PROFILE);
		}
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mBookReadView = (BookReadView) findViewById(R.id.book_read_view);
		mTopMenu = (TopMenuLayout) findViewById(R.id.top_menu);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			try {
	            Class<?> c = Class.forName("com.android.internal.R$dimen");
	            Object obj = c.newInstance();
	            Field field = c.getField("status_bar_height");
	            int id = field.getInt(obj);
				int statusBarHeight = getContext().getResources().getDimensionPixelSize(id/*com.glview.GLViewR.dimen.navigation_bar_height*/);
				((MarginLayoutParams) mTopMenu.getLayoutParams()).topMargin = statusBarHeight;
			} catch (Exception e) {}
		}
		mBottomMenu = (BottomMenuLayout) findViewById(R.id.bottom_menu);
		mBottomMenu.setReaderLayout(this);
		mBrightMenu = (BrightMenuLayout) findViewById(R.id.bottom_menu_bright);
		mPageStyleLayout = (PageStyleLayout) findViewById(R.id.bottom_menu_pagestyle);
		mFontMenu = (FontMenuLayout) findViewById(R.id.bottom_menu_font);
	}
	
	public BookReadView getBookReadView() {
		return mBookReadView;
	}
	
	private void showMenu() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
		}
		postToAndroid(new Runnable() {
			@Override
			public void run() {
				((Activity) getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			}
		});
		mTopMenu.show();
		mBottomMenu.show();
		mIsMenuShown = true;
	}
	
	public void hideMenu() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LOW_PROFILE);
		}
		postToAndroid(new Runnable() {
			@Override
			public void run() {
				((Activity) getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			}
		});
		mTopMenu.hide();
		mBottomMenu.hide();
		mBrightMenu.hide();
		mPageStyleLayout.hide();
		mFontMenu.hide();
		mIsMenuShown = false;
	}
	
	public void showBrightMenu() {
		mTopMenu.hide();
		mBottomMenu.hide();
		mBrightMenu.show();
		mPageStyleLayout.hide();
		mFontMenu.hide();
	}
	
	public void showPageStyleMenu() {
		mTopMenu.hide();
		mBottomMenu.hide();
		mBrightMenu.hide();
		mPageStyleLayout.show();
		mFontMenu.hide();
	}

	public void showFontMenu() {
		mTopMenu.hide();
		mBottomMenu.hide();
		mBrightMenu.hide();
		mPageStyleLayout.hide();
		mFontMenu.show();
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int keyCode = event.getKeyCode();
		if (mIsMenuShown && (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ESCAPE)) {
			if (KeyEvent.ACTION_UP == event.getAction()) {
				hideMenu();
			}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		if (mIsMenuShown) {
			return true;
		}
		return mBookReadView.onDown(e);
	}

	@Override
	public void onShowPress(MotionEvent e) {
		if (mIsMenuShown) {
			return;
		}
		mBookReadView.onShowPress(e);
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		if (mIsMenuShown) {
			hideMenu();
			return true;
		}
		if (e.getX() > getWidth() / 3 && e.getX() < getWidth() / 3 * 2) {
			showMenu();
			return true;
		}
		return mBookReadView.onSingleTapUp(e);
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		if (mIsMenuShown) {
			return true;
		}
		return mBookReadView.onScroll(e1, e2, distanceX, distanceY);
	}

	@Override
	public void onLongPress(MotionEvent e) {
		if (mIsMenuShown) {
			return;
		}
		mBookReadView.onLongPress(e);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (mIsMenuShown) {
			return true;
		}
		return mBookReadView.onFling(e1, e2, velocityX, velocityY);
	}

}
