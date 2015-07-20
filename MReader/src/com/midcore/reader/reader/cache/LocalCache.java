package com.midcore.reader.reader.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;

public class LocalCache {
	
	private final File mBaseCachePath;

	private LocalCache(Context context) {
		mBaseCachePath = context.getCacheDir();
		mBaseCachePath.mkdirs();
	}
	
	private static LocalCache sInstance;
	
	public static LocalCache instance(Context context) {
		if (sInstance == null) {
			sInstance = new LocalCache(context);
		}
		return sInstance;
	}
	
	public String getCachePath() {
		return mBaseCachePath.getAbsolutePath();
	}
	
	public String getCachePath(String file) {
		return getCachePath() + File.separator + file;
	}
	
	public boolean writeData(String fileName, Object data) {
		File file = new File(mBaseCachePath, fileName);
		file.getParentFile().mkdirs();
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(data);
			return true;
		} catch (Exception e) {
			file.delete();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
				}
			}
		}
		return false;
	}
	
	public Object readData(String fileName) {
		File file = new File(mBaseCachePath, fileName);
		file.getParentFile().mkdirs();
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			return ois.readObject();
		} catch (Exception e) {
			file.delete();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
				}
			}
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}
}
