package jtail.ui.tab;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import jtail.config.Config;
import jtail.log.MyLogger;
import jtail.logic.FileBlockHandler;
import jtail.logic.TailUpdater;
import jtail.logic.ViewContent;
import jtail.logic.ViewContentHandler;
import jtail.logic.filter.Filter;
import jtail.logic.filter.FilterHandler;
import jtail.logic.search.AutomaticTailStopCancelHandler;
import jtail.logic.search.SearchCondition;
import jtail.logic.search.SearchHandler;
import jtail.logic.search.SearchWord;
import jtail.resources.Resource;
import jtail.ui.MainWindow;
import jtail.ui.dialog.DialogUtil;
import jtail.ui.dialog.UseFiltersDialog;
import jtail.ui.event.ViewPortChangeHandler;
import jtail.ui.tab.pane.DisplayTriggerAction;
import jtail.ui.tab.pane.TextViewerPane;
import jtail.util.CursorHandler;
import jtail.util.Value;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public final class TailTab extends Composite {
	private File file;
	private MainWindow mainWindow;
	private TailUpdater tailUpdater;
	private AutomaticTailStopCancelHandler searchMonitor;
	private SearchCondition searchCondition = new SearchCondition();

	/*
	 * widgets
	 */	
	
	private SashForm sashForm;
	private TextViewerPane tailPane;
	
	private Combo searchCombo;
	private Composite bottomBarArea;
	
	private ToolItem upArrowButton;
	private ToolItem downArrowButton;
	
	private Button caseSensitiveCheckbox;
	private Button wholeWordCheckbox;
	private Button regExpressionCheckbox;
		
	private Label tailLabel;
	private ToolItem resumeTailing;
	private ToolItem pauseTailing;
		
	// ui change to indicate the search result.
	private Color DEFAULT_BG_COLOR;

	// background color for search field when search is not found, salmon color
	static final Color NO_FOUND_BG_COLOR = new Color(Display.getDefault(), 250, 128, 114);
	
	static final Color TAIL_RUNNING_LABEL_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
	static final Color TAIL_PAUSED_LABEL_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_RED);

	// this variable is set when 'clear' menu is clicked.
	// private int prevDisplayStartOffset = 0;
	private int displayStartOffset = 0;
	private boolean displayStartOffsetChanged = false;
	private ViewContentHandler viewContentHandler;
	private ViewPortChangeHandler viewPortChangeHandler;
	private FileBlockHandler fileBlockHandler;
	
	private List<Filter> filterList = new ArrayList<Filter>();
	
	public TailTab(Composite parent, int style, MainWindow mainWindow, File file) {
		super(parent, style);
		
		initializeInstanceVariables(mainWindow, file);	
		
		setLayout(new FillLayout());
		
		createSashForm();

		// add first part, text area to tail the file
		createTailViewArea();

		// add second part, filter & search area
		createBottomBarArea();
		
		GridDataFactory gdf = GridDataFactory.fillDefaults();
		
		createSearchPart(bottomBarArea, gdf);
				
		createSeparatorLabel(bottomBarArea, gdf);
				
		createTailPart(bottomBarArea, gdf);
		
		createSeparatorLabel(bottomBarArea, gdf);
		
		createUseFiltersPart(bottomBarArea, gdf);
			
		// set spaces for each area.
		sashForm.setWeights(new int[] { 95, 5 });
		// sashForm.setDragDetect(false);

		tailUpdater = new TailUpdater(this);
		this.resumeTail();

		this.addDisposeListener(new TailTabDisposeListener());				
	}


	private void createSeparatorLabel(Composite area, GridDataFactory gdf) {
		Label separator = new Label(area, SWT.SEPARATOR);
		gdf.copy().span(1, 1).hint(SWT.DEFAULT, 10).applyTo(separator);
	}

	private void initializeInstanceVariables(MainWindow mainWindow, File file) {
		this.mainWindow = mainWindow;
		this.file = file;
		this.fileBlockHandler = new FileBlockHandler(this.file);		
		this.filterList = FilterHandler.getSavedFilterList(this.file);
		this.viewContentHandler = new ViewContentHandler(this);
	}

	private void createBottomBarArea() {
		bottomBarArea = new Composite(sashForm, SWT.NONE);
		GridLayout configAreaLayout = new GridLayout();
		configAreaLayout.makeColumnsEqualWidth = false;
		configAreaLayout.numColumns = 100;
		bottomBarArea.setLayout(configAreaLayout);
	}

	private void createTailViewArea() {
		tailPane = new TextViewerPane(sashForm, SWT.NONE, this);
	}

	private void createSashForm() {
		sashForm = new SashForm(this, SWT.VERTICAL);
		sashForm.setLayout(new FillLayout());		
	}
	
	private void createSearchPart(Composite configArea, GridDataFactory gdf) {
		createSearchLabel(configArea, gdf);		
		createSearchDropdownList(configArea, gdf);		
		ToolBar toolBar = createToolBar(configArea);				
		addUpArrowButton(toolBar);		
		addDownArrowButton(toolBar);		
		createCaseSensitiveCheckbox(configArea, gdf);
		createWholeWordCheckbox(configArea, gdf);
		createRegularExpressionCheckbox(configArea, gdf);
	}
		
	private void createSearchLabel(Composite configArea, GridDataFactory gdf) {
		Label searchLabel = new Label(configArea, SWT.CENTER);
		searchLabel.setText(Resource.getString("search")+":");
		gdf.copy().align(SWT.FILL, SWT.CENTER).span(1, 1).applyTo(searchLabel);
	}
	
	private void createRegularExpressionCheckbox(Composite configArea, GridDataFactory gdf) {
		this.regExpressionCheckbox = new Button(configArea, SWT.CHECK);
		regExpressionCheckbox.setText(Resource.getString("regular.expression"));
		gdf.copy().span(1, 1).applyTo(regExpressionCheckbox);
		regExpressionCheckbox.addSelectionListener(new SearchConditionListener(
				regExpressionCheckbox));
		regExpressionCheckbox.setSelection(Config.bean.isDefaultRegularExpressionSearch());
	}

	private void createWholeWordCheckbox(Composite configArea, GridDataFactory gdf) {
		this.wholeWordCheckbox = new Button(configArea, SWT.CHECK);
		wholeWordCheckbox.setText(Resource.getString("whole.word"));
		gdf.copy().span(1, 1).applyTo(wholeWordCheckbox);
		wholeWordCheckbox.addSelectionListener(new SearchConditionListener(wholeWordCheckbox));
		wholeWordCheckbox.setSelection(Config.bean.isDefaultWholeWordSearch());
	}

	private void createCaseSensitiveCheckbox(Composite configArea, GridDataFactory gdf) {
		caseSensitiveCheckbox = new Button(configArea, SWT.CHECK);
		caseSensitiveCheckbox.setText(Resource.getString("case.sensitive"));
		gdf.copy().span(1, 1).applyTo(caseSensitiveCheckbox);
		caseSensitiveCheckbox.addSelectionListener(new SearchConditionListener(
				caseSensitiveCheckbox));
		caseSensitiveCheckbox.setSelection(Config.bean.isDefaultCaseSensitiveSearch());
	}

	private void addDownArrowButton(ToolBar toolBar) {
		downArrowButton = new ToolItem (toolBar, SWT.PUSH);
		try {
			downArrowButton.setImage(new Image(this.getDisplay(), Resource.getResourceURL(
					Resource.SEARCH_FORWARD_IMAGE_PATH).openStream()));
		} catch (Exception e){
			MyLogger.debug("Error:", e);
			downArrowButton.setText(Resource.getString("next"));
		}
		downArrowButton.setToolTipText(Resource.getString("find.next"));
		this.downArrowButton.addSelectionListener(new SearchNextAdapter());
	}

	private void addUpArrowButton(ToolBar toolBar) {
		this.upArrowButton = new ToolItem (toolBar, SWT.PUSH);
		try {
			this.upArrowButton.setImage(new Image(this.getDisplay(), Resource.getResourceURL(
				Resource.SEARCH_BACKWARD_IMAGE_PATH).openStream()));
		} catch (IOException e){
			MyLogger.debug("Error:", e);
			this.upArrowButton.setText(Resource.getString("previous"));
		}
		this.upArrowButton.setToolTipText(Resource.getString("find.previous"));
		this.upArrowButton.addSelectionListener(new SearchPreviousAdapter());
	}

	private ToolBar createToolBar(Composite configArea) {
		ToolBar toolBar = new ToolBar (configArea, SWT.FLAT);
		Rectangle clientArea = configArea.getClientArea ();
		toolBar.setLocation (clientArea.x, clientArea.y);
		return toolBar;
	}

	private void createSearchDropdownList(Composite configArea, GridDataFactory gdf) {
		searchCombo = new Combo(configArea, SWT.DROP_DOWN);
		searchCombo.setVisibleItemCount(1000);
		gdf.copy().span(1, 1).hint(200, SWT.DEFAULT).applyTo(searchCombo);
		searchCombo.addKeyListener(new SearchComboKeyListener(searchCombo));
		searchCombo.addFocusListener(new SearchComboFocusListener(searchCombo));
		searchCombo.addSelectionListener(new SearchComboSelectionAdapter());
		DEFAULT_BG_COLOR = searchCombo.getBackground();
	}

	

	private void createTailPart(Composite configArea, GridDataFactory gdf) {
		tailLabel = createLabelOnBottomBar(configArea, gdf, Resource.getString("tail.running"));
		ToolBar tailToolBar = createToolBar(configArea);		
		addResumeTailButton(tailToolBar);		
		addPauseTailButton(tailToolBar);
	}
	
	private void createUseFiltersPart(Composite composite, GridDataFactory gdf) {
		createUserFiltersButton(composite, gdf);		
		
	}

	private void createUserFiltersButton(Composite composite, GridDataFactory gdf) {
		Button useFilterButton = new Button(composite, SWT.PUSH);
		useFilterButton.setText(Resource.getString("use.filters"));
		gdf.copy().span(1, 1).applyTo(useFilterButton);
		
		useFilterButton.addSelectionListener(new UserFiltersAdapter());
	}



	private void addPauseTailButton(ToolBar tailToolBar) {
		pauseTailing = new ToolItem (tailToolBar, SWT.PUSH);
		try {
			pauseTailing.setImage(new Image(this.getDisplay(), Resource.getResourceURL(
					Resource.PAUSE_TAIL_IMAGE_PATH).openStream()));
		} catch (Exception e){
			MyLogger.debug("Error:", e);
			pauseTailing.setText(Resource.getString("pause"));
		}
		pauseTailing.setToolTipText(Resource.getString("pause.tail"));
		pauseTailing.addSelectionListener(new PauseTailAdapter());
	}

	private void addResumeTailButton(ToolBar tailToolBar) {
		resumeTailing = new ToolItem (tailToolBar, SWT.PUSH);
		try {
			resumeTailing.setImage(new Image(this.getDisplay(), Resource.getResourceURL(
				Resource.RESUME_TAIL_IMAGE_PATH).openStream()));
		} catch (Exception e){
			MyLogger.debug("Error:", e);
			resumeTailing.setText(Resource.getString("resume"));
		}
		resumeTailing.setToolTipText(Resource.getString("resume.tail"));
		resumeTailing.addSelectionListener(new ResumeTailAdapter());
	}

	private Label createLabelOnBottomBar(Composite parent, GridDataFactory gdf, String labelText) {
		Label label = new Label(parent, SWT.CENTER);
		label.setText(labelText);
		gdf.copy().align(SWT.FILL, SWT.CENTER).span(1, 1).applyTo(label);
		return label;
	}

	/*
	 * getter, setter
	 */
	public MainWindow getMainWindow() {
		return this.mainWindow;
	}

	public File getFile() {
		return this.file;
	}

	public TailUpdater getTailUpdater() {
		return this.tailUpdater;
	}

	public TextViewer getTextViewer() {
		return this.tailPane.getTextViewer();
	}

	public SearchHandler getSearchHandler() {
		return this.tailPane.getSearchHandler();
	}

	// other methods

	public void redrawViewArea(ViewContent viewContent, DisplayTriggerAction triggerAction) {
		tailPane.redrawViewArea(viewContent, triggerAction);		
	}
	
	public void refreshTail(){
		this.tailUpdater.fileChanged();
	}

	public boolean isTailRunning() {
		return this.tailUpdater.isTailRunning();
	}
	
	public boolean isTailPaused() {
		return !isTailRunning();
	}

	/**
	 * Stop current tailing if selected is true. Resume current tailing if selected is false.
	 * 
	 * @param selected
	 *            true if stop tail, otherwise false
	 */
	public void checkStopTailButton(boolean selected) {
		if (selected) {
			this.pauseTail();
		} else {
			this.resumeTail();
		}	
	}
		
	public void reflectSearchResultToUI(boolean found) {		
		if (found) {
//			if (!this.searchCombo.getBackground().equals(DEFAULT_BG_COLOR)) {
//				this.searchCombo.setBackground(DEFAULT_BG_COLOR);
//			}			
			MyLogger.debug("MATCH");
		} else {
			// clear selection.
			this.getTextViewer().setSelection(TextSelection.emptySelection());

			/*
			 * This is necessary because the code right above has a bug that set selection for first
			 * character of document. This code clear that selection.
			 */
			this.getTextViewer().getTextWidget().setCaretOffset(-1);
//			if (!this.searchCombo.getBackground().equals(NO_FOUND_BG_COLOR)) {
//				this.searchCombo.setBackground(NO_FOUND_BG_COLOR);				
//			}
			
			DialogUtil.displaySearchNotFoundDialog(this.getShell());
			MyLogger.debug("NO MATCH");
		}
	}


	public void search(String stringToFind, boolean isForwardSearch) {
		if (stringToFind == null || stringToFind.equals("")){
			return;
		}
		this.searchCondition.setForwardSearch(isForwardSearch);
		CursorHandler ch = new CursorHandler();
		ch.startWaitCursor();	
		try {
			getSearchHandler().search(new SearchWord(this.searchCondition, stringToFind));
		} catch (Throwable t){
			MyLogger.error("Throwable in TailTab.search()", t);
		} finally {
			ch.stopWaitCursor();	
		}
		
		sortWordsInComboBox(stringToFind);
	}

	private void sortWordsInComboBox(String searchText) {
		SortedSet<String> searchWordSet = Config.bean.getSearchWordSet();
		searchWordSet.add(searchText);
	}
	
	public void resumeTail() {
		if (!this.tailUpdater.isTailRunning()) {
			this.tailUpdater.startTail();
			this.refreshTail();			
		}
	}
	
	public void pauseTail() {
		if (this.tailUpdater.isTailRunning()) {
			this.tailUpdater.cancelTail();			
		}
	}
	
	class SearchComboFocusListener implements FocusListener {
		private Combo comboWidget;

		public SearchComboFocusListener(Combo widget) {
			this.comboWidget = widget;
		}

		public void focusGained(FocusEvent e) {
			SortedSet<String> searchWordSet = Config.bean.getSearchWordSet();
			String[] items = searchWordSet.toArray(new String[] {});
			this.comboWidget.setItems(items);
		}

		public void focusLost(FocusEvent e) {
		}

	}

	class SearchComboKeyListener implements KeyListener {
		private Combo comboWidget;

		public SearchComboKeyListener(Combo widget) {
			this.comboWidget = widget;
		}

		public void keyPressed(KeyEvent e) {
			// System.out.println("e.character:"+e.character+",e.stateMask:"+e.stateMask);
			// MyLogger.log("e.keyCode:"+e.keyCode+",e.stateMask:"+e.stateMask);
			// if (e.keyCode != (int) SWT.CR) { //(e.character != '\r') {
			if (e.character != '\r') {
				return;
			}
			boolean forwardSearch = false; // default is backward search
			if (e.stateMask == SWT.CONTROL) { // CTRL+Enter does forward search
				forwardSearch = true;
			}
			search(this.comboWidget.getText(), forwardSearch);			
		}

		public Combo getComboWidget() {
			return comboWidget;
		}

		public void keyReleased(KeyEvent e) {

		}
	}
	
	class SearchComboSelectionAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {			 
			Combo combo = (Combo)e.getSource();
			int selectedIndex = combo.getSelectionIndex();
			if (selectedIndex != -1){
				combo.traverse(SWT.TRAVERSE_TAB_NEXT);	
			}			
		}	
	}
	
	class SearchPreviousAdapter extends SelectionAdapter {		
		@Override
		public void widgetSelected(SelectionEvent e) {
			search(getSearchText(), false);
		}	
	}
	
	class SearchNextAdapter extends SelectionAdapter {		
		@Override
		public void widgetSelected(SelectionEvent e) {
			search(getSearchText(), true);
		}	
	}

	class ResumeTailAdapter extends SelectionAdapter {		
		@Override
		public void widgetSelected(SelectionEvent e) {
			resumeTail();
		}	
	}
	
	class PauseTailAdapter extends SelectionAdapter {		
		@Override
		public void widgetSelected(SelectionEvent e) {
			pauseTail();
		}	
	}
	
	class UserFiltersAdapter extends SelectionAdapter {		
		@Override
		public void widgetSelected(SelectionEvent e) {
			openUseFiltersDialog();
		}		
	}
	
	private void openUseFiltersDialog() {
		UseFiltersDialog dialog = new UseFiltersDialog(this.getShell(), this);
		dialog.open();
	}	
	
	class SearchConditionListener extends SelectionAdapter {
		private Button checkButton;

		public SearchConditionListener(Button checkButton) {
			this.checkButton = checkButton;
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			if (this.checkButton == TailTab.this.caseSensitiveCheckbox) {
				TailTab.this.searchCondition.setCaseSensitiveSearch(this.checkButton.getSelection());
			} else if (this.checkButton == TailTab.this.wholeWordCheckbox) {
				TailTab.this.searchCondition.setWholeWordSearch(this.checkButton.getSelection());
				/*
				 * whole word condition and regular expression condition can't be set both at the
				 * same time.
				 */
				if (this.checkButton.getSelection()
						&& TailTab.this.regExpressionCheckbox.getSelection()) {
					TailTab.this.regExpressionCheckbox.setSelection(false);
					TailTab.this.searchCondition.setRegularExpressionSearch(false);
				}
			} else if (this.checkButton == TailTab.this.regExpressionCheckbox) {
				TailTab.this.searchCondition.setRegularExpressionSearch(this.checkButton.getSelection());
				if (this.checkButton.getSelection()
						&& TailTab.this.wholeWordCheckbox.getSelection()) {
					TailTab.this.wholeWordCheckbox.setSelection(false);
					TailTab.this.searchCondition.setWholeWordSearch(false);
				}
			}
		}
	}

	class SearchDirectionMouseListener extends MouseAdapter {
		@Override
		public void mouseDown(MouseEvent e) {
			if (e.getSource() == TailTab.this.upArrowButton) {
				search(getSearchText(), false);
			} else if (e.getSource() == TailTab.this.downArrowButton) {
				search(getSearchText(), true);
			}
		}
	}

	public void clearViewport() {
		// int eofOffset = this.getTextViewer().getDocument().getLength();
		int eofOffset = getFileLength();
		setDisplayStartOffset(eofOffset);
		this.setDisplayStartOffsetSet(true);
	}

	public void reloadDocument() {
		setDisplayStartOffset(0);
		this.setDisplayStartOffsetSet(true);
	}

	// public void updateDisplayStartOffset(int newDisplayStartOffset){
	// this.prevDisplayStartOffset = this.displayStartOffset;
	// this.displayStartOffset = newDisplayStartOffset;
	// }
	//

	public int getDisplayStartOffset() {
		return this.displayStartOffset;
	}

	public void setDisplayStartOffset(int newValue) {
		this.displayStartOffset = newValue;
		// this.setDisplayStartOffsetSet(true);
	}

	public boolean displayStartOffsetChanged() {
		return this.displayStartOffsetChanged;
	}

	public void setDisplayStartOffsetSet(boolean b) {
		this.displayStartOffsetChanged = b;
	}

	public int getFileLength() {
		return (int) this.file.length();
	}
	
	/**
	 * This method is called to stop search to prevent BadLocationException
	 * if display area was cleared.
	 * @return true if displayed area was cleared, otherwise false.
	 */
	public boolean isSearchAreaCleared(){
		return this.getDisplayStartOffset() >= this.getFileLength();
	}

//	private String getFileContent() {
//		String fileContent = "";
//		try {
//			fileContent = FileUtil.readFile(file);
//		} catch (IOException e) {
//			DialogUtil.displayErrorDialog(e);
//			return "";
//		}
//
//		return fileContent;
//	}
	
		
	/**
	 * Return file content starting from startoffset. If file length is less than startoffset, all
	 * file content is returned.
	 * 
	 * @param startOffset
	 *            offset from file is read.
	 * @return
	 */
//	public String getFileContent(long startOffset) {
//		String fileContent = getFileContent();
//		String displayContent = "";
//		int fileSize = fileContent.length();
//		if (startOffset >= 0) {
//			if (fileSize >= startOffset) {
//				displayContent = fileContent.substring((int) startOffset);
//			} else {
//				/*
//				 * If file is cleared and rewritten from the scratch, file size is less than
//				 * start offset.
//				 */
//				displayContent = fileContent.substring(fileSize - 1000);
//			}
//		} else {
//			// if start offset is negative then display all file.
//			displayContent = fileContent.substring(fileSize - 1000);
//		}
//
//		return displayContent;
//	}

	/**
	 * This method does the clean up work when tab is closed as follows.
	 * 
	 * 1. shutdown tail updater thread. 2. mark this tail tab is closed.
	 */
	public void close() {
		getTailUpdater().shutdown();
		this.dispose();
	}

	public AutomaticTailStopCancelHandler getSearchMonitor() {
		if (this.searchMonitor == null) {
			this.searchMonitor = new AutomaticTailStopCancelHandler(this);
		}
		return this.searchMonitor;
	}

	public Combo getSearchCombo() {
		return this.searchCombo;
	}

	class TailTabDisposeListener implements DisposeListener {
		public void widgetDisposed(DisposeEvent e) {
			getSearchMonitor().stopThread();
			getTailUpdater().shutdown();
		}
	}

	public String getSearchText() {
		return this.searchCombo.getText();
	}

	public void setViewPortChangeHandler(ViewPortChangeHandler viewPortChangeHandler) {
		this.viewPortChangeHandler = viewPortChangeHandler;
	}
	
	public ViewPortChangeHandler getViewPortChangeHandler() {
		return this.viewPortChangeHandler;
	}
		
	public FileBlockHandler getFileBlockHandler() {
		return fileBlockHandler;
	}

	public List<Filter> getFilters() {
		return FilterHandler.getSavedFilterList(file);
	}

	public void enablePauseDisableResumeButton() {
		this.tailLabel.setForeground(TAIL_RUNNING_LABEL_COLOR);
		this.tailLabel.setText(Resource.getString("tail.running"));		
		
		this.resumeTailing.setEnabled(false);
		this.pauseTailing.setEnabled(true);			
		
		this.getMainWindow().getResumeTailMenuItem().setEnabled(false);
		this.getMainWindow().getPauseTailMenuItem().setEnabled(true);
	}

	public void enableResumeDisablePauseButton() {
		this.tailLabel.setForeground(TAIL_PAUSED_LABEL_COLOR);
		this.tailLabel.setText(Resource.getString("tail.paused"));
				
		this.resumeTailing.setEnabled(true);
		this.pauseTailing.setEnabled(false);
		
		this.getMainWindow().getResumeTailMenuItem().setEnabled(true);
		this.getMainWindow().getPauseTailMenuItem().setEnabled(false);
	}

	public TextViewerPane getTailPane() {
		return tailPane;
	}


	public List<Filter> getFilterList() {
		return filterList;
	}


	public void setFilterList(List<Filter> filterList) {
		this.filterList = filterList;
	}	
	
	public boolean isFilterUsed(){
		return !Value.isEmpty(this.getFilterList());
	}
}
