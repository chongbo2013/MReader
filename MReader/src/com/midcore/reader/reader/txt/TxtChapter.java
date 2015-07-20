package com.midcore.reader.reader.txt;

import java.util.ArrayList;
import java.util.List;

import com.midcore.reader.reader.Chapter;

public class TxtChapter extends Chapter {

	private static final long serialVersionUID = 1L;
	
	public int offset;
	public int length;
	
	public TxtBlock block;
	
	public transient List<TxtPage> pages = new ArrayList<TxtPage>();
	public transient TxtPage currentPage;
	
	public int getCurrentPageIndex() {
		return currentPage != null ? pages.indexOf(currentPage) : -1;
	}
	
	public boolean isTitleInContent() {
		return true;
	}

	@Override
	public String toString() {
		return String.format("title:%s, offset:%d, length:%d", title, offset, length);
	}
}
