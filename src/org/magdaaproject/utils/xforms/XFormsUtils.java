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
package org.magdaaproject.utils.xforms;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.text.TextUtils;

/**
 * a utility class which exposes utility methods for interacting with XForms data
 *
 */
public class XFormsUtils {

	/*
	 * private class level variables
	 */
	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;
	private DOMImplementation domImpl;
	private Document xmlDoc;
	private Element rootElement;
	private Element element;
	
	private TransformerFactory transFactory;
	private Transformer transformer;
	private StringWriter stringWriter;

	private boolean debugOutput = false;

	/**
	 * set the debug output flag, determines if output is indented or compact
	 * @param value the new debug output value
	 */
	public void setDebugOutput(boolean value) {
		debugOutput = value;
	}

	/**
	 * get the debug output flag, determines if output is indented or compact
	 * @return the current debug output value
	 */
	public boolean getDebugOutput() {
		return debugOutput;
	}


	/**
	 * build an XForms instance XML string using the elements defined in the elements HashMap
	 * 
	 * @param elements a HashMap containing the elements to include in the XForms instance XML string
	 * @param formId an identifier matching the form that this instance is based on
	 * 
	 * @return a string of XML in the XForms instance format
	 * 
	 * @throws XFormsException if something bad happens
	 */
	public final String buildXFormsData(HashMap<String, String> elements, String formId) throws XFormsException {

		// validate the parameters
		if(elements == null || elements.isEmpty() == true) {
			throw new IllegalArgumentException("the elements parameter is required");
		}

		if(TextUtils.isEmpty(formId)) {
			throw new IllegalArgumentException("the formId parameter is required");
		}

		// create the xml document builder factory object
		factory = DocumentBuilderFactory.newInstance();

		// create the xml document builder object and get the DOMImplementation object
		try {
			builder = factory.newDocumentBuilder();
		} catch (javax.xml.parsers.ParserConfigurationException e) {
			throw new XFormsException("unable to start to build xml", e);
		}

		domImpl = builder.getDOMImplementation();

		// create a document 
		xmlDoc = domImpl.createDocument(null, "data", null);

		// get the root element
		rootElement = this.xmlDoc.getDocumentElement();

		// add the id attribute to the root element
		rootElement.setAttribute("id", formId);

		// iterate over the hashmap adding elements
		for(Map.Entry<String, String> mEntry : elements.entrySet()) {

			element = xmlDoc.createElement(mEntry.getKey());
			element.setTextContent(mEntry.getValue());

			rootElement.appendChild(element);
		}

		// build the output
		try {
			// create a transformer 
			transFactory = TransformerFactory.newInstance();
			transformer = transFactory.newTransformer();

			// set some options on the transformer
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			
			// output formatted XML if required
			if(debugOutput) {
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			}

			// get a transformer and supporting classes
			stringWriter = new StringWriter();
			
			StreamResult result = new StreamResult(stringWriter);
			DOMSource    source = new DOMSource(xmlDoc);

			// transform the internal objects into XML
			transformer.transform(source, result);
			
			return stringWriter.toString();

		} catch (javax.xml.transform.TransformerException e) {
			throw new XFormsException("unable to transform the xml into a string", e);
		}
	}
	
	/**
	 * format a timestamp in the ISO 8601 format for 
	 * use in an XForms instance XML file
	 * @param timestamp the timestamp to format
	 * @return a formatted string
	 */
	public static String formatTimestamp(long timestamp) {
		SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ");
        return mSimpleDateFormat.format(timestamp);
	}
}
