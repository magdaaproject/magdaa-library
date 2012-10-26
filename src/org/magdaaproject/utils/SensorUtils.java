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
 * a utility class which exposes utility methods for interacting with sensors
 *
 */
public class SensorUtils {

	/*
	 * public class level constants
	 */
	
	/**
	 * constant to identify the TMP36 temperature sensor
	 */
	public static final int TMP36 = 0;
	
	/**
	 * constant to identify the HIH-5031 humidity sensor
	 */
	public static final int HIH5031 = 100;
	
	
	/**
	 * convert a voltage to a temperature
	 * 
	 * @param voltage the voltage reading
	 * @param sensorType the type of sensor used, one of the constants defined by this class
	 * @param scale the temperature scale to use, one of the constants defined the UnitConversionUtils class
	 * @return the temperature as derived from the voltage reading 
	 */
	public static float convertVoltageToTemp(float voltage, int sensorType, int scale) {
		
		float mTemperature;
		
		//determine which sensor type is being used
		switch(sensorType){
		case TMP36:
			// this is a TMP36 sensor
			// by default convert to celsius
			mTemperature = (voltage - 0.5f) * 100.0f;
			
			// convert to the required scale
			switch(scale) {
			case UnitConversionUtils.FAHRENHEIT:
				mTemperature =  UnitConversionUtils.comvertTemperature(mTemperature, UnitConversionUtils.CELSIUS, UnitConversionUtils.FAHRENHEIT);
			case UnitConversionUtils.KELVIN:
				mTemperature = UnitConversionUtils.comvertTemperature(mTemperature, UnitConversionUtils.CELSIUS, UnitConversionUtils.KELVIN);
			}
			break;
		default:
			throw new IllegalArgumentException("the sensorType is invalid");
		}
		
		return mTemperature;
	}
	
	/**
	 * convert a voltage to a relative humidity
	 * 
	 * @param voltage the voltage from the sensor
	 * @param sensorType the type of sensor used, one of the constants define by this class
	 * @return the relative humidity derived from the voltage reading
	 */
	public static float convertVoltageToRelativeHumidity(float voltage, int sensorType) {
		
		float mRelativeHumidity;
		
		// determine which sensor type is being used
		switch(sensorType) {
		case HIH5031:
			// this is a HIH-5031 sensor
			mRelativeHumidity = (1.0f / 0.00636f) * ((voltage / 3.3f) - 0.1515f);
			break;
		default:
			throw new IllegalArgumentException("the sensorType is invalid");
		}
		
		return mRelativeHumidity;
	}
	
	/**
	 * adjust the relative humidity to take into account the current temperature
	 * 
	 * @param relativeHumidity the relative humidity
	 * @param temperature the current temperature
	 * @param sensorType the type of sensor used, one of the constants define by this class
	 * @return
	 */
	public static float adjustRelativeHumidity(float relativeHumidity, float temperature, int sensorType) {
		
		float newHumidity;
		
		// determine which sensor is being used
		switch(sensorType) {
		case HIH5031:
			// this is a HIH-5031 sensor
			newHumidity = relativeHumidity / (1.0546f - (0.00216f * temperature));
			break;
		default:
			throw new IllegalArgumentException("the sensorType is invalid");
		}
		
		return newHumidity;
	}
	
	/**
	 * convert a byte array into its hex string representation
	 * @param bytes an array of bytes
	 * @return the byte array as a string of hex digits
	 */
	/*
	 * function taken from here:
	 * http://stackoverflow.com/a/9855338
	 * and considered to be in the public domain
	 */
	public static String byteArrayToHexString(byte[] bytes) {
	    final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	    char[] hexChars = new char[bytes.length * 2];
	    int v;
	    for ( int j = 0; j < bytes.length; j++ ) {
	        v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
}
