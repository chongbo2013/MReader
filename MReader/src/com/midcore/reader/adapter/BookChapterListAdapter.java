package com.midcore.reader.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.glview.view.LayoutInflater;
import com.glview.view.View;
import com.glview.view.ViewGroup;
import com.glview.widget.BaseAdapter;
import com.glview.widget.TextView;
import com.midcore.reader.R;
import com.midcore.reader.reader.Chapter;
import com.midcore.reader.reader.settings.Theme;

public class BookChapterListAdapter extends BaseAdapter {

	Context mContext;
	
	LayoutInflater mLayoutInflater;
	
	List<Chapter> mData = new ArrayList<Chapter>();

	int mCurrentPosition = -1;
	
	Theme mTheme;

	public BookChapterListAdapter(Context context, Theme theme) {
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
		mTheme = theme;
	}
	
	public void setCurrentPosition(int p) {
		mCurrentPosition = p;
	}

	public List<Chapter> getList() {
		return mData;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.chapter_list_item, parent, false);
		}
		Chapter chapter = (Chapter) getItem(position);
		TextView tv = (TextView) convertView;
		if (position == mCurrentPosition) {
			tv.setTextColor(mContext.getResources().getColor(R.color.red));
		} else {
			tv.setTextColor(mTheme.textColor);
		}
		tv.setText(chapter.title);
		return convertView;
	}

}
