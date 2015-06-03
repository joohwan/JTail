package jtail.ui.tab.pane;

import jtail.config.Config;
import jtail.log.MyLogger;
import jtail.logic.ViewContent;
import jtail.ui.tab.TailTab;

import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.Bullet;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.widgets.Display;

public class ViewUpdateTask implements Runnable {
	private final TailTab tailTab;
	//private final TextViewerPane textViewerPane;
	private final TextViewer textViewer;
	private final ViewContent viewContent;
	private final DisplayTriggerAction triggerAction;
	

	public ViewUpdateTask(TailTab tailTab, ViewContent viewContent, 
			DisplayTriggerAction triggerAction) {
		this.tailTab = tailTab;		
		this.viewContent = viewContent;
		this.triggerAction = triggerAction;
		this.textViewer = tailTab.getTailPane().getTextViewer();
	}
	
	/**
	 * update viewing area.
	 */
	public void run() {
		MyLogger.debug("tail is redrawn");
		StyledText textWidget = textViewer.getTextWidget();
		String content = this.viewContent.getText();
		textViewer.getDocument().set(content);
		
		if (Config.bean.isWordWrap()) {
			displayLineBullet();	
		}
		
		switch (triggerAction){
			case SEARCH_EXECUTED:
				if (isMatchFound()){
					locateMatchToCenter();		
					textViewer.setSelectedRange(this.viewContent.getMatchStartIndexWithinText(),
						this.viewContent.getMatchLength());
					if (Config.bean.isCopyMatch()) {
						textWidget.copy();
					}	
				}				
				break;			
			case SCROLL_UP:	
				textWidget.invokeAction(ST.TEXT_END);
				textWidget.invokeAction(ST.LINE_START); 
				break;
			case SCROLL_DOWN:
				textWidget.invokeAction(ST.TEXT_START);
				textWidget.invokeAction(ST.LINE_START);
				break;
			default: //case FILE_CHANGED:
				//tailTab.getSearchHandler().setMaximumStartOffset();
				textWidget.invokeAction(ST.TEXT_END);
				textWidget.invokeAction(ST.LINE_START);
				break;
		}
		
		tailTab.getTailPane().getViewPortChangeHandler().textViewerUpdated(
				this.viewContent.getStartIndexWithinFile(), 
				this.viewContent.getLengthWithinFile());
	}
	
	private boolean isMatchFound() {
		return this.viewContent.isMatchFound();
	}
	
	private void locateMatchToCenter() {
		StyledText textWidget = textViewer.getTextWidget(); 
		//int lineCount = textWidget.getLineCount();
		int lineIndexAtMatch = textWidget.getLineAtOffset(this.viewContent.getMatchRangeWithinText().getStartIndex());
		//int offsetAtLine = textWidget.getOffsetAtLine(lineIndexAtMatch);		
		int visibleTopLineIndex = textViewer.getTopIndex();
		int visibleBottomLineIndex = textViewer.getBottomIndex();
		int middleLineIndexFromTop = (visibleBottomLineIndex - visibleTopLineIndex) / 2;
		
		int newTopIndex = lineIndexAtMatch - middleLineIndexFromTop;
		if (newTopIndex < 0) {
			newTopIndex = 0;
		}
		this.textViewer.setTopIndex(newTopIndex);
	}
		
	public void setViewArea(int... locationFlags){
		StyledText textWidget = textViewer.getTextWidget();
		for (int flag : locationFlags){
			textWidget.invokeAction(flag);
		}
	}
	
	void displayLineBullet() {
		StyledText styledText = this.textViewer.getTextWidget();
		Display display = styledText.getDisplay();
		
		StyleRange style0 = new StyleRange();
		style0.metrics = new GlyphMetrics(0, 0, 20); //40);
		style0.foreground = display.getSystemColor(SWT.COLOR_BLUE);
		Bullet bullet0 = new Bullet(style0);
		styledText.setLineBullet(0, styledText.getLineCount(), bullet0);
//
//		 StyleRange style = new StyleRange();
//		 style.metrics = new GlyphMetrics(0, 0, 50);
//		 style.foreground = display.getSystemColor(SWT.COLOR_BLUE);
//		 style.background = display.getSystemColor(SWT.COLOR_WHITE);
//		 //Bullet bullet = new Bullet(ST.BULLET_NUMBER | ST.BULLET_TEXT, style);
//		 Bullet bullet = new Bullet(ST.BULLET_NUMBER, style);
//		 //bullet.text = ".";
//		 int lineIndexAtOffset = styledText.getLineAtOffset(block.getIndexInFile());
//		 styledText.setLineBullet(0, styledText.getLineCount(), bullet);
	}
}
