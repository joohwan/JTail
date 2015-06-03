package jtail.logic;

public class Range {
	private final int startIndex;
	private final int endIndex;	
	private final int length;
	
	//public static final Range EMPTY_RANGE = new Range(0, 0);

	public Range(int startIndex, int length){
		if (startIndex < 0 || length < 0){
			throw new RuntimeException("Incorrect values for Range("+startIndex+","+length+")");
		}
		this.startIndex = startIndex;
		this.length = length;
		this.endIndex = startIndex + length;
	}
	
	public int getStartIndex() {
		return startIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public int getLength() {
		return length;
	}		
	
	public String toString(){
		return ""+this.startIndex+","+this.endIndex+"("+this.length+")";
	}
	
	public static Range increaseRangeBy(Range range, int difference){
		int newStartIndex = range.startIndex + difference;
		int newLength = range.length;
		Range newRange = new Range(newStartIndex, newLength);
		return newRange;
	}
}
