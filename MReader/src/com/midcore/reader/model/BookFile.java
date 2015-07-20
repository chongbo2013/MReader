package com.midcore.reader.model;

import java.io.Serializable;

public class BookFile implements Serializable, Comparable<BookFile> {

	private static final long serialVersionUID = 1L;
	
	public String name;
	public String ext;
	public String path;
	public boolean isFile = false;

	public BookFile(String name, String path, boolean isFile) {
		this.name = name;
		this.path = path;
		this.isFile = isFile;
	}

	public BookFile(String name, String path, String ext, boolean isFile) {
		this(name, path, isFile);
		this.ext = ext;
	}

	@Override
	public int compareTo(BookFile another) {
		if (isFile && !another.isFile) {
			return -1;
		}
		if (!isFile && another.isFile) {
			return 1;
		}
		return name.compareTo(another.name);
	}

}
