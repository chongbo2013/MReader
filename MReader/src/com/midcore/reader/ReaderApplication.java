package com.midcore.reader;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.glview.app.GLApplication;
import com.midcore.reader.db.BookDBHelper;
import com.midcore.reader.model.Book;
import com.midcore.reader.utils.FileUtils;
import com.midcore.reader.utils.IOUtils;
import com.tencent.android.tpush.XGBasicPushNotificationBuilder;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGNotifaction;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.XGPushNotifactionCallback;

public class ReaderApplication extends GLApplication {

	final static String TAG = Constant.TAG;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		initPush();
		
		checkPresetBooks();
	}
	
	private void initPush() {
		
		// 在主进程设置信鸽相关的内容
		if (isMainProcess()) {
			// 为保证弹出通知前一定调用本方法，需要在application的onCreate注册
			// 收到通知时，会调用本回调函数。
			// 相当于这个回调会拦截在信鸽的弹出通知之前被截取
			// 一般上针对需要获取通知内容、标题，设置通知点击的跳转逻辑等等
			XGPushManager
					.setNotifactionCallback(new XGPushNotifactionCallback() {

						@Override
						public void handleNotify(XGNotifaction xGNotifaction) {
							Log.i(Constant.PUSH_TAG, "处理信鸽通知：" + xGNotifaction);
							// 获取标签、内容、自定义内容
							String title = xGNotifaction.getTitle();
							String content = xGNotifaction.getContent();
							String customContent = xGNotifaction
									.getCustomContent();
							// 其它的处理
							// 如果还要弹出通知，可直接调用以下代码或自己创建Notifaction，否则，本通知将不会弹出在通知栏中。
							xGNotifaction.doNotify();
						}
					});
			XGPushConfig.enableDebug(getApplicationContext(), true);
			// 注册接口
			XGPushManager.registerPush(getApplicationContext(), new XGIOperateCallback() {
				@Override
				public void onSuccess(Object data, int flag) {
					Log.d(Constant.PUSH_TAG, "注册成功，设备token为：" + data);
				}
				@Override
				public void onFail(Object data, int errCode, String msg) {
					Log.d(Constant.PUSH_TAG, "注册失败，错误码：" + errCode + ",错误信息：" + msg);
				}
			});
			XGBasicPushNotificationBuilder builder = new XGBasicPushNotificationBuilder();
			builder.setIcon(R.drawable.icon);
			builder.setFlags(Notification.FLAG_AUTO_CANCEL);
			builder.setDefaults(Notification.DEFAULT_VIBRATE);
			XGPushManager.setDefaultNotificationBuilder(getApplicationContext(), builder);
		}
	}
	
	public boolean isMainProcess() {
		ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
		List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
		String mainProcessName = getPackageName();
		int myPid = android.os.Process.myPid();
		for (RunningAppProcessInfo info : processInfos) {
			if (info.pid == myPid && mainProcessName.equals(info.processName)) {
				return true;
			}
		}
		return false;
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
