package jtail.logic.filter;

import java.util.ArrayList;
import java.util.List;

public class Filter implements Comparable<Filter> {
	private String filterName;
	private String regExp;
	private FilterType filterType;
	private List<RegExpOption> regExpOptionList;

	public Filter(String filterName, FilterType filterType, String regexp) {
		if ((filterName == null || filterName.trim().length() == 0) || (filterType == null)
				|| (regexp == null || regexp.trim().length() == 0)) {
			throw new IllegalArgumentException();
		}

		this.filterName = filterName;
		this.filterType = filterType;
		this.regExp = regexp;
		this.regExpOptionList = new ArrayList<RegExpOption>();
	}

	public Filter(String filterName, FilterType filterType, String regexp,
			List<RegExpOption> regExpOptionList) {
		this(filterName, filterType, regexp);
		this.regExpOptionList = regExpOptionList;
	}

	public int compareTo(Filter o) {
		return this.getFilterName().compareTo(o.getFilterName());
	}

	public String getFilterName() {
		return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	public String getRegExp() {
		return regExp;
	}

	public void setRegExp(String regExp) {
		this.regExp = regExp;
	}

	public FilterType getFilterType() {
		return filterType;
	}

	public void setFilterType(FilterType filterType) {
		this.filterType = filterType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((filterName == null) ? 0 : filterName.hashCode());
		result = prime * result + ((filterType == null) ? 0 : filterType.hashCode());
		result = prime * result + ((regExp == null) ? 0 : regExp.hashCode());
		result = prime * result + ((regExpOptionList == null) ? 0 : regExpOptionList.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Filter other = (Filter) obj;
		if (filterName == null) {
			if (other.filterName != null)
				return false;
		} else if (!filterName.equals(other.filterName))
			return false;
		if (filterType != other.filterType)
			return false;
		if (regExp == null) {
			if (other.regExp != null)
				return false;
		} else if (!regExp.equals(other.regExp))
			return false;
		if (regExpOptionList == null) {
			if (other.regExpOptionList != null)
				return false;
		} else if (!regExpOptionList.equals(other.regExpOptionList))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Filter [filterName=" + filterName + ", regExp=" + regExp + ", filterType="
				+ filterType + ", regExpOptionList=" + regExpOptionList + "]";
	}

	public List<RegExpOption> getRegExpOptionList() {
		return regExpOptionList;
	}

	public void setRegExpOptionList(List<RegExpOption> regExpOptionList) {
		this.regExpOptionList = regExpOptionList;
	}

	public int getPatternCompileFlag() {
		int patternCompileFlags = 0;
		for (RegExpOption option : this.regExpOptionList){
			patternCompileFlags = patternCompileFlags | option.getValue();
		}
		return patternCompileFlags;
	}
}
