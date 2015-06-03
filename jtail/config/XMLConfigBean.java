package jtail.config;

import java.awt.Rectangle;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import jtail.logic.filter.Filter;

public class XMLConfigBean {
	private static final int OPENED_FILE_MENU_SIZE_DEFAULT = 10;
	
	/*
	 * Now it is 1 year. If you want to enable auto-tail-resume, put the value you want.
	 */
	private static final int AUTO_TAIL_RESUME_SECONDS_DEFAULT = 60 * 60 * 24 * 365;
	
	public final static int DEFAULT_DISPLAY_BLOCK_SIZE_IN_KB = 50; // 50 KB 
	private final static int DEFAULT_MAXIMUM_DISPLAY_BLOCK_SIZE_IN_KB = 1024; // 1 MB
	
	/*
	 * if there is no filter, it reads one block and displays it.
	 * if there are filters associated with a file, it needs to read multiple blocks
	 * to display file content by display block size. but it takes too long if very big
	 * file (100MB) doesn't match with filters. in order to make application response
	 * within reasonable time, set this limit.
	 */
	public final static int DEFAULT_MAXIMUM_NUMBER_OF_BLOCKS_TO_BE_READ = 10;
	
	/*
	 * Do not read more than 10 MB from the file to tail or search.
	 */
	private final static int MAXIMUM_READ_SIZE_IN_KB = 1024 * 10; // 10 MB
	
	private boolean firstOpen = true;	
	
	/**
	 * Save the last position of window to open it same size and location next time. 
	 */
	private Rectangle lastPosition;
	
	private List<String> openedFilePathList;
	
	private int maximumOpenedFileMenuSize = OPENED_FILE_MENU_SIZE_DEFAULT;
	
	private int autoTailResumeSeconds = AUTO_TAIL_RESUME_SECONDS_DEFAULT;
	
	/**
	 * This keeps the words users entered for search.
	 * If configuration file doesn't contain it, it is empty by default.
	 */
	private SortedSet<String> searchWordSet = new TreeSet<String>();
	
	private SortedSet<Filter> filterSet = new TreeSet<Filter>();
	
	private Map<File, List<String>> usedFilterListMap = new HashMap<File, List<String>>();
	
	/*
	 * default search option.
	 */
	private boolean defaultCaseSensitiveSearch = false;	
	private boolean defaultRegularExpressionSearch = true;
	private boolean defaultWholeWordSearch = false;
	private boolean defaultForwardSearch = false;
	private boolean copyMatch = true;
	private boolean wordWrap = false;
	
	private int displayBlockSizeInKb = DEFAULT_DISPLAY_BLOCK_SIZE_IN_KB; 
	private int maximumNumberOfBlocksToBeRead = DEFAULT_MAXIMUM_NUMBER_OF_BLOCKS_TO_BE_READ;
	
	private static final int DEFAULT_SEARCH_VIEW_CONTENT_OVERLAP_PERCENTAGE = 10;
	
	/*
	 * If searching text is cut off in the middle by searchStartOffset then it misses the match
	 * even though there is a match. to prevent this case, if match is not found, move searchStartOffset 
	 * back to already searched text so match can be found. 
	 */
	private int searchViewContentOverlapPercentage = DEFAULT_SEARCH_VIEW_CONTENT_OVERLAP_PERCENTAGE;
	
	
	public XMLConfigBean(){}
		
	public synchronized boolean isWordWrap() {
		return wordWrap;
	}
	public synchronized void setWordWrap(boolean wordWrap) {
		this.wordWrap = wordWrap;
	}
	public synchronized boolean isCopyMatch() {
		return copyMatch;
	}
	public synchronized void setCopyMatch(boolean copyMatch) {
		this.copyMatch = copyMatch;
	}
		
	public synchronized  boolean isDefaultCaseSensitiveSearch() {
		return defaultCaseSensitiveSearch;
	}
	public synchronized void setDefaultCaseSensitiveSearch(boolean defaultCaseSensitiveSearch) {
		this.defaultCaseSensitiveSearch = defaultCaseSensitiveSearch;
	}
	public synchronized boolean isDefaultRegularExpressionSearch() {
		return defaultRegularExpressionSearch;
	}
	public synchronized void setDefaultRegularExpressionSearch(boolean defaultRegularExpressionSearch) {
		this.defaultRegularExpressionSearch = defaultRegularExpressionSearch;
	}
	public synchronized boolean isDefaultWholeWordSearch() {
		return defaultWholeWordSearch;
	}
	public synchronized void setDefaultWholeWordSearch(boolean defaultWholeWordSearch) {
		this.defaultWholeWordSearch = defaultWholeWordSearch;
	}
	public synchronized boolean isDefaultForwardSearch() {
		return defaultForwardSearch;
	}
	public synchronized void setDefaultForwardSearch(boolean defaultForwardSearch) {
		this.defaultForwardSearch = defaultForwardSearch;
	}
	
	public synchronized SortedSet<String> getSearchWordSet() {
		return searchWordSet;
	}
	public synchronized void setSearchWordSet(SortedSet<String> searchWordSet) {
		this.searchWordSet = searchWordSet;
	}
	public synchronized int getAutoTailResumeSeconds() {
		return (autoTailResumeSeconds <= 0 ? AUTO_TAIL_RESUME_SECONDS_DEFAULT : autoTailResumeSeconds);
	}
	public synchronized void setAutoTailResumeSeconds(int autoTailResumeSeconds) {
		this.autoTailResumeSeconds = autoTailResumeSeconds;
	}
	public synchronized List<String> getOpenedFilePathList() {
		return openedFilePathList;
	}
	public synchronized void setOpenedFilePathList(List<String> openedFilePathList) {
		this.openedFilePathList = openedFilePathList;
	}
		
	public synchronized int getMaximumOpenedFileMenuSize() {
		return (maximumOpenedFileMenuSize <= 0 ? OPENED_FILE_MENU_SIZE_DEFAULT : maximumOpenedFileMenuSize);
	}
	public synchronized void setMaximumOpenedFileMenuSize(int openedFileMenuSize) {
		this.maximumOpenedFileMenuSize = openedFileMenuSize;
	}
	
	public synchronized boolean isFirstOpen() {
		return firstOpen;
	}
	public synchronized void setFirstOpen(boolean isFirstOpen) {
		this.firstOpen = isFirstOpen;
	}
	public synchronized Rectangle getLastPosition() {
		return lastPosition;
	}
	public synchronized void setLastPosition(Rectangle lastPosition) {
		this.lastPosition = lastPosition;
	}

	public synchronized SortedSet<Filter> getFilterSet() {
		return filterSet;
	}

	public synchronized void setFilterSet(SortedSet<Filter> filterSet) {
		this.filterSet = filterSet;
	}

	public synchronized int getDisplayBlockSizeInKb() {
		return displayBlockSizeInKb;
	}

	public synchronized int setDisplayBlockSizeInKb(int displaySize) {
		if (displaySize <= 0){
			displaySize = DEFAULT_DISPLAY_BLOCK_SIZE_IN_KB;
		} else if (displaySize > DEFAULT_MAXIMUM_DISPLAY_BLOCK_SIZE_IN_KB){
			displaySize = DEFAULT_MAXIMUM_DISPLAY_BLOCK_SIZE_IN_KB;
		}
		
		this.displayBlockSizeInKb = displaySize;
		if (maxReadSizeExceedsLimit()){
			adjustMaximumNumberOfBlocksToBeRead();
		}
		return this.displayBlockSizeInKb;
	}

	public synchronized Map<File, List<String>> getUsedFilterListMap() {
		if (usedFilterListMap == null){
			usedFilterListMap = new HashMap<File, List<String>>();
		}
		return usedFilterListMap;
	}

	public synchronized void setUsedFilterListMap(Map<File, List<String>> usedFiltersForFile) {
		this.usedFilterListMap = usedFiltersForFile;
	}

	public synchronized int getMaximumNumberOfBlocksToBeRead() {
		return maximumNumberOfBlocksToBeRead;
	}

	public synchronized int setMaximumNumberOfBlocksToBeRead(int maximumNumberOfBlocksToBeRead) {
		if (maximumNumberOfBlocksToBeRead <= 0){
			maximumNumberOfBlocksToBeRead = DEFAULT_MAXIMUM_NUMBER_OF_BLOCKS_TO_BE_READ;
		}
		this.maximumNumberOfBlocksToBeRead = maximumNumberOfBlocksToBeRead;
		if (maxReadSizeExceedsLimit()){
			adjustDisplayBlockSizeInKb();
		}
		return this.maximumNumberOfBlocksToBeRead;
	}

	private boolean maxReadSizeExceedsLimit() {
		if (this.displayBlockSizeInKb * this.maximumNumberOfBlocksToBeRead > XMLConfigBean.MAXIMUM_READ_SIZE_IN_KB){
			return true;
		} else {
			return false;
		}
	}
	
	private void adjustMaximumNumberOfBlocksToBeRead() {
		int size = XMLConfigBean.MAXIMUM_READ_SIZE_IN_KB / this.displayBlockSizeInKb;
		this.setMaximumNumberOfBlocksToBeRead(size);
	}
	
	private void adjustDisplayBlockSizeInKb() {
		int size = XMLConfigBean.MAXIMUM_READ_SIZE_IN_KB / this.maximumNumberOfBlocksToBeRead;
		this.setDisplayBlockSizeInKb(size);
	}

	public synchronized int getSearchViewContentOverlapPercentage() {
		if (searchViewContentOverlapPercentage < 0){
			searchViewContentOverlapPercentage = 0;
		} else if (searchViewContentOverlapPercentage > 100){
			searchViewContentOverlapPercentage = DEFAULT_SEARCH_VIEW_CONTENT_OVERLAP_PERCENTAGE;
		}
		return searchViewContentOverlapPercentage;
	}

	public synchronized void setSearchViewContentOverlapPercentage(int searchViewContentOverlapPercentage) {
		this.searchViewContentOverlapPercentage = searchViewContentOverlapPercentage;
	}

	
}
