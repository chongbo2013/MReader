package com.midcore.reader.reader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.midcore.reader.Constant;
import com.midcore.reader.db.BookDBHelper;
import com.midcore.reader.exception.BookException;
import com.midcore.reader.exception.ErrorCode;
import com.midcore.reader.model.Book;
import com.midcore.reader.reader.Reader.InvalidateListener;
import com.midcore.reader.reader.settings.ReadSettings;
import com.midcore.reader.reader.settings.SettingsObserver;
import com.midcore.reader.reader.settings.Theme;
import com.midcore.reader.utils.IOUtils;
import com.midcore.reader.view.BookReadView;

public class BookPageManager implements SettingsObserver, InvalidateListener {
	
	final static String TAG = Constant.TAG;
	
	Book mBook;
	File mBookFile;
	long mBookLength;
	long mCurrentOffset;
	String mCharset;
	
	final BookDBHelper mDbHelper;
	final Context mContext;
	
	Reader mReader;
	
	final BookReadView mView;
	
	int mWidth = -1, mHeight = -1;

	public BookPageManager(Context context, BookReadView v) {
		mContext = context;
		mDbHelper = BookDBHelper.get(context);
		mView = v;
	}
	
	public Book getBook() {
		return mBook;
	}
	
	public void openBook(Book book) {
		if (mBook != null && !mBook.equals(book)) {
			destroy();
		}
		ReadSettings.addSettingsObserver(this);
		mBook = book;
		mReader = Reader.createReader(mContext, mBook);
	}
	
	public boolean openBookInternal() throws BookException {
		Book book = mBook;
		if (book == null) {
			throw new BookException(ErrorCode.FILE_NOT_EXIST);
		}
		File file = new File(book.file); 
		if (book.isPreset() && !file.exists()) {
			try {
				file.getParentFile().mkdirs();
				IOUtils.copy(mContext.getAssets().open(book.getPresetFile()), new FileOutputStream(file));
			} catch (IOException e) {
				Log.d(TAG, "copy preset book", e);
			}
		}
		if (!file.exists()) {
			throw new BookException(ErrorCode.FILE_NOT_EXIST);
		}
		try {
			mReader.setInvalidateListener(this);
			if (mWidth > 0 && mHeight > 0) {
				mReader.setSize(mWidth, mHeight);
			}
			if (mReader.loadBook(mBook)) {
				if (mDbHelper.insertBook(mBook) == -1) {
					mDbHelper.updateReadTime(mBook);
				}
				return true;
			}
		} catch (Exception e) {
			throw new BookException(ErrorCode.BOOK_LOAD_FAIL, e);
		}
		return false;
	}
	
	public boolean isBookLoaded() {
		return mReader != null && mReader.isBookLoaded();
	}
	
	public void saveReadPosition() {
		if (mReader != null && mReader.isBookLoaded() && mBook != null) {
			mBook.readPosition = mReader.getCurrentOffset();
			mDbHelper.updateBook(mBook);
		}
	}
	
	public void destroy() {
		ReadSettings.removeSettingsObserver(this);
		mBook = null;
		if (mReader != null) {
			mReader.destroy();
		}
		mReader = null;
	}
	
	public void setSize(int w, int h) {
		mWidth = w;
		mHeight = h;
		if (mReader != null) {
			mReader.setSize(w, h);
		}
	}
	
	public void shift(boolean forward) {
		if (mReader != null) {
			if (forward) {
				mReader.switchToNextPage();
			} else {
				mReader.switchToPreviousPage();
			}
		}
	}
	
	public boolean hasPreviousPage() {
		return mReader != null && mReader.hasPreviousPage();
	}
	
	public boolean hasNextPage() {
		return mReader != null && mReader.hasNextPage();
	}
	
	public BitmapPage getCurrentPage() {
		return mReader != null ? mReader.getCurrentPage() : null;
	}
	
	public BitmapPage getPreviousPage() {
		return mReader != null ? mReader.getPreviousPage() : null;
	}
	
	public BitmapPage getNextPage() {
		return mReader != null ? mReader.getNextPage() : null;
	}
	
	public void invalidate() {
		if (mReader != null) {
			mReader.invalidatePages();
		}
	}
	
	public List<Chapter> getChapters() {
		return mReader != null ? mReader.getChapters() : null;
	}
	
	public Chapter getCurrentChapter() {
		return mReader != null ? mReader.getCurrentChapter() : null;
	}
	
	public void seekToChapter(Chapter chapter) {
		if (mReader != null) {
			mReader.seekToChapter(chapter);
			mView.invalidate();
		}
	}
	
	public void seekTo(float progress) {
		if (mReader != null) {
			mReader.seekTo(progress);
			mView.invalidate();
		}
	}
	
	public float getCurrentProgress() {
		return mReader != null ? mReader.getCurrentProgress() : 0;
	}
	
	public void seekToPreviousChapter() {
		if (mReader != null) {
			mReader.seekToPreviousChapter();
			mView.invalidate();
		}
	}
	
	public void seekToNextChapter() {
		if (mReader != null) {
			mReader.seekToNextChapter();
			mView.invalidate();
		}
	}
	
	public void reset() {
	}

	@Override
	public void onChange(String key, Object oldValue, Object newValue) {
		if (mReader != null) {
			if (ReadSettings.FONT_SIZE.equals(key)) {
				mReader.setFontSize((Integer) newValue);
			} else if (ReadSettings.THEME.equals(key)) {
				mReader.setTheme((Theme) newValue);
			}
			mView.invalidate();
		}
	}
	
	@Override
	public void onInvalidate() {
		invalidate();
		mView.postInvalidate();
	}
	
	public void onPause() {
		saveReadPosition();
		if (mReader != null) {
			mReader.onPause();
		}
	}
	
	public void onResume() {
		if (mReader != null) {
			mReader.onResume();
		}
	}
}
