/*
 * Copyright (C) 2012 The MaGDAA Project
 *
 * This file is part of the MaGDAA Library Software
 *
 * MaGDAA Library Software is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.magdaaproject.utils;

/**
 * a utility class which exposes methods to format data for use with 
 * the Open Data Kit suite of applications
 */
public class OpenDataKitUtils {
	
	/**
	 * build a location string in the style used by Open Data Kit XForm instances
	 * 
	 * @param latitude the latitude geo-coordinate
	 * @param longitude the longitude geo-coordinate
	 * @param altitude the altitude part of the geo-coordinate
	 * @param accuracy the accuracy part of the geo-coordiante
	 * 
	 * @return a string containing the formatted location information, or an empty string
	 */
	public static String getLocationString(String latitude, String longitude, String altitude, String accuracy) {
		
		if(latitude == null || longitude == null) {
			return "";
		}
		
		// build the location string
		StringBuilder mBuilder = new StringBuilder();
		
		mBuilder.append(latitude);
		mBuilder.append(" ");
		mBuilder.append(longitude);
		
		// add altitude and accuracy information if present
		if(altitude != null && accuracy != null) {
			
			mBuilder.append(" ");
			mBuilder.append(altitude);
			mBuilder.append(" ");
			mBuilder.append(accuracy);
		}
		
		// return the string
		return mBuilder.toString();		
	}
}
