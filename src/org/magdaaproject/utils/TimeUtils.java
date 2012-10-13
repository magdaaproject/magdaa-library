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

import java.text.SimpleDateFormat;

/**
 * a utility class which exposes utility methods for manipulating and formating time
 */
public class TimeUtils {
	
	public static String DEFAULT_TIME_FORMAT = "h:mm:ss a";
	public static String DEFAULT_SHORT_DATE_FORMAT = "dd/MM/yyyy";
	public static String DEFAULT_LONG_DATE_FORMAT = "dd/MM/yyyy HH:mm:ss z";
	
	/**
	 * format a time into the format used by all MaGDAA software
	 * @param time the time in milliseconds to format
	 * @return a formated time string
	 */
	public static String formatTime(long time) {
		
		SimpleDateFormat mFormat = new SimpleDateFormat(DEFAULT_TIME_FORMAT);

		return mFormat.format(time);
		
	}
	
	/**
	 * format a date into the format used by all MaGDAA software
	 * @param date the date in milliseconds to format
	 * @return a formated date string
	 */
	public static String formatDate(long date) {
		
		SimpleDateFormat mFormat = new SimpleDateFormat(DEFAULT_SHORT_DATE_FORMAT);

		return mFormat.format(date);
		
	}
	
	/**
	 * format a date and time into the format used by all MaGDAA software
	 * @param time the date and time in milliseconds to format
	 * @return a formated date and time string
	 */
	public static String formatLongDate(long time) {
		
		SimpleDateFormat mFormat = new SimpleDateFormat(DEFAULT_LONG_DATE_FORMAT);

		return mFormat.format(time);
		
	}

}
