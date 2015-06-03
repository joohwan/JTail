package jtail.logic.filter;

public class RegExpOptionSetting {
	private final RegExpOption option;
	private final boolean isSet;
	
	public RegExpOptionSetting(RegExpOption option, boolean isSet) {
		super();
		this.option = option;
		this.isSet = isSet;
	}
	public RegExpOption getOption() {
		return option;
	}
	public boolean isSet() {
		return isSet;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isSet ? 1231 : 1237);
		result = prime * result + ((option == null) ? 0 : option.hashCode());
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
		RegExpOptionSetting other = (RegExpOptionSetting) obj;
		if (isSet != other.isSet)
			return false;
		if (option != other.option)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "RegExpOptionSetting [option=" + option + ", isSet=" + isSet + "]";
	}	
}
