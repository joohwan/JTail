package jtail.ui;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import jtail.config.Config;
import jtail.log.MyLogger;
import jtail.resources.Resource;
import jtail.ui.dialog.DialogUtil;
import jtail.ui.event.OpenedTailTabHandler;
import jtail.ui.event.mainWindow.BackwardSearchAction;
import jtail.ui.event.mainWindow.ClearAction;
import jtail.ui.event.mainWindow.CloseAction;
import jtail.ui.event.mainWindow.CopyAction;
import jtail.ui.event.mainWindow.ExitAction;
import jtail.ui.event.mainWindow.FilterAction;
import jtail.ui.event.mainWindow.ForwardSearchAction;
import jtail.ui.event.mainWindow.MainWindowDisposeListener;
import jtail.ui.event.mainWindow.OpenAction;
import jtail.ui.event.mainWindow.OptionsAction;
import jtail.ui.event.mainWindow.PauseTailAction;
import jtail.ui.event.mainWindow.ReloadAction;
import jtail.ui.event.mainWindow.ResumeTailAction;
import jtail.ui.event.mainWindow.SelectAllAction;
import jtail.ui.menu.OpenedFileMenuHandler;
import jtail.ui.tab.TailTab;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public final class MainWindow extends ApplicationWindow {
	private OpenAction openAction;
	private CloseAction closeAction;
	private CopyAction copyAction;
	private SelectAllAction selectAllAction;
	private ExitAction exitAction;
	private ClearAction clearAction;
	private ReloadAction reloadAction;
	private BackwardSearchAction backwardSearchAction;
	private ForwardSearchAction forwardSearchAction;	
	private FilterAction filterAction;
	private OptionsAction optionsAction;
	private ResumeTailAction resumeTailAction;
	private PauseTailAction pauseTailAction;
	
	private MenuManager tailMenuManager;

	private TabFolder tabFolder;
	private OpenedFileMenuHandler openedFileMenuHandler;
	private OpenedTailTabHandler openedTailTabHandler;
	
	private static MainWindow mainWindow = null;
	
	public static MainWindow getMainWindow(){
		if (mainWindow == null){
			mainWindow = new MainWindow();
		}
		return mainWindow;
	}

	private MainWindow() {
		super(null);
		defineActions();		
		this.addMenuBar();
		// this.addStatusLine();
		this.addToolBar(SWT.FLAT | SWT.WRAP);		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Shell shell = this.getShell();
		tabFolder = new TabFolder(parent, SWT.TOP);

		// window title
		String windowTitle = Resource.getString("jtail");
		shell.setText(windowTitle);

		// set icon.
		Display display = Display.getDefault();
		InputStream is = Resource.getResourceStream(Resource.MAIN_IMAGE_PATH);
		Image icon = new Image(display, is);
		shell.setImage(icon);

		locateWindow(parent);

		// restore opened file menu items from configuration file.
		openedFileMenuHandler = OpenedFileMenuHandler.getInstance(this);
		openedFileMenuHandler.loadMenuFromConfiguration();

		openedTailTabHandler = OpenedTailTabHandler.getInstance();

		parent.addDisposeListener(new MainWindowDisposeListener(this));
		return parent;
	}

	private void locateWindow(Composite parent) {
		/*
		 * If this application opens for the first time, maximize a main window.
		 * Otherwise locate it on the place it was closed last time.
		 */
		if (Config.bean.isFirstOpen()) {			
//			Point center = Geometry.centerPoint(this.getShell().getDisplay().getBounds());
//			int x = center.x - (lastBounds.width / 2);
//			int y = center.y - (lastBounds.height / 2);
			parent.setBounds(0, 0, this.getShell().getDisplay().getBounds().width,
					this.getShell().getDisplay().getBounds().height);			
			Config.bean.setFirstOpen(false);
		} else {
			Rectangle lastBounds = getLastBounds();
			parent.setBounds(lastBounds);
		}
	}
	
	private Rectangle getLastBounds() {
		Display display = Display.getCurrent();
		int displayWidth = display.getBounds().width;
		int displayHeight = display.getBounds().height;
		int x, y, width, height;
		x = Config.bean.getLastPosition().x;
		if (x > displayWidth) {
			x = 0;
		}
		y = Config.bean.getLastPosition().y;
		if (y > displayHeight) {
			y = 0;
		}
		width = Config.bean.getLastPosition().width;
		height = Config.bean.getLastPosition().height;
		return new Rectangle(x, y, width, height);
	}
	
	private void defineActions() {
		this.openAction = new OpenAction(this);
		this.closeAction = new CloseAction(this);
		this.copyAction = new CopyAction(this);
		this.selectAllAction = new SelectAllAction(this);
		this.clearAction = new ClearAction(this);
		this.reloadAction = new ReloadAction(this);
		this.exitAction = new ExitAction(this);
		this.forwardSearchAction = new ForwardSearchAction(this);
		this.backwardSearchAction = new BackwardSearchAction(this);
		this.filterAction = new FilterAction(this);
		this.optionsAction = new OptionsAction(this);
		this.resumeTailAction = new ResumeTailAction(this);
		this.pauseTailAction = new PauseTailAction(this);
	}
	
	@Override
	protected MenuManager createMenuManager() {
		MenuManager menuBar = new MenuManager("");

		MenuManager fileMenu = new MenuManager(Resource.getString("file.menu.label"));
		fileMenu.add(openAction);
		fileMenu.add(closeAction);
		fileMenu.add(exitAction);

		MenuManager editMenu = new MenuManager(Resource.getString("edit.menu.label"));
		editMenu.add(copyAction);
		editMenu.add(selectAllAction);
		editMenu.add(clearAction);
		editMenu.add(reloadAction);
		
		MenuManager searchMenu = new MenuManager(Resource.getString("search.menu.label"));
		searchMenu.add(backwardSearchAction);
		searchMenu.add(forwardSearchAction);
		
		tailMenuManager = new MenuManager(Resource.getString("tail.menu.label"));
		tailMenuManager.add(this.resumeTailAction);
		tailMenuManager.add(this.pauseTailAction);
						
		MenuManager toolsMenu = new MenuManager(Resource.getString("tools.menu.label"));
		toolsMenu.add(filterAction);
		toolsMenu.add(optionsAction);

		MenuManager helpMenu = new MenuManager(Resource.getString("help.menu.label"));

		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(searchMenu);
		menuBar.add(tailMenuManager);
		menuBar.add(toolsMenu);
		menuBar.add(helpMenu);

		return menuBar;
	}

	@Override
	protected ToolBarManager createToolBarManager(int style) {
		ToolBarManager toolBarManager = new ToolBarManager(style);
		toolBarManager.add(openAction);
		toolBarManager.add(closeAction);
		toolBarManager.add(new Separator());
		toolBarManager.add(copyAction);
		toolBarManager.add(selectAllAction);
		toolBarManager.add(clearAction);
		toolBarManager.add(reloadAction);
		toolBarManager.add(new Separator());
		toolBarManager.add(filterAction);
		toolBarManager.add(optionsAction);
		toolBarManager.add(new Separator());
		toolBarManager.add(exitAction);
		return toolBarManager;
	}

	@Override
	protected StatusLineManager createStatusLineManager() {
		StatusLineManager slm = new StatusLineManager();
		slm.setMessage("Hello, world!");
		return slm;
	}	

	public boolean isFileOpened(File selectedFile) {
		return this.openedTailTabHandler.isFileOpened(selectedFile);
	}

	/**
	 * when users open a new file for tail, it must call this method to add a new file path to
	 * opened file path set. Keep the opened tail tab size to the maximum size.
	 * 
	 * @param filePath
	 */
	public void updateOpenedFileMenu() {
		this.openedFileMenuHandler.updateMenu(this.openedTailTabHandler);
	}

	public void addToOpenedFileTabList(TailTab tailTab) {
		this.openedTailTabHandler.addTailTab(tailTab);
	}

	public boolean removeFromOpenedTailTabList(TailTab tailTab) {
		return this.openedTailTabHandler.removeTailTab(tailTab);

	}

	public List<TailTab> getOpenedTailTabList() {
		return this.openedTailTabHandler.getOpenedTailTabList();
	}
	
	public void stopAllRunningTail(){
		List<TailTab> tailTabList = getOpenedTailTabList();
		for (TailTab tailTab : tailTabList){
			tailTab.pauseTail();
		}
	}

	/**
	 * Return the list of files opened on each tab.
	 * 
	 * @return list of files opened.
	 */
	public List<File> getOpenedFileList() {
		return this.openedTailTabHandler.getOpenedFileList();
	}

	/**
	 * Update opened file path list of configuration.
	 */
	public void saveOpenedFileMenu(List<String> openedFilePathList) {
		Config.bean.setOpenedFilePathList(openedFilePathList);
	}

	public TabItem getSelectedTabItem() {
		TabItem selectedTabItem = null;
		int selectedTabIndex = tabFolder.getSelectionIndex();
		if (selectedTabIndex >= 0) {
			TabItem[] tabItems = tabFolder.getItems();
			if (tabItems != null && tabItems.length > 0) {
				selectedTabItem = tabItems[selectedTabIndex];
			}
		}
		return selectedTabItem;
	}

	public TailTab getSelectedTailTab() {
		TabItem selectedTabItem = getSelectedTabItem();
		return (selectedTabItem == null ? null : (TailTab) selectedTabItem.getControl());
	}

	public TailTab createTailTab(File file) {
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(file.getName());
		tabItem.setToolTipText(file.getAbsolutePath());
		TailTab tailTab = new TailTab(tabFolder, SWT.NONE, this, file);
		tabItem.setControl(tailTab);
		tabFolder.setSelection(tabItem);
		this.addToOpenedFileTabList(tailTab);
		return tailTab;
	}

	/*
	 * getter, setter
	 */
	public CopyAction getCopyAction() {
		return copyAction;
	}

	public IAction getCloseAction() {
		return this.closeAction;
	}

	public TabFolder getTabFolder() {
		return tabFolder;
	}

	public boolean selectTailTab(File file) {
		TabFolder tabFolder = getTabFolder();
		TabItem[] tabItems = tabFolder.getItems();
		boolean tabFound = false;

		/*
		 * loop through all tabs and if same tail tab is found, set it selected.
		 */
		for (TabItem tabItem : tabItems) {
			TailTab tailTab = (TailTab) tabItem.getControl();
			if (tailTab.getFile().equals(file)) {
				tabFolder.setSelection(tabItem);
				tabFound = true;
				break;
			}
		}

		return tabFound;
	}
	
	public static void main(String[] args) {
		setLogging(args);
		
		MainWindow win = null;
		try {
			win = MainWindow.getMainWindow();
			win.setBlockOnOpen(true);
			win.open();
		} catch (Throwable t) {
			MyLogger.debug("Error during opening of MainWindow", t);
			Display display = Display.getDefault();
			Shell shell = display.getActiveShell();
			if (shell == null) {
				new Shell();
			}
			DialogUtil.displayErrorDialog(t);			
		} finally {
			Display.getDefault().dispose();
		}
	}

	private static void setLogging(String[] args) {
		for (String argument : args){
			if ("-enableDebugLog=true".equalsIgnoreCase(argument)){
				MyLogger.enableDebugLog();
			} else if ("-enableDebugLog=false".equalsIgnoreCase(argument)){
				MyLogger.disableDebugLog();
			} else if ("-enableErrorLog=true".equalsIgnoreCase(argument)){
				MyLogger.enableErrorLog();
			} else if ("-enableErrorLog=false".equalsIgnoreCase(argument)){
				MyLogger.disableErrorLog();
			}
		}
		
	}

	public ResumeTailAction getResumeTailAction() {
		return resumeTailAction;
	}

	public MenuItem getResumeTailMenuItem() {
		return tailMenuManager.getMenu().getItem(0);
	}
	
	public MenuItem getPauseTailMenuItem() {
		return tailMenuManager.getMenu().getItem(1);
	}
}
