/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 ******************************************************************************/

package com.midcore.reader.dampedspring;

import com.glview.libgdx.graphics.math.Vector;

/*

//! Integrate one frame of the spring simulation using a simple euler method.
template <typename T, typename TF>
void DampedSpringIntegrateFrameEuler(T& aP, T& aV, const T& I, const TF Ks, const TF Kd, const TF dT) {
const T acc = ni::DampedSpringAcceleration(aP-I,aV,Ks,Kd);
aV += acc * dT;
aP += aV * dT;
}

template <typename T>
inline tF32 _DampedSpringDistance(const T& a, const T&b) { niCAssert(-1); return -1;  }

template <>
inline tF32 _DampedSpringDistance<tF32>(const tF32& a, const tF32& b) {
return ni::Abs(a-b);
}
template <>
inline tF32 _DampedSpringDistance<sVector2f>(const sVector2f& a, const sVector2f& b) {
return ni::VecLength(a-b);
}
template <>
inline tF32 _DampedSpringDistance<sVector3f>(const sVector3f& a, const sVector3f& b) {
return ni::VecLength(a-b);
}
template <>
inline tF32 _DampedSpringDistance<sVector4f>(const sVector4f& a, const sVector4f& b) {
return ni::VecLength(a-b);
}
*/


//! Damped spring with position helper template.
//template <typename T, typename TF, typename BASE = ni::cUnknown, typename TREF = const T&>
public class sDampedSpringPosition<T extends Vector<T>> extends sDampedSpring<T> {
	protected T   mvIdealPos;
	protected T   mvCurrentPos;
	
	protected T mApTem;
	protected T mAvTem;
	
	
	protected T mOutPutTem;
	protected T mVcpyTem;
	
	private float  mfEndThreshold;
	private float  mfStep;
	private float  mfSpeed;
	
	private static final float niEpsilon3  = 0.001f;//#define niEpsilon3  1e-3f
	
	public sDampedSpringPosition() {
		//ni::MemZero((tPtr)&mvIdealPos,sizeof(mvIdealPos));
		//ni::MemZero((tPtr)&mvCurrentPos,sizeof(mvCurrentPos));
	  
		//mvIdealPos   = (T)(new Vector<?>());
		//mvCurrentPos = (T)(new Vector<?>());
	  
		mfEndThreshold = niEpsilon3;
		mfStep = kfDampedSpringUpdateDefaultTimeStep;
		mfSpeed = 1.0f;
	}
	
	public void SetDampedParam(DampedSpringParam param){
		SetSpeed(param.speed);
		SetEndThreshold(param.endThreshold);		
		SetStiffnessAndDampingRatio(param.afKs, param.afE);
	}
	
	public void SetSpeed(float afSpeed) {
		mfSpeed = afSpeed;
	}
	public float getSpeed(){
		return mfSpeed;
	}
	
	public void SetStep(float afStep) {
		mfStep = afStep;
	}
	public float GetStep() {
		return mfStep;
	}
	
	public void SetIdealPosition(T avPos) {
		mvIdealPos.set(avPos);
	}
	public T GetIdealPosition(){
		return mvIdealPos;
	}
	public void SetCurrentPosition(T avPos) {
		mvCurrentPos.set(avPos);
	}
	public T GetCurrentPosition(){
		return mvCurrentPos;
	}
	
	public void SetEndThreshold(float afEndThreshold) {
		mfEndThreshold = afEndThreshold;
	}
	public float GetEndThreshold() {
		return mfEndThreshold;
	}
	
	public boolean GetIsEnded() {
		float fD = _DampedSpringDistance(mvIdealPos,mvCurrentPos);
		return (fD < mfEndThreshold);
	}
	
	public T UpdatePosition(float afDeltaTime) {
		if (super.mKs == 0.0f || super.mKd == 0.0f) {
			  mvCurrentPos.set(mvIdealPos);
		}
		else {
			float fUpdateTime = afDeltaTime*mfSpeed;
		    if (mfStep != 0) {
		    	while (fUpdateTime > mfStep) {
		    		DampedSpringIntegrateFrameEuler(mvCurrentPos,super.mvVel,mvIdealPos,
		        		super.mKs,super.mKd,mfStep);
		    		fUpdateTime -= mfStep;
		    	}
		    }
		    if (fUpdateTime > 0.0) {
		    	DampedSpringIntegrateFrameEuler(mvCurrentPos,super.mvVel,mvIdealPos,
		    		  super.mKs,super.mKd,fUpdateTime);
		    }
		}
		
		return mvCurrentPos;
	}
	
	
    public boolean advanceAnimation(long currentTimeMillis){
    	boolean ret = false;    	
    	
    	
		float timedelta = 0;
		timedelta = 1 / 30f;//Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f);
		
		if(!GetIsEnded()){
			float avD = 0.0f;
			//msDampedSpringPosition.UpdateVelocity(timedelta, avD);
    		UpdatePosition(timedelta);
    		ret = true;
		}
		
		if(ret && GetIsEnded()){
			SetCurrentPosition(GetIdealPosition());
		}
    	
    	
    	return ret;
    }
	
	public float _DampedSpringDistance(T a, T b) {
		return a.dst(b);
	}
	
	
	void DampedSpringIntegrateFrameEuler(T aP, T aV, T iP, float Ks, float Kd, float dT) {		
		T apTem = mApTem.set(aP);
		T acc = (T)DampedSpringAcceleration(mOutPutTem, mVcpyTem, (T)apTem.sub(iP),aV,Ks,Kd);
		//aV += acc * dT;
		//aP += aV * dT;		
		aV.add(acc.scl(dT));
		
		T avTemp = mAvTem.set(aV);		
		aP.add(avTemp.scl(dT));
	}
}