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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Scanner;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.os.Environment;
import android.text.TextUtils;

/**
 * a utility class which exposes utility methods for interacting with files and the filesystem
 */
public class FileUtils {
	
	/**
	 * Maximum file size that the {@link #readFile(String) readFile} method will process
	 */
	public static final int MAX_READ_FILE_SIZE = 1024;
	
	/**
	 * the default name of the index file in a MaGDAA Bundle used by the {@link #getMagdaaBundleIndex(String)} method
	 */
	public static final String MAGDAA_BUNDLE_INDEX_FILE_NAME = "_index.txt";
	
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
			mFile = File.createTempFile("magdaa-", ".txt", new File(directory));
		} catch (IOException e) {
			throw new IOException("unable to create temp file", e);
		}
		
		// open the file
		PrintWriter mWriter = null;
		try {
			mWriter = new PrintWriter(new FileWriter(mFile));
			
			// write the supplied contents
			mWriter.write(contents);
			
			// close the file
			mWriter.close();
		} catch (FileNotFoundException e) {
			throw new IOException("unable to open temp file", e);
		} catch (IOException e) {
			throw new IOException("unable to write temporary file", e);
		}
		
		try {
			return mFile.getCanonicalPath();
		} catch (IOException e) {
			throw new IOException("unable to get canonical path", e);
		}
	}
	
	/**
	 * write a file with the specified content using the supplied name in the required directory
	 * 
	 * @param contents the contents of the file
	 * @param fileName the name of the new file
	 * @param directory the name of the directory
	 * @return the full path to the new file 
	 * @throws IOException if something bad happens
	 */
	public static String writeNewFile(String contents, String fileName, String directory) throws IOException {
		
		// check to see if the supplied path is writeable
		if(isDirectoryWriteable(directory) == false) {
			throw new IOException("unable to access specified path '" + directory + "'");
		}
		
		// check the other parameters
		if(TextUtils.isEmpty(contents)) {
			throw new IllegalArgumentException("the contents of the file is required");
		}
		
		if(TextUtils.isEmpty(fileName)) {
			throw new IllegalArgumentException("the name of the file is required");
		}
		
		// create the new  file
		if(directory.endsWith(File.separator) == false) {
			directory += File.separator;
		}
		
		File mFile = new File(directory + fileName);
		
		// open the file
		PrintWriter mWriter = null;
		try {
			mWriter = new PrintWriter(new FileWriter(mFile));
			
			// write the supplied contents
			mWriter.write(contents);
			
			// close the file
			mWriter.close();
		} catch (FileNotFoundException e) {
			throw new IOException("unable to open temp file", e);
		} catch (IOException e) {
			throw new IOException("unable to write temporary file", e);
		}
		
		try {
			return mFile.getCanonicalPath();
		} catch (IOException e) {
			throw new IOException("unable to get canonical path", e);
		}
	}
	
	/**
	 * check to see if external storage is available
	 * 
	 * @return true if external storage is available
	 */
	public static boolean isExternalStorageAvailable() {
		String mStorageState = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(mStorageState);
	}
	
	/**
	 * write a file with the specified content using the supplied name in the required directory
	 * compressing the file using the gzip algorithm
	 * 
	 * @param contents the contents of the file
	 * @param fileName the name of the new file, excluding the .gz extension
	 * @param directory the name of the directory
	 * @return the full path to the new file 
	 * @throws IOException if something bad happens
	 */
	public static String writeNewGzipFile(String contents, String fileName, String directory) throws IOException {
		
		// check to see if the supplied path is writeable
		if(isDirectoryWriteable(directory) == false) {
			throw new IOException("unable to access specified path '" + directory + "'");
		}
		
		// check the other parameters
		if(TextUtils.isEmpty(contents)) {
			throw new IllegalArgumentException("the contents of the file is required");
		}
		
		if(TextUtils.isEmpty(fileName)) {
			throw new IllegalArgumentException("the name of the file is required");
		}
		
		// create the new  file
		if(directory.endsWith(File.separator) == false) {
			directory += File.separator;
		}
		
		File mFile = new File(directory + fileName + ".gz");
		
		// open the file and write its contents
		FileOutputStream mOutput = null;
		Writer mWriter = null;
		try {
			mOutput = new FileOutputStream(mFile);
			
			mWriter = new OutputStreamWriter(new GZIPOutputStream(mOutput), "UTF-8");
			
			mWriter.write(contents);
			
			mWriter.close();
			
		} catch (FileNotFoundException e) {
			throw new IOException("unable to open the file for writing", e);
		} catch (UnsupportedEncodingException e) {
			throw new IOException("unable to encode the file using 'UTF-8'", e);
		} catch (IOException e) {
			throw new IOException("unable to write data to the file", e);
		}
		
		try {
			return mFile.getCanonicalPath();
		} catch (IOException e) {
			throw new IOException("unable to get canonical path", e);
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
	public static String writeTempFile(byte[] contents, String directory) throws IOException {
		
		// check to see if the supplied path is writeable
		if(isDirectoryWriteable(directory) == false) {
			throw new IOException("unable to access specified path '" + directory + "'");
		}
		
		if(contents.length == 0) {
			throw new IllegalArgumentException("the contents of the file is required");
		}
		
		// create the new temporary file
		File mFile = null;
		try {
			mFile = File.createTempFile("magdaa-", ".txt", new File(directory));
		} catch (IOException e) {
			throw new IOException("unable to create temp file", e);
		}
		
		// open the file
		FileOutputStream mWriter;
		try {
			mWriter = new FileOutputStream(mFile);
			
			// write the supplied contents
			mWriter.write(contents);
			
			// close the file
			mWriter.close();
		} catch (FileNotFoundException e) {
			throw new IOException("unable to open temp file", e);
		} catch (IOException e) {
			throw new IOException("unable to write temporary file", e);
		}
		
		try {
			return mFile.getCanonicalPath();
		} catch (IOException e) {
			throw new IOException("unable to get canonical path", e);
		}
	}
	
	/**
	 * read a file and return the contents as a byte array
	 * @param path the path to the file to read
	 * @return the contents of the file as a byte array
	 * @throws IOException if something bad happens or the file size is greater than {@link #MAX_READ_FILE_SIZE MAX_READ_FILE_SIZE}
	 */
	public static byte[] readFile(String path) throws IOException {
		
		if(isFileReadable(path) == false) {
			throw new IOException("unable to find the specified file");
		}
		
		// read the file
		try {
			RandomAccessFile mFile = new RandomAccessFile(path, "r"); // only read the file
			
			// check on the size of the file
			if(mFile.length() <= MAX_READ_FILE_SIZE) {
			
				byte[] mBytes = new byte[(int) mFile.length()];
				
				mFile.read(mBytes);
				
				mFile.close();
				
				return mBytes;
			} else {
				throw new IOException("the file size exceeds '" + MAX_READ_FILE_SIZE + "' bytes and will not be read");
			}
		} catch (FileNotFoundException e) {
			throw new IOException("unable to open the file", e);
		} catch (IOException e) {
			throw new IOException("unable to read from the file", e);
		} 
	}
	
	/**
	 * extract the contents of a zip file to a specified path
	 * 
	 * @param zipFile the path to the zip file
	 * @param outputPath the path where to output the zip file
	 * @throws IOException if something bad happens
	 */
	public static void extractFromZipFile(String zipFile, String outputPath) throws IOException {
		
		// method implementation based on code here: http://stackoverflow.com/a/10997886
		// which is considered to be in the public domain
		
		// double check the parameters
		if(TextUtils.isEmpty(zipFile) == true || TextUtils.isEmpty(outputPath) == true) {
			throw new IllegalArgumentException("both parameters to this method is required");
		}
		
		if(isFileReadable(zipFile) == false) {
			throw new IOException("unable to access the specified file");
		}
		
		if(isDirectoryWriteable(outputPath) == false) {
			throw new IOException("unable to access the specified output directory");
		}
		
		if(outputPath.endsWith(File.separator) == false) {
			outputPath += File.separator;
		}

		// declare helper variables
		String mFileName;
		InputStream mInputStream = null;
		ZipInputStream mZipInputStream = null;
		ZipEntry mZipEntry;
		byte[] mBuffer = new byte[1024];
		int mByteCount;
		
		try {
			// open the file
			mInputStream = new BufferedInputStream(new FileInputStream(zipFile));
			mZipInputStream = new ZipInputStream(new BufferedInputStream(mInputStream));
			
			// extract all of the files
			while((mZipEntry = mZipInputStream.getNextEntry()) != null) {
				
				// create the new output path
				mFileName = mZipEntry.getName();
				
				// open the file
				FileOutputStream mOutputStream = new FileOutputStream(outputPath + mFileName);
				
				// write the data to the new file
				while((mByteCount = mZipInputStream.read(mBuffer)) != 1) {
					//write the uncompressed data to the file
					mOutputStream.write(mBuffer, 0, mByteCount);
				}
				
				// close the output file
				mOutputStream.close();
				
				// close this zip entry
				mZipInputStream.closeEntry();	
			}
		} catch (FileNotFoundException e) {
			throw new IOException("unable to open specified zip file", e);
		} catch (IOException e) {
			throw new IOException("unable to extract a file", e);
		} finally {
			if(mZipInputStream != null) {
				mZipInputStream.close();
			}
		}
	}
	
	/**
	 * extract the bundle index file from a MaGDAA Bundle file
	 * @param bundleFile the path to the bundle file
	 * @return the contents of the index file or null if no index file was found
	 * @throws IOException if something bad happens
	 */
	public String getMagdaaBundleIndex(String bundleFile) throws IOException {
		
		// double check the parameters
		if(TextUtils.isEmpty(bundleFile) == true) {
			throw new IllegalArgumentException("the path to the input file is required");
		}
		
		if(isFileReadable(bundleFile) == false) {
			throw new IOException("unable to access the specified file");
		}
		
		// declare helper variables
		String mFileName;
		InputStream mInputStream = null;
		ZipInputStream mZipInputStream = null;
		ZipEntry mZipEntry;
		String mFileContents = null;
		
		try {
			// open the file
			mInputStream = new BufferedInputStream(new FileInputStream(bundleFile));
			mZipInputStream = new ZipInputStream(new BufferedInputStream(mInputStream));
			
			// extract all of the files
			while((mZipEntry = mZipInputStream.getNextEntry()) != null) {
				
				// create the new output path
				mFileName = mZipEntry.getName();
				
				// check the name of the file
				if(mFileName.equals(MAGDAA_BUNDLE_INDEX_FILE_NAME) == true) {
					
					// read in the data 
					// based on method here: http://stackoverflow.com/a/5445161 
					// which is considered to be in the public domain
					Scanner mScanner = new Scanner(mZipInputStream, "UTF-8").useDelimiter("\\A");
					mFileContents = mScanner.next();
					
					// exit the loop early
					break;
				}
				
				// close this zip entry
				mZipInputStream.closeEntry();	
			}
		} catch (FileNotFoundException e) {
			throw new IOException("unable to open specified zip file", e);
		} catch (IOException e) {
			throw new IOException("unable to extract a file", e);
		} finally {
			if(mZipInputStream != null) {
				mZipInputStream.close();
			}
		}
		
		return mFileContents;
	}
}
