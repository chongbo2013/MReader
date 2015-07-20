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

//! 2D Damped spring position interface.
interface iDampedSpringPosition<T>
{
	//! Set the ideal (target) position of the spring.
	//! {Property}
	public void SetIdealPosition(T avPos);
	//! Get the ideal (target) position of the spring.
	//! {Property}
	public T GetIdealPosition();
	//! Set the current position of the spring.
	//! {Property}
	public void SetCurrentPosition(T avPos);
	//! Get the current position of the spring.
	//! {Property}
	public T GetCurrentPosition();
	//! Update the current position of the spring.
	//! \return The current position of the spring.
	public T UpdatePosition(float afDeltaTime);
	
	//! Set the maximum update step size.
	//! {Property}
	//! \remark Default is 1.0/50.0
	//! \remark If the step is zero the value is updated at once in UpdatePosition (effectivly disable step update)
	public void SetStep(float afD);
	//! Get the update step size.
	//! {Property}
	public float GetStep();
	//! Set the animation speed.
	//! {Property}
	//! \remark This value just multiplies afDeltaTime to ariticially speed up the spring's animation.
	//! \remark Default is 1.0.
	public void SetSpeed(float afD);
	//! Get the animation speed.
	//! {Property}
	public float GetSpeed();
	//! Set the end threshold.
	//! {Property}
	//! \remark Default is 1e-3f
	public void  SetEndThreshold(float afD);
	//! Get the end threshold.
	//! {Property}
	public float GetEndThreshold();
	//! Get the whether the distance between the ideal and the current positions are below the end threshold.
	//! {Property}
	public boolean GetIsEnded();
};

