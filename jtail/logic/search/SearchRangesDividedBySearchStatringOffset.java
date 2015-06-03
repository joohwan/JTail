package jtail.logic.search;

import jtail.logic.Range;

public class SearchRangesDividedBySearchStatringOffset {
	private final Range toBeSearchedFirst;
	private final Range toBeSearchedIfCircularSearchIsAllowed;
	
	public SearchRangesDividedBySearchStatringOffset(Range toBeSearchedFirst,
			Range toBeSearchedIfCircularSearchIsAllowed) {
		super();
		this.toBeSearchedFirst = toBeSearchedFirst;
		this.toBeSearchedIfCircularSearchIsAllowed = toBeSearchedIfCircularSearchIsAllowed;
	}

	public Range getToBeSearchedFirst() {
		return toBeSearchedFirst;
	}

	public Range getToBeSearchedIfCircularSearchIsAllowed() {
		return toBeSearchedIfCircularSearchIsAllowed;
	}
	
	public boolean secondPartExists(){
		return (this.toBeSearchedIfCircularSearchIsAllowed != null);
	}

	@Override
	public String toString() {
		return "SearchRangesDividedBySearchStatringOffset [toBeSearchedFirst=" + toBeSearchedFirst
				+ ", toBeSearchedIfCircularSearchIsAllowed="
				+ toBeSearchedIfCircularSearchIsAllowed + "]";
	}	
}
