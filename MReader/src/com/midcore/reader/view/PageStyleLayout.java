package com.midcore.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.glview.view.LayoutInflater;
import com.glview.view.View;
import com.glview.view.ViewGroup;
import com.glview.widget.FrameLayout;
import com.glview.widget.TextView;
import com.midcore.reader.R;
import com.midcore.reader.reader.settings.PageStyle;
import com.midcore.reader.reader.settings.ReadSettings;

public class PageStyleLayout extends FrameLayout {
	
	private ViewGroup mPageStyle;
	private ReaderHorizontalScrollView mPageStyleScroll;
	
	boolean mPageStyleLoaded = false;

	public PageStyleLayout(Context context) {
		super(context);
	}

	public PageStyleLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PageStyleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public PageStyleLayout(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		mPageStyle = (ViewGroup) findViewById(R.id.pagestyle);
		mPageStyleScroll = (ReaderHorizontalScrollView) findViewById(R.id.pagestyle_scroll);
	}
	
	public void show() {
		Fade.fadeIn(this, Fade.BOTTOM);
		
		if (!mPageStyleLoaded) {
			for (PageStyle style : PageStyle.values()) {
				View v = LayoutInflater.from(getContext()).inflate(R.layout.layout_menu_pagestyle_item, mPageStyle, false);
				v.setOnClickListener(mPageStyleItemClickListener);
				TextView textView = (TextView) v.findViewById(R.id.pagestyle_item);
				textView.setText(style.name);
				
				v.setTag(style);
				mPageStyle.addView(v);
			}
			selectPageStyleItem();
			mPageStyleLoaded = true;
		}
	}
	
	OnClickListener mPageStyleItemClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getTag() instanceof PageStyle) {
				ReadSettings.setPageStyle(getContext(), (PageStyle) v.getTag());
				selectPageStyleItem();
			}
		}
	};
	
	private void selectPageStyleItem() {
		PageStyle current = ReadSettings.getPageStyle(getContext());
		for (int i = 0; i < mPageStyle.getChildCount(); i ++) {
			View child = mPageStyle.getChildAt(i);
			if (current == child.getTag()) {
				child.setSelected(true);
				mPageStyleScroll.setSelectedView(child);
			} else {
				child.setSelected(false);
			}
		}
	}
	
	public void hide() {
		Fade.fadeOut(this, Fade.BOTTOM);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event) || true;
	}

}
