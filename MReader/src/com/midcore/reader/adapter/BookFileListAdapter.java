package com.midcore.reader.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.glview.view.LayoutInflater;
import com.glview.view.View;
import com.glview.view.ViewGroup;
import com.glview.widget.BaseAdapter;
import com.glview.widget.ImageView;
import com.glview.widget.TextView;
import com.midcore.reader.R;
import com.midcore.reader.model.BookFile;

public class BookFileListAdapter extends BaseAdapter {

	Context mContext;
	
	LayoutInflater mLayoutInflater;
	
	List<BookFile> mData = new ArrayList<BookFile>();

	public BookFileListAdapter(Context context) {
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
	}

	public List<BookFile> getList() {
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
			convertView = mLayoutInflater.inflate(R.layout.book_file_list_item, parent, false);
		}
		BookFile book = (BookFile) getItem(position);
		TextView tv = (TextView) convertView.findViewById(R.id.item_name);
		tv.setText(book.name);
		ImageView iv = (ImageView) convertView.findViewById(R.id.item_icon);
		if (!book.isFile) {
			iv.setImageResource(R.drawable.file_type_folder);
		} else {
			try {
				int r = R.drawable.class.getField("file_type_" + book.ext).getInt(null);
				iv.setImageResource(r);
			} catch (Exception e) {
			}
		}
		return convertView;
	}

}
