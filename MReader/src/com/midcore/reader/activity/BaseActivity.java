package com.midcore.reader.activity;

import android.os.Bundle;
import android.view.KeyEvent;

import com.glview.app.GLActivity;
import com.glview.view.View;

public class BaseActivity extends GLActivity {
	
	MenuDialog mMenuDialog;
	
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
		dismissMenuDialog();
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
		dismissMenuDialog();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mMenuDialog = new MenuDialog(BaseActivity.this);
				mMenuDialog.show();
			}
		});
	}
	
	protected void dismissMenuDialog() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (mMenuDialog != null && mMenuDialog.isShowing()) {
					mMenuDialog.dismiss();
					mMenuDialog = null;
				}
			}
		});
	}
	
	
}
