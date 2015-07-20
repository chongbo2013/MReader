package com.midcore.reader.reader.txt;

import java.util.ArrayList;
import java.util.List;

import com.midcore.reader.reader.Page;

public class TxtPage extends Page {
	
	public TxtChapter chapter;

	public List<TxtLine> lines = new ArrayList<TxtLine>();
	
	public int offset;
	
}
