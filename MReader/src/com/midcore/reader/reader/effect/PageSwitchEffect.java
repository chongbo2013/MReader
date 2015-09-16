package com.midcore.reader.reader.effect;

import android.content.Context;
import android.view.MotionEvent;

import com.glview.graphics.Rect;
import com.glview.hwui.GLCanvas;
import com.glview.view.GestureDetector.OnGestureListener;
import com.glview.view.View;
import com.midcore.reader.dampedspring.DampedSpringParam;
import com.midcore.reader.dampedspring.DampedSpringScroller;
import com.midcore.reader.reader.BitmapPage;

public abstract class PageSwitchEffect implements OnGestureListener {
	
	public static enum SwitchState {
		CURRENT, TO_NEXT, TO_PREVIOUS;
	}
	
	public static enum FlingState {
		IDLE, BACKWARD, FORWARD
	}
	
	protected int mWidth, mHeight;
	Rect mRect = new Rect();
	
	private SwitchState mState = SwitchState.CURRENT;
	private FlingState mFlingState = FlingState.IDLE;
	
	private PageSwitchListener mListener;
	
	protected final Context mContext;
	protected final View mTargetView;
	
	DampedSpringScroller mDampedSpringScroller;
	
	float mCurrentDistance;
	boolean mFling = false;
	boolean mIsTouching = false;
	
	public PageSwitchEffect(Context context, View view) {
		mContext = context;
		mTargetView = view;
		
		mDampedSpringScroller = new DampedSpringScroller(context);
		mDampedSpringScroller.setDampedParam(generateDampedSpringParam());
	}
	
	protected DampedSpringParam generateDampedSpringParam() {
		DampedSpringParam param = new DampedSpringParam();
		param.speed = 3;
		param.afKs = 10;
		return param;
	}
	
	protected int getStartPosition() {
		return 0;
	}
	
	protected int getEndPosition() {
		return mWidth;
	}
	
	public void setPageSwitchListener(PageSwitchListener listener) {
		mListener = listener;
	}
	
	public void setSize(int w, int h) {
		mWidth = w;
		mHeight = h;
		mRect.set(0, 0, mWidth, mHeight);
	}
	
	public boolean isInSwitching() {
		return mState != SwitchState.CURRENT;
	}
	
	public SwitchState getSwitchState() {
		return mState;
	}
	
	public void startSwitch(boolean forward) {
		if (isInSwitching()) {
			stopSwitching();
		}
		mState = forward ? SwitchState.TO_NEXT : SwitchState.TO_PREVIOUS;
		mFlingState = FlingState.FORWARD;
		mDampedSpringScroller.setScrollToTarget(forward ? getEndPosition() : getStartPosition(), 0);
		if (mListener != null) {
			mListener.onSwitchStarted(mState);
		}
	}
	
	public FlingState getFlingState() {
		return mFlingState;
	}
	
	public void forward() {
		if (isInSwitching()) {
			mFlingState = FlingState.FORWARD;
		} else {
			mFlingState = FlingState.IDLE;
		}
	}
	
	public void backward() {
		if (isInSwitching()) {
			mFlingState = FlingState.BACKWARD;
		} else {
			mFlingState = FlingState.IDLE;
		}
	}
	
	public void stopSwitching() {
		if (mListener != null) {
			mListener.onSwitchFinished(mState, mFlingState);
		}
		mState = SwitchState.CURRENT;
		mFlingState = FlingState.IDLE;
		
		mDampedSpringScroller.setScrollToTarget(getStartPosition(), 0);
		mDampedSpringScroller.forceFinished(true);
		mFling = false;
		mCurrentDistance = 0;
	}
	
	protected void onSwitching() {
		if (mListener != null) {
			mListener.onSwitching(mState);
		}
	}
	
	public boolean draw(GLCanvas canvas, BitmapPage page1, BitmapPage page2, boolean forward) {
		boolean more = mDampedSpringScroller.computeScrollOffset();
		if (more) {
			mCurrentDistance = mDampedSpringScroller.getCurrX();
		}
		onSwitching();
		if (page1 == null || page2 == null) {
			more = false;
		} else {
			innerDraw(canvas, page1, page2, forward);
		}
		if (!more && !mIsTouching) {
			stopSwitching();
		}
		return more;
	}
	
	abstract void innerDraw(GLCanvas canvas, BitmapPage page1, BitmapPage page2, boolean forward);

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		if (!isInSwitching()) {
			mCurrentDistance = 0;
			return false;
		}
		float distance = 0;
		/*if (getSwitchState() == SwitchState.TO_NEXT) {
			distance = e2.getX() - e1.getX();
			if (distance > 0) distance = 0;
		} else if (getSwitchState() == SwitchState.TO_PREVIOUS) {
			distance = e2.getX();
		}*/
		distance = e2.getX();
		mDampedSpringScroller.startScrollToTarget(Math.abs(distance), 0);
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (velocityX == 0) return false;
		if (isInSwitching()) {
			mFling = true;
			if (getSwitchState() == SwitchState.TO_NEXT) {
				if (velocityX > 0) {
					backward();
				} else {
					forward();
				}
			} else {
				if (velocityX < 0) {
					backward();
				} else {
					forward();
				}
			}
			fling(velocityX);
			return true;
		}
		return false;
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		boolean r = false;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			r = onTouchDown(event);
			break;
		case MotionEvent.ACTION_UP:
			r = onTouchUp(event);
		default:
			break;
		}
		return r;
	}
	
	protected boolean onTouchDown(MotionEvent event) {
		mIsTouching = true;
		if (isInSwitching()) {
			stopSwitching();
		}
		return true;
	}
	
	protected boolean onTouchUp(MotionEvent event) {
		mIsTouching = false;
		if (isInSwitching() && !mFling) {
			if (mCurrentDistance < mWidth / 2) {
				backward();
			} else {
				forward();
			}
			fling(mWidth);
			return true;
		}
		return false;
	}
	
	private void fling(float velocity) {
		if (isInSwitching()) {
			if ((getSwitchState() == SwitchState.TO_NEXT && getFlingState() == FlingState.FORWARD) || (getSwitchState() == SwitchState.TO_PREVIOUS && getFlingState() == FlingState.BACKWARD)) {
				mDampedSpringScroller.startScrollToTarget(getStartPosition(), 0);
			} else {
				mDampedSpringScroller.startScrollToTarget(getEndPosition(), 0);
			}
		}
	}
	
	public static interface PageSwitchListener {
		public void onSwitchStarted(SwitchState state);
		public void onSwitchFinished(SwitchState state, FlingState flingState);
		public void onSwitching(SwitchState state);
	}
	
}
