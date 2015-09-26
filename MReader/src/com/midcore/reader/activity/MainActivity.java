package com.midcore.reader.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.glview.view.View;
import com.glview.widget.AdapterView;
import com.glview.widget.AdapterView.OnItemClickListener;
import com.glview.widget.AdapterView.OnItemLongClickListener;
import com.glview.widget.GridView;
import com.midcore.reader.Constant;
import com.midcore.reader.R;
import com.midcore.reader.adapter.BookGridAdapter;
import com.midcore.reader.db.BookDBHelper;
import com.midcore.reader.model.Book;
import com.midcore.reader.utils.IOUtils;

public class MainActivity extends HeadActivity implements OnItemClickListener, OnItemLongClickListener {
	
	final static String TAG = Constant.TAG;
	
	final static String ASSET_PATH = "books/";
	
	BookGridAdapter mAdapter;
	
	GridView mGridView;
	
	GridItemMenuDialog mItemMenuDialog;

	private ProgressDialog mLoadingDialog;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPresetBooks();
        setGLContentView(R.layout.activity_main);
    }
    
    private void checkPresetBooks() {
    	SharedPreferences sp = getSharedPreferences("reader", Context.MODE_PRIVATE);
    	if (!sp.getBoolean("inited", false)) {
    		sp.edit().putBoolean("inited", true).commit();
    		showLoading();
    		new AsyncTask<String, String, Boolean>() {
				@Override
				protected Boolean doInBackground(String... params) {
					Log.d(TAG, "begin copy preset books");
					try {
						List<Book> presetBooks = BookDBHelper.get(getApplicationContext()).queryAllPresetBooks();
						for (Book book : presetBooks) {
							File file = new File(book.file);
							if (book.isPreset() && !file.exists()) {
								file.getParentFile().mkdirs();
								IOUtils.copy(getAssets().open(book.getPresetFile()), new FileOutputStream(file));
							}
						}
					} catch (Exception e) {
						Log.w(TAG, "checkPresetBooks", e);
					}
					Log.d(TAG, "end copy preset books");
					return true;
				}
				@Override
				protected void onPostExecute(Boolean result) {
					super.onPostExecute(result);
					cancelLoading();
				}
			}.execute();
    	}
    }
    
    private void showLoading() {
    	cancelLoading();
    	mLoadingDialog = new ProgressDialog(this);
    	mLoadingDialog.setMessage(getResources().getText(R.string.initialing));
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
    
    @Override
    public void onAttached(View content) {
    	super.onAttached(content);
    	
    	initHead(R.string.title_activity_main, R.drawable.bookshelf_sidebar_icon, /*R.drawable.bookstore_icon*/0);
    	
    	mGridView = (GridView) content.findViewById(R.id.book_grid);
    	mGridView.setOnItemClickListener(this);
    	mGridView.setOnItemLongClickListener(this);
    	mAdapter = new BookGridAdapter(this);
    	initData();
    	mGridView.setAdapter(mAdapter);
    }
    
    @Override
    public void onClick(View v) {
    	if (v == mLeft) {
    		showMenuDialog();
    	}
    }
    
    private void initData() {
    	mAdapter.getList().clear();
    	mAdapter.getList().addAll(BookDBHelper.get(getApplicationContext()).queryAllBooks());
    	Book book = new Book();
    	book.type = "new";
    	mAdapter.getList().add(book);
    	mAdapter.notifyDataSetChanged();
    }
    
    void reloadData() {
    	runOnGLThread(new Runnable() {
			@Override
			public void run() {
				if (mAdapter != null) {
					initData();
				}
			}
		});
    }
    
    void openBook(Book book) {
    	Intent intent = new Intent(this, ReaderActivity.class);
		intent.putExtra("book", book);
		startActivity(intent);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	reloadData();
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	if (mItemMenuDialog != null) {
    		try {
    			mItemMenuDialog.dismiss();
			} catch(Throwable tr) {}
		}
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	cancelLoading();
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Book book = (Book) mAdapter.getItem(position);
		if ("new".equals(book.type)) {
			startActivity(new Intent(this, FileActivity.class));
		} else {
			openBook(book);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			final int position, long id) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (mItemMenuDialog != null) {
					try {
						mItemMenuDialog.dismiss();
					} catch(Throwable tr) {}
				}
				mItemMenuDialog = new GridItemMenuDialog(MainActivity.this, (Book) mAdapter.getItem(position));
				try {
					mItemMenuDialog.show();
				} catch(Throwable tr) {}
			}
		});
		return true;
	}
}
