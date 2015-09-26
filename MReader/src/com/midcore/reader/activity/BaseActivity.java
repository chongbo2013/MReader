package com.midcore.reader.activity;

import android.os.Bundle;
import android.view.KeyEvent;

import com.glview.app.GLActivity;
import com.glview.view.View;

public class BaseActivity extends GLActivity {
	
	MenuDialog mMenuDialog;
	
	boolean mResumed = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onAttached(View content) {
		super.onAttached(content);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mResumed = false;
		dismissMenuDialog();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mResumed = true;
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			showMenuDialog();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	
	protected void showMenuDialog() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				dismissMenuDialog();
				if (mResumed) {
					mMenuDialog = new MenuDialog(BaseActivity.this);
					try {
						mMenuDialog.show();
					} catch(Throwable tr) {}
				}
			}
		});
	}
	
	protected void dismissMenuDialog() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (mMenuDialog != null && mMenuDialog.isShowing()) {
					try {
						mMenuDialog.dismiss();
					} catch(Throwable tr) {}
					mMenuDialog = null;
				}
			}
		});
	}
	
	
}
