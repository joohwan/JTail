/*
 * File: OpenedFileMenuHandler.java
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
package jtail.ui.menu;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jtail.config.Config;
import jtail.ui.MainWindow;
import jtail.ui.event.OpenedTailTabHandler;
import jtail.ui.tab.TailTab;
import jtail.util.Value;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabItem;

public final class OpenedFileMenuHandler {
	private static OpenedFileMenuHandler INSTANCE;
	private static final String OPENED_FILE = "openedFile";
	private static final String OPENED_FILE_SEPARATOR_MENU_ITEM = "openedFileSeparatorMenuItem";
	private final MainWindow mainWindow;
	
	private OpenedFileMenuHandler(MainWindow window) {
		mainWindow = window;		
	}
	
	public static OpenedFileMenuHandler getInstance(MainWindow window){
		if (INSTANCE == null){
			INSTANCE = new OpenedFileMenuHandler(window);
		}
		return INSTANCE;
	}
		
	/**
	 * Update opened file menu item when file is opened or closed.
	 * Also update opened file path list of configuration.
	 */
	public void updateMenu(OpenedTailTabHandler openedTailTabHandler){		
		updateOpenedFileMenuItem(openedTailTabHandler.getLatestTailTab());		
	}
	
	/**
	 * Update opened file menu item when file is opened.
	 */
	private void updateOpenedFileMenuItem(TailTab openingTailTab){
		// remove existing opened file menu items. 
		List<File> openedFileList = removeExistingOpenedFileMenuItem();
		
		this.addSeparatorForOpenedFileMenuItemsIfNotAdded();
		
		// add a MenuItem for the file just opened.
		final Menu fileMenu = getFileMenu();
		File openedFile = openingTailTab.getFile();			
		MenuItem menuItem = new MenuItem(fileMenu, SWT.PUSH);
		menuItem.setData(OPENED_FILE, openedFile);
		menuItem.setText(openedFile.getAbsolutePath());
		menuItem.addSelectionListener(new OpenedFileMenuListener(openedFile));
		
		Iterator<File> iterator = openedFileList.iterator();
		int i = 0;
		while (iterator.hasNext() && i < (Config.bean.getMaximumOpenedFileMenuSize() - 1)){
			menuItem = new MenuItem(fileMenu, SWT.PUSH);
			openedFile = iterator.next();
			menuItem.setData(OPENED_FILE, openedFile);
			menuItem.setText(openedFile.getAbsolutePath());
			menuItem.addSelectionListener(new OpenedFileMenuListener(openedFile));			
			
			i++;
		}		
		
		// update opened file list for serialization.
		mainWindow.saveOpenedFileMenu(getOpenedFileMenuPathList());	
	}
		
	private List<MenuItem> getOpenedFileMenuList() {
		List<MenuItem> openedFileMenuList = new ArrayList<MenuItem>();
		final Menu fileMenu = getFileMenu();
		for (int i = 0; i < fileMenu.getItemCount(); i++){
			MenuItem menuItem = fileMenu.getItem(i);
			Object data = menuItem.getData(OPENED_FILE);
			if (data != null){
				openedFileMenuList.add(menuItem);
			}
		}
		return openedFileMenuList;
	}
	
	private List<String> getOpenedFileMenuPathList() {
		List<String> openedFilePathList = new ArrayList<String>();
		final Menu fileMenu = getFileMenu();
		for (MenuItem menuItem : fileMenu.getItems()) {
			File file = (File) menuItem.getData(OPENED_FILE);
			if (file != null){
				openedFilePathList.add(file.getAbsolutePath());
			}
		}		
		return openedFilePathList;
	}
	
	/**
	 * Create opened file menu items when application starts.
	 */
	public void loadMenuFromConfiguration(){
		//removeExistingOpenedFileMenuItem();
		loadFromConfiguration();		
	}
		
	/**
	 * Remove existing opened file menu item under File menu
	 * and create separator menu item (---------) only if it was not created before.
	 * This is necessary if you want to update opened file menu items because api doesn't
	 * provide a way to insert one MenuItem in a specific location in the Menu. All the list
	 * of MenuItem need to be added again.
	 * 
	 * @return the list< file> file list of removed menu items
	 */
	private List<File> removeExistingOpenedFileMenuItem(){
		List<File> openedFileList = new ArrayList<File>();
		final Menu fileMenu = getFileMenu();
		for (int i = 0; i < fileMenu.getItemCount(); i++){
			File file = (File) fileMenu.getItem(i).getData(OPENED_FILE);
			if (file != null){
				openedFileList.add(file);
				
				// remove opened file menu item.
				fileMenu.getItem(i).dispose();
				
				// because menu item size decrease by dispose call.
				i--;		
			}
		}
		
		return openedFileList;
	}
	
	/**
	 * Create opened file menu item from configuration file.
	 */
	private void loadFromConfiguration(){
		if (!Value.isEmpty(Config.bean.getOpenedFilePathList())) {
			addSeparatorForOpenedFileMenuItemsIfNotAdded();
			
			for (String filePath : Config.bean.getOpenedFilePathList()) {
				final File file = new File(filePath);
				final Menu fileMenu = getFileMenu();
				final MenuItem menuItem = new MenuItem(fileMenu, SWT.PUSH);
				menuItem.setData(OPENED_FILE, file);
				//menuItem.setText(file.getName());
				menuItem.setText(file.getAbsolutePath());
				menuItem.addSelectionListener(new OpenedFileMenuListener(file));		
			}	
		}						
	}
	
	private void addSeparatorForOpenedFileMenuItemsIfNotAdded() {
		if (separatorMenuItemWasNotAdded()){
			final Menu fileMenu = getFileMenu();
			MenuItem separatorMenuItem = new MenuItem(fileMenu, SWT.SEPARATOR);
			separatorMenuItem.setData(OPENED_FILE_SEPARATOR_MENU_ITEM, true);	
		}				
	}

	private boolean separatorMenuItemWasNotAdded() {
		final Menu fileMenu = getFileMenu();
		for (int i = 0; i < fileMenu.getItemCount(); i++){
			Boolean isSeparator = (Boolean) fileMenu.getItem(i).getData(OPENED_FILE_SEPARATOR_MENU_ITEM);
			if (isSeparator != null && isSeparator.booleanValue() == true){
				return false;
			}
		}
		return true;
	}

	private Menu getFileMenu(){		
		final MenuManager menuBar = mainWindow.getMenuBarManager();
		final IContributionItem[] menus = menuBar.getItems();
		final MenuManager fileMenuManager = (MenuManager) menus[0];				
		final Menu fileMenu = fileMenuManager.getMenu();
		return fileMenu;
	}
	
	class OpenedFileMenuListener extends SelectionAdapter {
		//private TailTab tailTab;
		private File file;
		
//		public OpenedFileMenuListener(TailTab tab){
//			this.tailTab = tab;	
//			this.file = this.tailTab.getFile();
//		}
		
		/**
		 * Create opened file menu listener when application is launched
		 * and it creates opened file menu items from configuration file.
		 * @param file
		 */
		public OpenedFileMenuListener(File file){
			this.file = file;			
		}
		
		public void widgetSelected(SelectionEvent e) {
			selectOrCreateTailTab();
		}	
		
		private void selectOrCreateTailTab(){
			/*
			 * loop through all tabs and if same tail tab is found,
			 * set it selected.
			 */
			for (TabItem tabItem : mainWindow.getTabFolder().getItems()){
				TailTab tailTab = (TailTab)tabItem.getControl();
				if (tailTab.getFile().equals(this.file)) {
					mainWindow.getTabFolder().setSelection(tabItem);
					return;
				}
			}						
			
			/*
			 * If existing tab is not found because user closed it, create one same as
			 * open menu being clicked.
			 */
			mainWindow.createTailTab(file);
		}
	}

}
