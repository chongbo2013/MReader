/**
 * <This class for the catalog of one book.>
 *  Copyright (C) <2009>  <mingkg21,ACC http://androidos.cc/dev>
 *
 *   This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.midcore.reader.reader;

import java.io.Serializable;

/**
 * @author lijing.lj
 */
public class Chapter implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public String title;
	public String name;
	
	public boolean header = false;
	public boolean virtual = false;
	
	public transient boolean valid = false;
	
	
	public boolean isValid() {
		return valid;
	}
	
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	public void invalidate() {
		setValid(false);
	}
	
	public Chapter() {
	}
	
}
