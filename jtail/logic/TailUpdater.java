/*
 * File: TailUpdater.java
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
 *       update screen if file is updated.
 *
 * Change Control:
 *    Date        Ver            Who          Revision History
 * ---------- ----------- ------------------- --------------------------------------------- 
 *	22-Dec-2009					Joohwan Oh
 *  
 */
package jtail.logic;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import jtail.log.MyLogger;
import jtail.ui.tab.TailTab;
import jtail.ui.tab.pane.DisplayTriggerAction;

/**
 * The Class TailUpdater.
 */
public final class TailUpdater {
	
	/** The tail tab. */
	private final TailTab tailTab;
	
	/** The file. */
	private final File file;
	
	/** The scheduler. */
	private final ScheduledExecutorService scheduler;
	
	/** The scheduled future. */
	private ScheduledFuture<?> scheduledFuture;
	
	/** The last updated file time. */
	private long lastUpdatedFileTime = 0;
	
	/** The last updated file size. */
	private long lastUpdatedFileSize = 0;
	
	/** The REFRES h_ tim e_ i n_ millis. */
	private final int REFRESH_TIME_IN_MILLIS = 10;

	private ViewContent previousViewContent;
	
	/** The filter handler. */
	//private FilterHandler filterHandler;

	// private final int mask = FileMonitor.FILE_MODIFIED
	// | FileMonitor.FILE_DELETED
	// | FileMonitor.FILE_RENAMED;

	/**
	 * The Constructor.
	 * 
	 * @param tailTab the tail tab
	 */
	public TailUpdater(TailTab tailTab) {
		this.tailTab = tailTab;
		this.file = tailTab.getFile();
		//this.filterHandler = new FilterHandler();
		scheduler = Executors.newScheduledThreadPool(1);
	}

	/**
	 * Start tail.
	 */
	public void startTail() {
		scheduledFuture = scheduler.scheduleAtFixedRate(new FileChangeDetector(), 0,
				REFRESH_TIME_IN_MILLIS, TimeUnit.MILLISECONDS);
		this.tailTab.enablePauseDisableResumeButton();
		this.tailTab.getSearchHandler().clearPrevSearchWord();
//		MyLogger.debug("tail task (" + this.scheduledFuture + ") started.");
	}
	
	/**
	 * Cancel current tail. This method is called when user click on 'Stop Tail' checkbox.
	 */
	public void cancelTail(){
		boolean cancelled = this.scheduledFuture.cancel(true);
		this.tailTab.enableResumeDisablePauseButton();
//		MyLogger.debug("tail task (" + this.scheduledFuture + ") was cancelled: " + cancelled);
	}

	/**
	 * Shutdown tail updater. This method cancel running tail task and shutdown 
	 * tail thread when user close a file.
	 */
	public void shutdown() {
		boolean cancelled = this.scheduledFuture.cancel(true);
//		MyLogger.debug("tail task (" + this.scheduledFuture + ") for file ("+this.file.getName()+") was cancelled: " + cancelled);
		this.scheduler.shutdown();
//		MyLogger.debug("tail service (" + this.scheduler + ") was shutdowned.");
		try {
			this.scheduler.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			MyLogger.error("termination of tail service (" + this.scheduler + ") was interrupted.");
		}
	}
	
	/**
	 * Checks if is tail running.
	 * 
	 * @return true, if checks if is tail running
	 */
	public boolean isTailRunning() {
		if (this.scheduledFuture == null) {
			return false;
		}
		return !this.scheduledFuture.isCancelled();
	}

	/**
	 * Checks if is display start offset set.
	 * 
	 * @return true, if checks if is display start offset set
	 */
	public boolean displayStartOffsetChanged() {
		return this.tailTab.displayStartOffsetChanged();
	}

	/**
	 * Update viewing area as file is changed. This scrolls down to the bottom of tail area.
	 */
	public void fileChanged() {
//		int displayStartOffset = this.tailTab.getDisplayStartOffset();
//		displayStartOffset = adjustDisplayOffsetIfFileSizeReduced(displayStartOffset);

		tailTab.redrawViewArea(getLastViewContent(), DisplayTriggerAction.FILE_CHANGED);
		
		//tailTab.updateTailContent((int) (fileLength-TailTab.NUM_OF_CHARS_TO_DISPLAY), TailTab.NUM_OF_CHARS_TO_DISPLAY);
//		ViewContent currentViewContent = getLastViewContent();
//		if (this.previousViewContent != null) {
//			if (!currentViewContent.getText().equals(this.previousViewContent.getText())){
//				tailTab.redrawViewArea(getLastViewContent(), DisplayTriggerAction.FILE_CHANGED);
//				this.previousViewContent = currentViewContent;
//				MyLogger.debug("fileChanged");
//			}
//		} else {
//			tailTab.redrawViewArea(getLastViewContent(), DisplayTriggerAction.FILE_CHANGED);
//			this.previousViewContent = currentViewContent;
//			MyLogger.debug("fileChanged");
//		}		
	}
	
	private ViewContent getLastViewContent() {
		ViewContentHandler viewContentHandler = new ViewContentHandler(tailTab);
		ViewContent viewContent = viewContentHandler.getLastViewContent();
		return viewContent;
	}

	private FileBlock getLastFileBlock(int displayStartOffset){
		return this.getFileBlockHandler().getLastFileBlock(displayStartOffset);
	}

	private int adjustDisplayOffsetIfFileSizeReduced(int displayStartOffset) {
		int fileLength = this.tailTab.getFileLength();

		/*
		 * If file size is less than displayStartOffset such that file was rewritten from the
		 * scratch, display all file content.
		 */
		if (displayStartOffset > fileLength) {
			displayStartOffset = 0;
			this.tailTab.setDisplayStartOffset(displayStartOffset);
		}
		return displayStartOffset;
	}

	private FileBlockHandler getFileBlockHandler() {
		return this.tailTab.getFileBlockHandler();
	}

	/*
	 * nested classes
	 */

	/**
	 * The Class DetectFileChanged.
	 */
	private final class FileChangeDetector implements Runnable {
		
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			// MyLogger.log("DetectFileChanged.run() called");
			/*
			 * Algorithm to detect file change. 1. compare the file modified time of current and last
			 * time. 2. compare the file size of current and last time. 3. compare last n substring
			 * of current and last time.
			 */
			long fileModifiedTime = file.lastModified();
			long fileSize = file.length();
			String fileName = file.getName();
			boolean filterChanged = false;
			if (fileModifiedTime != TailUpdater.this.lastUpdatedFileTime) {
				TailUpdater.this.lastUpdatedFileTime = fileModifiedTime;
				fileChanged();
				// MyLogger.log("lastModified was updated and tail was updated:" + fileName);
			} else if (fileSize != TailUpdater.this.lastUpdatedFileSize) {
				TailUpdater.this.lastUpdatedFileSize = fileSize;
				fileChanged();
				// MyLogger.log("file size was updated and tail was updated:" + fileName);
			} else if (filterChanged) {
				fileChanged();
//				MyLogger.debug("filter was updated and tail was updated:" + fileName);
			} else if (displayStartOffsetChanged()) {
				fileChanged();
				tailTab.setDisplayStartOffsetSet(false);
//				MyLogger.debug("displayStartOffset was updated and tail was updated:" + fileName);
			}
			/*
			 * else { String content = getTailContent(); long start = System.currentTimeMillis();
			 * long end = 0; if (!content.equals(lastDisplayedFileContent)){ end =
			 * System.currentTimeMillis();
			 * MyLogger.log("file comparison time:"+file.getName()+","+(end
			 * -start)+" milli seconds."); TailUpdater.this.lastDisplayedFileContent = content;
			 * tailTab.updateTailContent(content); } end = System.currentTimeMillis();
			 * MyLogger.log("file comparison time:"
			 * +file.getName()+","+(end-start)+" milli seconds."); }
			 */
		}

	}

	// enum ViewportUpdateEventType {
	// modifiedTimeChanged,
	// fileSizeChanged,
	// filterChanged,
	// displayStartOffsetChange
	// }
}
