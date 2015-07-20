package com.midcore.reader.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.glview.view.View;
import com.glview.view.View.OnClickListener;
import com.glview.widget.ImageView;
import com.glview.widget.LinearLayout;
import com.glview.widget.SeekBar;
import com.glview.widget.SeekBar.OnSeekBarChangeListener;
import com.glview.widget.TextView;
import com.midcore.reader.R;
import com.midcore.reader.reader.settings.ReadSettings;
import com.midcore.reader.reader.settings.SettingsObserver;

public class BrightMenuLayout extends LinearLayout implements OnClickListener, OnSeekBarChangeListener, SettingsObserver {
	
	private View mEyeProtectionView;
	private ImageView mEyeProtectionIcon;
	private TextView mEyeProtectionText;
	
	private View mBrightSystemView;
	private ImageView mBrightSystemIcon;
	private TextView mBrightSystemText;
	
	private SeekBar mBrightSeekBar;
	
	private View mEyeProtectionCover;
	
	boolean mEyeProtection;
	boolean mBrightSystem;
	int mBright;
	
	public BrightMenuLayout(Context context) {
		super(context);
	}

	public BrightMenuLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BrightMenuLayout(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public BrightMenuLayout(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mEyeProtectionView = findViewById(R.id.eye_protection);
		mEyeProtectionView.setOnClickListener(this);
		mEyeProtectionIcon = (ImageView) findViewById(R.id.eye_protection_icon);
		mEyeProtectionText = (TextView) findViewById(R.id.eye_protection_text);
		
		mBrightSystemView = findViewById(R.id.bright_system);
		mBrightSystemView.setOnClickListener(this);
		mBrightSystemIcon = (ImageView) findViewById(R.id.bright_system_icon);
		mBrightSystemText = (TextView) findViewById(R.id.bright_system_text);
		
		mBrightSeekBar = (SeekBar) findViewById(R.id.bright_seek);
		mBrightSeekBar.setOnSeekBarChangeListener(this);
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		mEyeProtectionCover = getParent().findViewById(R.id.eye_protection_view);
		mEyeProtection = ReadSettings.getEyeProtection(getContext());
		updateEyeProtection();
		mBrightSystem = ReadSettings.getBrightSystem(getContext());
		mBright = ReadSettings.getBright(getContext());
		updateBright();
		ReadSettings.addSettingsObserver(this);
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		ReadSettings.removeSettingsObserver(this);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event) || true;
	}
	
	public void show() {
		Fade.fadeIn(this, Fade.BOTTOM);
	}
	
	public void hide() {
		Fade.fadeOut(this, Fade.BOTTOM);
	}
	
	private void updateEyeProtection() {
		if (mEyeProtection) {
			mEyeProtectionIcon.setImageResource(R.drawable.menu_eyes_icon2);
			mEyeProtectionText.setTextColor(getContext().getResources().getColor(R.color.eye_protection_on_color));
			mEyeProtectionCover.setVisibility(View.VISIBLE);
		} else {
			mEyeProtectionIcon.setImageResource(R.drawable.menu_eyes_icon1);
			mEyeProtectionText.setTextColor(getContext().getResources().getColor(R.color.eye_protection_off_color));
			mEyeProtectionCover.setVisibility(View.GONE);
		}
	}
	
	Runnable mBrightRunnable = new Runnable() {
		@Override
		public void run() {
			if (getContext() instanceof Activity) {
				Activity activity = (Activity) getContext();
				WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
				if (mBrightSystem) {
					lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
				} else {
					lp.screenBrightness = mBright * 1.0f / 255;
				}
				activity.getWindow().setAttributes(lp);
			}
		}
	};
	
	private void updateBright() {
		removeCallbacksFromAndroid(mBrightRunnable);
		postToAndroid(mBrightRunnable);
		
		if (mBrightSystem) {
			mBrightSystemIcon.setImageResource(R.drawable.menu_light_icon2);
			mBrightSystemText.setTextColor(getContext().getResources().getColor(R.color.bright_on_color));
		} else {
			mBrightSystemIcon.setImageResource(R.drawable.menu_light_icon1);
			mBrightSystemText.setTextColor(getContext().getResources().getColor(R.color.bright_off_color));
		}
		mBrightSeekBar.setProgress(ReadSettings.getBright(getContext()));
	}
	
	@Override
	public void onClick(View v) {
		if (v == mEyeProtectionView) {
			ReadSettings.setEyeProtection(getContext(), !ReadSettings.getEyeProtection(getContext()));
		} else if (v == mBrightSystemView) {
			ReadSettings.setBrightSystem(getContext(), !ReadSettings.getBrightSystem(getContext()));
		}
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (fromUser) {
			ReadSettings.setBright(getContext(), progress);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onChange(String key, Object oldValue, Object newValue) {
		if (ReadSettings.EYE_PROTECTION.equals(key)) {
			mEyeProtection = (Boolean) newValue;
			updateEyeProtection();
		} else if (ReadSettings.BRIGHT_SYSTEM.equals(key)) {
			mBrightSystem = (Boolean) newValue;
			updateBright();
		} else if (ReadSettings.BRIGHT.equals(key)) {
			mBright = (Integer) newValue;
			if (mBrightSystem) {
				ReadSettings.setBrightSystem(getContext(), false);
			}
			updateBright();
		}
	}

}
