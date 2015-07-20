package com.midcore.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.glview.view.View;
import com.glview.view.View.OnClickListener;
import com.glview.widget.LinearLayout;
import com.glview.widget.SeekBar;
import com.glview.widget.SeekBar.OnSeekBarChangeListener;
import com.midcore.reader.R;
import com.midcore.reader.activity.ChaptersDialog;

public class BottomMenuLayout extends LinearLayout implements OnClickListener, OnSeekBarChangeListener {
	
	private View mChapterIcon;
	private View mChapterPrevious;
	private View mChapterNext;
	private SeekBar mChapterSeek;
	
	private View mBrightMenuIcon;
	private View mPageStyleMenuIcon;
	private View mFontMenuIcon;
	
	ChaptersDialog mChaptersDialog;
	
	ReaderLayout mReaderLayout;
	BookReadView mBookReadView;

	public BottomMenuLayout(Context context) {
		super(context);
	}

	public BottomMenuLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BottomMenuLayout(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public BottomMenuLayout(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}
	
	public void setReaderLayout(ReaderLayout readerLayout) {
		mReaderLayout = readerLayout;
		mBookReadView = readerLayout.getBookReadView();
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mChapterIcon = findViewById(R.id.menu_chapter);
		mChapterIcon.setOnClickListener(this);
		mChapterPrevious = findViewById(R.id.chapter_previous);
		mChapterPrevious.setOnClickListener(this);
		mChapterNext = findViewById(R.id.chapter_next);
		mChapterNext.setOnClickListener(this);
		mChapterSeek = (SeekBar) findViewById(R.id.chapter_seek);
		mChapterSeek.setOnSeekBarChangeListener(this);
		
		mBrightMenuIcon = findViewById(R.id.menu_bright);
		mBrightMenuIcon.setOnClickListener(this);
		
		mPageStyleMenuIcon = findViewById(R.id.menu_pagestyle);
		mPageStyleMenuIcon.setOnClickListener(this);
		
		mFontMenuIcon = findViewById(R.id.menu_font);
		mFontMenuIcon.setOnClickListener(this);
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		dismissChaptersDialog();
	}
	
	public void show() {
		Fade.fadeIn(this, Fade.BOTTOM);
		loadCurrentProgress();
	}
	
	public void hide() {
		Fade.fadeOut(this, Fade.BOTTOM);
	}
	
	private void loadCurrentProgress() {
		mChapterSeek.setProgress((int) (mBookReadView.getBookPageManager().getCurrentProgress() * mChapterSeek.getMax()));
	}
	
	private void dismissChaptersDialog() {
		if (mChaptersDialog != null) {
			mChaptersDialog.dismiss();
			mChaptersDialog = null;
		}
	}
	
	private void showChaptersDialog() {
		dismissChaptersDialog();
		postToAndroid(new Runnable() {
			@Override
			public void run() {
				mChaptersDialog = new ChaptersDialog(getContext(), mBookReadView.getBookPageManager());
				mChaptersDialog.show();
			}
		});
		mReaderLayout.hideMenu();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event) || true;
	}
	
	@Override
	public void onClick(View v) {
		if (v == mChapterIcon) {
			showChaptersDialog();
		} else if (v == mChapterPrevious) {
			mBookReadView.getBookPageManager().seekToPreviousChapter();
			loadCurrentProgress();
		} else if (v == mChapterNext) {
			mBookReadView.getBookPageManager().seekToNextChapter();
			loadCurrentProgress();
		} else if (v == mBrightMenuIcon) {
			mReaderLayout.showBrightMenu();
		} else if (v == mPageStyleMenuIcon) {
			mReaderLayout.showPageStyleMenu();
		} else if (v == mFontMenuIcon) {
			mReaderLayout.showFontMenu();
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		int progress = seekBar.getProgress();
		int max = seekBar.getMax();
		float p = ((float) progress) / max;
		mBookReadView.getBookPageManager().seekTo(p);
	}

}
