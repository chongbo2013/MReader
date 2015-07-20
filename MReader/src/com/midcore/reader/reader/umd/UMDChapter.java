package com.midcore.reader.reader.umd;

import com.midcore.reader.reader.txt.TxtChapter;

public class UMDChapter extends TxtChapter {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean isTitleInContent() {
		return false;
	}
}
