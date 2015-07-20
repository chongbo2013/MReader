package com.midcore.reader.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {

	public final static String read(InputStream is) {
		byte[] buffer = new byte[512];
		int length = 0;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			while ((length = is.read(buffer)) > 0) {
				bos.write(buffer, 0, length);
			}
			return new String(bos.toByteArray());
		} catch (IOException e) {
		} finally {
			try {
				is.close();
			} catch (IOException e) {
			}
		}
		return null;
	}
	
	public final static void copy(InputStream is, OutputStream os) {
		byte[] buffer = new byte[2 * 1024];
		int length = 0;
		try {
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		} catch (IOException e) {
		} finally {
			try {
				is.close();
			} catch (IOException e) {
			}
			try {
				os.close();
			} catch (IOException e) {
			}
		}
	}
}
