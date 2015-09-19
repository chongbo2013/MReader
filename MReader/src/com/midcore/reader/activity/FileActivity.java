package com.midcore.reader.activity;

import java.io.File;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

import com.glview.view.View;
import com.glview.widget.AdapterView;
import com.glview.widget.AdapterView.OnItemClickListener;
import com.glview.widget.AdapterView.OnItemLongClickListener;
import com.glview.widget.ListView;
import com.glview.widget.TextView;
import com.midcore.reader.R;
import com.midcore.reader.adapter.BookFileListAdapter;
import com.midcore.reader.model.BookFile;
import com.midcore.reader.utils.FileUtils;

public class FileActivity extends HeadActivity implements OnItemClickListener, OnItemLongClickListener {

	ListView mListView;
	
	View mEmptyView;
	TextView mPathView;

	BookFileListAdapter mAdapter;
	
	final static String BASE_PATH = Environment.getExternalStorageDirectory().getPath();
	File mBasePath = new File(BASE_PATH);
	File mCurrentPath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setGLContentView(R.layout.activity_file);
	}

	@Override
	public void onAttached(View content) {
		super.onAttached(content);
		initHead(R.string.title_activity_file, R.drawable.return_button, R.drawable.bookshelf_sidebar_icon);
		
		mPathView = (TextView) content.findViewById(R.id.top_path);
		mEmptyView = content.findViewById(R.id.book_file_empty);
		mAdapter = new BookFileListAdapter(this);
		mListView = (ListView) content.findViewById(R.id.book_file_list);
		mListView.setOnItemClickListener(this);
		mListView.setAdapter(mAdapter);
		mCurrentPath = null;
		setFilePath(mBasePath);
	}
	
	private void setFilePath(File path) {
		mCurrentPath = path;
		
		mPathView.setText(mCurrentPath.getAbsolutePath());
		
		mAdapter.getList().clear();
		mAdapter.getList().addAll(FileUtils.getFiles(path.getAbsolutePath()));
		mAdapter.notifyDataSetChanged();
		if (mAdapter.isEmpty()) {
			mEmptyView.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.INVISIBLE);
		} else {
			mEmptyView.setVisibility(View.INVISIBLE);
			mListView.setVisibility(View.VISIBLE);
			mListView.setSelection(0);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		BookFile file = (BookFile) mAdapter.getItem(position);
		if (!file.isFile) {
			setFilePath(new File(mCurrentPath, file.name));
		} else {
			Intent intent = new Intent(this, ReaderActivity.class);
			intent.putExtra("file", file.path);
			startActivity(intent);
		}
	}

	@Override
	public void onClick(View v) {
		if (v == mLeft) {
			finish();
		} else if (v.getId() == R.id.path_back_text || v.getId() == R.id.path_back_icon) {
			if (mCurrentPath == null || mCurrentPath.getParentFile() == null) {
				finish();
				return;
			}
			setFilePath(mCurrentPath.getParentFile());
		} else if (v == mRight) {
			showMenuDialog();
		}
	}
	
	@Override
	public void onBackPressed() {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				if (mCurrentPath == null || mCurrentPath.getParentFile() == null || mCurrentPath.equals(mBasePath)) {
					finish();
				} else {
					setFilePath(mCurrentPath.getParentFile());
				}
			}
		};
		runOnGLThread(r);
	}
}
