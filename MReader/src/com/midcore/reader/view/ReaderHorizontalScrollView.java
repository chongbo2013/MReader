package com.midcore.reader.view;

import android.content.Context;
import android.util.AttributeSet;

import com.glview.view.View;
import com.glview.widget.HorizontalScrollView;

public class ReaderHorizontalScrollView extends HorizontalScrollView {
	
	View mSelectedView;

	public ReaderHorizontalScrollView(Context context) {
		super(context);
	}

	public ReaderHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ReaderHorizontalScrollView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public ReaderHorizontalScrollView(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}
	
	public void setSelectedView(View v) {
		mSelectedView = v;
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		resetScrollPosition();
	}
	
	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		if (visibility == View.VISIBLE) {
			resetScrollPosition();
		}
	}
	
	private void resetScrollPosition() {
		int scrollX = 0;
		if (mSelectedView != null) {
			if (mSelectedView.getRight() > getRight() - getPaddingRight() - getPaddingLeft()) {
				scrollX = mSelectedView.getRight() - getRight() + getPaddingRight() + getPaddingLeft();
			}
		}
		smoothScrollTo(scrollX, 0);
	}

}
