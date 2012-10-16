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

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;

import com.androidplot.Plot;
import com.androidplot.series.XYSeries;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.XYPlot;

/**
 * a utility class which exposes methods related to the formatting of a AndroidPlot chart
 */
public class ChartUtils {
	
	/*
	 * public class level constants
	 */
	
	/**
	 * the default divisor to use when using time and date as range labels
	 */
	public static final long DEFAULT_DIVISOR = 1000;
	
	/**
	 * format the supplied chart using the standard MaGDAA style
	 * @param chart an initialised chart to format
	 * @return a reference to the formatted chart
	 * @throws IllegalArgumentException if the chart parameter is null
	 */
	public static XYPlot formatChart(XYPlot chart) {
		
		// check on the parameter
		if(chart == null) {
			throw new IllegalArgumentException("the chart parameter is required");
		}
		
		chart.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
		chart.getGraphWidget().getGridLinePaint().setColor(Color.BLACK);
		chart.getGraphWidget().getGridLinePaint().setPathEffect(new DashPathEffect(new float[]{1,1}, 1));
		chart.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
		chart.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);
 
		chart.setBorderStyle(Plot.BorderStyle.SQUARE, null, null);
		chart.getBorderPaint().setStrokeWidth(1);
		chart.getBorderPaint().setAntiAlias(false);
		chart.getBorderPaint().setColor(Color.WHITE);
		
		return chart;
	}
	
	/**
	 * provide a LineAndPointFormatter using the standard MaGDAA style
	 * @return a LineAndPointFormatter which can be used to style a chart
	 */
	public static LineAndPointFormatter getLineStyle() {
		// Create a formatter to use for drawing a series using LineAndPointRenderer:
		LineAndPointFormatter mFormatter = new LineAndPointFormatter(Color.BLUE, Color.CYAN, Color.BLUE);
		
		// create a better fill paint
		Paint mLineFill = new Paint();
        mLineFill.setAlpha(200);
        mLineFill.setShader(new LinearGradient(0, 0, 0, 250, Color.WHITE, Color.BLUE, Shader.TileMode.MIRROR));
        
        mFormatter.setFillPaint(mLineFill);
        
        return mFormatter;
	}
	
	/**
	 * get a formatter that can be used to provide blank domain labels
	 * @return a formatter for blank domain labels
	 */
	public static Format getBlankDomainFormatter() {
		
		Format mFormat = new Format() {
			
			private static final long serialVersionUID = -6090275787464042111L;

			/*
			 * (non-Javadoc)
			 * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
			 */
			@Override
			public StringBuffer format(Object object, StringBuffer buffer, FieldPosition field) {
				// return an empty string buffer
                return new StringBuffer();
			}

			/*
			 * (non-Javadoc)
			 * @see java.text.Format#parseObject(java.lang.String, java.text.ParsePosition)
			 */
			@Override
			public Object parseObject(String string, ParsePosition position) {
				return null;
			}
		};
		
		return mFormat;		
	}
	
	/**
	 * get a formatter that can be used on date domain values
	 * @param divisor the divisor used to shrink the domain values before adding to the series
	 * @return a formatter which uses the TimeUtils.DEFAULT_SHORT_TIME_FORMAT to format dates
	 */
	public static Format getDateDomainFormatter(final long divisor) {
		
		Format mFormat = new Format() {
	
			private static final long serialVersionUID = 8742151483351282838L;
			
			// private class level variables
			private SimpleDateFormat dateFormat = new SimpleDateFormat(TimeUtils.DEFAULT_SHORT_TIME_FORMAT);

			/*
			 * (non-Javadoc)
			 * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
			 */
			@Override
			public StringBuffer format(Object object, StringBuffer buffer, FieldPosition field) {
				
				// adjust the times
				long timestamp = ((Number) object).longValue() * divisor;
                Date date = new Date(timestamp);
                return dateFormat.format(date, buffer, field);

			}

			/*
			 * (non-Javadoc)
			 * @see java.text.Format#parseObject(java.lang.String, java.text.ParsePosition)
			 */
			@Override
			public Object parseObject(String string, ParsePosition position) {
				return null;
			}
		};
		
		return mFormat;
	}
	
	/**
	 * get the minimum and maximum values for the range values
	 * @param series the series containing the y 'range' values
	 * @return an array with two elements containing the minimum, element 0, and maximum, element 1, range values
	 */
	public static Float[] getRangeBoundaries(XYSeries series) {
		
		Float[] mRange = new Float[2];
		
		mRange[1] = Float.MIN_NORMAL;
		mRange[0] = Float.MAX_VALUE;
		
		float mYValue;
		
		for(int i = 0; i < series.size(); i++) {
			
			mYValue = (Float) series.getY(i);
			
			if(mYValue < mRange[0]) {
				mRange[0] = mYValue;
			}
			
			if(mYValue > mRange[1]) {
				mRange[1] = mYValue;
			}
		}
		
		return mRange;
	}
}
