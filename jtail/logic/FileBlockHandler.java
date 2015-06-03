package jtail.logic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import jtail.config.Config;
import jtail.log.MyLogger;
import jtail.logic.search.FileRange;
import jtail.util.FileUtil;
import jtail.util.LoggingUtil;

public class FileBlockHandler {
	//public final static int getBlockSize() = 50 * 1000; // 100 KB 
	//private final static int MAXIMUM_getBlockSize() = 1000 * 1000; // 1 MB
		
	private final File file;	
	
	public FileBlockHandler(File file) {
		this.file = file;		
	}
	
	public static int getBlockSize(){
		int blockSizeInByte = Config.bean.getDisplayBlockSizeInKb() * 1024;
		return blockSizeInByte;
	}
	
	public static int getHalfBlockSize(){
		return (getBlockSize() / 2);
	}
	
//	private SearchOffsetHandler getSearchOffsetHandler(){
//		return this.tailTab.getSearchOffsetHandler();
//	}
	
	public List<FileRange> getFileRangeList(boolean isForwardSearch, int searchStartOffset){
		int fileLength = getFileLength();
		List<FileRange> list = new ArrayList<FileRange>();
		int blockSize = getBlockSize();
		
		if (fileLength < searchStartOffset){
			return list;
		}	
							
		if (fileLength <= blockSize){
			if (searchStartOffset < fileLength){
				list.add(new FileRange(this.file, searchStartOffset, fileLength - searchStartOffset));
			}
			if (0 < searchStartOffset) {
				list.add(new FileRange(this.file, 0, searchStartOffset));
			}
		} else {
			int blockStartOffset = searchStartOffset;
			int blockEndOffset = 0;
			boolean reachedAtFileEnd = false;
			while (true){
				blockEndOffset = blockStartOffset + blockSize;
				if (blockEndOffset < searchStartOffset){
					list.add(new FileRange(this.file, blockStartOffset, blockSize));
					blockStartOffset = blockEndOffset;
				} else if (reachedAtFileEnd && searchStartOffset <= blockEndOffset){
					list.add(new FileRange(this.file, blockStartOffset, searchStartOffset - blockStartOffset));
					break;
				} else if (blockEndOffset < fileLength){
					list.add(new FileRange(this.file, blockStartOffset, blockSize));
					blockStartOffset = blockEndOffset;
				} else { // reach at the end of file
					reachedAtFileEnd = true;
					list.add(new FileRange(this.file, blockStartOffset, fileLength - blockStartOffset));
					blockStartOffset = 0; // go to the start of the file
				}							
			}
		}			
		
		if (isForwardSearch
				|| list.size() <= 1){
			return list;
		} else {
			List<FileRange> reversedList = new ArrayList<FileRange>();
			for (int i = list.size() - 1; i >= 0; i--) {
				reversedList.add(list.get(i));
			}
			return reversedList;
		}
	}
	
	public List<FileBlock> getAllFileBlocks(){		
		List<FileBlock> list = new ArrayList<FileBlock>();
		int currentOffset = 0;
		
		FileBlock block = this.getFirstFileBlock();
		list.add(block);
		currentOffset = block.length();
				
		while (this.nextBlockExists(currentOffset)) {
			block = this.getNextFileBlock(block);
			list.add(block);
			currentOffset += block.length();
		}
		
		return list;
	}

	private int getFileLength() {
		return (int) this.file.length();
	}
	
	public FileBlock getFirstFileBlock() {
		int readSize = 0;
		if (this.getFileLength() - getBlockSize() >= 0){
			readSize = getBlockSize();
		} else {
			readSize = this.getFileLength();
		}
		
		return getFileBlock(0, readSize);
	}
	
	public FileBlock getLastFileBlock() {
		int skipSize = 0;
		int readSize = 0;
		if (getFileLength() <= getBlockSize()){
			skipSize = 0;
			readSize = getFileLength();
		} else {
			skipSize = getFileLength() - getBlockSize();
			readSize = getBlockSize();
		}
		String content = FileUtil.readFile(file, skipSize, readSize);
		return new FileBlock(file, skipSize, content);		
	}		
		
	public FileBlock getLastFileBlock(int blockStartIndexInFile) {
		int skipSize = 0;
		int readSize = 0;
		if (getFileLength() - blockStartIndexInFile >= getBlockSize()){
			skipSize = getFileLength() - getBlockSize();
			readSize = getBlockSize();
		} else {
			skipSize = blockStartIndexInFile;
			readSize = getFileLength() - blockStartIndexInFile;
		}
		
		return getFileBlock(skipSize, readSize);
	}
	
	public FileBlock getFileBlockTill(int blockEndIndexInFile) {
		int skipSize = 0;
		int readSize = 0;
		if (blockEndIndexInFile - getBlockSize() >= 0){
			skipSize = blockEndIndexInFile - getBlockSize();
			readSize = getBlockSize();
		} else {
			skipSize = 0;
			readSize = blockEndIndexInFile;
		}
						
		return getFileBlock(skipSize, readSize);
	}
	
	public FileBlock getPreviousFileBlock(FileBlock block) {
		if (block == null){
			return null;
		}
		return getFileBlockTill(block.getStartIndex());
	}
	
	public FileBlock getFileBlockFrom(int blockStartIndexInFile) {
		int skipSize = 0;
		int readSize = 0;
		int fileSize = this.getFileLength();
		if (fileSize - blockStartIndexInFile >= getBlockSize()){
			skipSize = blockStartIndexInFile;
			readSize = getBlockSize();
		} else {
			skipSize = blockStartIndexInFile;
			readSize = fileSize - blockStartIndexInFile;
		}
						
		return getFileBlock(skipSize, readSize);
	}
	
	public FileBlock getNextFileBlock(FileBlock block) {
		if (block == null){
			return null;
		}
		return getFileBlockTill(block.getStartIndex()+block.length());
	}
	
	public boolean previousBlockExists(int startOffsetOfCurrentBlock){
		return (startOffsetOfCurrentBlock > 0);
	}
	
	public boolean nextBlockExists(int endOffsetOfCurrentBlock){
		return (endOffsetOfCurrentBlock < this.getFileLength());
	}
		
	public boolean previousBlockExists(FileBlock block) {
		if (block == null){
			return false;
		}
		return (block.getStartIndex() > 0);
	}
	
	public boolean nextBlockExists(FileBlock block) {
		if (block == null){
			return false;
		}
		return (block.getEndIndex() < this.getFileLength());
	}

	public FileBlock getFileBlock(final int skipSize, final int readSize) {		
		long startTimeInMillis = System.currentTimeMillis();
		Callable<String> r = new Callable<String>(){
			public String call(){
				return FileUtil.readFile(file, skipSize, readSize);
			}
		};
						
		String fileContent = "";
		try {
			fileContent = r.call();
		} catch (Exception e){
			MyLogger.error("file read failed", e);
		}
		MyLogger.debug("getFileBlock("+skipSize+","+readSize+") took "+LoggingUtil.getElapsedTimeInSeconds(startTimeInMillis, 2));
		return new FileBlock(this.file, skipSize, fileContent);
	}
	
	public File getFile(){
		return this.file;
	}
}
