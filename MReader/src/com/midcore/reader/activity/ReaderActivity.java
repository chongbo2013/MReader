package com.midcore.reader.activity;

import java.io.File;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.glview.view.View;
import com.glview.view.View.OnClickListener;
import com.glview.widget.Toast;
import com.midcore.reader.Constant;
import com.midcore.reader.R;
import com.midcore.reader.db.BookDBHelper;
import com.midcore.reader.exception.BookException;
import com.midcore.reader.model.Book;
import com.midcore.reader.reader.BookPageManager;
import com.midcore.reader.utils.FileUtils;
import com.midcore.reader.utils.MimeType;
import com.midcore.reader.view.BookReadView;

public class ReaderActivity extends BaseActivity {
	
	final static String TAG = Constant.TAG;
	
	private ProgressDialog mLoadingDialog;

	private BookReadView mReadView;
	BookPageManager mBookPageManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setGLContentView(R.layout.activity_reader);
		init();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		init();
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (mBookPageManager == null || !mBookPageManager.isBookLoaded()) {
			return false;
		}
		return super.dispatchTouchEvent(ev);
	}
	
	private void init() {
		showLoading();
		Intent intent = getIntent();
		Book book = null;
		MimeType type = null;
		try {
			book = (Book) intent.getSerializableExtra("book");
		} catch (Exception e) {}
		if (book == null) {
			String file = intent.getStringExtra("file");
			if (file == null) {
				Uri data = intent.getData();
				if (data != null) {
					file = data.getPath();
					type = MimeType.valueOfMimeType(intent.getType());
					Log.d(TAG, "open file:" + file);
					Log.d(TAG, "mime type:" + type);
				}
			}
			if (file != null) {
				book = BookDBHelper.get(getApplicationContext()).queryBook(file);
				if (book == null) {
					File f = new File(file);
					if (f.exists() && f.canRead() && f.isFile()) {
						String fileName = f.getName();
						int dot = fileName.lastIndexOf(".");
						if (type == null) {
							if (dot > 0) {
								String fileEnd = fileName.substring(dot + 1).toLowerCase();
								if (FileUtils.accept(fileEnd)) {
									book = new Book();
									book.file = file;
									book.type = fileEnd;
									book.title = fileName.substring(0, dot);
								}
							}
						} else if (FileUtils.accept(type.name())) {
							book = new Book();
							book.file = file;
							book.type = type.name();
							book.title = dot > 0 ? fileName.substring(0, dot) : fileName;
						}
					}
				}
			}
		}
		if (book != null) {
			mBookPageManager.openBook(book);
			new AsyncTask<String, String, Boolean>() {
				@Override
				protected Boolean doInBackground(String... args) {
					try {
						boolean r = mBookPageManager.openBookInternal();
						if (!r) {
							Toast.showShortToast(ReaderActivity.this, R.string.error_load_book);
						}
						return r;
					} catch (BookException e) {
						Log.d(TAG, "openBook:", e);
						Toast.showShortToast(ReaderActivity.this, e.getErrorCode().getMessage());
					} catch (Exception e) {
						Log.d(TAG, "openBook:", e);
						Toast.showShortToast(ReaderActivity.this, R.string.error_load_book);
					}
					return false;
				}
				@Override
				protected void onPostExecute(Boolean result) {
					cancelLoading();
					if (result) {
						mReadView.postInvalidate();
					} else {
						finish();
					}
				};
			}.execute();
		} else {
			Toast.showShortToast(ReaderActivity.this, R.string.error_load_book);
			finish();
		}
	}
	
	@Override
	public void onAttached(View content) {
		super.onAttached(content);
		mReadView = (BookReadView) content.findViewById(R.id.book_read_view);
		mBookPageManager = new BookPageManager(this, mReadView);
		mReadView.setBootPageManager(mBookPageManager);
		content.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (mBookPageManager != null) {
			mBookPageManager.onPause();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (mBookPageManager != null) {
			mBookPageManager.onResume();
		}
	}
	
	@Override
	protected void onStop() {
		cancelLoading();
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private void showLoading() {
		cancelLoading();
		mLoadingDialog = new ProgressDialog(this);
		mLoadingDialog.setCancelable(false);
		mLoadingDialog.show();
	}
	
	private void cancelLoading() {
		if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
			try {
    			mLoadingDialog.dismiss();
			} catch(Throwable tr) {}
			mLoadingDialog = null;
		}
	}
}
