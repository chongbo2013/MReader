/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.midcore.reader.dampedspring;

import android.content.Context;

import com.glview.libgdx.graphics.math.Vector2;
import com.glview.view.animation.AnimationUtils;

/**
 * This class encapsulates scrolling with the ability to overshoot the bounds
 * of a scrolling operation. This class is a drop-in replacement for
 * {@link android.widget.Scroller} in most cases.
 */
public class DampedSpringScroller {
	private final static String TAG = "DampedSpringScroller";
    private Context mContext;

    protected sDampedSpringPosition2 msDampedSpringPosition2;    
    
    	
    Vector2 mTarget = new Vector2();
    
    /**
     * Creates an OverScroller with a viscous fluid scroll interpolator and flywheel.
     * @param context
     */
    public DampedSpringScroller(Context context) {
    	mContext = context;
    	
    	msDampedSpringPosition2 = new sDampedSpringPosition2(); 	
    	msDampedSpringPosition2.SetSpeed(5.0f);
    	
    	msDampedSpringPosition2.SetCurrentPosition(Vector2.Zero);
    	
    	msDampedSpringPosition2.SetEndThreshold(0.5f);
    	
    	mTarget.set(Vector2.Zero);
    }
    
    public void setDampedParam(DampedSpringParam param){
    	msDampedSpringPosition2.SetDampedParam(param);
    }
    
    
	public void setStiffnessAndDampingRatio(float aKs, float E) {
		msDampedSpringPosition2.SetStiffnessAndDampingRatio(aKs, E);
	}
    
    public void setSpeed(float speed){
    	msDampedSpringPosition2.SetSpeed(speed);
    }
    
    
    public void setEndThreshold(float threshold){
    	msDampedSpringPosition2.SetEndThreshold(threshold);
    }
    
    /**
     *
     * Returns whether the scroller has finished scrolling.
     *
     * @return True if the scroller has finished scrolling, false otherwise.
     */
    public final boolean isFinished() {
        return msDampedSpringPosition2.GetIsEnded();
    }

    /**
     * Returns the current X offset in the scroll.
     *
     * @return The new X offset as an absolute distance from the origin.
     */
    public final int getCurrX() {
        Vector2 v2 = msDampedSpringPosition2.GetCurrentPosition();
        
        return (int)v2.x;
    }

    /**
     * Returns the current Y offset in the scroll.
     *
     * @return The new Y offset as an absolute distance from the origin.
     */
    public final int getCurrY() {
        Vector2 v2 = msDampedSpringPosition2.GetCurrentPosition();
        
        return (int)v2.y;
    }

    /**
     * Returns where the scroll will end. Valid only for "fling" scrolls.
     *
     * @return The final X offset as an absolute distance from the origin.
     */
    public final int getFinalX() {
        Vector2 v2 = msDampedSpringPosition2.GetIdealPosition();
        
        return (int)v2.x;
    }
    

    /**
     * Returns where the scroll will end. Valid only for "fling" scrolls.
     *
     * @return The final Y offset as an absolute distance from the origin.
     */
    public final int getFinalY() {
        Vector2 v2 = msDampedSpringPosition2.GetIdealPosition();
        
        return (int)v2.y;
    }

    public void setFinalPosition(float newX, float newY) {
    	Vector2 v2 = new Vector2(newX, newY);
    	msDampedSpringPosition2.SetIdealPosition(v2);
    }
 
    public void setFinalPosition(int newX, int newY) {
    	Vector2 v2 = new Vector2(newX, newY);
    	msDampedSpringPosition2.SetIdealPosition(v2);
    }
    
    public void setCurrPosition(int newX, int newY) {
    	Vector2 v2 = new Vector2(newX, newY);
    	msDampedSpringPosition2.SetCurrentPosition(v2);
    }

    /**
     * Call this when you want to know the new location. If it returns true, the
     * animation is not yet finished.
     */
    public boolean computeScrollOffset() {
        if (isFinished()) {
            return false;
        }
        
        return msDampedSpringPosition2.advanceAnimation(AnimationUtils.currentAnimationTimeMillis());

//        return true;
    }
    
    public void forceFinished(boolean finished){
    	if(finished){
    		msDampedSpringPosition2.SetIdealPosition(msDampedSpringPosition2.GetCurrentPosition());
    	}
    }

    
    

    /**
     * Start scrolling by providing a starting point and the distance to travel.
     *
     * @param startX Starting horizontal scroll offset in pixels. Positive
     *        numbers will scroll the content to the left.
     * @param startY Starting vertical scroll offset in pixels. Positive numbers
     *        will scroll the content up.
     * @param dx Horizontal distance to travel. Positive numbers will scroll the
     *        content to the left.
     * @param dy Vertical distance to travel. Positive numbers will scroll the
     *        content up.
     * @param duration Duration of the scroll in milliseconds.
     */
    public void startScrollToTarget(float x, float y) {    	    		
    	mTarget.set(x, y);    	
    	
    	msDampedSpringPosition2.SetIdealPosition(mTarget);
//    	Log.v(TAG, "mTarget = " + mTarget);
    }
    
    public void startScrollToDeltaTarget(int dx, int dy){    	    	
    	Vector2 v = msDampedSpringPosition2.GetIdealPosition();    
    	
    	startScrollToTarget((int)(v.x + dx), (int)(v.y + dy));
    }
    
    
    public void startScrollToDeltaCurr(int dx, int dy){    	    	
    	Vector2 v = msDampedSpringPosition2.GetCurrentPosition();    	
    	
    	startScrollToTarget((int)(v.x + dx), (int)(v.y + dy));
    }
    
    
    public void setScrollToTarget(int x, int y){
    	Vector2 v = new Vector2(x, y);
    	msDampedSpringPosition2.SetCurrentPosition(v);
    	msDampedSpringPosition2.SetIdealPosition(v);
    }
}
