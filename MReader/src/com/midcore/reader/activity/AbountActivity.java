package com.midcore.reader.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.midcore.reader.R;

public class AbountActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_abount);
	}
	
	public void back(View v) {
		if (v.getId() == R.id.back) {
			finish();
		}
	}
}
