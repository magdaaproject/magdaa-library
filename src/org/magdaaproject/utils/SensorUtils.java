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

import java.io.UnsupportedEncodingException;

import org.magdaaproject.utils.readings.WeatherReading;

import android.content.Context;

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
	 * constant to identify the Vantage Vue weather station
	 */
	public static final int VANTAGE_VUE = 200;
	
	
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
	 * @return the adjusted relative humidity value
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
	 * convert the barometric trend value into a string representation
	 * @param barometricTrend the numeric barometric trend value 
	 * @param weatherStationType the weather station identifier, one of the constants defined in this class
	 * @param context a context object used to retrieve the appropriate string
	 * @return the barometric trend as a string
	 * @throws IllegalArgumentException if the weather station type is invalid
	 */
	public static String barometricTrendAsString(int barometricTrend, int weatherStationType, Context context) {
		
		String mTrend;
		Context mContext;
		mContext = context.getApplicationContext();
		
		switch(weatherStationType) {
		case VANTAGE_VUE:
			// use the vantage vue conversion method
			mTrend = vantageVueBarometricTrend(barometricTrend, mContext);
			break;
		default:
			throw new IllegalArgumentException("the weatherStationType is invalid");
		}
		
		return mTrend;
	}
	
	/*
	 * convert the barometric trend value into string for the Vantage Vue weather station
	 */
	private static String vantageVueBarometricTrend(int barometricTrend, Context context) {
		switch(barometricTrend) {
		case -60:
			return context.getString(R.string.magdaa_lib_sensor_barometric_trend_falling_rapidly);
		case -20:
			return context.getString(R.string.magdaa_lib_sensor_barometric_trend_falling_slowly);
		case 0:
			return context.getString(R.string.magdaa_lib_sensor_barometric_trend_steady);
		case 20:
			return context.getString(R.string.magdaa_lib_sensor_barometric_trend_rising_slowly);
		case 60:
			return context.getString(R.string.magdaa_lib_sensor_barometric_trend_rising_rapidly);
		default:
			return context.getString(R.string.magdaa_lib_sensor_barometric_trend_no_data);
		}
	}
	
	/**
	 * get a compass point representation of the windDirection
	 * @param windDirection the wind direction in degrees
	 * @param context a context object used to retrieve the appropriate string 
	 * @return the wind direction as a string
	 * @throws IllegalArgumentException if the windDirection is invalid
	 */
	public static String windDirectionAsCompassPoint(int windDirection, Context context) {
		
		String mWindDirection = null;
		
		// check the parameter
		if(windDirection < 0 || windDirection > 360) {
			throw new IllegalArgumentException("the wind direction is invalid");
		}
		
		// convert from degrees to string
		if(windDirection == 0) {
			mWindDirection = context.getString(R.string.magdaa_lib_wind_direction_no_data);
		} else if(windDirection <= 22) {
			mWindDirection = context.getString(R.string.magdaa_lib_wind_direction_nne);
		} else if(windDirection <= 45) {
			mWindDirection = context.getString(R.string.magdaa_lib_wind_direction_ne);
		} else if(windDirection <= 67) {
			mWindDirection = context.getString(R.string.magdaa_lib_wind_direction_ene);
		} else if(windDirection <= 90) {
			mWindDirection = context.getString(R.string.magdaa_lib_wind_direction_e);
		} else if(windDirection <= 112) {
			mWindDirection = context.getString(R.string.magdaa_lib_wind_direction_ese);
		} else if(windDirection <= 135) {
			mWindDirection = context.getString(R.string.magdaa_lib_wind_direction_se);
		} else if(windDirection <= 157) {
			mWindDirection = context.getString(R.string.magdaa_lib_wind_direction_sse);
		} else if(windDirection <= 180) {
			mWindDirection = context.getString(R.string.magdaa_lib_wind_direction_s);
		} else if(windDirection <= 202) {
			mWindDirection = context.getString(R.string.magdaa_lib_wind_direction_ssw);
		} else if(windDirection <= 225) {
			mWindDirection = context.getString(R.string.magdaa_lib_wind_direction_sw);
		} else if(windDirection <= 247) {
			mWindDirection = context.getString(R.string.magdaa_lib_wind_direction_wsw);
		} else if(windDirection <= 270) {
			mWindDirection = context.getString(R.string.magdaa_lib_wind_direction_w);
		} else if(windDirection <= 292) {
			mWindDirection = context.getString(R.string.magdaa_lib_wind_direction_wnw);
		} else if(windDirection <= 315) {
			mWindDirection = context.getString(R.string.magdaa_lib_wind_direction_nw);
		} else if(windDirection <= 337) {
			mWindDirection = context.getString(R.string.magdaa_lib_wind_direction_nnw);
		} else if(windDirection <= 360) {
			mWindDirection = context.getString(R.string.magdaa_lib_wind_direction_n);
		}
	
		return mWindDirection;
	}
	
	/**
	 * convert a rain click value into mm
	 * @param rainClicks the number of rain clicks
	 * @param weatherStationType the weather station type
	 * @return the amount of rain in millimeters
	 * @throws IllegalArgumentException if the weather station type is invalid
	 */
	public static float convertRainClicksToMillimeters(int rainClicks, int weatherStationType) {
		
		float mConversionFactor = 0f;
		
		switch(weatherStationType) {
		case VANTAGE_VUE:
			mConversionFactor = 0.2f;
			break;
		default:
			throw new IllegalArgumentException("the weather station type is invalid");
		}
		
		return rainClicks * mConversionFactor;
		
	}
	
	/**
	 * parse a binary weather record into a WeatherReading object
	 * @param bytes an array of bytes to parse
	 * @param weatherStationType the weather station type
	 * @return a populated weather record, with a timestamp of the current time
	 * @throws IllegalArgumentException if the weatherStationType does not validate
	 * @throws SensorUtilsException if an error occurs during the parsing of the record
	 */
	public static WeatherReading parseWeatherRecord(byte[] bytes, int weatherStationType) throws SensorUtilsException {
		
		switch(weatherStationType) {
		case VANTAGE_VUE:
			return parseVantageViewLoopPacket(bytes);
		default:
			throw new IllegalArgumentException("the weather station type is invalid");
		}
	}
	
	private static WeatherReading parseVantageViewLoopPacket(byte[] bytes) throws SensorUtilsException {
		
		// validate the parameters
		if(bytes.length != 99) {
			throw new SensorUtilsException("record is too short, expected 99 bytes got " + bytes.length + " bytes");
		}
		
		WeatherReading mWeatherReading = new WeatherReading();
		
		/*
		 * parse the record
		 */
		// validate the header
		if(getStringFromBytes(bytes[0], bytes[1], bytes[2]).equals("LOO") == false) {
			throw new SensorUtilsException("missing record header");
		}
		
		int mIntValue;
		short mShortValue;
		
		// barometric trend
		mIntValue = (int) bytes[3];
		mWeatherReading.setBarometricTrend(mIntValue);
		
		// barometer
		mShortValue = getShortFromBytes(bytes[7], bytes[8]);
		mWeatherReading.setBarometer(
				UnitConversionUtils.convertBarometer(
						(float) mShortValue / 1000, 
						UnitConversionUtils.HG_INCH, 
						UnitConversionUtils.HPA
					)
				);
		
		// temperature
		mShortValue = getShortFromBytes(bytes[12], bytes[13]);
		mWeatherReading.setTemperature(
				UnitConversionUtils.comvertTemperature(
						(float) mShortValue / 10, 
						UnitConversionUtils.FAHRENHEIT, 
						UnitConversionUtils.CELSIUS)
				);
		
		// wind speed
		mIntValue = bytes[14] & 0xff;
		mWeatherReading.setWindSpeed(
				UnitConversionUtils.convertSpeed(
						(float) mIntValue, 
						UnitConversionUtils.MPH, 
						UnitConversionUtils.KPH)
				);
		
		// average wind speed
		mIntValue = bytes[15] & 0xff;
		mWeatherReading.setAverageWindSpeed(
				UnitConversionUtils.convertSpeed(
						(float) mIntValue, 
						UnitConversionUtils.MPH, 
						UnitConversionUtils.KPH)
				);
		
		// wind direction
		mShortValue = getShortFromBytes(bytes[16], bytes[17]);
		mWeatherReading.setWindDirection(mShortValue);
		
		// humidity
		mIntValue = bytes[33] & 0xff;
		mWeatherReading.setHumidity(mShortValue);
		
		// rain rate
		mShortValue = getShortFromBytes(bytes[41], bytes[42]);
		mWeatherReading.setRainRate(
				SensorUtils.convertRainClicksToMillimeters(
						mShortValue, 
						VANTAGE_VUE
					)
				);
		
		// rain today
		mShortValue = getShortFromBytes(bytes[50], bytes[51]);
		mWeatherReading.setRainToday(
				SensorUtils.convertRainClicksToMillimeters(
						mShortValue, 
						VANTAGE_VUE
					)
				);
		
		return mWeatherReading;
	}
	
	private static String getStringFromBytes(byte... bytes) {
		try {
			return new String(bytes, "ASCII");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
	private static short getShortFromBytes(byte byte01, byte byte02) {
		return (short) ((byte01 & 0xff) | (byte02 << 8));
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
