package jtail.logic;

import java.io.File;

import jtail.util.Value;

public class FileBlock {
	private final File file;
	private final Range range;	
	private final String text;
		
	public FileBlock(File file, int indexInFile, String text) {
		this.file = file;		
		this.range = new Range(indexInFile, text.length());
		this.text = text;		
	}

	public File getFile() {
		return file;
	}

	public Range getRange() {
		return range;
	}

	public String getText() {
		return text;
	}
	
	public int getStartIndex(){
		return this.range.getStartIndex();
	}
	
	public int getEndIndex(){
		return this.range.getEndIndex();
	}
	
	public int length(){
		return this.text.length();
	}

	public ViewContent toViewContent() {
		Range range = new Range(this.getStartIndex(), this.length());
		ViewContent viewContent = new ViewContent(this.text, range, null);
		return viewContent;
	}

	@Override
	public String toString() {
		String textPart = Value.getCompactedString(this.text, 100, 100);
		return "FileBlock [file=" + file + ", range=" + range + ", text=" + textPart + "]";
	}	
}
