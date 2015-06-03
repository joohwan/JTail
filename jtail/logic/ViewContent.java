package jtail.logic;

import jtail.util.Value;

public class ViewContent {
	private String text;
	private Range rangeWithinFile;
	private Range matchRangeWithinText;
	
	public ViewContent(String text, Range rangeWithinFile, Range matchRangeWithinText) {
		super();
		this.text = text;
		this.rangeWithinFile = rangeWithinFile;
		this.matchRangeWithinText = matchRangeWithinText;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public int getStartIndexWithinFile() {
		return this.rangeWithinFile.getStartIndex();
	}
	
	public int getEndIndexWithinFile() {
		return this.rangeWithinFile.getEndIndex();
	}
	
	public int getLengthWithinFile(){
		return this.rangeWithinFile.getLength();
	}

	public Range getMatchRangeWithinText() {
		return matchRangeWithinText;
	}
	
	public int getMatchStartIndexWithinText(){
		if (this.matchRangeWithinText == null){
			return -1;
		} else {
			return this.matchRangeWithinText.getStartIndex();
		}
	}
	
	public int getMatchEndIndexWithinText(){
		if (this.matchRangeWithinText == null){
			return -1;
		} else {
			return this.matchRangeWithinText.getEndIndex();
		}
	}
	
	public int getTextLength(){
		return this.text.length();
	}
	
	public int getMatchLength(){
		if (this.matchRangeWithinText == null){
			return 0;
		} else {
			return this.matchRangeWithinText.getLength();
		}
	}

	public void setMatchRangeWithinText(Range matchRange) {
		this.matchRangeWithinText = matchRange;
	}	
	
	public boolean isMatchFound(){
		return (this.matchRangeWithinText != null);
	}

	public Range getRangeWithinFile() {
		return rangeWithinFile;
	}

	public void setRangeWithinFile(Range rangeWithinFile) {
		this.rangeWithinFile = rangeWithinFile;
	}
	
	@Override
	public String toString() {
		String textPart = Value.getCompactedString(this.text, 200, 200);
		return "ViewContent [rangeWithinFile="
				+ rangeWithinFile + ", matchRangeWithinText=" + matchRangeWithinText + ", text=" + textPart + "]";
	}
}
