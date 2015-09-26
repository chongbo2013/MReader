package com.midcore.reader.activity;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.glview.app.GLDialog;
import com.glview.content.GLContext;
import com.glview.graphics.drawable.BitmapDrawable;
import com.glview.graphics.drawable.Drawable;
import com.glview.graphics.shader.TileMode;
import com.glview.view.View;
import com.glview.widget.AdapterView;
import com.glview.widget.AdapterView.OnItemClickListener;
import com.glview.widget.ListView;
import com.glview.widget.TextView;
import com.midcore.reader.Constant;
import com.midcore.reader.R;
import com.midcore.reader.adapter.BookChapterListAdapter;
import com.midcore.reader.reader.BookPageManager;
import com.midcore.reader.reader.Chapter;
import com.midcore.reader.reader.settings.ReadSettings;
import com.midcore.reader.reader.settings.Theme;

public class ChaptersDialog extends GLDialog implements OnItemClickListener {
	
	final static String TAG = Constant.TAG;
	
	View mHeadView;
	TextView mTitleView;
	ListView mListView;
	
	BookChapterListAdapter mAdapter;
	
	BookPageManager mBookPageManager;
	List<Chapter> mChapters;
	Chapter mCurrentChapter;
	int mCurrentPosition = -1;
	
	Theme mTheme;
	boolean mEyeProtection;

	public ChaptersDialog(Context context, BookPageManager bookPageManager) {
		super(context, R.style.ChapterDialog);
		mTheme = ReadSettings.getTheme(getContext());
		mEyeProtection = ReadSettings.getEyeProtection(getContext());
		mBookPageManager = bookPageManager;
		mChapters = mBookPageManager.getChapters();
		mCurrentChapter = mBookPageManager.getCurrentChapter();
		mAdapter = new BookChapterListAdapter(context, mTheme);
		if (mChapters != null) {
			mAdapter.getList().addAll(mChapters);
			if (mAdapter.getCount() > 0 && ((Chapter) mAdapter.getItem(0)).header) {
				mAdapter.getList().remove(0);
			}
			if (mCurrentChapter != null) {
				mCurrentPosition = mAdapter.getList().indexOf(mCurrentChapter);
			}
			if (mCurrentPosition < 0) mCurrentPosition = 0;
			mAdapter.setCurrentPosition(mCurrentPosition);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setGLContentView(R.layout.layout_chapters);
	}
	
	@Override
	public void onAttached(View content) {
		super.onAttached(content);
		if (mTheme.background > 0) {
			Drawable drawable = GLContext.get().getResources().getDrawable(mTheme.background);
			if (drawable != null) {
				if (drawable instanceof BitmapDrawable && mTheme.tile) {
					((BitmapDrawable) drawable).setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
				}
				content.setBackground(drawable);
			}
		} else {
			content.setBackgroundColor(mTheme.backgroundColor);
		}
		if (mEyeProtection) {
			content.findViewById(R.id.eye_protection_view).setVisibility(View.VISIBLE);
		}
		mHeadView = content.findViewById(R.id.head_view);
		mHeadView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					dismiss();
				} catch(Throwable tr) {}
			}
		});
		mTitleView = (TextView) content.findViewById(R.id.title_view);
		mTitleView.setText(mBookPageManager.getBook().title);
		mTitleView.setTextColor(mTheme.textColor);
		mListView = (ListView) content.findViewById(R.id.chapter_list);
		mListView.setOnItemClickListener(this);
		mListView.setAdapter(mAdapter);
		if (mCurrentPosition >= 0) {
			mListView.setSelection(mCurrentPosition);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Chapter chapter = (Chapter) mAdapter.getItem(position);
		Log.d(TAG, "onItemClick:position=" + position + ", chapter=" + chapter);
		if (chapter != mCurrentChapter) {
			mBookPageManager.seekToChapter(chapter);
		}
		try {
			dismiss();
		} catch(Throwable tr) {}
	}

}
