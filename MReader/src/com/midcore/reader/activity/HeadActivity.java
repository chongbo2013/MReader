package com.midcore.reader.activity;

import com.glview.view.View;
import com.glview.widget.ImageView;
import com.glview.widget.TextView;
import com.midcore.reader.R;

public abstract class HeadActivity extends BaseActivity {
	
	protected TextView mTitle;
	protected ImageView mLeft, mRight;

	@Override
	public void onAttached(View content) {
		super.onAttached(content);
		mTitle = (TextView) content.findViewById(R.id.head_title);
		mLeft = (ImageView) content.findViewById(R.id.head_left);
		mRight = (ImageView) content.findViewById(R.id.head_right);
	}
	
	protected void initHead(int title, int left, int right) {
		mTitle.setText(getString(title));
		mLeft.setImageResource(left);
		mRight.setImageResource(right);
	}
	
	public void onClick(View v) {
	}
}
