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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * a utility class which exposes methods for interacting with the Serval Mesh software
 */
public class ServalUtils {

	/*
	 * public class level constants
	 */
	
	/**
	 * identify the package name of the Serval Mesh software
	 */
	public static final String SERVAL_MESH_PACKAGE_NAME = "org.servalproject";
	
	/**
	 * identify the state that the Serval Mesh is on
	 */
	public static final int SERVAL_STATUS_ON = 0;
	
	/**
	 * identify the state where the Serval Mesh is off
	 */
	public static final int SERVAL_STATUS_OFF = 1;
	
	/**
	 * identify the state whereby the Serval Mesh status is unknown
	 */
	public static final int SERVAL_STATUS_UNKOWN = -1;
	
	/**
	 * identify the intent actions used to check Serval State
	 */
	public static final String[] SERVAL_STATUS_ACTIONS = {"org.servalproject.ACTION_STATE_CHECK_UPDATE","org.servalproject.ACTION_STATE"};
	
	/*
	 * private class level variables
	 */
	/*
	 * an enum representing the different states of the Serval Mesh software
	 * derived from the org.serval.project.ServalBatphoneApplication class
	 */
	private static enum BatphoneState{
		Installing,
		Upgrading,
		Off,
		Starting,
		On,
		Stopping,
		Broken
	}
	
	/**
	 * check to see if the Serval Mesh software is installed
	 * 
	 * @param context a context object used to gain access to system resources
	 * @return true if the Serval Mesh software is installed
	 */
	public static boolean isServalMeshInstalled(Context context) {
		
		try {
			context.getPackageManager().getApplicationInfo(SERVAL_MESH_PACKAGE_NAME, PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			return false;
		}

		return true;
	}
	
	/**
	 * get a broadcast receiver that can be used to receive intents about the status
	 * of the Serval Mesh software
	 * 
	 * @return a BroadcastReceiver
	 */
	public static BroadcastReceiver getStatusReceiver() {
		return statusReceiver;
	}
	
	private static BroadcastReceiver statusReceiver = new BroadcastReceiver() {
		
		private int status = SERVAL_STATUS_UNKOWN;

		/*
		 * (non-Javadoc)
		 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
		 */
		@Override
		public void onReceive(Context context, Intent intent) {
			
			// check the intent action
			if(intent.getAction().equals(SERVAL_STATUS_ACTIONS[0]) || intent.getAction().equals(SERVAL_STATUS_ACTIONS[1])) {
				
				// get the state from the intent
				int mIndicator = intent.getIntExtra("state", -1);
				
				// determine which state is indicated
				if(mIndicator != -1) {
					BatphoneState mState = BatphoneState.values()[mIndicator];
					
					switch(mState) {
					case On:
						status = SERVAL_STATUS_ON;
						break;
					case Off:
						status = SERVAL_STATUS_OFF;
						break;
					default:
						status = SERVAL_STATUS_UNKOWN;
					}
				} else {
					status = SERVAL_STATUS_UNKOWN;
				}
			}
		}
		
		/**
		 * get the current status of the Serval Mesh software
		 * @return
		 */
		@SuppressWarnings("unused")
		public int getStatus() {
			return status;
		}
	};
}
