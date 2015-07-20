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

import com.glview.libgdx.graphics.math.Vector2;


//! Damped spring with position helper template.
//template <typename T, typename TF, typename BASE = ni::cUnknown, typename TREF = const T&>
public class sDampedSpringPosition2 extends sDampedSpringPosition<Vector2> {	
	public sDampedSpringPosition2() {
		super();
		
		mvIdealPos   = new Vector2(0, 0);
		mvCurrentPos = new Vector2(0, 0);
		
		mApTem       = new Vector2(0, 0);
		mAvTem       = new Vector2(0, 0);
		
		mvVel = new Vector2(0, 0);
		
		mOutPutTem = new Vector2(0, 0);
		mVcpyTem = new Vector2(0, 0);
	}
}