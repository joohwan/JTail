package jtail.ui.event;

import jtail.logic.FileBlockHandler;
import jtail.logic.ViewContentHandler;
import jtail.ui.tab.TailTab;
import jtail.ui.tab.pane.DisplayTriggerAction;

import org.eclipse.jface.text.IViewportListener;
import org.eclipse.jface.text.TextViewer;

public class ViewPortChangeHandler implements IViewportListener {
	private int subDocStartOffsetInFile = 0;
	private int subDocEndOffsetInFile = 0;
	private int textViewerBottomIndex = 0;
	private final TextViewer textViewer;
	private final TailTab tailTab;
	private final ViewContentHandler viewContentHandler;

	public ViewPortChangeHandler(TextViewer textViewer, TailTab tailTab) {
		this.textViewer = textViewer;
		this.tailTab = tailTab;
		this.viewContentHandler = new ViewContentHandler(tailTab);
	}

	private FileBlockHandler getFileBlockHandler() {
		return this.tailTab.getFileBlockHandler();
	}

	public void viewportChanged(int verticalOffset) {
//		MyLogger.log("viewport changed, verticalOffset:" + verticalOffset + ", lastLineIndex:"
//				+ textViewerBottomIndex + ", textViewer.getBottomIndex():"
//				+ textViewer.getBottomIndex());

//		if (verticalOffset == 0) {
//			if (previousBlockExist()) {
//				displayPreviousBlock();
//				MyLogger.log("previous block was displayed");
//			}
//		} else if (this.textViewer.getBottomIndex() == this.textViewerBottomIndex) {
//			if (nextBlockExist()) {
//				displayNextBlock();
//				MyLogger.log("next block was displayed");
//			}
//		}
	}
	
	public void displayFirstBlock() {
		this.tailTab.redrawViewArea(this.viewContentHandler.getFirstViewContent(),
				DisplayTriggerAction.SCROLL_UP);
	}

	public void displayLastBlock() {
		this.tailTab.redrawViewArea(this.viewContentHandler.getLastViewContent(),
				DisplayTriggerAction.SCROLL_DOWN);
	}
	
	public void displayPreviousBlock() {
		this.tailTab.redrawViewArea(
				this.viewContentHandler.getViewContentTill(subDocStartOffsetInFile),
				DisplayTriggerAction.SCROLL_UP);
	}

	public void displayNextBlock() {
		this.tailTab.redrawViewArea(
				this.viewContentHandler.getViewContentFrom(subDocEndOffsetInFile),
				DisplayTriggerAction.SCROLL_DOWN);
	}

	public boolean nextBlockExist() {
		return (this.subDocEndOffsetInFile < tailTab.getFileLength());
	}

	public void displayNextBlockIfExists() {
		if (this.nextBlockExist()) {
			this.displayNextBlock();
		}
	}

	public void displayPreviousBlockIfExists() {
		if (this.previousBlockExist()) {
			this.displayPreviousBlock();
		}
	}

	public TextViewer getTextViewer() {
		return textViewer;
	}	

	public boolean previousBlockExist() {
		return (this.tailTab.getDisplayStartOffset() < this.subDocStartOffsetInFile);
	}

	public void textViewerUpdated(int startOffset, int readSize) {
		this.subDocStartOffsetInFile = startOffset;
		this.subDocEndOffsetInFile = startOffset + readSize;
		this.textViewerBottomIndex = this.textViewer.getBottomIndex();
		// MyLogger.log("textViewerUpdated: subDocStartOffsetInFile=" +
		// subDocStartOffsetInFile+", lastLineIndex:"+lastLineIndex);
	}
}
