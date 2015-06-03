/*
 * File: OpenedTailTabHandler.java
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
 *	21-Dec-2009					Joohwan Oh
 *  
 */
package jtail.ui.event;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import jtail.ui.tab.TailTab;


/**
 * The Class OpenedTailTabHandler.
 */
public final class OpenedTailTabHandler {
	
	/** The INSTANCE. */
	private static OpenedTailTabHandler INSTANCE;
	
	/** The opened tail tab list. */
	private final LinkedList<TailTab> openedTailTabList;
	
	/**
	 * The Constructor.
	 */
	private OpenedTailTabHandler(){
		openedTailTabList = new LinkedList<TailTab>();
	}
	
	/**
	 * Gets the instance.
	 * 
	 * @return the instance
	 */
	public static OpenedTailTabHandler getInstance(){
		if (INSTANCE == null){
			INSTANCE = new OpenedTailTabHandler();
		}
		return INSTANCE;
	}
	
	/**
	 * Adds the tail tab to the start of the list.
	 * This will be displayed at the top of the opened file menu list. 
	 * 
	 * @param tailTab the tail tab
	 */
	public void addTailTab(TailTab tailTab){
		this.openedTailTabList.addFirst(tailTab);		
	}
	
	/**
	 * Removes the tail tab.
	 * 
	 * @param tailTab the tail tab
	 * 
	 * @return true, if removes the tail tab
	 */
	public boolean removeTailTab(TailTab tailTab){
		return this.openedTailTabList.remove(tailTab);		
	}

	/**
	 * Gets the opened tail tab list.
	 * 
	 * @return the opened tail tab list
	 */
	public LinkedList<TailTab> getOpenedTailTabList() {
		// use defensive copy.
		LinkedList<TailTab> list = new LinkedList<TailTab>(this.openedTailTabList);
		return list;
	}
	
	/**
	 * Checks if is file opened.
	 * 
	 * @param file the file
	 * 
	 * @return true, if checks if is file opened
	 */
	public boolean isFileOpened(File file){
		for (TailTab tailTab : this.openedTailTabList){
			if (tailTab.getFile().equals(file)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Gets the opened file list.
	 * 
	 * @return the opened file list
	 */
	public List<File> getOpenedFileList(){
		List<File> fileList = new ArrayList<File>();
		for (TailTab tailTab : this.openedTailTabList) {
			File file = tailTab.getFile();
			fileList.add(file);
		}
		return fileList;
	}

	/**
	 * Gets the latest tail tab.
	 * 
	 * @return the latest tail tab
	 */
	public TailTab getLatestTailTab() {
		if (this.openedTailTabList.size() < 1){
			return null;
		} else {
			return this.openedTailTabList.getFirst();
		}
	}

}
