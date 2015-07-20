package com.midcore.reader.reader.effect;

import android.content.Context;
import android.graphics.Color;

import com.glview.graphics.shader.LinearGradient;
import com.glview.hwui.GLCanvas;
import com.glview.hwui.GLPaint;
import com.glview.view.View;
import com.midcore.reader.reader.BitmapPage;
import com.midcore.reader.reader.effect.mesh.SimulationYMesh;

/**
 * 现在只支持以垂直方向有边界为中心轴的翻页效果，
 * 以书角为中心的翻页效果，算法比较复杂，暂未实现
 * 
 * @author lijing.lj
 */
public class SimulationEffect extends PageSwitchEffect {
	
	final static float RIGHT_SHADOW_SIZE = 100;
	final static float LEFT_SHADOW_SIZE = 50;
	final static float LIGHT_SIZE = 50;
	
	SimulationYMesh mMesh;
	
	final static int STYLE_VERTICAL = 1;
	final static int STYLE_BOTTOM_RIGHT = 3;
	final static int STYLE_TOP_RIGHT = 1;
	
	int mStyle = STYLE_VERTICAL;

	LinearGradient mRightShadowShader = new LinearGradient(0, 0, RIGHT_SHADOW_SIZE, 0, 0x88000000, Color.TRANSPARENT);
	LinearGradient mLeftShadowShader = new LinearGradient(LEFT_SHADOW_SIZE, 0, 0, 0, 0x88000000, Color.TRANSPARENT);
	LinearGradient mLightShadowShader = new LinearGradient(LIGHT_SIZE, 0, 0, 0, 0x55888888, Color.TRANSPARENT);
	
	GLPaint mPaint = new GLPaint();
	
	public SimulationEffect(Context context, View view) {
		super(context, view);
	}
	
	@Override
	public void setSize(int w, int h) {
		super.setSize(w, h);
		mMesh = new SimulationYMesh(w, h);
	}
	
	@Override
	protected int getStartPosition() {
		return - mWidth;
	}
	
	@Override
	void innerDraw(GLCanvas canvas, BitmapPage page1, BitmapPage page2, boolean forward) {
		if (mStyle == STYLE_VERTICAL) {
			mMesh.setCurrentPosition(mCurrentDistance);
			if (forward) {
				canvas.drawBitmap(page2.getBitmap(), null, mRect, null);
				canvas.drawBitmapMesh(page1.getBitmap(), mMesh, null);
			} else {
				canvas.drawBitmap(page1.getBitmap(), null, mRect, null);
				canvas.drawBitmapMesh(page2.getBitmap(), mMesh, null);
			}
			// TODO绘制反面的内容
			
			mPaint.setAlpha(255);
			float leftShadow = mMesh.getLeftShadowPosition();
			float rightShadow = mMesh.getRightShadowPosition();
			
			canvas.save();
			if (rightShadow < RIGHT_SHADOW_SIZE) {
				mPaint.setAlpha((int) ((rightShadow) / RIGHT_SHADOW_SIZE * 255));
			}
			canvas.translate(mMesh.getRightShadowPosition(), 0);
			mPaint.setShader(mRightShadowShader);
			canvas.drawRect(0, 0, RIGHT_SHADOW_SIZE, mHeight, mPaint);
			
			if (rightShadow > leftShadow) {
				float lightSize = rightShadow - leftShadow;
				if (lightSize > LIGHT_SIZE) {
					lightSize = LIGHT_SIZE;
				}
				mPaint.setAlpha(255);
				canvas.translate(- lightSize, 0);
				mPaint.setShader(mLightShadowShader);
				canvas.drawRect(0, 0, lightSize, mHeight, mPaint);
			}
			canvas.restore();
			
			canvas.translate(leftShadow - LEFT_SHADOW_SIZE, 0);
			mPaint.setShader(mLeftShadowShader);
			if (mWidth - leftShadow < LEFT_SHADOW_SIZE) {
				mPaint.setAlpha((int) ((mWidth - leftShadow) / LEFT_SHADOW_SIZE * 255));
			}
			canvas.drawRect(0, 0, LEFT_SHADOW_SIZE, mHeight, mPaint);
		}
	}

}
