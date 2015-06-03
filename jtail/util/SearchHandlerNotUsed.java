package jtail.util;

import jtail.log.MyLogger;
import jtail.ui.tab.TailTab;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextViewer;

public final class SearchHandlerNotUsed implements IDocumentListener {
	private final TextViewer text;
	private int startOffset = 0;
	private boolean forwardSearch = false;
	private final FindReplaceDocumentAdapter frdAdapter;

	// keep track of search word.
	private SearchWord prevSearchWord = null;

	// to keep track of search direction
	private boolean prevForwardSearch = false;
	private int prevMatchLength = 0;

	public SearchHandlerNotUsed(TextViewer text) {
		this.text = text;
		this.frdAdapter = new FindReplaceDocumentAdapter(text.getDocument());
	}

	private void setDefaultStartOffset() {
		if (this.forwardSearch)
			setMinimumStartOffset();
		else
			setMaximumStartOffset();
	}

	private void setMinimumStartOffset() {
		this.startOffset = 0;
	}

	private void setMaximumStartOffset() {
		this.startOffset = text.getDocument().getLength() - 1;
	}

	/**
	 * Execute the search for the current tailing document and perform UI change needed.
	 * 
	 * @param tailTab
	 *            TailTab where this search was initiated.
	 */
	public void search(TailTab tailTab, boolean forwardSearch, SearchWord currentSearchWord) {
		if (tailTab.isTailRunning()) {
			tailTab.pauseTail();
		}

		this.setForwardSearch(forwardSearch);

		if (!currentSearchWord.equals(prevSearchWord)) {
			// if search word was changed, set startoffset to the end of file.
			this.searchWordChanged();
		} else {
			// if search word is same, check if search direction was changed or not.
			if (this.prevForwardSearch != this.forwardSearch) {
				if (this.forwardSearch) {
					// from backward to forward
					this.setStartOffset(this.startOffset + this.prevMatchLength);
				} else {
					// from forward to backward
					this.setStartOffset(this.startOffset - this.prevMatchLength);
				}
			}
		}

		IRegion region = null;
		CursorHandler ch = new CursorHandler();
		try {
			// MyLogger.log("search start");
			ch.startWaitCursor();
			region = frdAdapter.find(startOffset, currentSearchWord.searchString, forwardSearch,
					currentSearchWord.caseSensitive, currentSearchWord.wholeWord,
					currentSearchWord.regExSearch);
		} catch (BadLocationException ex) {
			throw new RuntimeException(ex);
		} finally {
			ch.stopWaitCursor();
			// MyLogger.log("search end");
		}

		if (region == null) { // no match
			if (currentSearchWord.equals(prevSearchWord) && this.prevMatchLength > 0) {
				/*
				 * If there was previous match for the same search word and now it doesn't find the
				 * match, start search from either start of the file (forward search) or end of the
				 * file (backward search) according to search direction. This enables circular
				 * search supported by most of editors such as UltraEdit.
				 */
				setDefaultStartOffset();
				search(tailTab, this.forwardSearch, currentSearchWord);
				return;
			} else {
				MyLogger.debug("NO MATCH");
				tailTab.reflectSearchResultToUI(false);
				return;
			}
		} else {
			MyLogger.debug("MATCH");
			tailTab.reflectSearchResultToUI(true);
		}

		// if search succeeded, display the match.
		int offset = region.getOffset();
		int length = region.getLength();

		// highlight matched region.
		text.setSelectedRange(offset, length);

		// copy the match.
		text.getTextWidget().copy();

		// text.setTopIndex(offset);
		int visibleTopLineIndex = text.getTopIndex();
		int visibleBottomLineIndex = text.getBottomIndex();
		int middleLineIndexFromTop = (visibleBottomLineIndex - visibleTopLineIndex) / 2;
		int matchLineIndex = getLineIndex(offset);

		// top line index after moving matched line to middle of viewport
		int newTopIndex = matchLineIndex - middleLineIndexFromTop;
		if (newTopIndex < 0) {
			newTopIndex = 0;
		}

		this.text.setTopIndex(newTopIndex);
		this.updateStartOffset(offset, length);

		// save current search word to be compared to next search word.
		this.prevSearchWord = currentSearchWord;

		// save current search direction to be compared to next search direction.
		this.prevForwardSearch = this.forwardSearch;
		this.prevMatchLength = length;
	}

	/**
	 * Clear previous search information and set startoffset to the end of file for the new search.
	 */
	private void searchWordChanged() {
		// clear previous search information.
		this.prevSearchWord = null;
		this.prevMatchLength = 0;
		this.prevForwardSearch = false;

		// if new search has begun, set startoffset according to search direction.
		this.setDefaultStartOffset();
	}

	/**
	 * This works if line wrapping is not enabled on TextViewer.
	 * 
	 * @param offset
	 *            offset in TextViewer's document
	 * @return line index at offset
	 */
	private int getLineIndex(int offset) {
		int lineIndexAtOffset = 0;
		try {
			lineIndexAtOffset = this.text.getDocument().getLineOfOffset(offset);
		} catch (BadLocationException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		return lineIndexAtOffset;
	}

	private void updateStartOffset(int offset, int length) {
		if (this.forwardSearch) {
			this.setStartOffset(offset + length);
		} else {
			this.setStartOffset(offset);
		}
	}

	// getter, setter

	/**
	 * Set startOffset. If new offset is out of legal range, adjust it.
	 * 
	 * @param offset
	 *            new offset
	 */
	public void setStartOffset(int offset) {
		if (offset < 0)
			offset = 0;
		else if (offset >= text.getDocument().getLength())
			offset = text.getDocument().getLength() - 1;
		this.startOffset = offset;
	}

	public void setForwardSearch(boolean forwardSearch) {
		this.forwardSearch = forwardSearch;
	}

	public void documentAboutToBeChanged(DocumentEvent event) {
		// TODO Auto-generated method stub

	}

	public void documentChanged(DocumentEvent event) {
		this.resetStartOffsetToEnd(event.getDocument());
	}

	public void resetStartOffsetToEnd(IDocument doc) {
		this.startOffset = doc.getLength() - 1;
	}

	public void setSearchWord(boolean caseSensitive, String findString, boolean regExSearch,
			boolean wholeWord) {

	}

	// innner classes
	class SearchWord {
		private String searchString = "";
		private boolean caseSensitive = false;
		private boolean wholeWord = false;
		private boolean regExSearch = true;

		public SearchWord(boolean caseSensitive, String findString, boolean regExSearch,
				boolean wholeWord) {
			this.caseSensitive = caseSensitive;
			this.searchString = findString;
			this.regExSearch = regExSearch;
			this.wholeWord = wholeWord;
		}

		public boolean equals(Object o) {
			if (o == this) {
				return true;
			}
			if (!(o instanceof SearchWord)) {
				return false;
			}
			SearchWord sc = (SearchWord) o;
			return (sc.caseSensitive == this.caseSensitive && sc.regExSearch == this.regExSearch
					&& sc.wholeWord == this.wholeWord && sc.searchString.equals(this.searchString));

		}

		public int hashCode() {
			int result = 17;
			result = 37 * result + Boolean.valueOf(this.caseSensitive).hashCode();
			result = 37 * result + Boolean.valueOf(this.regExSearch).hashCode();
			result = 37 * result + Boolean.valueOf(this.wholeWord).hashCode();
			result = 37 * result + this.searchString.hashCode();
			return result;
		}
	}
}
