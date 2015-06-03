/*
 * File: SearchCondition.java
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
 *	4-Feb-2010					Joohwan Oh
 *  
 */
package jtail.logic.search;

/**
 * The Class SearchCondition.
 */
public final class SearchCondition {
	
	/** The is case sensitive search. */
	private boolean isCaseSensitiveSearch;
	
	/** The is regular expression search. */
	private boolean isRegularExpressionSearch;
	
	/** The is whole word search. */
	private boolean isWholeWordSearch;
	
	/** The is forward search. */
	private boolean isForwardSearch;
	
	/**
	 * The Constructor.
	 */
	public SearchCondition(){
		this.isCaseSensitiveSearch = false;
		this.isRegularExpressionSearch = true;
		this.isWholeWordSearch = false;
		this.isForwardSearch = false;
	}
	
	/**
	 * The Constructor.
	 * 
	 * @param isCaseSensitiveSearch the is case sensitive search
	 * @param isForwardSearch the is forward search
	 * @param isRegularExpressionSearch the is regular expression search
	 * @param isWholeWordSearch the is whole word search
	 */
	public SearchCondition(boolean isCaseSensitiveSearch, boolean isForwardSearch,
			boolean isRegularExpressionSearch, boolean isWholeWordSearch) {
		this.isCaseSensitiveSearch = isCaseSensitiveSearch;
		this.isForwardSearch = isForwardSearch;
		this.isRegularExpressionSearch = isRegularExpressionSearch;
		this.isWholeWordSearch = isWholeWordSearch;
	}	

	/**
	 * Checks if is case sensitive search.
	 * 
	 * @return true, if checks if is case sensitive search
	 */
	public boolean isCaseSensitiveSearch() {
		return isCaseSensitiveSearch;
	}

	/**
	 * Sets the case sensitive search.
	 * 
	 * @param isCaseSensitiveSearch the case sensitive search
	 */
	public void setCaseSensitiveSearch(boolean isCaseSensitiveSearch) {
		this.isCaseSensitiveSearch = isCaseSensitiveSearch;
	}

	/**
	 * Checks if is regular expression search.
	 * 
	 * @return true, if checks if is regular expression search
	 */
	public boolean isRegularExpressionSearch() {
		return isRegularExpressionSearch;
	}

	/**
	 * Sets the regular expression search.
	 * 
	 * @param isRegularExpressionSearch the regular expression search
	 */
	public void setRegularExpressionSearch(boolean isRegularExpressionSearch) {
		this.isRegularExpressionSearch = isRegularExpressionSearch;
	}

	/**
	 * Checks if is whole word search.
	 * 
	 * @return true, if checks if is whole word search
	 */
	public boolean isWholeWordSearch() {
		return isWholeWordSearch;
	}

	/**
	 * Sets the whole word search.
	 * 
	 * @param isWholeWordSearch the whole word search
	 */
	public void setWholeWordSearch(boolean isWholeWordSearch) {
		this.isWholeWordSearch = isWholeWordSearch;
	}

	/**
	 * Checks if is forward search.
	 * 
	 * @return true, if checks if is forward search
	 */
	public boolean isForwardSearch() {
		return isForwardSearch;
	}

	/**
	 * Sets the forward search.
	 * 
	 * @param isForwardSearch the forward search
	 */
	public void setForwardSearch(boolean isForwardSearch) {
		this.isForwardSearch = isForwardSearch;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "[case sensitive:"+this.isCaseSensitiveSearch+
			", whole word:"+this.isWholeWordSearch+
			", regular expression:"+this.isRegularExpressionSearch+
			", forward search:"+this.isForwardSearch+"]";
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if (!(o instanceof SearchCondition)) return false;
		if (o == this) return true;
		SearchCondition searchCondtion = (SearchCondition)o;
		return (this.isCaseSensitiveSearch() == searchCondtion.isCaseSensitiveSearch()
				&& this.isForwardSearch() == searchCondtion.isForwardSearch()
				&& this.isRegularExpressionSearch() == searchCondtion.isRegularExpressionSearch()
				&& this.isWholeWordSearch() == searchCondtion.isWholeWordSearch());
	}
	
	public boolean equalsExceptSearchDirection(SearchCondition searchCondition) {
		if (searchCondition == null){
			return false;
		}
		return (this.isCaseSensitiveSearch() == searchCondition.isCaseSensitiveSearch()
				&& this.isRegularExpressionSearch() == searchCondition.isRegularExpressionSearch()
				&& this.isWholeWordSearch() == searchCondition.isWholeWordSearch());
	}	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int result = 17;
		result = 37 * result + Boolean.valueOf(this.isCaseSensitiveSearch).hashCode();
		result = 37 * result + Boolean.valueOf(this.isForwardSearch).hashCode();
		result = 37 * result + Boolean.valueOf(this.isRegularExpressionSearch).hashCode();
		result = 37 * result + Boolean.valueOf(this.isWholeWordSearch).hashCode();
		return result;
	}	
}
