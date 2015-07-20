package com.midcore.reader.reader.umd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.midcore.reader.Constant;
import com.midcore.reader.model.Book;
import com.midcore.reader.reader.Chapter;
import com.midcore.reader.reader.cache.LocalCache;
import com.midcore.reader.reader.txt.TxtChapter;
import com.midcore.reader.reader.txt.TxtReader;
import com.midcore.reader.utils.FileUtils;

public class UMDReader extends TxtReader {
	
	final static String TAG = Constant.TAG;
	
	private UMD mUMD;
	
	public UMDReader(Context context) {
		super(context);
	}
	
	@Override
	public boolean loadBook(Book book) throws IOException {
		mBookLoaded = false;
		mBook = book;
		UMD umd = null;
		Log.d(TAG, "loadUmd begin");
		mBookFile = new File(book.file);
		try {
			if (book.fileSign == mBookFile.length()) {
				umd = loadCache(book);
			}
			if (umd == null) {
				umd = new UMDDecoder().decode(new File(book.file));
				if (umd != null) {
					saveCache(book, umd);
				}
			}
		} catch (Exception e) {
			Log.w(TAG, "loadBook", e);
		}
		if (umd != null && !"error".equals(book.cover) && (book.cover == null || !new File(book.cover).exists())) {
			saveCover(book, umd);
		}
		mUMD = umd;
		Log.d(TAG, "loadUmd end");
		mBookLoaded = mUMD != null;
		if (mUMD != null) {
			if (mUMD.getTitle() != null && mUMD.getTitle().length() > 0) {
				book.title = mUMD.getTitle();
			}
			book.fileSign = mBookFile.length();
			
			mCurrentBlock = mUMD.getBlock();
			mBlocks.clear();
			mBlocks.add(mCurrentBlock);
			mCurrentBlockString = mUMD.getContent();
			mTotalLength = mCurrentBlock.length;
			mTotalStringLength = mCurrentBlock.stringLength;
			loadChapters();
		}
		return mBookLoaded;
	}
	
	private void loadChapters() {
		if (mBookLoaded) {
			for (TxtChapter chapter : mCurrentBlock.chapters) {
				if (getCurrentOffset() > chapter.offset) {
					mCurrentChapter = chapter;
				}
			}
			if (mCurrentChapter == null) {
				mCurrentChapter = mCurrentBlock.chapters.get(0);
			}
			mHasChapter = true;
			mCurrentChapterString = mCurrentBlockString.substring(mCurrentChapter.offset, mCurrentChapter.offset + mCurrentChapter.length);
		}
	}
	
	@Override
	public List<Chapter> getChapters() {
		if (!mBookLoaded) {
			return null;
		}
		List<Chapter> list = new ArrayList<Chapter>();
		list.addAll(mCurrentBlock.chapters);
		return list;
	}
	
	private UMD loadCache(Book book) {
		try {
			return (UMD) LocalCache.instance(mContext).readData(FileUtils.getBookCacheFile(book));
		} catch (Exception e) {
		}
		return null;
	}
	
	private void saveCache(Book book, UMD umd) {
		try {
			LocalCache.instance(mContext).writeData(FileUtils.getBookCacheFile(book), umd);
		} catch (Exception e) {
		}
	}
	
	private void saveCover(Book book, UMD umd) {
		FileOutputStream fos = null;
		try {
			if (umd.getCovers() != null) {
				Bitmap bitmap = BitmapFactory.decodeByteArray(umd.getCovers(), 0, umd.getCovers().length);
				if (bitmap != null) {
					String path = LocalCache.instance(mContext).getCachePath(FileUtils.getCoverCacheFile(book));
					File file = new File(path);
					file.getParentFile().mkdirs();
					fos = new FileOutputStream(file);
					bitmap.compress(CompressFormat.PNG, 100, fos);
					book.cover = path;
				} else {
					book.cover = "error";
					Log.w(TAG, "saveCover fail");
				}
			} else {
				book.cover = "error";
				Log.w(TAG, "saveCover fail");
			}
		} catch (Exception e) {
			book.cover = "error";
			Log.w(TAG, "saveCover error", e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
		}
	}

}
