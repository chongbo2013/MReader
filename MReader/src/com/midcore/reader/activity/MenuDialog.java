package com.midcore.reader.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.midcore.reader.R;

public class MenuDialog extends Dialog implements android.view.View.OnClickListener {
	
	View mAbout = null;

	public MenuDialog(Context context) {
		super(context, R.style.MenuDialogTheme);
		setCanceledOnTouchOutside(true);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		getWindow().setAttributes(lp);
		getWindow().getDecorView().setPadding(0, 0, 0, 0);
		
		setContentView(R.layout.layout_menu_dialog);
		mAbout = findViewById(R.id.menu_about);
		mAbout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == mAbout) {
			Intent intent = new Intent(getContext(), AbountActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getContext().startActivity(intent);
			dismiss();
		}
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			dismiss();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

}
