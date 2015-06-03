package jtail.logic.search;

import java.io.File;
import java.util.List;
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jtail.config.Config;
import jtail.log.MyLogger;
import jtail.logic.FileBlockHandler;
import jtail.logic.Range;
import jtail.logic.ViewContent;
import jtail.logic.ViewContentHandler;
import jtail.logic.filter.Filter;
import jtail.logic.filter.FilterHandler;
import jtail.resources.Resource;
import jtail.ui.tab.TailTab;
import jtail.ui.tab.pane.DisplayTriggerAction;
import jtail.util.Value;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;

public final class SearchHandler implements IDocumentListener {
	private static final int MINIMUM_START_OFFSET = 0;	
	private int searchStartOffset = MINIMUM_START_OFFSET;
	
	private SearchWord prevSearchWord = null;
	private SearchWord currentSearchWord = null;
	private int prevMatchLength = 0;
	private final File file;
	private final TailTab tailTab;
	private final ViewContentHandler viewContentHandler;
	private final FileBlockHandler fileBlockHandler;
	private static SortedSet<String> searchWordSet = Config.bean.getSearchWordSet();
	private boolean reachedFileStartOrEndDuringSearch = false;
	private int searchStartOffsetWhenSearchMethodIsCalled = 0;

	/*
	 * record the file length when search is started, theoretically file size
	 * can be decreased or increased (most cases) during the execution of search method.
	 * use this value while search is executed.
	 * TODO: if file gets smaller or even deleted, what is going to happen?
	 */
	private int fileLengthWhenSearchStarted = 0;

	public SearchHandler(TailTab tailTab) {
		this.tailTab = tailTab;
		this.file = this.tailTab.getFile();
		this.fileBlockHandler = new FileBlockHandler(this.file);
		this.viewContentHandler = new ViewContentHandler(tailTab);
		// this.frdAdapter = new FindReplaceDocumentAdapter(text.getDocument());
	}

	/*
	 * This method is called when user clicks on forward or backward 
	 * search arrow button.
	 */
	public void search(SearchWord searchWord) {		
		this.setFileLengthWhenSearchStarted(this.getFileLength());
		this.setCurrentSearchWord(searchWord);
		this.setReachedFileStartOrEndDuringSearch(false);
		
		boolean tailPaused = stopTail();
		if (tailPaused) {
			setSearcStartOffsetWhenTailPaused();
		}		
				
		// startAutomaticTailStopCancelHandler(tailTab);
		updateSearchCombo();
		adjustSearchStartOffsetWhenSearchDirectionChanged();
		syncSearchStartOffsetWhenSearchMethodIsCalledWithSearchStartOffset();
		
		ViewContent searchResult = null;
		do {
			ViewContent viewContent = getNextViewContent();
			searchResult = doSearch(viewContent);
			if (searchResult.isMatchFound()){
				break;
			} else {
				setReachedAtStartOrEndOfFileDuringSearch();
			}
			
			if (this.isReachedFileStartOrEndDuringSearch()) {
				if (searchStartedInMiddleOfFile()
						|| matchFoundForSamePreviousSearch()) {
					if (userWantsCircularSearch()) {
						resetSearchStartOffsetForCircularSearch();
						viewContent = getNextViewContent();
						searchResult = doSearch(viewContent);
						if (searchResult.isMatchFound()){
							break;
						} else {
							setReachedAtStartOrEndOfFileDuringSearch();
						}
					} else {
						break;
					}
				} else {
					break;
				}				
			}			
		} while (!allFileContentIsSearched(this.getFileLengthWhenSearchStarted()));
		
		if (searchResult.isMatchFound()) {
			if (viewContentSizeIsLessThanBlockSize(searchResult)){
				searchResult = makeViewContentSizeSameAsBlockSize(searchResult);
			}
			this.tailTab.redrawViewArea(searchResult, DisplayTriggerAction.SEARCH_EXECUTED);					
			tailTab.reflectSearchResultToUI(true);
		} else {
			if (!matchFoundForSamePreviousSearch()) {
				tailTab.reflectSearchResultToUI(false);	
			}
		}		
		
		updatePreviousSearchInfo(searchWord, searchResult);
	}
	
	private boolean fileSizeIsBiggerThanFileBlockSize(int fileLength) {
		return (FileBlockHandler.getBlockSize() < fileLength);
	}

	private ViewContent makeViewContentSizeSameAsBlockSize(ViewContent viewContent) {
		if (viewContent == null){
			return null;
		}
			
		int sizeDifference = 0;
		if (this.isForwardSearch()){
			ViewContent lastViewContent = this.getLastViewContent();
			sizeDifference = lastViewContent.getTextLength() - viewContent.getTextLength();
			if (sizeDifference > 0){
				if (viewContent.isMatchFound()){					
					Range matchRangeWithinText = viewContent.getMatchRangeWithinText();
					Range adjustedMatchRangeWithinText = Range.increaseRangeBy(matchRangeWithinText, sizeDifference);
					lastViewContent.setMatchRangeWithinText(adjustedMatchRangeWithinText);
					return lastViewContent;
				} else {
					return viewContent;
				}
			} else {
				return viewContent;
			}
		} else {
			ViewContent firstViewContent = this.getFirstViewContent();
			sizeDifference = firstViewContent.getTextLength() - viewContent.getTextLength();
			if (sizeDifference > 0){					
				if (viewContent.isMatchFound()){					
					Range matchRangeWithinText = viewContent.getMatchRangeWithinText();
					firstViewContent.setMatchRangeWithinText(matchRangeWithinText);
					return firstViewContent;
				} else {
					return viewContent;
				}
			} else {
				return viewContent;
			}
		}		
	}

	private boolean viewContentSizeIsLessThanBlockSize(ViewContent viewContent) {
		if (viewContent == null){
			return false;
		} else {		 
			return (viewContent.getText().length() < FileBlockHandler.getBlockSize());
		}
	}

	private boolean searchStartedInMiddleOfFile() {
		return (0 < this.getSearchStartOffsetWhenSearchMethodIsCalled() 
				&& this.getSearchStartOffsetWhenSearchMethodIsCalled() < this.getFileLengthWhenSearchStarted());
	}

	private boolean allFileContentIsSearched(int fileLength) {
		boolean allFileContentIsSearched = false;
		if (this.isForwardSearch()){
			if (this.getSearchStartOffsetWhenSearchMethodIsCalled() == 0){
				allFileContentIsSearched = (this.getSearchStartOffset() >= fileLength);
			} else {
				if (this.isReachedFileStartOrEndDuringSearch()){
					allFileContentIsSearched = this.getSearchStartOffsetWhenSearchMethodIsCalled() <= this.getSearchStartOffset();
				} else {
					allFileContentIsSearched = false;
				}
			}			
		} else {
			if (this.getSearchStartOffsetWhenSearchMethodIsCalled() == fileLength){
				allFileContentIsSearched = (this.getSearchStartOffset() <= 0);
			} else {
				if (this.isReachedFileStartOrEndDuringSearch()){
					allFileContentIsSearched = this.getSearchStartOffset() <= this.getSearchStartOffsetWhenSearchMethodIsCalled();
				} else {
					allFileContentIsSearched = false;
				}
			}
		}
		return allFileContentIsSearched;
	}

	private void resetSearchStartOffsetForCircularSearch() {
		if (this.isForwardSearch()){
			this.setSearchStartOffset(0);
		} else {
			this.setSearchStartOffset(this.getFileLengthWhenSearchStarted());
		}		
		
		this.setReachedFileStartOrEndDuringSearch(false);
	}
	
	private void setReachedAtStartOrEndOfFileDuringSearch() {
		if (this.isReachedFileStartOrEndDuringSearch()){
			return;
		}
		
		boolean reachedFileStartOrEndDuringSearch = false;
		if (this.isForwardSearch()){
			reachedFileStartOrEndDuringSearch 
				= (this.getSearchStartOffset() >= this.getFileLengthWhenSearchStarted());
		} else {
			reachedFileStartOrEndDuringSearch = (this.getSearchStartOffset() <= 0);
		}
		
		this.setReachedFileStartOrEndDuringSearch(reachedFileStartOrEndDuringSearch);		
	}
	
	private int getSearchStartOffset() {
		return this.searchStartOffset;
	}
		
	private ViewContent getFirstViewContent(){
		return this.viewContentHandler.getFirstViewContent();
	}
	
	private ViewContent getLastViewContent(){
		return this.viewContentHandler.getLastViewContent();
	}

	private ViewContent getNextViewContent() {
		ViewContent viewContent = null;		
		if (this.isForwardSearch()){
			viewContent = this.getViewContentHandler().getViewContentFrom(this.getSearchStartOffsetUsingOverlap());			
		} else {
			viewContent = this.getViewContentHandler().getViewContentTill(this.getSearchStartOffsetUsingOverlap());			
		}
		
		return viewContent;
	}
	
	private int getSearchStartOffsetUsingOverlap() {
		int nextSearchStartOffset = 0;
		int searchStartOffsetAdjustment = this.getSearchStartOffsetAdjustment();		
		if (this.hadReadViewContentButMatchNotFound()){			
			if (this.isForwardSearch()){			
				nextSearchStartOffset = this.getSearchStartOffset() - searchStartOffsetAdjustment;
				if (nextSearchStartOffset < 0){
					nextSearchStartOffset = 0;
				}
			} else {
				nextSearchStartOffset = this.getSearchStartOffset() + searchStartOffsetAdjustment;
				if (nextSearchStartOffset > this.getFileLengthWhenSearchStarted()){
					nextSearchStartOffset = this.getFileLengthWhenSearchStarted();
				}
			}
		} else {
			nextSearchStartOffset = this.getSearchStartOffset();
		}
		
		return nextSearchStartOffset;
	}
	
	private boolean hadReadViewContentButMatchNotFound(){
		boolean hadReadViewContentButMatchNotFound 
			= this.getSearchStartOffsetWhenSearchMethodIsCalled() != this.getSearchStartOffset();
		return hadReadViewContentButMatchNotFound;
	}	
	
	private int getSearchStartOffsetAdjustment() {
		double overlapPercentage = (double) Config.bean.getSearchViewContentOverlapPercentage();
		int searchStartOffsetAdjustment = (int) ((double) FileBlockHandler.getBlockSize() * (double) (overlapPercentage / 100d));	
		return searchStartOffsetAdjustment;
	}
	
	private ViewContent getViewContent(int offset) {
		ViewContent viewContent = null;
		if (this.isForwardSearch()){
			viewContent = this.getViewContentHandler().getViewContentFrom(offset);			
		} else {
			viewContent = this.getViewContentHandler().getViewContentTill(offset);			
		}
		
		return viewContent;
	}

	private ViewContent doSearch(ViewContent viewContent) {
		boolean found = false;
		int startIndex = 0;
		int endIndex = 0;
		
		if (this.getCurrentSearchWord().isRegularExpressionSearch()) { // it means it is not whole word
																	// search.
			int flags = Pattern.MULTILINE; // Pattern.DOTALL; //Pattern.MULTILINE;
			if (!this.getCurrentSearchWord().isCaseSensitive()) {
				flags = flags | Pattern.CASE_INSENSITIVE;
			}
			Pattern p = Pattern.compile(this.getCurrentSearchWord().getStringToFind(), flags);
			Matcher m = p.matcher(viewContent.getText());

			while (m.find()) {
				found = true;
				startIndex = m.start();
				endIndex = m.end();
				if (isForwardSearch()) {
					break;
				}
			}
		} else {
			startIndex = viewContent.getText().indexOf(getStringToFind());
			if (startIndex != -1) {
				endIndex = startIndex + getStringToFind().length();
				found = true;
			}
		}

		if (found) {
			Range matchRangeWithinText = new Range(startIndex, endIndex - startIndex);
			viewContent.setMatchRangeWithinText(matchRangeWithinText);
		}
		
		setNextSearchStartOffset(viewContent);
		
		return viewContent;
	}
	
	private void setNextSearchStartOffset(ViewContent viewContent) {
		if (viewContent == null){
			return;
		}
		
		if (viewContent.isMatchFound()){			
			if (this.isForwardSearch()){
				this.setSearchStartOffset(viewContent.getStartIndexWithinFile()
						+ viewContent.getMatchEndIndexWithinText());
			} else {
				this.setSearchStartOffset(viewContent.getStartIndexWithinFile()
						+ viewContent.getMatchStartIndexWithinText());
			}
		} else {	
			if (this.isForwardSearch()){
				this.setSearchStartOffset(this.getSearchStartOffset() + viewContent.getLengthWithinFile());	
			} else {
				this.setSearchStartOffset(this.getSearchStartOffset() - viewContent.getLengthWithinFile());
			}
		}
	}

	private String getStringToFind() {
		return this.currentSearchWord.getStringToFind();
	}

	private boolean isForwardSearch() {
		return this.currentSearchWord.isForwardSearch();
	}
	
	private boolean isFilterUsed() {
		return (this.tailTab.isFilterUsed());
	}
	
	private boolean userWantsCircularSearch() {
		return MessageDialog.openConfirm(this.tailTab.getShell(),
				Resource.getString("jtail"), Resource.getString("continue.search.question"));
	}

	private String applyFilters(String source) {
		List<Filter> filterList = this.tailTab.getFilterList();
		if (Value.isEmpty(filterList)) {
			return source;
		}

		for (Filter filter : filterList) {
			if (filter == null) {
				continue;
			}
			source = FilterHandler.applyFilter(source, filter);
		}
		return source;
	}
	
	private void updatePreviousSearchInfo(SearchWord searchWord, ViewContent searchResult) {
		// save current search word to be compared to next search word.
		this.prevSearchWord = new SearchWord(searchWord);
		if (searchResult.isMatchFound()){
			this.setPrevMatchLength(searchResult.getMatchRangeWithinText().getLength());
		}		
	}
	
	private boolean matchFoundForSamePreviousSearch() {
		if (this.getPrevSearchWord() == null || this.getCurrentSearchWord() == null){
			return false;
		} else {
			if (this.getPrevSearchWord().equalsExceptSearchDirection(this.getCurrentSearchWord())){
				return (this.getPrevMatchLength() > 0);
			}
		}
		return false;
	}

	private void setSearcStartOffsetWhenTailPaused() {
		if (isForwardSearch()) {
			setMinimumStartOffset();
			// this.getViewPortChangeHandler().displayFirstBlock();
		} else {
			this.setSearchStartOffset(this.getFileLengthWhenSearchStarted());
			// this.getViewPortChangeHandler().displayLastBlock();
		}		
	}

	private void setMinimumStartOffset() {
		this.setSearchStartOffset(MINIMUM_START_OFFSET);
	}

	private void setMaximumStartOffset() {
		// this.setStartOffset(getDocumentLength() - 1);
		this.setSearchStartOffset(this.getFileLength());
	}

	// private int getDocumentLength() {
	// return textViewer.getDocument().getLength();
	// }

	private void updateSearchCombo() {
		if (!searchWordSet.contains(this.currentSearchWord.getStringToFind())) {
			searchWordSet.add(this.currentSearchWord.getStringToFind());
		}
	}

	private boolean stopTail() {
		// stop current tail if it was not stopped already for search.
		if (tailTab.isTailRunning()) {
			// tailTab.checkStopTailButton(true);
			tailTab.pauseTail();
			return true;
		}
		return false;
	}

	private void adjustSearchStartOffsetWhenSearchDirectionChanged() {
		if (this.getPrevSearchWord() == null || this.getCurrentSearchWord() == null){
			return;
		}
		
		if (this.getPrevSearchWord().isForwardSearch() != this.getCurrentSearchWord().isForwardSearch()) {
			if (this.getCurrentSearchWord().isForwardSearch()) {
				// from backward to forward
				this.setSearchStartOffset(this.getSearchStartOffset() + this.getPrevMatchLength());
			} else {
				// from forward to backward
				this.setSearchStartOffset(this.getSearchStartOffset() - this.getPrevMatchLength());
			}			
		}		
	}

	private void startAutomaticTailStopCancelHandler(TailTab tailTab) {
		/*
		 * Resume search monitor if it is alive in case it was suspended. Start the search monitor
		 * if it died.
		 */
		AutomaticTailStopCancelHandler searchMonitor = tailTab.getSearchMonitor();
		searchMonitor.updateSearchTime();
		if (searchMonitor.isAlive()) {
			if (searchMonitor.isThreadSuspended()) {
				searchMonitor.setThreadSuspended(false);
				MyLogger.debug("searchMonitor was resumed.");
			}
		} else {
			searchMonitor.start();
			MyLogger.debug("searchMonitor is not alive so started it.");
		}
	}

	/**
	 * Clear previous search information and set startoffset to the end of file for the new search.
	 */
//	private void searchWordChanged(boolean isForwardSearch) {
//		MyLogger.debug("searchWordChanged(), isForwardSearch:" + isForwardSearch);
//		// clear previous search information.
//		this.prevSearchWord = null;
//		this.prevMatchLength = 0;
//
//		// if new search has begun, set startoffset according to search direction.
//		this.setDefaultStartOffset(isForwardSearch);
//	}

	// getter, setter

	/**
	 * Set startOffset. If new offset is out of legal range, adjust it.
	 * 
	 * @param offset
	 *            new offset
	 */
	private void setSearchStartOffset(int offset) {
		if (offset < 0) {
			offset = 0;
		} else if (offset > getFileLength()) {
			offset = getFileLength();
		}
		this.searchStartOffset = offset;		
		MyLogger.debug("setSearchStartOffset():" + this.searchStartOffset);
	}

	public void documentAboutToBeChanged(DocumentEvent event) {

	}

	public void documentChanged(DocumentEvent event) {
		// this.resetStartOffsetToEnd(event.getDocument());
	}

	// public void resetStartOffsetToEnd(IDocument doc) {
	// this.setStartOffset(doc.getLength() - 1);
	// }

	private TailTab getTailTab() {
		return tailTab;
	}

	private File getFile() {
		return this.file;
	}

	private int getFileLength() {
		return (int) this.file.length();
	}

	private FileBlockHandler getFileBlockHandler() {
		return this.fileBlockHandler;
	}
	
	private int getSearchStartOffsetWhenSearchMethodIsCalled(){
		return this.searchStartOffsetWhenSearchMethodIsCalled;
	}
	
	private void setSearchStartOffsetWhenSearchMethodIsCalled(int offset){
		this.searchStartOffsetWhenSearchMethodIsCalled = offset;
	}
	
	private void syncSearchStartOffsetWhenSearchMethodIsCalledWithSearchStartOffset() {
		setSearchStartOffsetWhenSearchMethodIsCalled(getSearchStartOffset());
	}

	private boolean isReachedFileStartOrEndDuringSearch() {
		return reachedFileStartOrEndDuringSearch;
	}

	private void setReachedFileStartOrEndDuringSearch(boolean reachedFileStartOrEndDuringSearch) {
		this.reachedFileStartOrEndDuringSearch = reachedFileStartOrEndDuringSearch;
	}

	private SearchWord getPrevSearchWord() {
		return prevSearchWord;
	}

	private void setPrevSearchWord(SearchWord prevSearchWord) {
		this.prevSearchWord = prevSearchWord;
	}
	
	public void clearPrevSearchWord(){
		this.prevSearchWord = null;
	}
		
	private SearchWord getCurrentSearchWord() {
		return currentSearchWord;
	}

	private void setCurrentSearchWord(SearchWord currentSearchWord) {
		this.currentSearchWord = currentSearchWord;
	}

	private ViewContentHandler getViewContentHandler() {
		return viewContentHandler;
	}

	private int getPrevMatchLength() {
		return prevMatchLength;
	}

	private void setPrevMatchLength(int prevMatchLength) {
		this.prevMatchLength = prevMatchLength;
	}

	private static SortedSet<String> getSearchWordSet() {
		return searchWordSet;
	}

	private static void setSearchWordSet(SortedSet<String> searchWordSet) {
		SearchHandler.searchWordSet = searchWordSet;
	}

	private int getFileLengthWhenSearchStarted() {
		return fileLengthWhenSearchStarted;
	}

	public void setFileLengthWhenSearchStarted(int fileLengthWhenSearchStarted) {
		this.fileLengthWhenSearchStarted = fileLengthWhenSearchStarted;
	}
}
