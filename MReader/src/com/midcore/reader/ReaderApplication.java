package com.midcore.reader;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.baidu.android.pushservice.BasicPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.glview.app.GLApplication;
import com.midcore.reader.db.BookDBHelper;
import com.midcore.reader.model.Book;
import com.midcore.reader.utils.FileUtils;
import com.midcore.reader.utils.IOUtils;

public class ReaderApplication extends GLApplication {

	final static String TAG = Constant.TAG;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		initBaiduPush();
		
		checkPresetBooks();
	}
	
	private void initBaiduPush() {
		BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder();
		builder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
		builder.setNotificationDefaults(Notification.DEFAULT_VIBRATE);
		builder.setStatusbarIcon(this.getApplicationInfo().icon);
		PushManager.setDefaultNotificationBuilder(getApplicationContext(), builder);
		
		PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, Constant.BAIDU_PUSH_APP_KEY);
	}
	
	private void checkPresetBooks() {
    	if (!checkVersion()) {
    		Log.d(TAG, "version Changed, check preset books!");
			try {
				String jsonStr = IOUtils.read(getAssets().open("books/books.json"));
				Log.d(TAG, "preset json=" + jsonStr);
				JSONArray jsonArray = new JSONArray(jsonStr);
				for (int i = 0; i < jsonArray.length(); i ++) {
					JSONObject json = jsonArray.getJSONObject(i);
					Book book = new Book();
					String presetFile = json.getString("presetFile");
					int dir = presetFile.lastIndexOf("/");
					book.presetFile = Book.PRESET_PREFIX + presetFile;
					book.type = json.getString("type");
					book.title = json.getString("title");
					book.file = FileUtils.getPresetBookPath(this, dir >= 0 ? presetFile.substring(dir) : presetFile);
					if (FileUtils.accept(book.type)) {
						BookDBHelper.get(this).insertBook(book);
					}
				}
			} catch (Exception e) {
				Log.w(TAG, "createPresetBooks", e);
			}
    	}
    }
    
    boolean checkVersion() {
		SharedPreferences sp = getSharedPreferences("reader", Context.MODE_PRIVATE);
		int lastVersion = sp.getInt("version", -1);
		int currentVersion = getVersionCode();
		if (lastVersion != currentVersion) {
			sp.edit().putInt("version", currentVersion).commit();
			sp.edit().putBoolean("inited", false);
			return false;
		}
		return true;
	}
	
	int getVersionCode() {
		try {
			return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
