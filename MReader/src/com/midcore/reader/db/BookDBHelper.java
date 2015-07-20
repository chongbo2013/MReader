/**
 * < CoolReader Database operator.>
 *  Copyright (C) <2009>  <Wang XinFeng,ACC http://androidos.cc/dev>
 *
 *   This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package com.midcore.reader.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.midcore.reader.Constant;
import com.midcore.reader.model.Book;

/**
 * Database operator
 * 
 * @author lijing.lj
 */
public class BookDBHelper extends SQLiteOpenHelper {
	
	private final static String TAG = Constant.TAG;
	
	public final static String DB_NAME = "book.db";
	public final static int DB_VERSION = 1;
	
	private final static String TABLE_SETTINGS_TABLE = "settings";
	private final static String TABLE_SETTINGS_KEY = "key";
	private final static String TABLE_SETTINGS_VALUE = "value";
	
	private final static String TABLE_BOOK_TABLE = "books";
	private final static String TABLE_BOOK_BOOK_ID = "book_id";
	private final static String TABLE_BOOK_BOOK_TITLE = "book_title";
	private final static String TABLE_BOOK_BOOK_FILE = "book_file";
	private final static String TABLE_BOOK_BOOK_PRESET_FILE = "book_file_preset";
	private final static String TABLE_BOOK_BOOK_TYPE = "book_type";
	private final static String TABLE_BOOK_BOOK_COVER = "book_cover";
	private final static String TABLE_BOOK_READ_POSITION = "read_position";
	private final static String TABLE_BOOK_FILE_SIGN = "file_sign";
	private final static String TABLE_BOOK_ADD_TIME = "add_time";
	private final static String TABLE_BOOK_READ_TIME = "read_time";
	
	private SQLiteDatabase mDatabase = null;
	
	private static BookDBHelper sInstance;
	
	public static BookDBHelper get(Context c) {
		if (sInstance == null) {
			sInstance = new BookDBHelper(c);
		}
		return sInstance;
	}

	private BookDBHelper(Context c) {
		super(c, DB_NAME, null, DB_VERSION);
		getDatabase();
	}
	
	private SQLiteDatabase getDatabase() {
		if (mDatabase == null) {
			mDatabase = getWritableDatabase();
		}
		return mDatabase;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "start create table");
		db.execSQL(
				"CREATE TABLE IF NOT EXISTS " + TABLE_BOOK_TABLE + "(" +
					TABLE_BOOK_BOOK_ID + " INTEGER PRIMARY KEY," +
					TABLE_BOOK_BOOK_TITLE + " TEXT NOT NULL," +
					TABLE_BOOK_BOOK_FILE + " TEXT UNIQUE NOT NULL, " +
					TABLE_BOOK_BOOK_PRESET_FILE + " TEXT, " +
					TABLE_BOOK_BOOK_TYPE + " TEXT NOT NULL," + 
					TABLE_BOOK_BOOK_COVER + " TEXT," +
					TABLE_BOOK_READ_POSITION + " DOUBLE NOT NULL," + 
					TABLE_BOOK_FILE_SIGN + " INTEGER NOT NULL," + 
					TABLE_BOOK_ADD_TIME + " INTEGER NOT NULL," + 
					TABLE_BOOK_READ_TIME + " INTEGER NOT NULL" + ")");
		
		db.execSQL(
				"CREATE TABLE IF NOT EXISTS " + TABLE_SETTINGS_TABLE + "(" +
					TABLE_SETTINGS_KEY + " TEXT PRIMARY KEY," +
					TABLE_SETTINGS_VALUE + " TEXT NOT NULL)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
	/** save the book info to database */
	public int insertBook(Book book) {
		Log.d(TAG, "insert the book into database");
		if (queryBook(book.file) == null) {
			ContentValues values = new ContentValues();
			values.put(TABLE_BOOK_BOOK_FILE, book.file);
			values.put(TABLE_BOOK_BOOK_PRESET_FILE, book.presetFile);
			values.put(TABLE_BOOK_BOOK_TITLE, book.title);
			values.put(TABLE_BOOK_BOOK_TYPE, book.type);
			values.put(TABLE_BOOK_BOOK_COVER, book.cover);
			values.put(TABLE_BOOK_READ_POSITION, book.readPosition);
			values.put(TABLE_BOOK_FILE_SIGN, book.fileSign);
			long time = System.currentTimeMillis();
			values.put(TABLE_BOOK_ADD_TIME, time);
			values.put(TABLE_BOOK_READ_TIME, time);
			book.id = (int) getDatabase().insert(TABLE_BOOK_TABLE, null, values);
			return book.id;
		}
		return -1;
	}
	
	public Book queryBook(String file) {
		Log.d(TAG, "query the book form database");
		Log.d(TAG, "query the book file:" + file);
		String[] col = new String[] {TABLE_BOOK_BOOK_ID, TABLE_BOOK_BOOK_FILE, TABLE_BOOK_BOOK_PRESET_FILE, TABLE_BOOK_BOOK_TITLE, TABLE_BOOK_BOOK_TYPE, TABLE_BOOK_BOOK_COVER, TABLE_BOOK_READ_POSITION, TABLE_BOOK_FILE_SIGN};
		Cursor cur = getDatabase().query(TABLE_BOOK_TABLE, col, TABLE_BOOK_BOOK_FILE + "=?", new String[]{file}, null, null, null);
		if (cur.moveToFirst()) {
			Book book = new Book();
			book.id = cur.getInt(0);
			book.file = cur.getString(1);
			book.presetFile = cur.getString(2);
			book.title = cur.getString(3);
			book.type = cur.getString(4);
			book.cover = cur.getString(5);
			book.readPosition = cur.getDouble(6);
			book.fileSign = cur.getLong(7);
			cur.close();
			return book;
		}
		return null;
	}
	
	public int updateBook(Book book) {
		ContentValues values = new ContentValues();
		values.put(TABLE_BOOK_BOOK_FILE, book.file);
		values.put(TABLE_BOOK_BOOK_PRESET_FILE, book.presetFile);
		values.put(TABLE_BOOK_BOOK_TITLE, book.title);
		values.put(TABLE_BOOK_BOOK_TYPE, book.type);
		values.put(TABLE_BOOK_BOOK_COVER, book.cover);
		values.put(TABLE_BOOK_READ_POSITION, book.readPosition);
		values.put(TABLE_BOOK_FILE_SIGN, book.fileSign);
		return getDatabase().update(TABLE_BOOK_TABLE, values, TABLE_BOOK_BOOK_ID + "=?", new String[]{String.valueOf(book.id)});
	}
	
	public int updateReadTime(Book book) {
		ContentValues values = new ContentValues();
		values.put(TABLE_BOOK_READ_TIME, System.currentTimeMillis());
		return getDatabase().update(TABLE_BOOK_TABLE, values, TABLE_BOOK_BOOK_ID + "=?", new String[]{String.valueOf(book.id)});
	}
	
	public List<Book> queryAllBooks() {
		List<Book> list = new ArrayList<Book>();
		Log.d(TAG, "query all books form database");
		String[] col = new String[] {TABLE_BOOK_BOOK_ID, TABLE_BOOK_BOOK_FILE, TABLE_BOOK_BOOK_PRESET_FILE, TABLE_BOOK_BOOK_TITLE, TABLE_BOOK_BOOK_TYPE, TABLE_BOOK_BOOK_COVER, TABLE_BOOK_READ_POSITION, TABLE_BOOK_FILE_SIGN};
		Cursor cur = getDatabase().query(TABLE_BOOK_TABLE, col, null, null, null, null, TABLE_BOOK_READ_TIME + " desc");
		while (cur.moveToNext()) {
			Book book = new Book();
			book.id = cur.getInt(0);
			book.file = cur.getString(1);
			book.presetFile = cur.getString(2);
			book.title = cur.getString(3);
			book.type = cur.getString(4);
			book.cover = cur.getString(5);
			book.readPosition = cur.getDouble(6);
			book.fileSign = cur.getLong(7);
			list.add(book);
		}
		cur.close();
		return list;
	}
	
	public List<Book> queryAllPresetBooks() {
		List<Book> list = new ArrayList<Book>();
		Log.d(TAG, "query all preset books form database");
		String[] col = new String[] {TABLE_BOOK_BOOK_ID, TABLE_BOOK_BOOK_FILE, TABLE_BOOK_BOOK_PRESET_FILE, TABLE_BOOK_BOOK_TITLE, TABLE_BOOK_BOOK_TYPE, TABLE_BOOK_BOOK_COVER, TABLE_BOOK_READ_POSITION, TABLE_BOOK_FILE_SIGN};
		Cursor cur = getDatabase().query(TABLE_BOOK_TABLE, col, TABLE_BOOK_BOOK_PRESET_FILE + " is not null", null, null, null, TABLE_BOOK_READ_TIME + " desc");
		while (cur.moveToNext()) {
			Book book = new Book();
			book.id = cur.getInt(0);
			book.file = cur.getString(1);
			book.presetFile = cur.getString(2);
			book.title = cur.getString(3);
			book.type = cur.getString(4);
			book.cover = cur.getString(5);
			book.readPosition = cur.getDouble(6);
			book.fileSign = cur.getLong(7);
			list.add(book);
		}
		cur.close();
		return list;
	}
	
	public int deleteBook(Book book) {
		return deleteBook(book.id);
	}
	
	public int deleteBook(int id) {
		return getDatabase().delete(TABLE_BOOK_TABLE, TABLE_BOOK_BOOK_ID + "=?", new String[]{String.valueOf(id)});
	}
	
	public String getSettingsValue(String key) {
		String value = null;
		String[] col = new String[] {TABLE_SETTINGS_VALUE};
		Cursor cur = getDatabase().query(TABLE_SETTINGS_TABLE, col, TABLE_SETTINGS_KEY + "=?", new String[]{key}, null, null, null);
		if (cur.moveToNext()) {
			value = cur.getString(0);
		}
		cur.close();
		return value;
	}
	
	public void addOrUpdateSettingsValue(String key, String value) {
		String oldValue = getSettingsValue(key);
		if (oldValue != null) {
			if (!oldValue.equals(value)) {
				ContentValues values = new ContentValues();
				values.put(TABLE_SETTINGS_VALUE, value);
				getDatabase().update(TABLE_SETTINGS_TABLE, values, TABLE_SETTINGS_KEY + "=?", new String[]{key}); 
			}
		} else {
			ContentValues values = new ContentValues();
			values.put(TABLE_SETTINGS_KEY, key);
			values.put(TABLE_SETTINGS_VALUE, value);
			getDatabase().insert(TABLE_SETTINGS_TABLE, null, values);
		}
	}

}