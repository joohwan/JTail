package jtail.logic.filter;

public enum FilterType {
	INCLUDE {
		public String toString() {
			return "include";
		}
	},
	EXCLUDE {
		public String toString() {
			return "include";
		}
	};
	
	public abstract String toString();
}