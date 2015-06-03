/*
 * File: FilterUtil.java
 *
 * Version:    01.00.00
 *
 *   (c) Copyright 2009 - TELUS Health Solutions
 *                      ALL RIGHTS RESERVED
 *       
 *   PROPRIETARY RIGHTS NOTICE All rights reserved.  This material
 *   contains the valuable properties and trade secrets of TELUS Health Solutions, 
 *   embodying substantial creative efforts and confidential information, ideas 
 *   and expressions, no part of which may be reproduced, distributed or transmitted 
 *   in any form or by any means electronic, mechanical or otherwise, including
 *   photocopying and recording or in conjunction with any information storage 
 *   or retrieval system without the express written permission of TELUS Health Solutions.
 *   
 *   Description:
 *       TODO: Add class description
 *
 * Change Control:
 *    Date        Ver            Who          Revision History
 * ---------- ----------- ------------------- --------------------------------------------- 
 *	27-Apr-2009					Joohwan Oh       created.
 *  
 */

package jtail.util;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jtail.config.PropertiesHandler;
import jtail.logic.filter.FilterType;
import jtail.resources.Resource;

/**
 * This class interacts with properties file for filter definition.
 * @author joohwan oh, 2009-04-27
 *
 */
public class FilterHandlerNotUsed {
	private final PropertiesHandler propHandler;
	private static final String VALUE_SEPARATOR = ",";
	private static final String REGEX_COMBINEDER = "|";
	public FilterHandlerNotUsed(){
		InputStream in = Resource.getResourceStream(Resource.FILTER_PROPERTIES);
		propHandler = new PropertiesHandler(in);		
	}
	
	
	public String getFilteredContent(String originalContent){
		String filteredContent = originalContent;
		String currentFilter = propHandler.getValue("currentFilter");
		String nonDisplayRegex = getNonDisplayRegex(currentFilter);		 
		if (!Value.isEmptyOrNull(nonDisplayRegex)){
			filteredContent = applyFilter(originalContent, nonDisplayRegex, FilterType.EXCLUDE);
		}
		
		String displayRegex = getDisplayRegex(currentFilter);
		if (!Value.isEmptyOrNull(displayRegex)){
			filteredContent = applyFilter(filteredContent, displayRegex, FilterType.INCLUDE);
		}
		
		return filteredContent;
	}
	
	private String getNonDisplayRegex(String currentFilter) {
		// comma separated string of keys for regular expression definition. 
		String regexList = propHandler.getValue("filter."+currentFilter+".nonDisplayRegex");
		String regex = regexNameListToOneRegex(regexList);
		return regex;
	}


	private String getDisplayRegex(String currentFilter) {
		// comma separated string of keys for regular expression definition. 
		String regexList = propHandler.getValue("filter."+currentFilter+".displayRegex");
		String regex = regexNameListToOneRegex(regexList);
		return regex;
	}


	/**
	 * @param regexList comma seperated list of names for regex, i.e regex.sql,regex.inquirySignOn
	 * @return one combined regular expression
	 */
	private String regexNameListToOneRegex(String regexList) {
		String[] regexNames = regexList.split(VALUE_SEPARATOR);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < regexNames.length; i++) {
			String regex = this.propHandler.getValue(regexNames[i]);
			sb.append(regex);
			if (i < regexNames.length-1){
				sb.append(REGEX_COMBINEDER);
			}
		}
		return sb.toString();
	}


	private String applyFilter(String source, String regex, FilterType filterType){
		int matchStart = 0;
		int matchEnd = 0;
		int appendStart = 0;
		int appendEnd = 0;
		Pattern p = Pattern.compile(regex, Pattern.MULTILINE|Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE);
		Matcher m = p.matcher(source);
		StringBuilder selected = new StringBuilder();
		String displayString = "";
		while (m.find()) {
			matchStart = m.start();
			if (filterType == FilterType.INCLUDE){
				appendStart = matchStart;
			} else {
				appendStart = matchEnd;
			}
			matchEnd = m.end();
			if (filterType == FilterType.INCLUDE){
				appendEnd = matchEnd;
			} else {
				appendEnd = matchStart;
			}
			//displayString = source.substring(appendStart, appendEnd) + Value.NL;
			displayString = source.substring(appendStart, appendEnd);
			selected.append(displayString);
		}
		
		// append part after last match.
		if (filterType == FilterType.EXCLUDE){
			displayString = source.substring(matchEnd);
			selected.append(displayString);
		}
		
		String selectedContent = selected.toString();
//		if (Value.isEmptyOrNull(selectedContent)) {
//			// if regular expression doesn't match, return origianl content.
//			return source;
//		} else {
//			return selectedContent;
//		}
		return selectedContent;
	}	
}
