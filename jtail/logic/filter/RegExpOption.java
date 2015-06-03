package jtail.logic.filter;

import java.util.regex.Pattern;

public enum RegExpOption {
	UNIX_LINES {
		public int getValue() {
			return Pattern.UNIX_LINES;
		}
		public String toString() {
			return "UNIX_LINES";
		}
	},
	CASE_INSENSITIVE {
		public int getValue() {
			return Pattern.CASE_INSENSITIVE;
		}
		public String toString() {
			return "CASE_INSENSITIVE";
		}
	},
	COMMENTS {
		public int getValue() {
			return Pattern.COMMENTS;
		}
		public String toString() {
			return "COMMENTS";
		}
	},
	MULTILINE {
		public int getValue() {
			return Pattern.MULTILINE;
		}
		public String toString() {
			return "MULTILINE";
		}
	},
	LITERAL {
		public int getValue() {
			return Pattern.LITERAL;
		}
		public String toString() {
			return "LITERAL";
		}
	},
	DOTALL {
		public int getValue() {
			return Pattern.DOTALL;
		}
		public String toString() {
			return "DOTALL";
		}
	},
	UNICODE_CASE {
		public int getValue() {
			return Pattern.UNICODE_CASE;
		}
		public String toString() {
			return "UNICODE_CASE";
		}
	},
	CANON_EQ {
		public int getValue() {
			return Pattern.CANON_EQ;
		}
		public String toString() {
			return "CANON_EQ";
		}
	};
	
	public abstract String toString();
	
	public abstract int getValue();
}
