package jtail.logic.search;

public class SearchWord {
	private final SearchCondition searchConditon;
	private final String stringToFind;
		
	public SearchWord(SearchCondition searchConditon, String stringToFind) {
		this.searchConditon = searchConditon;
		this.stringToFind = stringToFind;
	}
	
	public SearchWord(SearchWord searchWord) {
		this.searchConditon = new SearchCondition(searchWord.isCaseSensitive(),
				searchWord.isForwardSearch(), searchWord.isRegularExpressionSearch(),
				searchWord.isWholeWord());
		this.stringToFind = searchWord.getStringToFind();
	}

	public String getStringToFind() {
		return stringToFind;
	}
	
	public SearchCondition getSearchCondition() {
		return searchConditon;
	}

	public boolean isForwardSearch() {
		return searchConditon.isForwardSearch();
	}
	
	public boolean isCaseSensitive() {
		return searchConditon.isCaseSensitiveSearch();
	}

	public boolean isWholeWord() {
		return searchConditon.isWholeWordSearch();
	}

	public boolean isRegularExpressionSearch() {
		return searchConditon.isRegularExpressionSearch();
	}
		
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof SearchWord)) {
			return false;
		}
		SearchWord sc = (SearchWord) o;
		return (this.stringToFind.equals(sc.stringToFind)
				&& this.searchConditon.equals(sc.searchConditon));
	}
	
	public boolean equalsExceptSearchDirection(SearchWord searchWord) {
		if (searchWord == null) {
			return false;
		}
		return (this.stringToFind.equals(searchWord.stringToFind)
				&& this.searchConditon.equalsExceptSearchDirection(searchWord.searchConditon));

	}

	public int hashCode() {
		int result = 17;
		result = 37 * result + this.stringToFind.hashCode();
		result = 37 * result + this.searchConditon.hashCode();
		return result;
	}

	public boolean equalsStringToFind(SearchWord searchWord) {
		if (searchWord == null) return false;
		else return (this.stringToFind.equals(searchWord.stringToFind));
	}
	
	public String toString(){
		return this.stringToFind+"("+this.searchConditon+")";
	}
}