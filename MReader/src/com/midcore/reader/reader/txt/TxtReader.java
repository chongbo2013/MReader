package com.midcore.reader.reader.txt;

import java.io.DataInput;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.midcore.reader.Constant;
import com.midcore.reader.model.Book;
import com.midcore.reader.reader.BitmapPage;
import com.midcore.reader.reader.Chapter;
import com.midcore.reader.reader.Page;
import com.midcore.reader.reader.Reader;
import com.midcore.reader.reader.cache.LocalCache;
import com.midcore.reader.reader.charset.Charset;
import com.midcore.reader.reader.txt.TxtLine.LineType;

/**
 * TODO 代码结构有待优化
 * @author lijing.lj
 */
public class TxtReader extends Reader {
	
	private final static String TAG = Constant.TAG;
	
	private final static int BUFFER_SIZE = 512 * 1024;
	private final static int MAX_CHAPTER_LENGTH = 8 * 1024;
	private final static int MAX_LENGTH_WITH_NO_CHAPTER = 10 * 1024;
	private final static int FLOATING_BUFFER_SIZE = 128 * 1024;
	
	private final static String LINE_BREAKER = "\r|\r\n|\n|\u2029";
	// "(第)([0-9零一二两三四五六七八九十百千万壹贰叁肆伍陆柒捌玖拾佰仟]{1,10})([章节回集卷])(.*)"
	final String[] mChapterPatterns = new String[] {"^(.{0,8})(\u7b2c)([0-9\u96f6\u4e00\u4e8c\u4e24\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341\u767e\u5343\u4e07\u58f9\u8d30\u53c1\u8086\u4f0d\u9646\u67d2\u634c\u7396\u62fe\u4f70\u4edf]{1,10})([\u7ae0\u8282\u56de\u96c6\u5377])(.{0,30})$",
													"^(\\s{0,4})([\\(\u3010\u300a]?(\u5377)?)([0-9\u96f6\u4e00\u4e8c\u4e24\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341\u767e\u5343\u4e07\u58f9\u8d30\u53c1\u8086\u4f0d\u9646\u67d2\u634c\u7396\u62fe\u4f70\u4edf]{1,10})([\\.:\uff1a\u0020\f\t])(.{0,30})$",
													"^(\\s{0,4})([\\(\uff08\u3010\u300a])(.{0,30})([\\)\uff09\u3011\u300b])(\\s{0,2})$",
													"^(\\s{0,4})(\u6b63\u6587)(.{0,20})$",
													"^(.{0,4})(Chapter|chapter)(\\s{0,4})([0-9]{1,4})(.{0,30})$"};
	Pattern mChapterPattern = null;
	final static Pattern mLineBreakerPattern = Pattern.compile("(" + LINE_BREAKER + ")");
	// "序(章)|前言"
	final static Pattern mPreChapterPattern = Pattern.compile("^(\\s{0,10})((\u5e8f[\u7ae0\u8a00]?)|(\u524d\u8a00)|(\u6954\u5b50))(\\s{0,10})$", Pattern.MULTILINE);
	byte[] mLineBreaker;
	String mLineBreakerStr;

	protected List<TxtBlock> mBlocks = new ArrayList<TxtBlock>();
	
	protected TxtBlock mCurrentBlock = null;
	protected TxtChapter mCurrentChapter, mPreviousChapter, mNextChapter;
	
	protected File mBookFile;
	protected long mTotalLength;
	protected long mTotalStringLength;
	private Charset mCharset;
	
	private RandomAccessFile mFileStream;
	byte[] mBuffer = new byte[BUFFER_SIZE + FLOATING_BUFFER_SIZE];
	int mCodeBufferLength = BUFFER_SIZE;
	protected String mCurrentBlockString, mPreviousBlockString, mNextBlockString;
	protected String mCurrentChapterString;
	protected String mPreviousChapterString;
	protected String mNextChapterString;
	
	final LocalCache mLocalCache;
	
	Bitmap.Config mConfig = Bitmap.Config.RGB_565;
	
	protected BitmapPage mBitmapPage, mBitmapPage1;
	
	protected boolean mHasChapter;
	
	public TxtReader(Context context) {
		super(context);
		mLocalCache = LocalCache.instance(context);
	}
	
	@Override
	public void setSize(int w, int h) {
		super.setSize(w, h);
		mBitmapPage = new BitmapPage(w, h, mConfig);
		mBitmapPage1 = new BitmapPage(w, h, mConfig);
	}
	
	public boolean loadBook(Book book) throws IOException {
		System.gc();
		System.runFinalization();
		System.gc();
		System.runFinalization();
		mBookLoaded = false;
		mBookTitle = null;
		mBook = book;
		mBookFile = new File(book.file);
		mTotalLength = mBookFile.length();
		loadBookInner();
		mBookLoaded = true;
		return mBookLoaded;
	}
	
	public void destroy() {
		super.destroy();
		if (mFileStream != null) {
			try {
				mFileStream.close();
			} catch (IOException e) {
			}
		}
		if (mBitmapPage != null) {
			mBitmapPage.recycle();
		}
		if (mBitmapPage1 != null) {
			mBitmapPage1.recycle();
		}
	}
	
	private void loadBookInner() throws IOException {
		Log.v(TAG, "loadBook begin " + mBook);
		mFileStream = new RandomAccessFile(mBookFile, "r");
		int tmpLength = BUFFER_SIZE;
		mCodeBufferLength = mFileStream.read(mBuffer, 0, tmpLength);
		mCharset = codeString(mBuffer);
		try {
			loadChapters(mCodeBufferLength > 0);
		} catch (Exception e) {
			Log.w(TAG, "loadChapters error", e);
			// try load with out chapters.
			loadChapters(false);
		}
		// load buffer
		loadBuffer();
		try {
			mCurrentChapterString = mCurrentBlockString.substring(mCurrentChapter.offset, mCurrentChapter.offset + mCurrentChapter.length);
		} catch (Exception e) {}
		loadCurrentChapter();
//		loadPreviousChapter();
//		loadNextChapter();
		Log.v(TAG, "loadBook end");
	}
	
	public BitmapPage getCurrentPage() {
		if (mBookLoaded) {
			if (!mBitmapPage.isValid()) {
				if (mCurrentChapter != null) {
					if (!mCurrentChapter.isValid()) {
						loadCurrentChapter();
					}
					if (mCurrentChapter.isValid()) {
						draw(new Canvas(mBitmapPage.getBitmap().getBitmap()), mCurrentChapter.currentPage);
						mBitmapPage.setValid(true);
					}
				}
			}
			return mBitmapPage;
		}
		return null;
	}
	
	public BitmapPage getPreviousPage() {
		if (mBookLoaded) {
			if (!mBitmapPage1.isValid()) {
				TxtPage page = ensurePreviousPage();
				if (page != null) {
					draw(new Canvas(mBitmapPage1.getBitmap().getBitmap()), page);
					mBitmapPage1.setValid(true);
				} else {
					return null;
				}
			}
			return mBitmapPage1;
		}
		return null;
	}
	
	protected TxtPage ensurePreviousPage() {
		int pageIndex = mCurrentChapter.pages.indexOf(mCurrentChapter.currentPage);
		if (pageIndex > 0) {
			return mCurrentChapter.pages.get(pageIndex - 1);
		}
		if (mPreviousChapter == null || !mPreviousChapter.isValid()) {
			loadPreviousChapter();
		}
		if (mPreviousChapter != null && mPreviousChapter.pages.size() > 0) {
			return mPreviousChapter.pages.get(mPreviousChapter.pages.size() - 1);
		}
		return null;
	}
	
	public BitmapPage getNextPage() {
		if (mBookLoaded) {
			if (!mBitmapPage1.isValid()) {
				TxtPage page = ensureNextPage();
				if (page != null) {
					draw(new Canvas(mBitmapPage1.getBitmap().getBitmap()), page);
					mBitmapPage1.setValid(true);
				} else {
					return null;
				}
			}
			return mBitmapPage1;
		}
		return null;
	}
	
	protected TxtPage ensureNextPage() {
		int pageIndex = mCurrentChapter.pages.indexOf(mCurrentChapter.currentPage);
		if (pageIndex + 1 < mCurrentChapter.pages.size()) {
			return mCurrentChapter.pages.get(pageIndex + 1);
		}
		if (mNextChapter == null || !mNextChapter.isValid()) {
			loadNextChapter();
		}
		if (mNextChapter != null && mNextChapter.pages.size() > 0) {
			return mNextChapter.pages.get(0);
		}
		return null;
	}
	
	public void invalidatePages() {
		if (mBitmapPage != null) {
			mBitmapPage.invalidate();
		}
		if (mBitmapPage1 != null) {
			mBitmapPage1.invalidate();
		}
	}
	
	public boolean hasPreviousPage() {
		boolean r = mCurrentChapter.getCurrentPageIndex() > 0;
		if (!r) {
			r = mCurrentBlock.chapters.indexOf(mCurrentChapter) > 0;
		}
		if (!r) {
			r = mBlocks.indexOf(mCurrentBlock) > 0;
		}
		return r;
	}
	
	public boolean hasNextPage() {
		boolean r = mCurrentChapter.getCurrentPageIndex() < mCurrentChapter.pages.size() - 1;
		if (!r) {
			r = mCurrentBlock.chapters.indexOf(mCurrentChapter) < mCurrentBlock.chapters.size() - 1;
		}
		if (!r) {
			r = mBlocks.indexOf(mCurrentBlock) < mBlocks.size() - 1;
		}
		return r;
	}
	
	public void switchToPreviousPage() {
		try {
			int pageIndex = mCurrentChapter.pages.indexOf(mCurrentChapter.currentPage);
			if (pageIndex <= 0) {
				switchToPreviousChapter();
			} else {
				mCurrentChapter.currentPage = mCurrentChapter.pages.get(pageIndex - 1);
				resetCurrentOffset(mCurrentChapter.block, mCurrentChapter, mCurrentChapter.currentPage);
			}
			mBitmapPage.invalidate();
			mBitmapPage1.invalidate();
		} catch (Throwable tr) {}
	}
	
	protected boolean switchToPreviousChapter() {
		boolean r = false;
		int chapterIndex = mCurrentBlock.chapters.indexOf(mCurrentChapter);
		if (chapterIndex > 0) {
			mNextChapter = mCurrentChapter;
			mNextChapterString = mCurrentChapterString;
			mCurrentChapter.currentPage = null;
			mCurrentChapter = mCurrentBlock.chapters.get(chapterIndex - 1);
			if (mPreviousChapterString != null) {
				mCurrentChapterString = mPreviousChapterString;
			} else {
				try {
					mCurrentChapterString = mCurrentBlockString.substring(mCurrentChapter.offset, mCurrentChapter.offset + mCurrentChapter.length);
				} catch (Exception e) {
				}
			}
			invalidateChapters();
			r = true;
		} else {
			r = switchToPreviousBlock();
		}
		mPreviousChapterString = null;
		mPreviousChapter = null;
		System.gc();
		System.runFinalization();
		return r;
	}
	
	private boolean switchToPreviousBlock() {
		boolean r = false;
		int blockIndex = mBlocks.indexOf(mCurrentBlock);
		if (blockIndex > 0) {
			mNextChapter = mCurrentChapter;
			mNextChapterString = mCurrentChapterString;
			mCurrentChapter.currentPage = null;
			mCurrentBlock = mBlocks.get(blockIndex - 1);
			if (mPreviousBlockString != null) {
				mCurrentBlockString = mPreviousBlockString;
			} else {
				mCurrentBlockString = null;
				loadBuffer();
			}
			mCurrentChapter = mCurrentBlock.chapters.get(mCurrentBlock.chapters.size() - 1);
			try {
				mCurrentChapterString = mCurrentBlockString.substring(mCurrentChapter.offset, mCurrentChapter.offset + mCurrentChapter.length);
			} catch (Exception e) {
			}
			r = true;
			invalidateChapters();
		}
		mPreviousBlockString = null;
		mNextBlockString = null;
		System.gc();
		System.runFinalization();
		return r;
	}
	
	public void switchToNextPage() {
		try {
			int pageIndex = mCurrentChapter.pages.indexOf(mCurrentChapter.currentPage);
			if (pageIndex + 1 >= mCurrentChapter.pages.size()) {
				switchToNextChapter();
			} else {
				mCurrentChapter.currentPage = mCurrentChapter.pages.get(pageIndex + 1);
				resetCurrentOffset(mCurrentChapter.block, mCurrentChapter, mCurrentChapter.currentPage);
			}
			mBitmapPage.invalidate();
			mBitmapPage1.invalidate();
		} catch (Throwable tr) {}
	}
	
	protected boolean switchToNextChapter() {
		boolean r = false;
		int chapterIndex = mCurrentBlock.chapters.indexOf(mCurrentChapter);
		if (chapterIndex < mCurrentBlock.chapters.size() - 1) {
			mPreviousChapter = mCurrentChapter;
			mPreviousChapterString = mCurrentChapterString;
			mCurrentChapter.currentPage = null;
			mCurrentChapter = mCurrentBlock.chapters.get(chapterIndex + 1);
			if (mNextChapterString != null) {
				mCurrentChapterString = mNextChapterString;
			} else {
				try {
					mCurrentChapterString = mCurrentBlockString.substring(mCurrentChapter.offset, mCurrentChapter.offset + mCurrentChapter.length);
				} catch (Exception e) {
				}
			}
			mNextChapterString = null;
			mCurrentChapter.invalidate();
			invalidateChapters();
			r = true;
		} else {
			r = switchToNextBlock();
		}
		mNextChapter = null;
		mNextChapterString = null;
		System.gc();
		System.runFinalization();
		return r;
	}
	
	private boolean switchToNextBlock() {
		boolean r = false;
		int blockIndex = mBlocks.indexOf(mCurrentBlock);
		if (blockIndex < mBlocks.size() - 1) {
			mPreviousChapter = mCurrentChapter;
			mPreviousChapterString = mCurrentChapterString;
			mCurrentChapter.currentPage = null;
			mCurrentBlock = mBlocks.get(blockIndex + 1);
			if (mNextBlockString != null) {
				mCurrentBlockString = mNextBlockString;
			} else {
				mCurrentBlockString = null;
				loadBuffer();
			}
			mCurrentChapter = mCurrentBlock.chapters.get(0);
			try {
				mCurrentChapterString = mCurrentBlockString.substring(mCurrentChapter.offset, mCurrentChapter.offset + mCurrentChapter.length);
			} catch (Exception e) {
			}
			invalidateChapters();
			r = true;
		}
		mPreviousBlockString = null;
		mNextBlockString = null;
		System.gc();
		System.runFinalization();
		return r;
	}
	
	public void invalidateChapters() {
		if (mCurrentChapter != null) {
			mCurrentChapter.invalidate();
		}
		if (mPreviousChapter != null) {
			mPreviousChapter.invalidate();
			mPreviousChapter = null;
		}
		if (mNextChapter != null) {
			mNextChapter.invalidate();
			mNextChapter = null;
		}
	}
	
	protected void loadCurrentChapter() {
		if (mWidth > 0 && mHeight > 0) {
			mCurrentChapter.setValid(loadChapterPages(mCurrentChapter, mCurrentChapterString));
			findCurrentPage();
		}
	}
	
	private void findCurrentPage() {
		if (mCurrentChapter.isValid()) {
			for (TxtPage page : mCurrentChapter.pages) {
				if (mCurrentChapter.currentPage == null) {
					mCurrentChapter.currentPage = page;
				}
				if (getCurrentOffset() >= calculateOffset(mCurrentChapter.block, mCurrentChapter, page)) {
					mCurrentChapter.currentPage = page;
				} else {
					break;
				}
			}
			if (mCurrentChapter.currentPage != null) {
				resetCurrentOffset(mCurrentChapter.block, mCurrentChapter, mCurrentChapter.currentPage);
			}
		}
	}
	
	protected double calculateOffset(TxtBlock block, TxtChapter chapter, TxtPage page) {
		return (block != null ? block.stringOffset : 0) + (chapter != null ? chapter.offset : 0) + (page != null ? page.offset : 0);
	}
	
	protected void loadPreviousChapter() {
		if (mPreviousChapter != null) {
			mPreviousChapter.invalidate();
			mPreviousChapter = null;
		}
		int chapterIndex = mCurrentBlock.chapters.indexOf(mCurrentChapter);
		if (chapterIndex > 0) {
			mPreviousChapter = mCurrentBlock.chapters.get(chapterIndex - 1);
			mPreviousChapter.invalidate();
		} else {
			int blockIndex = mBlocks.indexOf(mCurrentBlock);
			if (blockIndex > 0 && mBlocks.get(blockIndex - 1).chapters.size() > 0) {
				mPreviousChapter = mBlocks.get(blockIndex - 1).chapters.get(mBlocks.get(blockIndex - 1).chapters.size() - 1);
				mPreviousChapter.invalidate();
			}
		}
		if (mWidth > 0 && mHeight > 0 && mPreviousChapter != null) {
			if (mPreviousChapterString == null) {
				if (mPreviousChapter.block == mCurrentBlock) {
					mPreviousChapterString = mCurrentBlockString.substring(mPreviousChapter.offset, mPreviousChapter.offset + mPreviousChapter.length);
				} else {
					if (mPreviousBlockString == null) {
						int index = mBlocks.indexOf(mCurrentBlock);
						if (index > 0) {
							TxtBlock previous = mBlocks.get(index - 1);
							mPreviousBlockString = loadBuffer(previous);
						}
					}
					if (mPreviousBlockString != null) {
						mPreviousChapterString = mPreviousBlockString.substring(mPreviousChapter.offset, mPreviousChapter.offset + mPreviousChapter.length);
					}
				}
			}
			if (mPreviousChapterString != null) {
				mPreviousChapter.setValid(loadChapterPages(mPreviousChapter, mPreviousChapterString));
			}
		}
	}
	
	protected void loadNextChapter() {
		if (mNextChapter != null) {
			mNextChapter.invalidate();
			mNextChapter = null;
		}
		int chapterIndex = mCurrentBlock.chapters.indexOf(mCurrentChapter);
		if (chapterIndex + 1 < mCurrentBlock.chapters.size()) {
			mNextChapter = mCurrentBlock.chapters.get(chapterIndex + 1);
			mNextChapter.invalidate();
		} else {
			int blockIndex = mBlocks.indexOf(mCurrentBlock);
			if (blockIndex + 1 < mBlocks.size() && mBlocks.get(blockIndex + 1).chapters.size() > 0) {
				mNextChapter = mBlocks.get(blockIndex + 1).chapters.get(0);
				mNextChapter.invalidate();
			}
		}
		if (mWidth > 0 && mHeight > 0 && mNextChapter != null) {
			if (mNextChapterString == null) {
				if (mNextChapter.block == mCurrentBlock) {
					mNextChapterString = mCurrentBlockString.substring(mNextChapter.offset, mNextChapter.offset + mNextChapter.length);
				} else {
					if (mNextBlockString == null) {
						int index = mBlocks.indexOf(mCurrentBlock);
						if (index + 1 < mBlocks.size()) {
							TxtBlock next = mBlocks.get(index + 1);
							mNextBlockString = loadBuffer(next);
						}
					}
					if (mNextBlockString != null) {
						mNextChapterString = mNextBlockString.substring(mNextChapter.offset, mNextChapter.offset + mNextChapter.length);
					}
				}
			}
			if (mNextChapterString != null) {
				mNextChapter.setValid(loadChapterPages(mNextChapter, mNextChapterString));
			}
		}
	}
	
	static int memoryClass = -1;
	protected void systemGC() {
		if (memoryClass < 0) {
			ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
			memoryClass = am.getLargeMemoryClass();
		}
		if (memoryClass < 48) {
			System.gc();
			System.runFinalization();
		}
	}
	
	int mSelectedIndex = - 1;
	
	private Matcher matchChapterContent(String content) {
		if (mChapterPattern != null) {
			return mChapterPattern.matcher(content);
		} else {
			Matcher m;
			for (int i = mSelectedIndex; i < mChapterPatterns.length; i ++) {
				mSelectedIndex = i;
				String pattern = mChapterPatterns[i];
				Pattern p = Pattern.compile(pattern, Pattern.MULTILINE);
				m = p.matcher(content);
				if (m.find()) {
					mChapterPattern = p;
					Log.d(TAG, "select pattern=" + pattern);
					return m;
				}
			}
			return null;
		}
	}
	
	/**
	 * @param hasChapter
	 * @throws IOException
	 */
	private void loadChapters(boolean hasChapter) throws IOException {
		Log.v(TAG, "loadChapters hasChapter=" + hasChapter);
		int codeBufferLength = mCodeBufferLength;
		mCodeBufferLength = 0;
		mChapterPattern = null;
		mSelectedIndex ++;
		mHasChapter = hasChapter;
		mCurrentChapter = null;
		mCurrentBlock = null;
		boolean findChapter = false;
		mBlocks.clear();
		int bufferSize = BUFFER_SIZE;
		if (hasChapter) {
			if (codeBufferLength == 0) {
				mFileStream.seek(0);
				codeBufferLength = mFileStream.read(mBuffer, 0, bufferSize);
			}
		} else {
			mFileStream.seek(0);
		}
		final byte[] buffer = mBuffer;
		int length = 0;
		int totalLength = 0;
		int stringLength = 0;
		int stringTotalLength = 0;
		boolean preChapterChecked = false;
		int chapterCount = 0;
		while (true) {
			if (mBlocks.size() <= 0 && hasChapter) {
				length = codeBufferLength;
			} else {
				length = mFileStream.read(buffer, 0, bufferSize);
			}
			if (length <= 0) break;
			if (length < bufferSize || (totalLength + length) >= mTotalLength) {
			} else {
				length += readParagraphForward(mFileStream, buffer, length);
			}
			TxtBlock block = null;
			systemGC();
			String str = new String(buffer, 0, (int) length, mCharset.getName());
			stringLength = str.length();
			boolean pcChecked = preChapterChecked;
			if (!preChapterChecked) {
				preChapterChecked = true;
			}
			if (hasChapter) {
				Matcher m = matchChapterContent(str);
				if (m == null) {
					loadChapters(false);
					return;
				}
				int find = 0;
				int end = 0;
				int tmpend = 0;
				int lastOffset = 0;
				boolean first = true;
				if (m.find(end)) {
					if (!findChapter) {
						findChapter = true;
					}
					block = new TxtBlock();
					while (true) {
						if (tmpend >= str.length() || !m.find(tmpend)) break;
						find = m.start();
						tmpend = m.end();
						String title = null;
						if (!pcChecked) {
							pcChecked = true;
							Matcher pm = mPreChapterPattern.matcher(str);
							if (pm.find(0)) {
								if (pm.start() < find) {
									find = pm.start();
									tmpend = pm.end();
									title = pm.group();
								}
							}
						}
						if (!first && find - end < 20) {//内容太短不足以自成一章
							continue;
						}
						if (first) {
							first = false;
						}
						if (title == null) {
							title = m.group();
						}
						chapterCount ++;
						if (end == 0 && find > 0) {
							if (mBlocks.size() > 0) {
								TxtBlock lastblock = mBlocks.get(mBlocks.size() - 1);
								int l = str.substring(0, find).getBytes(mCharset.getName()).length;
								lastblock.length += l;
								if (lastblock.length > bufferSize + FLOATING_BUFFER_SIZE) {
									// too big
									loadChapters(chapterCount < 5);
									return;
								}
								lastblock.stringLength += find;
								lastOffset = find;
								if (lastblock.chapters.size() > 0) {
									TxtChapter lastChapter = lastblock.chapters.get(lastblock.chapters.size() - 1);
									int oldLength = lastChapter.length;
									lastChapter.length += find;
									splitVirtualChapters(lastChapter, str, 0, oldLength);
								}
								totalLength += l;
								length -= l;
								stringLength -= find;
								stringTotalLength += find;
							} else {
								TxtChapter head = new TxtChapter();
								head.header = true;
								head.title = "";
								head.name = "";
								head.offset = 0;
								block.chapters.add(head);
								head.block = block;
								mCurrentChapter = head;
							}
						}
						end = tmpend;
						TxtChapter chapter = new TxtChapter();
						chapter.title = title;
						chapter.name = chapter.title;//m.group(TITILE_GROUP_INDEX);
						chapter.offset = find - lastOffset;
						if (block.chapters.size() > 0) {
							TxtChapter last = block.chapters.get(block.chapters.size() - 1);
							last.length = find - lastOffset - last.offset;
							splitVirtualChapters(last, str, last.offset + lastOffset, 0);
						}
						if (getCurrentOffset() >= stringTotalLength + chapter.offset) {
							mCurrentChapter = chapter;
						}
						block.chapters.add(chapter);
						chapter.block = block;
					}
					if (block.chapters.size() > 0) {
						TxtChapter last = block.chapters.get(block.chapters.size() - 1);
						last.length = str.length() - last.offset - lastOffset;
						splitVirtualChapters(last, str, last.offset + lastOffset, 0);
					}
					block.offset = totalLength;
					block.length = length;
					block.stringOffset = stringTotalLength;
					block.stringLength = stringLength;
					if (getCurrentOffset() >= block.stringOffset) {
						mCurrentBlock = block;
					}
				} else {
					if (mBlocks.size() > 0) {
						if (length >= bufferSize) {
							// too big
							loadChapters(chapterCount < 5);
							return;
						}
						block = mBlocks.get(mBlocks.size() - 1);
						block.length += length;
						block.stringLength += str.length() - lastOffset;
						if (block.chapters.size() > 0) {
							TxtChapter lastChapter = block.chapters.get(block.chapters.size() - 1);
							int oldLength = lastChapter.length;
							lastChapter.length += str.length() - lastOffset;
							splitVirtualChapters(lastChapter, str, 0, oldLength);
						}
					} else {
						loadChapters(chapterCount < 5);
						return;
					}
				}
			} else {
				// no chapter
				block = new TxtBlock();
				block.offset = totalLength;
				block.length = length;
				block.stringOffset = stringTotalLength;
				block.stringLength = stringLength;
				int sLength = stringLength;
				int lastOffset = 0;
				int lastLength = 0;
				while (sLength > 0) { //分虚拟章节，提高加载速度
					TxtChapter chapter = null;
					if (sLength > MAX_LENGTH_WITH_NO_CHAPTER / 4) {
						int s = (sLength > MAX_LENGTH_WITH_NO_CHAPTER ? MAX_LENGTH_WITH_NO_CHAPTER : sLength) + lastOffset + lastLength;
						int index = str.indexOf(mLineBreakerStr, s) - s;
						if (index < 0) {
							index = 0;
						}
						chapter = new TxtChapter();
						chapter.virtual = true;
						chapter.title = "";
						chapter.name = "";
						chapter.offset = lastOffset + lastLength;
						chapter.length = s + index - lastOffset - lastLength;
						lastOffset = chapter.offset;
						lastLength = chapter.length;
						chapter.block = block;
						block.chapters.add(chapter);
						if (getCurrentOffset() >= block.stringOffset + chapter.offset) {
							mCurrentBlock = block;
							mCurrentChapter = chapter;
						}
					} else {
						if (block.chapters.size() > 0) {
							chapter = block.chapters.get(block.chapters.size() - 1);
							chapter.length += sLength;
						} else {
							chapter = new TxtChapter();
							chapter.virtual = true;
							chapter.title = "";
							chapter.name = "";
							chapter.offset = 0;
							chapter.length = sLength;
							chapter.block = block;
							block.chapters.add(chapter);
							if (getCurrentOffset() >= block.stringOffset + chapter.offset) {
								mCurrentBlock = block;
								mCurrentChapter = chapter;
							}
						}
					}
					sLength -= chapter.length;
				}
			}
			if (hasChapter && block.length > bufferSize + FLOATING_BUFFER_SIZE) {
				loadChapters(chapterCount < 5);
				return;
			}
			totalLength += length;
			stringTotalLength += stringLength;
			if (mBlocks.size() > 0) {
				TxtBlock last = mBlocks.get(mBlocks.size() - 1);
				last.length = block.offset - last.offset;
				last.stringLength  = block.stringOffset - last.stringOffset;
			}
			if (mBlocks.indexOf(block) < 0) {
				mBlocks.add(block);
			}
		}
		if (mBlocks.size() > 0) {
			TxtBlock last = mBlocks.get(mBlocks.size() - 1);
			last.length = totalLength - last.offset;
			last.stringLength  = stringTotalLength - last.stringOffset;
		}
		mTotalStringLength = stringTotalLength;
		mHasChapter &= findChapter;
		System.gc();
		System.runFinalization();
	}
	
	private void splitVirtualChapters(TxtChapter lastchapter, String str, int start, int oldLength) {//分虚拟章节，提高加载速度
		if (lastchapter.length > MAX_CHAPTER_LENGTH) {
			int cacheLength = lastchapter.length - oldLength;
			int index = str.indexOf(mLineBreakerStr, start + MAX_CHAPTER_LENGTH) - start - MAX_CHAPTER_LENGTH;
			if (index < 0) return;
			if (index + MAX_CHAPTER_LENGTH >= cacheLength) return;
			lastchapter.length = oldLength + MAX_CHAPTER_LENGTH + index;
			
			int lastOffset = lastchapter.offset;
			int lastLength = lastchapter.length;
			start += MAX_CHAPTER_LENGTH + index;
			int containLength = cacheLength - index - MAX_CHAPTER_LENGTH;
			while (containLength > 0) {
				TxtChapter chapter = null;
				int l = (containLength > MAX_CHAPTER_LENGTH ? MAX_CHAPTER_LENGTH : containLength);
				int s = l + start;
				index = str.indexOf(mLineBreakerStr, s) - s;
				if (containLength > MAX_CHAPTER_LENGTH / 4 && index >= 0 && index < containLength) {
					chapter = new TxtChapter();
					chapter.virtual = true;
					chapter.realChapter = lastchapter.realChapter;
					chapter.title = "";
					chapter.name = "";
					chapter.offset = lastOffset + lastLength;
					chapter.length = l + index;
					lastOffset = chapter.offset;
					lastLength = chapter.length;
					chapter.block = lastchapter.block;
					lastchapter.block.chapters.add(chapter);
					containLength -= chapter.length;
					start += chapter.length;
					if (mCurrentChapter == null || mCurrentChapter.realChapter == chapter.realChapter) {
						if (getCurrentOffset() >= calculateOffset(chapter.block, chapter, null)) {
							mCurrentChapter = chapter;
						}
					}
				} else {
					chapter = lastchapter.block.chapters.get(lastchapter.block.chapters.size() - 1);
					chapter.length += containLength;
					break;
				}
			}
		}
	}
	
	protected void loadBuffer() {
		mCurrentBlockString = loadBuffer(mCurrentBlock);
		mPreviousBlockString = null;
		mNextBlockString = null;
		System.gc();
		System.runFinalization();
	}
	
	protected String loadBuffer(TxtBlock block) {
		try {
			mFileStream.seek(block.offset);
			mFileStream.read(mBuffer, 0, block.length);
		} catch (IOException e) {
		}
		try {
			return new String(mBuffer, 0, block.length, mCharset.getName());
		} catch (IOException e) {
			Log.d(TAG, "loadBuffer error", e);
		}
		return "";
	}
	
	int mLineBreaderByteCount = -1;
	protected boolean loadChapterPages(TxtChapter chapter, String content) {
		if (content == null) {
			return false;
		}
		if (chapter.pages == null) {
			chapter.pages = new ArrayList<TxtPage>();
		}
		int offset = 0;
		boolean title = false;
		if (chapter.title.length() > 0) {
			title = true;
			if (content.startsWith(chapter.title)) {
				content = content.substring(chapter.title.length());
			}
		}
		chapter.pages.clear();
		String[] lines = mLineBreakerPattern.split(content);
		int lineBreakerByteCount = 0;
		if (mLineBreaderByteCount < 0) {
			Matcher m = mLineBreakerPattern.matcher(content);
			if (m.find(0)) {
				lineBreakerByteCount = m.group().length();
				mLineBreaderByteCount = lineBreakerByteCount;
			}
		} else {
			lineBreakerByteCount = mLineBreaderByteCount;
		}
		int titleLineCount = 0;
		int lineCount = 0;
        int paragraphCount = 0;
        
        TxtPage page = new TxtPage();
		page.chapter = chapter;
		page.offset = offset;
        if (title) {
        	String str = chapter.title;
        	boolean top = false;
        	while (str.length() > 0) {
				if (getTotalHeight(titleLineCount + 1, lineCount, paragraphCount) > getContentHeight()) {
					lineCount = 0;
					paragraphCount = 0;
					titleLineCount = 0;
					chapter.pages.add(page);
					page = new TxtPage();
					page.chapter = chapter;
					page.offset = offset;
					break;
				}
				int nSize = mTitlePaint.breakText(str, true, getContentWidth(),
                        null);
				String s = str.substring(0, nSize);
				if (!top) {
					page.lines.add(new TxtLine(s, LineType.TITLE_TOP, getContentWidth()));
					top = true;
				} else {
					page.lines.add(new TxtLine(s, LineType.TITLE, getContentWidth()));
				}
				titleLineCount ++;
                str = str.substring(nSize);
			}
        	page.lines.add(new TxtLine("", LineType.TITLE_BOTTOM, getContentWidth()));
        	titleLineCount ++;
        }
        
		if (lines != null) {
			for (String line : lines) {
				offset += line.length() + lineBreakerByteCount;
				if (line.length() == 0) {
					continue;
				}
				String str = line;
				while (str.length() > 0) {
					if (getTotalHeight(titleLineCount, lineCount + 1, paragraphCount) > getContentHeight()) {
						titleLineCount = 0;
						lineCount = 0;
						paragraphCount = 0;
						chapter.pages.add(page);
						page = new TxtPage();
						page.chapter = chapter;
						page.offset = offset;
					}
					int nSize = mPaint.breakText(str, true, getContentWidth(),
	                        null);
					String s = str.substring(0, nSize);
					str = str.substring(nSize);
					page.lines.add(new TxtLine(s, LineType.NORMAL, str.length() > 0 ? mPaint.measureText(s) : getContentWidth()));
	                lineCount ++;
				}
				if (str.length() == 0) {
					page.lines.add(new TxtLine("", LineType.PARAGRAPH, getContentWidth()));
					paragraphCount ++;
				}
				if (getTotalHeight(titleLineCount, lineCount, paragraphCount) >= getContentHeight()) {
					titleLineCount = 0;
					lineCount = 0;
					paragraphCount = 0;
					chapter.pages.add(page);
					page = new TxtPage();
					page.chapter = chapter;
					page.offset = offset;
				}
			}
			if (page != null && page.lines.size() > 0) {
				chapter.pages.add(page);
			}
		}
		if (chapter.pages.size() == 0) {
			chapter.pages.add(new TxtPage());
		}
		System.gc();
		System.runFinalization();
		return true;
	}
	
	// 读取下一段
    protected int readParagraphForward(DataInput input, byte[] buffer, int offset) throws IOException {
        int start = offset;
        long length = buffer.length;
        int i = start;
        int j = 0;
        byte b0 = 0;
        boolean flag = false;
        while (i < mLineBreaker.length && i < length - 1) {
        	b0 = input.readByte();
        	buffer[i++] = b0;
        }
        if (mLineBreaker.length > 0 && i >= mLineBreaker.length) {
        	do {
        		flag = true;
        		for (j = 0; j < mLineBreaker.length; j ++) {
        			if (buffer[i - mLineBreaker.length + j] != mLineBreaker[j]) {
        				flag = false;
        			}
        		}
        		if (flag) {
        			break;
        		}
        		b0 = input.readByte();
            	buffer[i++] = b0;
        	} while (i < length - 1);
        }
        return i - start;
    }
    
    @Override
    protected void draw(Canvas canvas, Page ipage) {
    	super.draw(canvas, ipage);
    	TxtPage page = (TxtPage) ipage;
    	int contentWidth = getContentWidth();
    	List<TxtLine> lines = page.lines;
    	if (mBookTitle == null) {
    		mBookTitle = measureText(mInfoPaint, mBook.title, contentWidth * 2 / 5);
    	}
    	String title = measureText(mInfoPaint, page.chapter.realChapter.title, contentWidth * 2 / 5);
		float y = mTopMargin;
		float x = mLeftMargin;
		Paint paint = mPaint;
		for (TxtLine line : lines) {
			if (line.type == LineType.PARAGRAPH) {
				paint = mPaint;
				y += mParagraphSpace;
			} else if (line.type == LineType.NORMAL) {
				paint = mPaint;
				y += mLineHeight;
			} else if (line.type == LineType.TITLE) {
				paint = mTitlePaint;
				y += mTitleLineHeight;
			} else if (line.type == LineType.TITLE_BOTTOM) {
				paint = mTitlePaint;
				y += mTitleLineHeight;
			} else if (line.type == LineType.TITLE_TOP) {
				paint = mTitlePaint;
				y += mTitleLineHeight + mTitleMarginTop;
			}
			if (contentWidth != line.width && line.width > 0) {
				paint.setTextScaleX(contentWidth / line.width);
			}
			
			if (line.type == LineType.TITLE_BOTTOM) {
				canvas.drawLine(x, y, mWidth - mRightMargin, y, paint);
			} else {
				canvas.drawText(line.line, x, y, paint);
			}
			paint.setTextScaleX(1f);
            y += mLineSpace;
        }
		drawInfos(canvas, mBookTitle, title, calculateProgress(page));
	}
    
    protected double calculateProgress(TxtPage page) {
		double p = 1d * (page.offset + page.chapter.offset + page.chapter.block.stringOffset) / mTotalStringLength;
		return p;
	}
	
	/**
     * 判断txt文件的编码格式
     * 
     * @param fileName
     *            :file
     * @return 文件编码格式
	 * @throws IOException 
     * @throws Exception
     */
    private Charset codeString(byte[] buffer) throws IOException {
        Charset code = Charset.GBK;
        if (buffer[0] == -1 && buffer[1] == -2 )  
            code = Charset.UTF16LE;  
        if (buffer[0] == -2 && buffer[1] == -1 )  
            code = Charset.UTF16BE;  
        if(buffer[0]==-17 && buffer[1]==-69 && buffer[2] ==-65)  
            code = Charset.UTF8;  
        Log.d(TAG, "codeString=" + code.getName());
        int tmpLength = 5 * 1024;
        if (tmpLength > mCodeBufferLength) {
        	tmpLength = mCodeBufferLength;
        }
        String str = new String(buffer, 0, tmpLength, code.getName());
        if (mLineBreaker == null) {
			Matcher m = mLineBreakerPattern.matcher(str);
			if (m.find(0)) {
				mLineBreakerStr = m.group();
				mLineBreaker = mLineBreakerStr.getBytes(code.getName());
			} else {
				if (mCharset == Charset.UTF16LE) {
					mLineBreaker = new byte[] {0x0a, 0x00};
				} else if (mCharset == Charset.UTF16BE) {
					mLineBreaker = new byte[]{0x00, 0x0a};
				} else {
					mLineBreaker = new byte[]{0x0a};
				}
				mLineBreakerStr = new String(mLineBreaker, mCharset.getName());
			}
		}
        return code;
    }

	@Override
	public List<Chapter> getChapters() {
		if (!mBookLoaded || !mHasChapter) {
			return null;
		}
		List<Chapter> list = new ArrayList<Chapter>();
		for (TxtBlock block : mBlocks) {
			for (Chapter chapter : block.chapters) {
				if (!chapter.header && !chapter.virtual) {
					list.add(chapter);
				}
			}
		}
		return list;
	}
	
	@Override
	public Chapter getCurrentChapter() {
		return mCurrentChapter.realChapter;
	}
	
	@Override
	public boolean seekToChapter(Chapter chapter) {
		if (!mBookLoaded) return false;
		if (chapter instanceof TxtChapter) {
			if (mCurrentChapter != chapter) {
				if (mCurrentChapter != null) {
					mCurrentChapter.currentPage = null;
				}
				mCurrentChapter = (TxtChapter) chapter;
				if (mCurrentBlock != mCurrentChapter.block) {
					mCurrentBlock = mCurrentChapter.block;
					loadBuffer();
				}
				try {
					mCurrentChapterString = mCurrentBlockString.substring(mCurrentChapter.offset, mCurrentChapter.offset + mCurrentChapter.length);
				} catch (Exception e) {
				}
				mPreviousChapterString = null;
				mNextChapterString = null;
				resetCurrentOffset(mCurrentBlock, mCurrentChapter, null);
				invalidateChapters();
				invalidatePages();
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean seekTo(float progress) {
		if (!mBookLoaded) return false;
		double offset = progressToOffset(progress);
		if (offset == getCurrentOffset()) return false;
		TxtBlock toBlock = null;
		TxtChapter toChapter = null;
		for (TxtBlock block : mBlocks) {
			if (offset >= calculateOffset(block, null, null)) {
				toBlock = block;
			} else {
				break;
			}
		}
		if (toBlock != null) {
			for (TxtChapter chapter : toBlock.chapters) {
				if (offset >= calculateOffset(toBlock, chapter, null)) {
					toChapter = chapter;
				} else {
					break;
				}
			}
		}
		if (toChapter != null) {
			boolean c = seekToChapter(toChapter);
			setCurrentOffset(offset);
			if (!c) {
				invalidatePages();
				findCurrentPage();
			}
			return true;
		}
		return false;
	}
	
	protected double progressToOffset(float progress) {
		return mTotalStringLength * progress;
	}
	
	@Override
	public boolean seekToPreviousChapter() {
		if (!mBookLoaded || !mHasChapter) return false;
		int chapterIndex = mCurrentBlock.chapters.indexOf(mCurrentChapter.realChapter);
		if (chapterIndex > 0) {
			seekToChapter(mCurrentBlock.chapters.get(chapterIndex - 1).realChapter);
			return true;
		} else {
			int blockIndex = mBlocks.indexOf(mCurrentBlock);
			if (blockIndex > 0) {
				TxtBlock block = mBlocks.get(blockIndex - 1);
				seekToChapter(block.chapters.get(block.chapters.size() - 1).realChapter);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean seekToNextChapter() {
		if (!mBookLoaded || !mHasChapter) return false;
		int chapterIndex = mCurrentBlock.chapters.indexOf(mCurrentChapter.realChapter);
		while (chapterIndex < mCurrentBlock.chapters.size() - 1) {
			TxtChapter next = mCurrentBlock.chapters.get(chapterIndex + 1).realChapter;
			if (next != mCurrentChapter.realChapter) {
				seekToChapter(next);
				return true;
			}
			chapterIndex ++;
		}
		int blockIndex = mBlocks.indexOf(mCurrentBlock);
		if (blockIndex > mBlocks.size() - 1) {
			TxtBlock block = mBlocks.get(blockIndex + 1);
			seekToChapter(block.chapters.get(block.chapters.size() - 1).realChapter);
			return true;
		}
		return false;
	}
	
	protected void resetCurrentOffset(TxtBlock block, TxtChapter chapter, TxtPage page) {
		setCurrentOffset(calculateOffset(block, chapter, page));
	}
	
	@Override
	public float getCurrentProgress() {
		if (mBookLoaded && mTotalStringLength > 0) {
			return ((float) getCurrentOffset()) / mTotalStringLength;
		}
		return 0;
	}
	
}
