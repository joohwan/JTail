package jtail.logic;

import jtail.logic.filter.FilterHandler;
import jtail.ui.tab.TailTab;

public class ViewContentHandler {
	private final TailTab tailTab;
	
	public ViewContentHandler(TailTab tailTab) {
		super();
		this.tailTab = tailTab;		
	}
	
	public TailTab getTailTab(){
		return this.tailTab;
	}
	
	public ViewContent getFirstViewContent() {
		ViewContent viewContent;
		FileBlock startingBlock = this.getFileBlockHandler().getFirstFileBlock();
		if (this.tailTab.isFilterUsed()){
			viewContent = FilterHandler.getFilteredContentFrom(this.tailTab, startingBlock);			
		} else {
			viewContent = startingBlock.toViewContent();
		}
		return viewContent;
	}
	
	public ViewContent getLastViewContent() {
		ViewContent viewContent;
		FileBlock endingBlock = this.getFileBlockHandler().getLastFileBlock();
		if (this.tailTab.isFilterUsed()){
			viewContent = FilterHandler.getFilteredContentTill(this.tailTab, endingBlock);			
		} else {
			viewContent = endingBlock.toViewContent();
		}
		return viewContent;
	}
	
	public ViewContent getViewContentFrom(int viewContentStartIndex) {
		ViewContent viewContent;
		FileBlock startingBlock = this.getFileBlockHandler().getFileBlockFrom(viewContentStartIndex);
		if (this.tailTab.isFilterUsed()){
			viewContent = FilterHandler.getFilteredContentFrom(this.tailTab, startingBlock);			
		} else {
			viewContent = startingBlock.toViewContent();
		}
		return viewContent;
	}
	
	public ViewContent getViewContentTill(int viewContentEndIndex) {
		ViewContent viewContent;
		FileBlock endingBlock = this.getFileBlockHandler().getFileBlockTill(viewContentEndIndex);
		if (this.tailTab.isFilterUsed()){
			viewContent = FilterHandler.getFilteredContentTill(this.tailTab, endingBlock);
			
		} else {
			viewContent = endingBlock.toViewContent();
		}
		return viewContent;
	}
		
	private FileBlockHandler getFileBlockHandler() {
		return this.tailTab.getFileBlockHandler();
	}	
}
