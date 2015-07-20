package com.midcore.reader.reader;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;

import com.midcore.reader.Constant;
import com.midcore.reader.R;
import com.midcore.reader.model.Book;
import com.midcore.reader.reader.epub.EPubReader;
import com.midcore.reader.reader.settings.Background;
import com.midcore.reader.reader.settings.ReadSettings;
import com.midcore.reader.reader.settings.SettingsObserver;
import com.midcore.reader.reader.settings.Theme;
import com.midcore.reader.reader.txt.TxtReader;
import com.midcore.reader.reader.umd.UMDReader;

public abstract class Reader implements SettingsObserver {
	
	final static String TAG = Constant.TAG;
	
	protected int mWidth = -1, mHeight = -1;

	protected int mTextSize;
	protected Theme mTheme;
	
	protected int mLineSpace;  //行间距
	protected int mLineHeight;
	protected int mTitleLineHeight;
	protected int mTitleMarginTop;
	protected int mParagraphSpace;
	protected int mLeftMargin;
	protected int mRightMargin;
	protected int mTopMargin;
	protected int mBottomMargin;
	
	protected int mInfoLineHeight;
	protected int mInfoBottomMargin;
	
	protected boolean mBookLoaded = false;
	
	protected Paint mInfoPaint;
	protected Paint mInfoBatteryPaint;
	
	protected Paint mPaint;
	protected Paint mTitlePaint;
	
	protected Background mBackground;
	protected Rect mRect = new Rect();
	
	protected Paint mClearPaint;
	
	protected final Context mContext;
	
	protected Book mBook;
	protected String mBookTitle;
	
	BroadcastReceiver mBatteryReceiver;
	BroadcastReceiver mTimeReceiver;
	protected int mBatteryValue = 0;
	protected int mBatteryMax = 100;
	RectF mBatteryRect = new RectF();
	int mBatteryLength, mBatteryHeight, mBatteryHeadLength;
	protected DateFormat mDateFormat = new SimpleDateFormat("HH:mm  ");
	protected DecimalFormat mProgressFormat = new java.text.DecimalFormat("0.00%");
	
	InvalidateListener mInvalidateListener;
	
	public Reader(Context context) {
		mContext = context;
		mTextSize = ReadSettings.getFontSize(mContext);
		mTheme = ReadSettings.getTheme(mContext);
		
		Resources res = context.getResources();
		mLeftMargin = res.getDimensionPixelSize(R.dimen.reader_margin_left);
		mRightMargin = res.getDimensionPixelSize(R.dimen.reader_margin_right);
		mTopMargin = res.getDimensionPixelSize(R.dimen.reader_margin_top);
		mBottomMargin = res.getDimensionPixelSize(R.dimen.reader_margin_bottom);
		mTitleMarginTop = res.getDimensionPixelSize(R.dimen.reader_title_margin_top);
		mLineSpace = res.getDimensionPixelSize(R.dimen.reader_line_space);
		mParagraphSpace = res.getDimensionPixelSize(R.dimen.reader_paragraph_space);
		
		mInfoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mInfoPaint.setTextSize(res.getDimensionPixelSize(R.dimen.font_size_small));
		mInfoPaint.setColor(mTheme.textColor);
		mInfoLineHeight = mInfoPaint.getFontMetricsInt(null);
		mInfoBottomMargin = res.getDimensionPixelSize(R.dimen.reader_bottom_info_margin_bottom);
		mInfoBatteryPaint = new Paint();
		mInfoBatteryPaint.setColor(mTheme.textColor);
		mBatteryLength = res.getDimensionPixelSize(R.dimen.reader_battery_length);
		mBatteryHeight = res.getDimensionPixelSize(R.dimen.reader_battery_height);
		mBatteryHeadLength = res.getDimensionPixelSize(R.dimen.reader_battery_head_length);
		
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(mTheme.textColor);
		mPaint.setTextSize(mTextSize);
		mTitlePaint = new Paint(mPaint);
		mTitlePaint.setTextSize(mTextSize * 1.2f);
		mTitlePaint.setStrokeWidth(2f);
		mTitlePaint.setTypeface(Typeface.DEFAULT_BOLD);
		
		mLineHeight = mPaint.getFontMetricsInt(null);
		mTitleLineHeight = mTitlePaint.getFontMetricsInt(null);
		
		mBackground = Background.create(mContext, mTheme);
		
		mClearPaint = new Paint();
		mClearPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		
		ReadSettings.addSettingsObserver(this);
		
		registerReceivers();
	}
	
	private void registerReceivers() {
		if (mBatteryReceiver == null) {
			mBatteryReceiver = new BatteryBroadcastReceiver();
			Intent intent = mContext.registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
			invalidateBattery(intent);
		}
		if (mTimeReceiver == null) {
			mTimeReceiver = new TimeBroadcastReceiver();
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
			intentFilter.addAction(Intent.ACTION_TIME_TICK);
			intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
			mContext.registerReceiver(mTimeReceiver, intentFilter);
			if (mInvalidateListener != null) {
				mInvalidateListener.onInvalidate();
			}
		}
	}
	
	private void unregisterReceiver() {
		if (mBatteryReceiver != null) {
			mContext.unregisterReceiver(mBatteryReceiver);
			mBatteryReceiver = null;
		}
		if (mTimeReceiver != null) {
			mContext.unregisterReceiver(mTimeReceiver);
			mTimeReceiver = null;
		}
	}
	
	private void invalidateBattery(Intent intent) {
		if (intent != null) {
			int batteryValue = intent.getIntExtra("level", 0);
			int batteryMax = intent.getIntExtra("scale", 100);
			if (mBatteryValue != batteryValue || mBatteryMax != batteryMax) {
				mBatteryValue = batteryValue;
				mBatteryMax = batteryMax;
				if (mInvalidateListener != null) {
					mInvalidateListener.onInvalidate();
				}
			}
		}
	}
	
	class BatteryBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
				Log.d(TAG, "receive battery change level:" + intent.getIntExtra("level", 0) + ", scale:" + intent.getIntExtra("scale", 100));
				invalidateBattery(intent);
			}
		}
	};
	
	class TimeBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "receive time change action=" + intent.getAction());
			if (mInvalidateListener != null) {
				mInvalidateListener.onInvalidate();
			}
		}
	};
	
	public void onPause() {
		unregisterReceiver();
	}
	
	public void onResume() {
		registerReceivers();
	}
	
	public void setInvalidateListener(InvalidateListener l) {
		mInvalidateListener = l;
	}
	
	public static Reader createReader(Context context, Book book) {
		if ("txt".equalsIgnoreCase(book.type)) {
			return new TxtReader(context);
		} else if ("umd".equalsIgnoreCase(book.type)) {
			return new UMDReader(context);
		} else if ("epub".equalsIgnoreCase(book.type)) {
			return new EPubReader(context);
		}
		return null;
	}
	
	protected void draw(Canvas canvas, Page page) {
		canvas.drawPaint(mClearPaint);
		if (mBackground != null) {
			mRect.set(0, 0, mWidth, mHeight);
			mBackground.draw(canvas, mRect);
		}
	}
	
	protected void drawInfos(Canvas canvas, String topLeft, String topRight, double progress) {
		if (topLeft != null) {
			canvas.drawText(topLeft, mLeftMargin, mInfoLineHeight, mInfoPaint);
		}
		if (topRight != null) {
			canvas.drawText(topRight, mWidth - mRightMargin - mInfoPaint.measureText(topRight), mInfoLineHeight, mInfoPaint);
		}
		canvas.drawText(mProgressFormat.format(progress), mLeftMargin, mHeight - mInfoBottomMargin, mInfoPaint);
		drawTimeAndBattery(canvas);
	}
	
	protected void drawTimeAndBattery(Canvas canvas) {
		if (mBatteryMax == 0) {
			mBatteryMax = 100;
		}
        float p = mBatteryValue / (float) mBatteryMax;
        if (p > 1) p = 1;
        mBatteryRect.set(0, 0, mBatteryLength, mBatteryHeight);
        mBatteryRect.offset(mWidth - mRightMargin - mBatteryRect.width() - mBatteryHeadLength, mHeight - mInfoBottomMargin - mBatteryRect.height());
        mInfoBatteryPaint.setStyle(Style.STROKE);
        canvas.drawRect(mBatteryRect, mInfoBatteryPaint);
        mBatteryRect.set(mBatteryRect.left + 2, mBatteryRect.top + 2, mBatteryRect.right - 1, mBatteryRect.bottom - 1);
        mBatteryRect.right = mBatteryRect.left + mBatteryRect.width() * p;
        mInfoBatteryPaint.setStyle(Style.FILL);
        canvas.drawRect(mBatteryRect, mInfoBatteryPaint);
        mBatteryRect.set(0, 0, mBatteryHeadLength, mBatteryHeight / 5);
        mBatteryRect.offset(mWidth - mRightMargin - mBatteryRect.width(), mHeight - mInfoBottomMargin - (mBatteryHeight + mBatteryRect.height()) / 2);
        mInfoBatteryPaint.setStyle(Style.FILL);
        canvas.drawRect(mBatteryRect, mInfoBatteryPaint);
        String time = mDateFormat.format(new Date());
        canvas.drawText(time, mWidth - mRightMargin - mBatteryHeadLength - mBatteryLength - mInfoPaint.measureText(time), mHeight - mInfoBottomMargin, mInfoPaint);
	}
	
	public void setSize(int w, int h) {
		if (mWidth != w || mHeight != h) {
			mWidth = w;
			mHeight = h;
			invalidateChapters();
			invalidatePages();
		}
		if (mWidth <= 0 || mHeight <= 0) {
			throw new IllegalArgumentException("Page size should not be small than zero!");
		}
	}
	
	public void setFontSize(int fontSize) {
		if (mTextSize != fontSize) {
			mTextSize = fontSize;
			mPaint.setTextSize(fontSize);
			mTitlePaint.setTextSize(fontSize * 1.2f);
			mLineHeight = mPaint.getFontMetricsInt(null);
			mTitleLineHeight = mTitlePaint.getFontMetricsInt(null);
			invalidatePages();
			invalidateChapters();
		}
	}
	
	public void setTheme(Theme theme) {
		if (mTheme != theme) {
			mTheme = theme;
			mInfoPaint.setColor(mTheme.textColor);
			mInfoBatteryPaint.setColor(mTheme.textColor);
			mPaint.setColor(mTheme.textColor);
			mTitlePaint.setColor(mTheme.textColor);
			mBackground = Background.create(mContext, mTheme);
			invalidatePages();
		}
	}
	
	@Override
	public void onChange(String key, Object oldValue, Object newValue) {
		if (ReadSettings.FONT_SIZE.equals(key)) {
			setFontSize((Integer) newValue);
		} else if (ReadSettings.THEME.equals(key)) {
			setTheme((Theme) newValue);
		}
	}
	
	public int getContentWidth() {
		return mWidth - mLeftMargin - mRightMargin;
	}
	
	public int getContentHeight() {
		return mHeight - mTopMargin - mBottomMargin;
	}
	
	public boolean isBookLoaded() {
		return mBookLoaded;
	}
	
	protected int getTotalHeight(int titleLineCount, int lineCount, int paragraphCount) {
		return (titleLineCount > 0 ? mTitleMarginTop : 0) + mTitleLineHeight * titleLineCount + mLineHeight * lineCount + mParagraphSpace * paragraphCount + mLineSpace * (titleLineCount + lineCount + paragraphCount);
	}
	
	public void destroy() {
		ReadSettings.removeSettingsObserver(this);
		if (mBatteryReceiver != null) {
			mContext.unregisterReceiver(mBatteryReceiver);
			mBatteryReceiver = null;
		}
		System.gc();
		System.gc();
		System.runFinalization();
	}
	
	public boolean loadBook(Book book) throws IOException {
		return false;
	}
	
	public double getCurrentOffset() {
		return mBook.readPosition;
	}
	
	public void setCurrentOffset(double offset) {
		mBook.readPosition = offset;
	}
	
	public void switchToNextPage() {
	}
	
	public void switchToPreviousPage() {
	}
	
	public void invalidatePages() {}
	
	public void invalidateChapters() {}
	
	public boolean hasPreviousPage() {
		return false;
	}
	
	public boolean hasNextPage() {
		return false;
	}
	
	public BitmapPage getCurrentPage() {
		return null;
	}
	
	public BitmapPage getPreviousPage() {
		return null;
	}
	
	public BitmapPage getNextPage() {
		return null;
	}
	
	public List<Chapter> getChapters() {
		return null;
	}
	
	public Chapter getCurrentChapter() {
		return null;
	}
	
	public boolean seekToChapter(Chapter chapter) {
		return false;
	}
	
	/**
	 * 00.00%
	 * @param progress (0-1)
	 */
	public boolean seekTo(float progress) {
		return false;
	}
	
	public float getCurrentProgress() {
		return 0;
	}
	
	public boolean seekToPreviousChapter() {
		return false;
	}
	
	public boolean seekToNextChapter() {
		return false;
	}
	
	protected static String measureText(Paint paint, String text, int width) {
		if (width <= 0) {
			return text;
		}
		int w = width;
		if (paint.measureText(text, 0, text.length()) <= w)
			return text;

		int i;
		String str = null;
		for (i = text.length(); i > 0; i --) {
			str = text.substring(0, i) + "...";
			float w1 = paint.measureText(str);
			if (w1 < w) {
				break;
			}
		}
		return str;
	}
	
	public static interface InvalidateListener {
		void onInvalidate();
	}
}
