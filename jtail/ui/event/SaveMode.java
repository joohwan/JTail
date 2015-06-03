package jtail.ui.event;

public enum SaveMode {
	NEW {
		public String toString(){
			return "new";
		}
	},
	UPDATE {
		public String toString(){
			return "update";
		}
	};
	
	abstract public String toString();
}
