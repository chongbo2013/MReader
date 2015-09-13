package com.midcore.reader.activity;

import android.os.Bundle;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.glview.app.GLActivity;
import com.glview.view.View;
import com.midcore.reader.Constant;

public class BaseActivity extends GLActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initBaiduPush();
	}

	@Override
	public void onAttached(View content) {
		super.onAttached(content);
	}
	
	private void initBaiduPush() {
		if (!PushManager.isPushEnabled(getApplicationContext())) {
			PushManager.startWork(this, PushConstants.LOGIN_TYPE_API_KEY, Constant.BAIDU_PUSH_APP_KEY);
		}
	}
	
}
