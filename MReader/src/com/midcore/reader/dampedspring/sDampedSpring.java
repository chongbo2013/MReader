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

import android.util.Log;

import com.glview.libgdx.graphics.math.Vector;

public class sDampedSpring<T extends Vector<T>> implements iDampedSpring<T>
{
	static final float kfDampedSpringUpdateDefaultTimeStep = 1.0f/50.0f;
	
	
	public T DampedSpringAcceleration(T outPut, T Vcpy, T D, T V, float Ks, float Kd) {
		//return (D * -Ks) - (V * Kd);
		/*
		T DTemp = (T)D.cpy();
		T VTemp = (T)V.cpy();
		DTemp.scl(-Ks);
		VTemp.scl(Kd);
		return (T)DTemp.sub(VTemp); 
		*/
		
		outPut.set(D);
		Vcpy.set(V);
		
		outPut.scl(-Ks);
		Vcpy.scl(Kd);
		
		outPut.sub(Vcpy);
		
		return outPut;
		/*
		T DTemp = D;
		T VTemp = V;
		DTemp.scl(-Ks);
		VTemp.scl(Kd);
		return (T)DTemp.sub(VTemp); 
		*/
	}

	//! Get the damping ratio of a spring set with the specified Ks/Kd constants.
	
	public static float DampedSpringGetDampingRatio(float Ks, float Kd) {
	  return (float)(Kd / (2.0f*Math.sqrt(Ks)));
	}

	//! Compute the value of Kd for the specified Ks that will result in a spring with the specified damping ratio.
	public static float DampedSpringComputeKdFromDampingRatio(float Ks, float E) {
	  return (float)((2.0f*Math.sqrt(Ks))*E);
	}
	
	
	protected T mvVel;
	protected float  mKs, mKd;

	public sDampedSpring() {    
		//mvVel = new T();
		SetStiffnessAndDampingRatio(10.0f,1.0f);
	}

	
	public void SetKd(float aKd) {
		mKd = aKd;
	}
	public float GetKd() {
		return mKd;
	}
	public void SetKs(float aKs) {
		mKs = aKs;
	}
	public float GetKs() {
		return mKs;
	}

	public void SetStiffnessAndDampingRatio(float aKs, float E) {
		SetKs(aKs);
		SetKd(DampedSpringComputeKdFromDampingRatio(aKs,E));
    
		Log.v("sDampedValue", " mKd = " + mKd + " mKs = " + mKs);
	}

	public void SetDampingRatio(float E) {
		SetStiffnessAndDampingRatio(mKs,E);
	}
	public float GetDampingRatio() {
		return DampedSpringGetDampingRatio(mKs,mKd);
	}

	public void SetVelocity(T avVel) {
		mvVel = avVel;
	}
	public T GetVelocity() {
		return mvVel;
	}

	public T ComputeAcceleration(T avD) {
		return DampedSpringAcceleration(avD.cpy(), avD.cpy(), avD, mvVel, mKs, mKd);
	}

	public void UpdateVelocity(float afDeltaTime, T avD) {
		float time = afDeltaTime;
		while (time > kfDampedSpringUpdateDefaultTimeStep) {
			T vSpringAcc = DampedSpringAcceleration(avD.cpy(), avD.cpy(), avD, mvVel, mKs, mKd);
			//mvVel += vSpringAcc * kfDampedSpringUpdateDefaultTimeStep;      
			mvVel.add(vSpringAcc.scl(kfDampedSpringUpdateDefaultTimeStep));
			time -= kfDampedSpringUpdateDefaultTimeStep;
		}
		if (time > 0.0f) {
			T vSpringAcc = DampedSpringAcceleration(avD.cpy(), avD.cpy(),avD, mvVel, mKs, mKd);
			//mvVel += vSpringAcc * time;
			mvVel.add(vSpringAcc.scl(time));
		}
	}
  
	public void UpdateVelocityWithAcceleration(float afDeltaTime, T avAcc) {
	    float time = afDeltaTime;
	    while (time > kfDampedSpringUpdateDefaultTimeStep) {
	      //mvVel += avAcc * kfDampedSpringUpdateDefaultTimeStep;
	    	 
		    T acAccCopy = (T)avAcc.cpy();	
	    	mvVel.add(acAccCopy.scl(kfDampedSpringUpdateDefaultTimeStep));
	    	
	    	time -= kfDampedSpringUpdateDefaultTimeStep;
	    }
	    if (time > 0.0f) {
	      //mvVel += avAcc * time;
	      
	    	T acAccCopy = (T)avAcc.cpy();	      
	    	mvVel.add(acAccCopy.scl(time));
	    }
	}
};
