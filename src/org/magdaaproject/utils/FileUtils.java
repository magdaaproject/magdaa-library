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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import android.text.TextUtils;

/**
 * a utility class which exposes utility methods for interacting with files and the filesystem
 */
public class FileUtils {
	
	/**
	 * check to see if a directory is writeable if it exists, if it doesn't exist this method
	 * tries to create it
	 * 
	 * @param path the file system path to check
	 * @return true if the directory is writeable
	 */
	public static boolean isDirectoryWriteable(String path) {
		
		if(TextUtils.isEmpty(path) == true) {
			throw new IllegalArgumentException("the path parameter is required");
		}

		File mPath = new File(path);

		if(mPath.isDirectory() && mPath.canWrite()) {
			return true;
		} else {
			return mPath.mkdirs();
		}
	}
	
	/**
	 * check to see if a file exists and is readable
	 * 
	 * @param path the full path of a file to test
	 * @return true if the path is a file and is readable
	 */
	public static boolean isFileReadable(String path) {

		if(TextUtils.isEmpty(path) == true) {
			throw new IllegalArgumentException("the path parameter is required");
		}

		File mFile = new File(path);

		if(mFile.isFile() && mFile.canRead()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * write a temporary file into the specified directory with the supplied contents
	 * 
	 * @param contents the contents of the file
	 * @param directory the path of the directory to contain the file
	 * @return the full path to the new file 
	 * @throws IOException if something bad happens
	 */
	public static String writeTempFile(String contents, String directory) throws IOException {
		
		// check to see if the supplied path is writeable
		if(isDirectoryWriteable(directory) == false) {
			throw new IOException("unable to access specified path '" + directory + "'");
		}
		
		if(TextUtils.isEmpty(contents)) {
			throw new IllegalArgumentException("the contents of the file is required");
		}
		
		// create the new temporary file
		File mFile = null;
		try {
			mFile = File.createTempFile("magdaa", "txt", new File(directory));
		} catch (IOException e) {
			throw new IOException("unable to create temp file", e);
		}
		
		// open the file
		PrintWriter mWriter = null;
		try {
			mWriter = new PrintWriter(new FileWriter(mFile));
		} catch (IOException e) {
			throw new IOException("unable to open temp file", e);
		}
		
		// write the supplied contents
		mWriter.write(contents);
		
		// close the file
		mWriter.close();
		
		try {
			return mFile.getCanonicalPath();
		} catch (IOException e) {
			throw new IOException("unable to get canonical path", e);
		}
	}
}