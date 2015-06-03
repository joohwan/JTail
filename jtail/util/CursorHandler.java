package jtail.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;

public class CursorHandler {
	private Cursor waitCursor;
	
	public CursorHandler(){
		this.waitCursor = new Cursor(Display.getDefault(), SWT.CURSOR_WAIT);
	}
	
	public void startWaitCursor(){
		Display.getDefault().getActiveShell().setCursor(this.waitCursor);
	}
	
	public void stopWaitCursor(){
		this.waitCursor.dispose();
		Display.getDefault().getActiveShell().setCursor(null);
	}
}
