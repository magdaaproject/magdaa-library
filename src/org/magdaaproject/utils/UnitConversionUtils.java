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
 * a utility class which exposes utility methods for converting values between different
 * scales
 */
public class UnitConversionUtils {
	
	/**
	 * constant to define the celsius temperature scale
	 */
	public static final int CELSIUS = 0;
	
	/**
	 * constant to define the fahrenheit scale
	 */
	public static final int FAHRENHEIT = 1;
	
	/**
	 * constant to define the kelvin temperature scale
	 */
	public static final int KELVIN = 2;
	
	
	/**
	 * convert a temperature value from one scale to another
	 * 
	 * @param value the temperature
	 * @param fromScale the scale to convert from
	 * @param toScale the scale to convert to
	 * @return the converted temperature
	 * @throws IllegalArgumentException of the fromScale or toScale parameters are invalid
	 */
	public static float comvertTemperature(float value, int fromScale, int toScale) {
		
		/*
		 * determine which conversion to undertake
		 */
		switch(fromScale) {
		case CELSIUS:
			// convert from celsius
			switch(toScale) {
			case FAHRENHEIT:
				// switch from celsius to fahrenheit
				return convertFromCelsiusToFahrenheit(value);
			case KELVIN:
				// switch from celsius to Kelvin
				return convertFromCelsiusToKelvin(value);
			default:
				throw new IllegalArgumentException("the toScale is invalid");
			}
		case FAHRENHEIT:
			// convert from FAHRENHEIT
			switch(toScale) {
			case CELSIUS:
				// convert from fahrenheit to celsius
				return convertFromFahrenheitToCelsius(value);
			case KELVIN:
				// convert from fahrenheit to kelvin
				return convertFromFahrenheitToKelvin(value);
			default:
				throw new IllegalArgumentException("the toScale is invalid");
			}
		case KELVIN:
			// convert from kelvin
			switch(toScale) {
			case CELSIUS:
				return convertFromKelvinToCelsius(value);
			case FAHRENHEIT:
				return convertFromKelvinToFahrenheit(value);
			default:
				throw new IllegalArgumentException("the toScale is invalid");
			}
		default:
			throw new IllegalArgumentException("the fromScale was not recognised");
		}
	}
	
	private static float convertFromCelsiusToFahrenheit(float value) {
		return value * 9/5 + 32;
	}
	
	private static float convertFromCelsiusToKelvin(float value) {
		return value + 273.15f;
	}
	
	private static float convertFromFahrenheitToCelsius(float value) {
		return (value - 32) * 5/9;
	}
	
	private static float convertFromFahrenheitToKelvin(float value) {		
		return convertFromCelsiusToKelvin(convertFromFahrenheitToCelsius(value));
	}
	
	private static float convertFromKelvinToCelsius(float value) {
		return value - 273.15f;
	}
	
	private static float convertFromKelvinToFahrenheit(float value) {
		return convertFromCelsiusToFahrenheit(convertFromKelvinToCelsius(value));
	}
}
