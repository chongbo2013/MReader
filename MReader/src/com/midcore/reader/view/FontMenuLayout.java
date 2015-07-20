package com.midcore.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.glview.view.LayoutInflater;
import com.glview.view.View;
import com.glview.view.View.OnClickListener;
import com.glview.view.View.OnLongClickListener;
import com.glview.view.View.OnTouchListener;
import com.glview.view.ViewGroup;
import com.glview.widget.LinearLayout;
import com.glview.widget.TextView;
import com.midcore.reader.R;
import com.midcore.reader.reader.settings.ReadSettings;
import com.midcore.reader.reader.settings.Theme;

public class FontMenuLayout extends LinearLayout implements OnClickListener, OnLongClickListener, OnTouchListener {
	
	private View mFontSizeMinus;
	private View mFontSizePlus;
	private TextView mFontSizeText;
	
	int mFontSize = -1;
	
	private ViewGroup mFontTheme;
	private ReaderHorizontalScrollView mFontThemeScroll;
	
	boolean mFontThemeLoaded = false;

	public FontMenuLayout(Context context) {
		super(context);
	}

	public FontMenuLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FontMenuLayout(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public FontMenuLayout(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mFontSizeMinus = findViewById(R.id.font_size_minus);
		mFontSizeMinus.setOnClickListener(this);
		mFontSizeMinus.setOnLongClickListener(this);
		mFontSizeMinus.setOnTouchListener(this);
		mFontSizePlus = findViewById(R.id.font_size_plus);
		mFontSizePlus.setOnClickListener(this);
		mFontSizePlus.setOnLongClickListener(this);
		mFontSizePlus.setOnTouchListener(this);
		mFontSizeText = (TextView) findViewById(R.id.font_size_value);
		
		mFontTheme = (ViewGroup) findViewById(R.id.theme);
		mFontThemeScroll = (ReaderHorizontalScrollView) findViewById(R.id.theme_scroll);
	}

	public void show() {
		Fade.fadeIn(this, Fade.BOTTOM);
		mFontSizeText.setText(String.valueOf(ReadSettings.getFontSize(getContext())));
		
		if (!mFontThemeLoaded) {
			for (Theme theme : Theme.values()) {
				View v = LayoutInflater.from(getContext()).inflate(R.layout.layout_menu_theme_item, mFontTheme, false);
				v.setOnClickListener(mThemeItemClickListener);
				TextView textView = (TextView) v.findViewById(R.id.content);
				textView.setText(theme.name);
				textView.setTextColor(theme.textColor);
				if (theme.background > 0) {
					textView.setBackgroundResource(theme.background);
				} else {
					textView.setBackgroundColor(theme.backgroundColor);
				}
				
				v.setTag(theme);
				mFontTheme.addView(v);
			}
			selectThemeItem();
			mFontThemeLoaded = true;
		}
	}
	
	public void hide() {
		Fade.fadeOut(this, Fade.BOTTOM);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event) || true;
	}
	
	@Override
	public void onClick(View v) {
		if (v == mFontSizeMinus) {
			changeFontSize(false);
		} else if (v == mFontSizePlus) {
			changeFontSize(true);
		}
	}

	private void changeFontSize(boolean plus) {
		if (mFontSize == -1) {
			mFontSize = ReadSettings.getFontSize(getContext());
		}
		mFontSize = ReadSettings.checkFontSize(plus ? mFontSize + 1 : mFontSize - 1);
		mFontSizeText.setText(String.valueOf(mFontSize));
		removeCallbacks(mChangeFontSizeRunnable);
		postDelayed(mChangeFontSizeRunnable, 200);
	}
	
	Runnable mChangeFontSizeRunnable = new Runnable() {
		@Override
		public void run() {
			ReadSettings.setFontSize(getContext(), mFontSize);
		}
	};
	
	@Override
	public boolean onLongClick(View v) {
		if (v == mFontSizeMinus) {
			mFontSizeMinusRunnable.run();
		} else if (v == mFontSizePlus) {
			mFontSizePlusRunnable.run();
		}
		return true;
	}
	
	Runnable mFontSizeMinusRunnable = new Runnable() {
		@Override
		public void run() {
			changeFontSize(false);
			postDelayed(this, 100);
		}
	};
	
	Runnable mFontSizePlusRunnable = new Runnable() {
		@Override
		public void run() {
			changeFontSize(true);
			postDelayed(this, 100);
		}
	};
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (v == mFontSizeMinus || v == mFontSizePlus) {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				removeCallbacks(mFontSizeMinusRunnable);
				removeCallbacks(mFontSizePlusRunnable);
			}
		}
		return false;
	}
	
	OnClickListener mThemeItemClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getTag() instanceof Theme) {
				ReadSettings.setTheme(getContext(), (Theme) v.getTag());
				selectThemeItem();
			}
		}
	};
	
	private void selectThemeItem() {
		Theme current = ReadSettings.getTheme(getContext());
		for (int i = 0; i < mFontTheme.getChildCount(); i ++) {
			View child = mFontTheme.getChildAt(i);
			if (current == child.getTag()) {
				child.setSelected(true);
				mFontThemeScroll.setSelectedView(child);
			} else {
				child.setSelected(false);
			}
		}
	}

}
