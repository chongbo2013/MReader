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
interface iDampedSpring<T>
{
	  //! Set the Kd constant.
	  //! {Property}
	  public void SetKd(float afD);
	  //! Get the Kd constant.
	  //! {Property}
	  public float GetKd();
	  //! Set the Ks constant.
	  //! {Property}
	  public void SetKs(float afD);
	  //! Get the Ks constant.
	  //! {Property}
	  public float GetKs();

	  //! Set the value of Kd for the specified Ks that will result in a spring with the specified damping ratio.
	  //! \remark Sets Ks with parameter's value, sets Kd in function of Ks.
	  public void SetStiffnessAndDampingRatio(float afKs, float afE);

	  //! Set the spring's damping ratio.
	  //! {Property}
	  //! \remark Sets Kd in function of the current value of Ks to achieve the specified damping ratio.
	  public void SetDampingRatio(float afE);
	  //! Get the spring's damping ratio.
	  //! {Property}
	  public float GetDampingRatio();

	  //! Set the spring's velocity.
	  //! {Property}
	  public void SetVelocity(T avVel);
	  //! Get the spring's velocity.
	  //! {Property}
	  public T GetVelocity();

	  //! Compute the spring's acceleration for the specified displacement.
	  public T ComputeAcceleration(T avD);
	  
	  //! Update the spring's velocity from the specified displacement.
	  public void UpdateVelocity(float afDeltaTime, T avD);
	  
	  //! Update the spring's velocity from the specified acceleration.
	  public void UpdateVelocityWithAcceleration(float afDeltaTime, T avAcc);
};
