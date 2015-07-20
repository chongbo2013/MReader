package com.midcore.reader.view;

import android.content.Context;

import com.glview.view.View;
import com.glview.view.animation.Animation;
import com.glview.view.animation.AnimationUtils;
import com.midcore.reader.R;

public class Fade {
	
	public final static int LEFT = 1;
	public final static int TOP = 2;
	public final static int RIGHT = 3;
	public final static int BOTTOM = 4;
	
	public static void fadeIn(View view, int direction) {
		if (view.getVisibility() != View.VISIBLE) {
			view.setVisibility(View.VISIBLE);
			Animation animation = loadAnimation(view.getContext(), true, direction);
			if (animation != null) {
				view.startAnimation(animation);
			}
		}
	}
	
	public static void fadeOut(View view, int direction) {
		if (view.getVisibility() == View.VISIBLE) {
			view.setVisibility(View.GONE);
			Animation animation = loadAnimation(view.getContext(), false, direction);
			if (animation != null) {
				view.startAnimation(animation);
			}
		}
	}
	
	private static Animation loadAnimation(Context context, boolean in, int direction) {
		int anim = 0;
		switch (direction) {
		case LEFT:
			anim = in ? R.anim.left_fade_in : R.anim.left_fade_out;
			break;
		case TOP:
			anim = in ? R.anim.top_fade_in : R.anim.top_fade_out;
			break;
		case RIGHT:
			anim = in ? R.anim.right_fade_in : R.anim.right_fade_out;
			break;
		case BOTTOM:
			anim = in ? R.anim.bottom_fade_in : R.anim.bottom_fade_out;
			break;
		default:
			break;
		}
		if (anim > 0) {
			return AnimationUtils.loadAnimation(context, anim);
		}
		return null;
	}

}
