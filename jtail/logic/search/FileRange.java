package jtail.logic.search;

import java.io.File;

import jtail.logic.Range;

public class FileRange extends Range {
	private final File file;
	
	public static final FileRange EMPTY_RANGE = new FileRange(null, 0, 0);
	
	public FileRange(File file, int startIndex, int length) {
		super(startIndex, length);
		this.file = file;
	}

	public File getFile() {
		return file;
	}
	
	public String toString(){
		return super.toString()+" "+this.file.getAbsolutePath();
	}

}
