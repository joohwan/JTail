package jtail.ui.tab.pane;

/**
 * @deprecated Please use DisplayTriggerAction class.
 */
public class ViewUpdateInfo {
	public static final int FILE_CHANGED = Integer.parseInt("1", 2);
	public static final int MOVE_UP = Integer.parseInt("10", 2);
	public static final int MOVE_DOWN = Integer.parseInt("100", 2);
	//public static final int HIGHLIGHT_MATCH = Integer.parseInt("1000", 2);
	//public static final int COPY_MATCH = Integer.parseInt("10000", 2);
	public static final int LOCATE_MATCH_TO_CENTER = Integer.parseInt("100000", 2);
	//public static final int WORD_WRAP = Integer.parseInt("1000000", 2);
	
	private final int value;
	
	public ViewUpdateInfo(int value){
		this.value = value;	
	}
	
	public int getValue(){
		return value;		
	}
}
