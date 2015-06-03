/*
 * File: SearchMonitor.java
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
 *       This class monitors tail tab and resumes tail if predetermined time has passed
 *       since tail has stopped.
 *
 * Change Control:
 *    Date        Ver            Who          Revision History
 * ---------- ----------- ------------------- --------------------------------------------- 
 *	22-Dec-2009					Joohwan Oh
 *  
 */
package jtail.logic.search;

import jtail.config.Config;
import jtail.execute.GuiExecutor;
import jtail.log.MyLogger;
import jtail.ui.tab.TailTab;

/**
 * This class enables automatic resumption of tailing. Tailing stops if user starts the search to
 * help user keeps searching without being interrupted by automatic scrolling of tailing area.
 * 
 * @author joohwan oh, 2009-08-20
 */
public class AutomaticTailStopCancelHandler extends Thread {
	
	private static final int ONE_SECOND = 1000;

	/** The should stop thread. */
	private volatile boolean shouldStopThread = false;
	
	/** The tail tab. */
	private final TailTab tailTab;
	
	/** The last time search done. */
	private long lastTimeSearchDone;
	
	/** The thread suspended. */
	private volatile boolean threadSuspended = false;
		
	/**
	 * Checks if is thread suspended.
	 * 
	 * @return true, if checks if is thread suspended
	 */
	public synchronized boolean isThreadSuspended() {
		return threadSuspended;
	}

	/**
	 * Sets the thread suspended.
	 * 
	 * @param threadSuspended the thread suspended
	 */
	public synchronized void setThreadSuspended(boolean threadSuspended) {
		this.threadSuspended = threadSuspended;
	}

	/**
	 * The Constructor.
	 * 
	 * @param tailTab the tail tab
	 */
	public AutomaticTailStopCancelHandler(TailTab tailTab) {
		this.tailTab = tailTab;
	}
	
	/**
	 * Stop thread. Thread.stop() is not safe and deprecated so 
	 * used this approach.
	 */
	public void stopThread(){
		this.shouldStopThread = true;
		MyLogger.debug("SearchMonitor, stopThread()");
	}

	/*
	 * (non-Javadoc) If specified time has passed since last search, resume tailing.
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while (!this.shouldStopThread) {
			long currentTime = System.currentTimeMillis(); 
			long autoTailResumeMills = Config.bean.getAutoTailResumeSeconds() * ONE_SECOND;

			// if it passed resume-time-period, resume tail and suspend this thread.
			if (tailTab.isTailPaused()
					&& (currentTime - this.getLastTimeSearchDone()) > autoTailResumeMills) {
				setThreadSuspended(true);
				GuiExecutor.getInstance().execute(new Runnable() {
					public void run() {
						//tailTab.checkStopTailButton(false);
						tailTab.resumeTail();
					}
				});

				/*
				 * After resuming tail, suspend this thread in order to not check
				 * if we need to resume tail. 
				 */
				while (this.isThreadSuspended()) {
					try {
						Thread.sleep(ONE_SECOND);
					} catch (InterruptedException e) {
						MyLogger.debug("SearchMonitor thread was interrupted.", e);
					}
				}
			} else {
				/*
				 * If tail was stopped or it doesn't pass resume-time-period,
				 * keep sleeping.
				 */
				try {
					Thread.sleep(ONE_SECOND);
				} catch (InterruptedException e) {
					MyLogger.debug("SearchMonitor thread was interrupted.", e);
				}
			}
		}
	}

	/**
	 * Update search time.
	 */
	public synchronized void updateSearchTime() {
		this.lastTimeSearchDone = System.currentTimeMillis();
	}

	/**
	 * Gets the last time search done.
	 * 
	 * @return the last time search done
	 */
	public synchronized long getLastTimeSearchDone() {
		return this.lastTimeSearchDone;
	}
}
