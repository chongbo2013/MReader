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


public class DampedSpringParam{
	public float afKs;//拉力的大小，值越大，拉力越大，感觉突然就被拉动了
	public float afE;//衰减的系数,一般取1.0f
	public float speed;//更新的速度，速度越大，更新的步长越长，这样整体动画的时间越短
	public float endThreshold;//结束的阈值
	public DampedSpringParam(){
		afKs         = 10f;
		afE          = 1f;
		speed        = 5f;
		endThreshold = 0.5f;
	}
};
