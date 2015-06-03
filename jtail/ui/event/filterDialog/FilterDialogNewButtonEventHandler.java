package jtail.ui.event.filterDialog;

import jtail.ui.dialog.FilterDialog;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class FilterDialogNewButtonEventHandler extends SelectionAdapter {
	private FilterDialog filterDialog;
	
	public FilterDialogNewButtonEventHandler(FilterDialog filterDialog){
		this.filterDialog = filterDialog;
	}
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		this.filterDialog.setNewMode();
	}

}
