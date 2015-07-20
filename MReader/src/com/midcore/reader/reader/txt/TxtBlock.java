package com.midcore.reader.reader.txt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TxtBlock implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public int offset;
	public int length;
	
	public int stringOffset;
	public int stringLength;
	
	public List<TxtChapter> chapters = new ArrayList<TxtChapter>();

	public TxtBlock() {
	}

}
